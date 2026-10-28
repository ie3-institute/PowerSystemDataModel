/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import static tech.units.indriya.unit.Units.PERCENT;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.models.result.CongestionResult;
import edu.ie3.datamodel.models.result.CongestionResult.InputModelType;
import edu.ie3.datamodel.utils.Try;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

public class CongestionResultFactory
    extends ResultEntityFactory<CongestionResult, CongestionResult> {
  public CongestionResultFactory() {
    super(CongestionResult.class);
  }

  @Override
  protected CongestionResult buildModel(FieldMap data, ZonedDateTime time, UUID inputModel) {
    InputModelType type =
        Try.of(() -> InputModelType.parse(data.getField(TYPE)), ParsingException.class)
            .transformF(FactoryException::new)
            .getOrThrow();

    int subgrid = data.getInt(SUBGRID);

    ComparableQuantity<Dimensionless> value = data.getQuantity(VALUE, PERCENT);
    ComparableQuantity<Dimensionless> min = data.getQuantity(MIN, PERCENT);
    ComparableQuantity<Dimensionless> max = data.getQuantity(MAX, PERCENT);

    return new CongestionResult(time, inputModel, type, subgrid, value, min, max);
  }
}
