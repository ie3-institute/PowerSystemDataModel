/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;

/**
 * Interface that should be implemented by all elements holding a {@link ThermalBusInput} elements
 * and should be processable by the {@link Extractor}.
 *
 * @version 0.1
 * @since 31.03.20
 */
public interface HasThermalBus extends NestedEntity {

  ThermalBusInput getThermalBus();
}
