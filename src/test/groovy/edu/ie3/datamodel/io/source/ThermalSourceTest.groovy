/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.io.factory.input.ThermalUnitInputEntityData
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput
import edu.ie3.datamodel.utils.Try
import spock.lang.Specification

import java.util.stream.Stream

class ThermalSourceTest extends Specification {

  def "A ThermalSource should build thermal unit input entity from valid and invalid input data as expected"() {
    given:
    def operator = new OperatorInput(UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "testOperator")
    def validFieldsToAttributes = [
      "uuid"			: "717af017-cc69-406f-b452-e022d7fb516a",
      "id"			    : "test_thermal_unit",
      "operator"		: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"	: "2020-03-24 15:11:31",
      "operatesUntil"	: "2020-03-25 15:11:31",
      "thermalBus"      : "0d95d7f2-49fb-4d49-8636-383a5220384e"
    ]
    def assetInputEntityData = Stream.of(new Try.Success(new AssetInputEntityData(validFieldsToAttributes, ThermalUnitInput, operator)))

    when:
    def resultingDataOpt = ThermalSource.thermalUnitInputEntityDataStream(assetInputEntityData, thermalBuses as Set).toList()

    then:
    resultingDataOpt.size() == 1
    resultingDataOpt.first().data.present == resultIsPresent
    resultingDataOpt.first().data.ifPresent({ resultingData ->
      assert (resultingData == expectedThermalUnitInputEntityData)
    })

    where:
    thermalBuses || resultIsPresent | expectedThermalUnitInputEntityData
    []           || false           | null  // thermal buses are not present -> method should return an empty optional -> do not check for thermal unit entity data
    [
      new ThermalBusInput(UUID.fromString("0d95d7f2-49fb-4d49-8636-383a5220384e"), "test_thermal_bus")
    ]            || true            |
    new ThermalUnitInputEntityData([
      "uuid"         : "717af017-cc69-406f-b452-e022d7fb516a",
      "id"           : "test_thermal_unit",
      "operator"     : "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom" : "2020-03-24 15:11:31",
      "operatesUntil": "2020-03-25 15:11:31"],
    ThermalUnitInput,
    new OperatorInput(UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "testOperator"),
    new ThermalBusInput(UUID.fromString("0d95d7f2-49fb-4d49-8636-383a5220384e"), "test_thermal_bus"))
  }
}
