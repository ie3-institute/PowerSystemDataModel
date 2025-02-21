/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.container.ThermalGrid
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.test.common.ThermalUnitInputTestData
import edu.ie3.util.TimeUtil
import edu.ie3.util.quantities.interfaces.HeatCapacity
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity
import edu.ie3.util.quantities.interfaces.ThermalConductance
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Temperature
import javax.measure.quantity.Volume

class ThermalValidationUtilsTest extends Specification {

  // General data
  private static final UUID thermalUnitUuid = UUID.fromString("717af017-cc69-406f-b452-e022d7fb516a")
  private static final String id = "thermal_unit_test"
  private static final OperationTime operationTime = OperationTime.builder()
  .withStart(TimeUtil.withDefaults.toZonedDateTime("2020-03-24T15:11:31Z"))
  .withEnd(TimeUtil.withDefaults.toZonedDateTime("2020-03-25T15:11:31Z")).build()
  private static final OperatorInput operator = new OperatorInput(
  UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator")

  // Specific data for thermal house input
  private static final ComparableQuantity<ThermalConductance> thermalConductance = Quantities.getQuantity(10, StandardUnits.THERMAL_TRANSMISSION)
  private static final ComparableQuantity<HeatCapacity> ethCapa = Quantities.getQuantity(20, StandardUnits.HEAT_CAPACITY)
  private static final ComparableQuantity<Temperature> TARGET_TEMPERATURE = Quantities.getQuantity(20, StandardUnits.TEMPERATURE)
  private static final ComparableQuantity<Temperature> UPPER_TEMPERATURE_LIMIT = Quantities.getQuantity(25, StandardUnits.TEMPERATURE)
  private static final ComparableQuantity<Temperature> LOWER_TEMPERATURE_LIMIT = Quantities.getQuantity(15, StandardUnits.TEMPERATURE)
  private static final String HOUSING_TYPE = "House"
  private static final Integer NUMBER_INHABITANTS = 2.0

  // Specific data for thermal cylindrical storage input
  private static final ComparableQuantity<Volume> storageVolumeLvl = Quantities.getQuantity(100, StandardUnits.VOLUME)
  private static final ComparableQuantity<Temperature> inletTemp = Quantities.getQuantity(100, StandardUnits.TEMPERATURE)
  private static final ComparableQuantity<Temperature> returnTemp = Quantities.getQuantity(80, StandardUnits.TEMPERATURE)
  private static final ComparableQuantity<SpecificHeatCapacity> c = Quantities.getQuantity(1.05, StandardUnits.SPECIFIC_HEAT_CAPACITY)

  // Thermal House

  def "Smoke Test: Correct thermal house throws no exception"() {
    given:
    def thermalHouseInput = ThermalUnitInputTestData.thermalHouseInput

    when:
    ValidationUtils.check(thermalHouseInput)

    then:
    noExceptionThrown()
  }

  def "ThermalValidationUtils.checkThermalHouse() recognizes all potential errors for a thermal house"() {
    when:
    List<Try<Void, ? extends ValidationException>> exceptions = ThermalValidationUtils.check(invalidThermalHouse).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidThermalHouse                                                                                                                                                                                                                                                                           || expectedSize || expectedException
    new ThermalHouseInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, Quantities.getQuantity(-10, StandardUnits.THERMAL_TRANSMISSION), ethCapa, TARGET_TEMPERATURE, UPPER_TEMPERATURE_LIMIT, LOWER_TEMPERATURE_LIMIT, HOUSING_TYPE, NUMBER_INHABITANTS)   || 1            || new InvalidEntityException("The following quantities have to be zero or positive: -10 kW/K", invalidThermalHouse)
    new ThermalHouseInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, thermalConductance, Quantities.getQuantity(0, StandardUnits.HEAT_CAPACITY), TARGET_TEMPERATURE, UPPER_TEMPERATURE_LIMIT, LOWER_TEMPERATURE_LIMIT, HOUSING_TYPE, NUMBER_INHABITANTS) || 1            || new InvalidEntityException("The following quantities have to be positive: 0 kWh/K", invalidThermalHouse)
    new ThermalHouseInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, thermalConductance, ethCapa, Quantities.getQuantity(0, StandardUnits.TEMPERATURE), UPPER_TEMPERATURE_LIMIT, LOWER_TEMPERATURE_LIMIT, HOUSING_TYPE, NUMBER_INHABITANTS)              || 1            || new InvalidEntityException("Target temperature must be higher than lower temperature limit and lower than upper temperature limit", invalidThermalHouse)
    new ThermalHouseInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, thermalConductance, ethCapa, TARGET_TEMPERATURE, Quantities.getQuantity(0, StandardUnits.TEMPERATURE), LOWER_TEMPERATURE_LIMIT, HOUSING_TYPE, NUMBER_INHABITANTS)                   || 1            || new InvalidEntityException("Target temperature must be higher than lower temperature limit and lower than upper temperature limit", invalidThermalHouse)
    new ThermalHouseInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, thermalConductance, ethCapa, TARGET_TEMPERATURE, UPPER_TEMPERATURE_LIMIT, Quantities.getQuantity(30, StandardUnits.TEMPERATURE), HOUSING_TYPE, NUMBER_INHABITANTS)                  || 1            || new InvalidEntityException("Target temperature must be higher than lower temperature limit and lower than upper temperature limit", invalidThermalHouse)
    new ThermalHouseInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, thermalConductance, ethCapa, TARGET_TEMPERATURE, TARGET_TEMPERATURE, LOWER_TEMPERATURE_LIMIT, HOUSING_TYPE, NUMBER_INHABITANTS)                                                     || 1            || new InvalidEntityException("Target temperature must be higher than lower temperature limit and lower than upper temperature limit", invalidThermalHouse)
    new ThermalHouseInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, thermalConductance, ethCapa, TARGET_TEMPERATURE, UPPER_TEMPERATURE_LIMIT, LOWER_TEMPERATURE_LIMIT, "someWrongType", NUMBER_INHABITANTS)                                             || 1            || new InvalidEntityException("Housing type must be either 'house' or 'flat'", invalidThermalHouse)
    new ThermalHouseInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, thermalConductance, ethCapa, TARGET_TEMPERATURE, UPPER_TEMPERATURE_LIMIT, LOWER_TEMPERATURE_LIMIT, HOUSING_TYPE, 0d)                                                                || 1            || new InvalidEntityException("Number of inhabitants must be greater than zero", invalidThermalHouse)
  }

  // Thermal Cylindrical Storage

  def "Smoke Test: Correct thermal cylindrical storage throws no exception"() {
    given:
    def cylindricalStorageInput = ThermalUnitInputTestData.cylindricStorageInput

    when:
    ValidationUtils.check(cylindricalStorageInput)

    then:
    noExceptionThrown()
  }

  def "ThermalValidationUtils.checkCylindricalStorage() recognizes all potential errors for a thermal cylindrical storage"() {
    when:
    List<Try<Void, ? extends ValidationException>> exceptions = ThermalValidationUtils.check(invalidCylindricalStorage).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidCylindricalStorage                                                                                                                                                                                                                                       || expectedSize || expectedException
    new CylindricalStorageInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, storageVolumeLvl, Quantities.getQuantity(100, StandardUnits.TEMPERATURE), Quantities.getQuantity(200, StandardUnits.TEMPERATURE), c)            || 1            || new InvalidEntityException("Inlet temperature of the cylindrical storage cannot be lower or equal than outlet temperature", invalidCylindricalStorage)
    new CylindricalStorageInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, storageVolumeLvl, Quantities.getQuantity(100, StandardUnits.TEMPERATURE), Quantities.getQuantity(100, StandardUnits.TEMPERATURE), c)            || 1            || new InvalidEntityException("Inlet temperature of the cylindrical storage cannot be lower or equal than outlet temperature", invalidCylindricalStorage)
    new CylindricalStorageInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, Quantities.getQuantity(-100, StandardUnits.VOLUME), inletTemp, returnTemp, Quantities.getQuantity(-1.05, StandardUnits.SPECIFIC_HEAT_CAPACITY)) || 1            || new InvalidEntityException("The following quantities have to be positive: -100 ㎥, -1.05 kWh/K*m³", invalidCylindricalStorage)
  }

  def "ThermalValidationUtils.check() works for complete ThermalGrid as well"() {
    when:
    def thermalBus = ThermalUnitInputTestData.thermalBus
    def cylindricalStorageInput = [
      ThermalUnitInputTestData.cylindricStorageInput
    ]


    ThermalGrid thermalGrid = new ThermalGrid(thermalBus, [thermalHouse], cylindricalStorageInput)


    List<Try<Void, ? extends ValidationException>> exceptions = ThermalValidationUtils.check(thermalGrid).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.class == expectedException.class
    ex.message == expectedException.message


    where:
    thermalHouse                                                                                                                                                                                                                                                                       || expectedSize || expectedException
    new ThermalHouseInput(thermalUnitUuid, id, operator, operationTime, SystemParticipantTestData.thermalBus, thermalConductance, ethCapa, Quantities.getQuantity(0, StandardUnits.TEMPERATURE), UPPER_TEMPERATURE_LIMIT, LOWER_TEMPERATURE_LIMIT, HOUSING_TYPE, NUMBER_INHABITANTS)   || 1            || new InvalidEntityException("Target temperature must be higher than lower temperature limit and lower than upper temperature limit", thermalHouse)
  }
}
