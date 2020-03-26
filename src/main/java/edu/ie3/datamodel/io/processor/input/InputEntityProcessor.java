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
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

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
              LineGraphicInput.class));

  public InputEntityProcessor(Class<? extends InputEntity> registeredClass) {
    super(registeredClass);
  }

  @Override
  protected Optional<String> handleProcessorSpecificQuantity(
      Quantity<?> quantity, String fieldName) {
    Optional<String> normalizedQuantityValue = Optional.empty();
    switch (fieldName) {
      case "vTarget":
        normalizedQuantityValue =
            quantityValToOptionalString(
                quantity.asType(Dimensionless.class).to(StandardUnits.TARGET_VOLTAGE_MAGNITUDE));
        break;
      case "vrated":
        normalizedQuantityValue =
            quantityValToOptionalString(
                quantity.asType(ElectricPotential.class).to(StandardUnits.RATED_VOLTAGE_MAGNITUDE));
        break;
      case "sRated":
        normalizedQuantityValue =
            quantityValToOptionalString(quantity.asType(Power.class).to(StandardUnits.S_RATED));
        break;
      case "eConsAnnual":
        normalizedQuantityValue =
            quantityValToOptionalString(quantity.asType(Energy.class).to(StandardUnits.ENERGY_IN));
        break;
      default:
        log.error(
            "Cannot process quantity with value '{}' for field with name {} in result entity processing!",
            quantity,
            fieldName);
        break;
    }
    return normalizedQuantityValue;
  }

  @Override
  protected List<Class<? extends InputEntity>> getAllEligibleClasses() {
    return eligibleEntityClasses;
  }
}
