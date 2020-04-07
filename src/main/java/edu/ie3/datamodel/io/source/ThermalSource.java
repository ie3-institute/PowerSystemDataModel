/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.Collection;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 07.04.20
 */
public interface ThermalSource {

  Collection<ThermalBusInput> getThermalBuses();

  Collection<ThermalStorageInput> getThermalStorages();

  Collection<ThermalHouseInput> getThermalHouses();

  Collection<CylindricalStorageInput> getCylindricStorages();
}
