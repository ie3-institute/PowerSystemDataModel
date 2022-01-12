/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.input;

import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
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
              TimeSeriesMappingSource.MappingEntry.class,
              /* - AssetInput */
              NodeInput.class,
              LineInput.class,
              Transformer2WInput.class,
              Transformer3WInput.class,
              SwitchInput.class,
              MeasurementUnitInput.class,
              ThermalBusInput.class,
              /* -- SystemParticipantInput */
              ChpInput.class,
              BmInput.class,
              EvInput.class,
              EvcsInput.class,
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
  protected List<Class<? extends InputEntity>> getEligibleEntityClasses() {
    return eligibleEntityClasses;
  }
}
