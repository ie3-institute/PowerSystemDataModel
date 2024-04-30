/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.VoltageLevelException;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.*;
import javax.measure.quantity.ElectricPotential;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;

/**
 * Data used by {@link EntityFactory} to create an instance of an entity than can be created based
 * only on a mapping of fieldName to value. This class can be used whenever no additional data is
 * needed, but also functions as a parent class for extensions.
 *
 * @version 0.1
 * @since 28.01.20
 */
public class EntityData extends FactoryData {
  private static final Logger logger = LoggerFactory.getLogger(EntityData.class);
  private static final GeoJsonReader geoJsonReader = new GeoJsonReader();

  /**
   * Creates a new EntityData object
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   */
  public EntityData(Map<String, String> fieldsToAttributes, Class<? extends Entity> entityClass) {
    super(fieldsToAttributes, entityClass);
  }

  /**
   * Creates a new EntityData object based on a given {@link FactoryData} object
   *
   * @param factoryData The factory data object to use attributes of
   */
  protected EntityData(FactoryData factoryData) {
    super(factoryData.getFieldsToValues(), factoryData.getTargetClass());
  }

  @Override
  @SuppressWarnings("unchecked cast")
  public Class<? extends Entity> getTargetClass() {
    return (Class<? extends Entity>) super.getTargetClass();
  }

  /**
   * Returns boolean value for given field name. Throws {@link FactoryException} if field does not
   * exist, or field value is null or empty.
   *
   * @param field field name
   * @return true if value is "1" or "true", false otherwise
   */
  public boolean getBoolean(String field) {
    final String value = getField(field);

    if (value == null || value.trim().isEmpty())
      throw new FactoryException(String.format("Field \"%s\" is null or empty", field));

    return value.trim().equals("1") || value.trim().equalsIgnoreCase("true");
  }

  /**
   * Parses and returns a geometry from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param field field name
   * @return Geometry if field value is not empty, empty Optional otherwise
   */
  private Optional<Geometry> getGeometry(String field) {
    String value = getField(field);
    try {
      if (value.trim().isEmpty()) return Optional.empty();
      else return Optional.of(geoJsonReader.read(value));
    } catch (ParseException pe) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse geometry of field \"%s\" with value \"%s\"",
              field, value),
          pe);
    }
  }

  /**
   * Parses and returns a geometrical LineString from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param field field name
   * @return LineString if field value is not empty, empty Optional otherwise
   */
  public Optional<LineString> getLineString(String field) {
    Optional<Geometry> geom = getGeometry(field);
    if (geom.isPresent()) {
      if (geom.get() instanceof LineString lineString) return Optional.of(lineString);
      else
        throw new FactoryException(
            "Geometry is of type "
                + geom.getClass().getSimpleName()
                + ", but type LineString is required");
    } else return Optional.empty();
  }

  /**
   * Parses and returns a geometrical Point from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param field field name
   * @return Point if field value is not empty, empty Optional otherwise
   */
  public Optional<Point> getPoint(String field) {
    Optional<Geometry> geom = getGeometry(field);
    if (geom.isPresent()) {
      if (geom.get() instanceof Point point) return Optional.of(point);
      else
        throw new FactoryException(
            "Geometry is of type "
                + geom.getClass().getSimpleName()
                + ", but type Point is required");
    } else return Optional.empty();
  }

  /**
   * Parses and returns a voltage level from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param voltLvlField name of the field containing the voltage level
   * @param ratedVoltField name of the field containing the rated voltage
   * @return Voltage level
   */
  public VoltageLevel getVoltageLvl(String voltLvlField, String ratedVoltField) {
    try {
      final String voltLvlId = getField(voltLvlField);
      final ComparableQuantity<ElectricPotential> vRated = getQuantity(ratedVoltField, KILOVOLT);

      return parseToGermanVoltLvlOrIndividual(voltLvlId, vRated);
    } catch (IllegalArgumentException iae) {
      throw new FactoryException("VoltageLevel could not be parsed", iae);
    }
  }

  /**
   * Parses the given voltage level information to German voltage level or builds an individual one,
   * if no suitable one can be found.
   *
   * @param voltLvlId Identifier of the voltage level
   * @param vRated Foreseen rated voltage
   * @return A suitable German {@link edu.ie3.datamodel.models.voltagelevels.CommonVoltageLevel} or
   *     an individual one
   */
  private VoltageLevel parseToGermanVoltLvlOrIndividual(
      String voltLvlId, ComparableQuantity<ElectricPotential> vRated) {
    try {
      return GermanVoltageLevelUtils.parse(voltLvlId, vRated);
    } catch (VoltageLevelException e) {
      logger.warn(
          "Cannot parse ({}, {}) to common German voltage level. Build an individual one.",
          voltLvlId,
          vRated);
      return new VoltageLevel(voltLvlId, vRated);
    }
  }

  @Override
  public String toString() {
    return "EntityData{"
        + "fieldsToAttributes="
        + getFieldsToValues()
        + ", targetClass="
        + getTargetClass()
        + '}';
  }
}
