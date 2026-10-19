/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.container.SoilLayer;
import edu.ie3.datamodel.models.input.container.SoilType;
import edu.ie3.datamodel.utils.validation.SoilValidationUtils;
import java.util.List;

/**
 * Source for soil layers and soil types. It holds the source agnostic reading logic and delegates
 * the transport specific row access to the provided {@link DataSource}.
 */
public class SoilDataSource extends EntitySource {
  private final DataSource dataSource;

  /**
   * Constructor for a soil data source.
   *
   * @param dataSource the data source to read soil types and layers from
   */
  public SoilDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Reads all soil types from the underlying source.
   *
   * @return the soil types
   * @throws SourceException if the source cannot be read
   */
  public List<SoilType> getSoilTypes() throws SourceException {
    return SoilDataParser.readSoilTypes(dataSource.getSourceData(SoilType.class));
  }

  /**
   * Reads all soil layers from the underlying source.
   *
   * @return the soil layers
   * @throws SourceException if the source cannot be read
   */
  public List<SoilLayer> getSoilLayers() throws SourceException {
    return SoilDataParser.readSoilLayers(dataSource.getSourceData(SoilLayer.class));
  }

  @Override
  public void validate() throws ValidationException {
    try {
      SoilValidationUtils.validateAll(getSoilLayers(), getSoilTypes());
    } catch (SourceException e) {
      throw new FailedValidationException(
          "Unable to validate soil source because it could not be read.", e);
    }
  }
}
