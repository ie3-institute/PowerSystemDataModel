/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import static tech.units.indriya.unit.Units.PERCENT;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.result.CongestionResult;
import edu.ie3.datamodel.models.result.CongestionResult.InputModelType;
import edu.ie3.datamodel.utils.Try;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

/** The type Congestion result factory. */
public class CongestionResultFactory extends ResultEntityFactory<CongestionResult> {
  private static final String TYPE = "type";
  private static final String SUBGRID = "subgrid";
  private static final String VALUE = "value";
  private static final String MIN = "min";
  private static final String MAX = "max";

  /** Instantiates a new Congestion result factory. */
  public CongestionResultFactory() {
    super(CongestionResult.class);
  }

  /**
   * Instantiates a new Congestion result factory.
   *
   * @param dateTimeFormatter the date time formatter
   */
  public CongestionResultFactory(DateTimeFormatter dateTimeFormatter) {
    super(dateTimeFormatter, CongestionResult.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return List.of(newSet(TIME, INPUT_MODEL, TYPE, SUBGRID, MIN, MAX));
  }

  @Override
  protected CongestionResult buildModel(EntityData data) {
    ZonedDateTime zdtTime = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModel = data.getUUID(INPUT_MODEL);

    InputModelType type =
        Try.of(() -> InputModelType.parse(data.getField(TYPE)), ParsingException.class)
            .transformF(FactoryException::new)
            .getOrThrow();

    int subgrid = data.getInt(SUBGRID);

    ComparableQuantity<Dimensionless> value = data.getQuantity(VALUE, PERCENT);
    ComparableQuantity<Dimensionless> min = data.getQuantity(MIN, PERCENT);
    ComparableQuantity<Dimensionless> max = data.getQuantity(MAX, PERCENT);

    return new CongestionResult(zdtTime, inputModel, type, subgrid, value, min, max);
  }
}
