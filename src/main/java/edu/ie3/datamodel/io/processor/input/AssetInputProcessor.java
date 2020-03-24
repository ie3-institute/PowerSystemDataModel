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
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.result.system.*;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 23.03.20
 */
public class AssetInputProcessor extends EntityProcessor<AssetInput> {

  /** The entities that can be used within this processor */
  public static final List<Class<? extends AssetInput>> eligibleEntityClasses =
      Collections.unmodifiableList(
          Arrays.asList(
              FixedFeedInInput.class,
              PvInput.class,
              WecInput.class,
              ChpInput.class,
              BmInput.class,
              EvInput.class,
              LoadInput.class,
              StorageInput.class,
              HpInput.class,
              LineInput.class,
              SwitchInput.class,
              Transformer2WInput.class,
              Transformer3WInput.class,
              ThermalHouseInput.class,
              CylindricalStorageInput.class,
              ThermalBusInput.class,
              MeasurementUnitInput.class,
              NodeInput.class,
              EvcsInput.class));

  public AssetInputProcessor(Class<? extends AssetInput> registeredClass) {
    super(registeredClass);
  }

  //    @Override
  //    protected Optional<LinkedHashMap<String, String>> processEntity(AssetInput entity) {
  //        Optional<LinkedHashMap<String, String>> resultMapOpt;
  //
  //        try {
  //            LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
  //            for(String fieldName : headerElements) {
  //                Method method = fieldNameToMethod.get(fieldName);
  //                Optional<Object> methodReturnObjectOpt =
  // Optional.ofNullable(method.invoke(entity));
  //
  //                if(methodReturnObjectOpt.isPresent()) {
  //                    resultMap.put(fieldName, processMethodResult(methodReturnObjectOpt.get(),
  // method, fieldName));
  //                } else {
  //                    resultMap.put(fieldName, "");
  //                }
  //            }
  //            resultMapOpt = Optional.of(resultMap);
  //        } catch(Exception e) {
  //            log.error("Error during entity processing in ResultEntityProcessor:", e);
  //            resultMapOpt = Optional.empty();
  //        }
  //        return resultMapOpt;
  //    }

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
  protected List<Class<? extends AssetInput>> getAllEligibleClasses() {
    return eligibleEntityClasses;
  }
}
