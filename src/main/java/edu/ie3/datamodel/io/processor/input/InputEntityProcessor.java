/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.input;

import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.measure.Quantity;
import javax.measure.quantity.*;

/**
 * Processes all {@link InputEntity}s and it's child classes
 *
 * @version 0.1
 * @since 23.03.20
 */
public class InputEntityProcessor extends EntityProcessor<InputEntity> {

  /** The entities that can be used within this processor */
  public static final List<Class<? extends InputEntity>> eligibleEntityClasses =
      Collections.unmodifiableList(
          Arrays.asList(
              /* InputEntity */
              OperatorInput.class,
              RandomLoadParameters.class,
              /* - AssetInput */
              NodeInput.class,
              LineInput.class,
              Transformer2WInput.class,
              Transformer3WInput.class,
              SwitchInput.class,
              MeasurementUnitInput.class,
              EvcsInput.class,
              ThermalBusInput.class,
              /* -- SystemParticipantInput */
              ChpInput.class,
              BmInput.class,
              EvInput.class,
              FixedFeedInInput.class,
              HpInput.class,
              LoadInput.class,
              PvInput.class,
              StorageInput.class,
              WecInput.class,
              /* -- ThermalUnitInput */
              ThermalHouseInput.class,
              CylindricalStorageInput.class,
              /* - GraphicInput */
              NodeGraphicInput.class,
              LineGraphicInput.class,
              /* - AssetTypeInput */
              BmTypeInput.class,
              ChpTypeInput.class,
              EvTypeInput.class,
              HpTypeInput.class,
              LineTypeInput.class,
              Transformer2WTypeInput.class,
              Transformer3WTypeInput.class,
              StorageTypeInput.class,
              WecTypeInput.class));

  public InputEntityProcessor(Class<? extends InputEntity> registeredClass) {
    super(registeredClass);
  }

  @Override
  protected Optional<String> handleProcessorSpecificQuantity(
      Quantity<?> quantity, String fieldName) {
    Optional<String> normalizedQuantityValue = Optional.empty();
    switch (fieldName) {
      case "energy":
      case "eConsAnnual":
      case "eStorage":
        normalizedQuantityValue =
            quantityValToOptionalString(quantity.asType(Energy.class).to(StandardUnits.ENERGY_IN));
        break;
      case "q":
        normalizedQuantityValue =
            quantityValToOptionalString(
                quantity.asType(Power.class).to(StandardUnits.REACTIVE_POWER_IN));
        break;
      case "p":
      case "pMax":
      case "pOwn":
      case "pThermal":
        normalizedQuantityValue =
            quantityValToOptionalString(
                quantity.asType(Power.class).to(StandardUnits.ACTIVE_POWER_IN));
        break;
      default:
        log.error(
            "Cannot process quantity with value '{}' for field with name {} in input entity processing!",
            quantity,
            fieldName);
        break;
    }
    return normalizedQuantityValue;
  }

  @Override
  protected List<Class<? extends InputEntity>> getEligibleEntityClasses() {
    return eligibleEntityClasses;
  }
}
