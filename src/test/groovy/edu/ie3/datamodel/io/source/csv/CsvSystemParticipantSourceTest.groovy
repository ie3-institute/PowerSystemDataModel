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
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.*
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData as sptd
import spock.lang.Specification

class CsvSystemParticipantSourceTest extends Specification implements CsvTestDataMeta {

  def "A SystemParticipantSource with csv input should provide an instance of SystemParticipants based on valid input data correctly"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    def thermalSource = new ThermalSource(typeSource, new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def rawGridSource = new RawGridSource(typeSource, new CsvDataSource(csvSep, gridDefaultFolderPath, fileNamingStrategy))
    def csvSystemParticipantSource = new SystemParticipantSource(
        typeSource,
        thermalSource,
        rawGridSource,
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
    def csvSystemParticipantSource = new SystemParticipantSource(
    typeSource,
    thermalSource,
    rawGridSource,
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    when:
    def systemParticipants = Try.of(() -> csvSystemParticipantSource.systemParticipants, SystemParticipantsException)

    then:
    systemParticipants.failure
    systemParticipants.data == Optional.empty()

    Exception ex = systemParticipants.exception.get()
    ex.class == SystemParticipantsException
    ex.message.startsWith("10 error(s) occurred while initializing system participants.  " +
    "edu.ie3.datamodel.exceptions.FailureException: 1 exception(s) occurred within \"FixedFeedInInput\" data, one is: " +
    "edu.ie3.datamodel.exceptions.FactoryException: edu.ie3.datamodel.exceptions.SourceException: " +
    "Linked node with UUID 4ca90220-74c2-4369-9afa-a18bf068840d was not found for entity AssetInputEntityData")
  }

  def "A SystemParticipantSource with csv input should return data from a valid heat pump input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def nodeMap = map([sptd.hpInput.node])

    expect:
    def heatPumps = Try.of(() -> csvSystemParticipantSource.getHeatPumps(map(operators), nodeMap, map(types), map(thermalBuses)), SourceException)

    if (heatPumps.success) {
      heatPumps.data.get().size() == resultingSize
      heatPumps.data.get() == resultingSet as Set
    } else {
      heatPumps.exception.get().class == SourceException
    }

    where:
    nodes               | operators               | types               | thermalBuses              || resultingSize | resultingSet
    [sptd.hpInput.node] | [sptd.hpInput.operator] | [sptd.hpInput.type] | [sptd.hpInput.thermalBus] || 1             | [sptd.hpInput]
    [sptd.hpInput.node] | []                      | [sptd.hpInput.type] | [sptd.hpInput.thermalBus] || 1             | [
      new HpInput(sptd.hpInput.uuid, sptd.hpInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.hpInput.operationTime, sptd.hpInput.node, sptd.hpInput.thermalBus, sptd.hpInput.qCharacteristics, sptd.hpInput.type)
    ]
    []                  | []                      | []                  | []                        || 0             | []
    [sptd.hpInput.node] | []                      | []                  | []                        || 0             | []
    [sptd.hpInput.node] | [sptd.hpInput.operator] | []                  | []                        || 0             | []
    [sptd.hpInput.node] | [sptd.hpInput.operator] | [sptd.hpInput.type] | []                        || 0             | []
  }

  def "A SystemParticipantSource with csv input should return data from a valid chp input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def nodeMap = map([sptd.hpInput.node])

    expect:
    def chpUnits = Try.of(() -> csvSystemParticipantSource.getChpPlants(map(operators), nodeMap, map(types), map(thermalBuses), map(thermalStorages)), SourceException)

    if (chpUnits.success) {
      chpUnits.data.get().size() == resultingSize
      chpUnits.data.get() == resultingSet as Set
    } else {
      chpUnits.exception.get().class == SourceException
    }

    where:
    operators                | types                | thermalBuses               | thermalStorages                || resultingSize || resultingSet
    [sptd.chpInput.operator] | [sptd.chpInput.type] | [sptd.chpInput.thermalBus] | [sptd.chpInput.thermalStorage] || 1             || [sptd.chpInput]
    []                       | [sptd.chpInput.type] | [sptd.chpInput.thermalBus] | [sptd.chpInput.thermalStorage] || 1             || [
      new ChpInput(sptd.chpInput.uuid, sptd.chpInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.chpInput.operationTime, sptd.chpInput.node, sptd.chpInput.thermalBus, sptd.chpInput.qCharacteristics, sptd.chpInput.type, sptd.chpInput.thermalStorage, sptd.chpInput.marketReaction)
    ]
    []                       | []                   | []                         | []                             || 0             || []
    []                       | []                   | []                         | []                             || 0             || []
    [sptd.chpInput.operator] | []                   | []                         | []                             || 0             || []
    [sptd.chpInput.operator] | [sptd.chpInput.type] | []                         | []                             || 0             || []
  }

