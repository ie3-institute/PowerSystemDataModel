/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.Set;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 07.04.20
 */
public interface ThermalSource extends DataSource {

  Set<ThermalBusInput> getThermalBuses();

  Set<ThermalBusInput> getThermalBuses(Set<OperatorInput> operators);

  Set<ThermalStorageInput> getThermalStorages();

  Set<ThermalStorageInput> getThermalStorages(
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses);

  Set<ThermalHouseInput> getThermalHouses();

  Set<ThermalHouseInput> getThermalHouses(
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses);

  Set<CylindricalStorageInput> getCylindricStorages();

  Set<CylindricalStorageInput> getCylindricStorages(
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses);
}
