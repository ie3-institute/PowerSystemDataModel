/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.exceptions.SystemParticipantsException
import edu.ie3.datamodel.io.source.*
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData as sptd
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

class CsvSystemParticipantSourceTest extends Specification implements CsvTestDataMeta {

  def "A SystemParticipantSource with csv input should provide an instance of SystemParticipants based on valid input data correctly"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    def thermalSource = new ThermalSource(typeSource, new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def rawGridSource = new RawGridSource(typeSource, new CsvDataSource(csvSep, gridDefaultFolderPath, fileNamingStrategy))
    def energyManagementSource = new EnergyManagementSource(typeSource, new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def csvSystemParticipantSource = new SystemParticipantSource(
        typeSource,
        thermalSource,
        rawGridSource,
        energyManagementSource,
        new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    when:
    def systemParticipants = csvSystemParticipantSource.systemParticipants

    then:
    systemParticipants.allEntitiesAsList().size() == 10
    systemParticipants.pvPlants.first().uuid == sptd.pvInput.uuid
    systemParticipants.bmPlants.first().uuid == sptd.bmInput.uuid
    systemParticipants.chpPlants.first().uuid == sptd.chpInput.uuid
    systemParticipants.evs.first().uuid == sptd.evInput.uuid
    systemParticipants.fixedFeedIns.first().uuid == sptd.fixedFeedInInput.uuid
    systemParticipants.heatPumps.first().uuid == sptd.hpInput.uuid
    systemParticipants.loads.first().uuid == sptd.loadInput.uuid
    systemParticipants.wecPlants.first().uuid == sptd.wecInput.uuid
    systemParticipants.storages.first().uuid == sptd.storageInput.uuid
    systemParticipants.evcs.first().uuid == sptd.evcsInput.uuid
  }

  def "A SystemParticipantSource with csv input should process invalid input data as expected when requested to provide an instance of SystemParticipants"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    def thermalSource = new ThermalSource(typeSource, new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def rawGridSource = Spy(RawGridSource, constructorArgs: [
      typeSource,
      new CsvDataSource(csvSep, gridDefaultFolderPath, fileNamingStrategy)
    ]) {
      // partly fake the return method of the csv raw grid source to always return empty node sets
      // -> elements to build NodeGraphicInputs are missing
      getNodes() >> [:]
      getNodes(_) >> [:]
    } as RawGridSource
    def energyManagementSource = new EnergyManagementSource(typeSource, new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def csvSystemParticipantSource = new SystemParticipantSource(
    typeSource,
    thermalSource,
    rawGridSource,
    energyManagementSource,
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    when:
    def systemParticipants = Try.of(() -> csvSystemParticipantSource.systemParticipants, SystemParticipantsException)

    then:
    systemParticipants.failure
    systemParticipants.data == Optional.empty()

    Exception ex = systemParticipants.exception.get()
    ex.class == SystemParticipantsException
    ex.message.startsWith("10 error(s) occurred while initializing system participants.  " +
    "edu.ie3.datamodel.exceptions.FailureException: 1 exception(s) occurred within " +
    "\"FixedFeedInInput\" data, one is: edu.ie3.datamodel.exceptions.FactoryException: " +
    "edu.ie3.datamodel.exceptions.SourceException: Entity with uuid 4ca90220-74c2-4369-9afa-a18bf068840d was not provided.")
  }

  def "A SystemParticipantSource with csv input should return data from valid input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def operatorMap = map([sptd.operator])
    def nodeMap = map([sptd.participantNode])
    def emUnitsMap = map([sptd.emInput])
    def thermalBusMap = map([sptd.thermalBus])
    def thermalStorageMap = map([sptd.thermalStorage])

    expect:
    def heatPumps = Try.of(() -> csvSystemParticipantSource.getHeatPumps(operatorMap, nodeMap, emUnitsMap, map([sptd.hpTypeInput]), thermalBusMap), SourceException)
    heatPumps.success
    heatPumps.data.get() == [sptd.hpInput] as Set

    def chpUnits = Try.of(() -> csvSystemParticipantSource.getChpPlants(operatorMap, nodeMap, emUnitsMap, map([sptd.chpTypeInput]), thermalBusMap, thermalStorageMap), SourceException)
    chpUnits.success
    chpUnits.data.get() == [sptd.chpInput] as Set

    def evs = Try.of(() -> csvSystemParticipantSource.getEvs(operatorMap, nodeMap, emUnitsMap, map([sptd.evTypeInput])), SourceException)
    evs.success
    evs.data.get() == [sptd.evInput] as Set

    def wecs = Try.of(() -> csvSystemParticipantSource.getWecPlants(operatorMap, nodeMap, emUnitsMap, map([sptd.wecType])), SourceException)
    wecs.success
    wecs.data.get() == [sptd.wecInput] as Set

    def storages = Try.of(() -> csvSystemParticipantSource.getStorages(operatorMap, nodeMap, emUnitsMap, map([sptd.storageTypeInput])), SourceException)
    storages.success
    storages.data.get() == [sptd.storageInput] as Set

    def bms = Try.of(() -> csvSystemParticipantSource.getBmPlants(operatorMap, nodeMap, emUnitsMap, map([sptd.bmTypeInput])), SourceException)
    bms.success
    bms.data.get() == [sptd.bmInput] as Set

    def evcs = Try.of(() -> csvSystemParticipantSource.getEvcs(operatorMap, nodeMap, emUnitsMap), SourceException)
    evcs.success
    evcs.data.get() == [sptd.evcsInput] as Set

    def loads = Try.of(() -> csvSystemParticipantSource.getLoads(operatorMap, nodeMap, emUnitsMap), SourceException)
    loads.success
    loads.data.get() == [sptd.loadInput] as Set

    def pvs = Try.of(() -> csvSystemParticipantSource.getPvPlants(operatorMap, nodeMap, emUnitsMap), SourceException)
    pvs.success
    pvs.data.get() == [sptd.pvInput] as Set

    def fixedFeedIns = Try.of(() -> csvSystemParticipantSource.getFixedFeedIns(operatorMap, nodeMap, emUnitsMap), SourceException)
    fixedFeedIns.success
    fixedFeedIns.data.get() == [sptd.fixedFeedInInput] as Set
  }

  def "A SystemParticipantSource with csv input should throw an exception from an invalid heat pump input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def nodeMap = map([sptd.participantNode])
    def emUnitsMap = map([sptd.emInput])

    expect:
    def heatPumps = Try.of(() -> csvSystemParticipantSource.getHeatPumps(map(operators), nodeMap, emUnitsMap, map(types), map(thermalBuses)), SourceException)

    heatPumps.failure
    heatPumps.exception.get().class == SourceException

    where:
    operators               | types               | thermalBuses
    []                      | []                  | []
    [sptd.hpInput.operator] | []                  | []
    [sptd.hpInput.operator] | [sptd.hpInput.type] | []
  }

  def "A SystemParticipantSource with csv input should throw an exception from a invalid chp input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def nodeMap = map([sptd.participantNode])
    def emUnitsMap = map([sptd.emInput])

    expect:
    def chpUnits = Try.of(() -> csvSystemParticipantSource.getChpPlants(map(operators), nodeMap, emUnitsMap, map(types), map(thermalBuses), map(thermalStorages)), SourceException)

    chpUnits.failure
    chpUnits.exception.get().class == SourceException

    where:
    operators                | types                | thermalBuses               | thermalStorages
    []                       | []                   | []                         | [] as List
    []                       | []                   | []                         | [] as List
    [sptd.chpInput.operator] | []                   | []                         | [] as List
    [sptd.chpInput.operator] | [sptd.chpInput.type] | []                         | [] as List
  }

  def "A SystemParticipantSource with csv input should throw an exception from invalid ev input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def nodeMap = map([sptd.participantNode])
    def emUnitsMap = map([sptd.emInput])

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getEvs(map(operators), nodeMap, emUnitsMap, map(types)), SourceException)