  def "A SystemParticipantSource with csv input should return data from valid ev input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getEvs(map(operators), map(nodes), map(types)), SourceException)

    if (sysParts.success) {
      sysParts.data.get().size() == resultingSize
      sysParts.data.get() == resultingSet as Set
    } else {
      sysParts.exception.get().class == SourceException
    }

    where:
    nodes               | operators               | types               || resultingSize || resultingSet
    [sptd.evInput.node] | [sptd.evInput.operator] | [sptd.evInput.type] || 1             || [sptd.evInput]
    [sptd.evInput.node] | []                      | [sptd.evInput.type] || 1             || [
      new EvInput(sptd.evInput.uuid, sptd.evInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.evInput.operationTime, sptd.evInput.node, sptd.evInput.qCharacteristics, sptd.evInput.type)
    ]
    [sptd.evInput.node] | [sptd.evInput.operator] | []                  || 0             || []
    [sptd.evInput.node] | []                      | []                  || 0             || []
    []                  | []                      | []                  || 0             || []
  }

  def "A SystemParticipantSource with csv input should return data from valid wec input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getWecPlants(map(operators), map(nodes), map(types)), SourceException)

    if (sysParts.success) {
      sysParts.data.get().size() == resultingSize
      sysParts.data.get() == resultingSet as Set
    } else {
      sysParts.exception.get().class == SourceException
    }

    where:
    nodes                | operators                | types                || resultingSize || resultingSet
    [sptd.wecInput.node] | [sptd.wecInput.operator] | [sptd.wecInput.type] || 1             || [sptd.wecInput]
    [sptd.wecInput.node] | []                       | [sptd.wecInput.type] || 1             || [
      new WecInput(sptd.wecInput.uuid, sptd.wecInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.wecInput.operationTime, sptd.wecInput.node, sptd.wecInput.qCharacteristics, sptd.wecInput.type, sptd.wecInput.marketReaction)
    ]
    [sptd.wecInput.node] | [sptd.wecInput.operator] | []                   || 0             || []
    [sptd.wecInput.node] | []                       | []                   || 0             || []
    []                   | []                       | []                   || 0             || []
  }

  def "A SystemParticipantSource with csv input should return data from valid storage input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getStorages(map(operators), map(nodes), map(types)), SourceException)

    if (sysParts.success) {
      sysParts.data.get().size() == resultingSize
      sysParts.data.get() == resultingSet as Set
    } else {
      sysParts.exception.get().class == SourceException
    }

    where:
    nodes                    | operators                    | types                    || resultingSize || resultingSet
    [sptd.storageInput.node] | [sptd.storageInput.operator] | [sptd.storageInput.type] || 1             || [sptd.storageInput]
    [sptd.storageInput.node] | []                           | [sptd.storageInput.type] || 1             || [
      new StorageInput(sptd.storageInput.uuid, sptd.storageInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.storageInput.operationTime, sptd.storageInput.node, sptd.storageInput.qCharacteristics, sptd.storageInput.type)
    ]
    [sptd.storageInput.node] | [sptd.storageInput.operator] | []                       || 0             || []
    [sptd.storageInput.node] | []                           | []                       || 0             || []
    []                       | []                           | []                       || 0             || []
  }

  def "A SystemParticipantSource with csv input should return data from valid bm input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getBmPlants(map(operators), map(nodes), map(types)), SourceException)

    if (sysParts.success) {
      sysParts.data.get().size() == resultingSize
      sysParts.data.get() == resultingSet as Set
    } else {
      sysParts.exception.get().class == SourceException
    }

    where:
    nodes               | operators               | types               || resultingSize || resultingSet
    [sptd.bmInput.node] | [sptd.bmInput.operator] | [sptd.bmInput.type] || 1             || [sptd.bmInput]
    [sptd.bmInput.node] | []                      | [sptd.bmInput.type] || 1             || [
      new BmInput(sptd.bmInput.uuid, sptd.bmInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.bmInput.operationTime, sptd.bmInput.node, sptd.bmInput.qCharacteristics, sptd.bmInput.type, sptd.bmInput.marketReaction, sptd.bmInput.costControlled, sptd.bmInput.feedInTariff)
    ]
    [sptd.bmInput.node] | [sptd.bmInput.operator] | []                  || 0             || []
    [sptd.bmInput.node] | []                      | []                  || 0             || []
    []                  | []                      | []                  || 0             || []
  }

  def "A SystemParticipantSource with csv input should return data from valid ev charging station input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getEvcs(map(operators), map(nodes)), SourceException)

    if (sysParts.success) {
      sysParts.data.get().size() == resultingSize
      sysParts.data.get() == resultingSet as Set
    } else {
      sysParts.exception.get().class == SourceException
    }

    where:
    nodes                 | operators                 || resultingSize || resultingSet
    [sptd.evcsInput.node] | [sptd.evcsInput.operator] || 1             || [sptd.evcsInput]
    [sptd.evcsInput.node] | []                        || 1             || [
      new EvcsInput(sptd.evcsInput.uuid, sptd.evcsInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.evcsInput.operationTime, sptd.evcsInput.node, sptd.evcsInput.qCharacteristics, sptd.evcsInput.type, sptd.evcsInput.chargingPoints, sptd.evcsInput.cosPhiRated, sptd.evcsInput.locationType, sptd.evcsInput.v2gSupport)
    ]
    []                    | [sptd.evcsInput.operator] || 0             || []
    []                    | []                        || 0             || []
  }

  def "A SystemParticipantSource with csv input should return data from valid load input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getLoads(map(operators), map(nodes)), SourceException)

    if (sysParts.success) {
      sysParts.data.get().size() == resultingSize
      sysParts.data.get() == resultingSet as Set
    } else {
      sysParts.exception.get().class == SourceException
    }

    where:
    nodes                 | operators                 || resultingSize || resultingSet
    [sptd.loadInput.node] | [sptd.loadInput.operator] || 1             || [sptd.loadInput]
    [sptd.loadInput.node] | []                        || 1             || [
      new LoadInput(sptd.loadInput.uuid, sptd.loadInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.loadInput.operationTime, sptd.loadInput.node, sptd.loadInput.qCharacteristics, sptd.loadInput.loadProfile, sptd.loadInput.dsm, sptd.loadInput.eConsAnnual, sptd.loadInput.sRated, sptd.loadInput.cosPhiRated)
    ]
    []                    | [sptd.loadInput.operator] || 0             || []
    []                    | []                        || 0             || []
  }

  def "A SystemParticipantSource with csv input should return data from valid pv input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getPvPlants(map(operators), map(nodes)), SourceException)

    if (sysParts.success) {
      sysParts.data.get().size() == resultingSize
      sysParts.data.get() == resultingSet as Set
    } else {
      sysParts.exception.get().class == SourceException
    }

    where:
    nodes               | operators               || resultingSize || resultingSet
    [sptd.pvInput.node] | [sptd.pvInput.operator] || 1             || [sptd.pvInput]
    [sptd.pvInput.node] | []                      || 1             || [
      new PvInput(sptd.pvInput.uuid, sptd.pvInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.pvInput.operationTime, sptd.pvInput.node, sptd.pvInput.qCharacteristics, sptd.pvInput.albedo, sptd.pvInput.azimuth, sptd.pvInput.etaConv, sptd.pvInput.elevationAngle, sptd.pvInput.kG, sptd.pvInput.kT, sptd.pvInput.marketReaction, sptd.pvInput.sRated, sptd.pvInput.cosPhiRated)
    ]
    []                  | [sptd.pvInput.operator] || 0             || []
    []                  | []                      || 0             || []
  }

  def "A SystemParticipantSource with csv input should return data from valid fixedFeedIn input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getFixedFeedIns(map(operators), map(nodes)), SourceException)

    if (sysParts.success) {
      sysParts.data.get().size() == resultingSize
      sysParts.data.get() == resultingSet as Set
    } else {
      sysParts.exception.get().class == SourceException
    }

    where:
    nodes                        | operators                        || resultingSize || resultingSet
    [sptd.fixedFeedInInput.node] | [sptd.fixedFeedInInput.operator] || 1             || [sptd.fixedFeedInInput]
    [sptd.fixedFeedInInput.node] | []                               || 1             || [
      new FixedFeedInInput(sptd.fixedFeedInInput.uuid, sptd.fixedFeedInInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.fixedFeedInInput.operationTime, sptd.fixedFeedInInput.node, sptd.fixedFeedInInput.qCharacteristics, sptd.fixedFeedInInput.sRated, sptd.fixedFeedInInput.cosPhiRated)
    ]
    []                           | [sptd.fixedFeedInInput.operator] || 0             || []
    []                           | []                               || 0             || []
  }
}
