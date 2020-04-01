/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 01.04.20
 */
public interface HasBus extends NestedEntity {

  ThermalBusInput getBus();
}