    sysParts.failure
    sysParts.exception.get().class == SourceException

    where:
    operators               | types
    [sptd.evInput.operator] | []
    []                      | []
  }

  def "A SystemParticipantSource with csv input should throw an exception from invalid wec input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def nodeMap = map([sptd.participantNode])
    def emUnitsMap = map([sptd.emInput])

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getWecPlants(map(operators), nodeMap, emUnitsMap, map(types)), SourceException)

    sysParts.failure
    sysParts.exception.get().class == SourceException

    where:
    operators                | types
    [sptd.wecInput.operator] | []
    []                       | []
  }

  def "A SystemParticipantSource with csv input should throw an exception from invalid storage input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def nodeMap = map([sptd.participantNode])
    def emUnitsMap = map([sptd.emInput])

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getStorages(map(operators), nodeMap, emUnitsMap, map(types)), SourceException)

    sysParts.failure
    sysParts.exception.get().class == SourceException

    where:
    operators                    | types
    [sptd.storageInput.operator] | []
    []                           | []
  }

  def "A SystemParticipantSource with csv input should throw an exception from invalid bm input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def nodeMap = map([sptd.participantNode])
    def emUnitsMap = map([sptd.emInput])

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getBmPlants(map(operators), nodeMap, emUnitsMap, map(types)), SourceException)

    sysParts.failure
    sysParts.exception.get().class == SourceException

    where:
    operators               | types
    [sptd.bmInput.operator] | []
    []                      | []
  }

  def "A SystemParticipantSource with csv input should throw an exception from invalid ev charging station input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def emUnitsMap = map([sptd.emInput])

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getEvcs(map(operators), map(nodes), emUnitsMap), SourceException)

    sysParts.failure
    sysParts.exception.get().class == SourceException

    where:
    nodes                 | operators
    []                    | [sptd.evcsInput.operator]
    []                    | []
  }

  def "A SystemParticipantSource with csv input should throw an exception from invalid load input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def emUnitsMap = map([sptd.emInput])

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getLoads(map(operators), map(nodes), emUnitsMap), SourceException)

    sysParts.failure
    sysParts.exception.get().class == SourceException

    where:
    nodes                 | operators
    []                    | [sptd.loadInput.operator]
    []                    | []
  }

  def "A SystemParticipantSource with csv input should throw an exception from invalid pv input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def emUnitsMap = map([sptd.emInput])

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getPvPlants(map(operators), map(nodes), emUnitsMap), SourceException)

    sysParts.failure
    sysParts.exception.get().class == SourceException

    where:
    nodes               | operators
    []                  | [sptd.pvInput.operator]
    []                  | []
  }

  def "A SystemParticipantSource with csv input should throw an exception from invalid fixedFeedIn input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    Mock(EnergyManagementSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def emUnitsMap = map([sptd.emInput])

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getFixedFeedIns(map(operators), map(nodes), emUnitsMap), SourceException)

    sysParts.failure
    sysParts.exception.get().class == SourceException

    where:
    nodes                        | operators
    []                           | [sptd.fixedFeedInInput.operator]
    []                           | []
  }
}
