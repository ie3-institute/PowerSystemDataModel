/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.result;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.io.factory.result.SystemParticipantResultFactory;
import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.connector.SwitchResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WResult;
import edu.ie3.datamodel.models.result.system.*;
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult;
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import edu.ie3.util.TimeUtil;
import edu.ie3.util.exceptions.QuantityException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

/**
 * 'Serializer' for {@link ResultEntity}s into a fieldName to value representation to allow for an
 * easy processing into a database or file sink e.g. .csv It is important that the units used in
 * this class are equal to the units used {@link SystemParticipantResultFactory} to prevent invalid
 * interpretation of unit prefixes!
 *
 * @version 0.1
 * @since 31.01.20
 */
public class ResultEntityProcessor extends EntityProcessor<ResultEntity> {

  /** The entities that can be used within this processor */
  public static final List<Class<? extends ResultEntity>> eligibleEntityClasses =
      List.of(
          LoadResult.class,
          FixedFeedInResult.class,
          BmResult.class,
          PvResult.class,
          ChpResult.class,
          WecResult.class,
          StorageResult.class,
          EvcsResult.class,
          EvResult.class,
          HpResult.class,
          Transformer2WResult.class,
          Transformer3WResult.class,
          LineResult.class,
          SwitchResult.class,
          NodeResult.class,
          ThermalHouseResult.class,
          CylindricalStorageResult.class,
          EmResult.class,
          FlexOptionsResult.class);

  public ResultEntityProcessor(Class<? extends ResultEntity> registeredClass)
      throws EntityProcessorException {
    super(registeredClass, TimeUtil.withDefaults.getDateTimeFormatter());
  }

  public ResultEntityProcessor(
      Class<? extends ResultEntity> registeredClass, DateTimeFormatter dateTimeFormatter)
      throws EntityProcessorException {
    super(registeredClass, dateTimeFormatter);
  }

  @Override
  protected Try<String, QuantityException> handleProcessorSpecificQuantity(
      Quantity<?> quantity, String fieldName) {
    return switch (fieldName) {
      case "energy", "eConsAnnual", "eStorage":
        yield Success.of(
            quantityValToOptionalString(
                quantity.asType(Energy.class).to(StandardUnits.ENERGY_RESULT)));
      case "q":
        yield Success.of(
            quantityValToOptionalString(
                quantity.asType(Power.class).to(StandardUnits.REACTIVE_POWER_RESULT)));
      case "p", "pMax", "pOwn", "pThermal", "pRef", "pMin":
        yield Success.of(
            quantityValToOptionalString(
                quantity.asType(Power.class).to(StandardUnits.ACTIVE_POWER_RESULT)));
      default:
        yield Failure.of(
            new QuantityException(
                "Cannot process quantity with value '"
                    + quantity
                    + "' for field with name "
                    + fieldName
                    + " in result entity processing!"));
    };
  }

  @Override
  protected List<Class<? extends ResultEntity>> getEligibleEntityClasses() {
    return eligibleEntityClasses;
  }
}
