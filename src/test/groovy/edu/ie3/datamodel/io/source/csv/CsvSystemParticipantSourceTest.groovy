/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.exceptions.SystemParticipantsException
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.ChpInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.HpInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.SystemParticipantTypedEntityData
import edu.ie3.datamodel.io.source.RawGridSource
import edu.ie3.datamodel.io.source.SystemParticipantSource
import edu.ie3.datamodel.io.source.ThermalSource
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.BmInput
import edu.ie3.datamodel.models.input.system.ChpInput
import edu.ie3.datamodel.models.input.system.EvInput
import edu.ie3.datamodel.models.input.system.EvcsInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.HpInput
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.StorageInput
import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData as sptd
import spock.lang.Specification

class CsvSystemParticipantSourceTest extends Specification implements CsvTestDataMeta {

  def "A CsvSystemParticipantSource should provide an instance of SystemParticipants based on valid input data correctly"() {
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
    systemParticipants.allEntitiesAsList().size() == 11
    systemParticipants.pvPlants.first().uuid == sptd.pvInput.uuid
    systemParticipants.bmPlants.first().uuid == sptd.bmInput.uuid
    systemParticipants.chpPlants.first().uuid == sptd.chpInput.uuid
    systemParticipants.evs.first().uuid == sptd.evInput.uuid
    systemParticipants.fixedFeedIns.first().uuid == sptd.fixedFeedInInput.uuid
    systemParticipants.heatPumps.first().uuid == sptd.hpInput.uuid
    systemParticipants.loads.first().uuid == sptd.loadInput.uuid
    systemParticipants.wecPlants.first().uuid == sptd.wecInput.uuid
    systemParticipants.storages.first().uuid == sptd.storageInput.uuid
    systemParticipants.evCS.first().uuid == sptd.evcsInput.uuid
    systemParticipants.emSystems.first().uuid == sptd.emInput.uuid
  }

  def "A CsvSystemParticipantSource should process invalid input data as expected when requested to provide an instance of SystemParticipants"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    def thermalSource = new ThermalSource(typeSource, new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))
    def rawGridSource = Spy(RawGridSource, constructorArgs: [
      typeSource,
      new CsvDataSource(csvSep, gridDefaultFolderPath, fileNamingStrategy)
    ]) {
      // partly fake the return method of the csv raw grid source to always return empty node sets
      // -> elements to build NodeGraphicInputs are missing
      getNodes() >> new HashSet<NodeInput>()
      getNodes(_) >> new HashSet<NodeInput>()
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

    Exception ex = systemParticipants.exception()
    ex.class == SystemParticipantsException
    ex.message.startsWith("11 error(s) occurred while initializing system participants.  " +
    "edu.ie3.datamodel.exceptions.FailureException: 1 exception(s) occurred within \"FixedFeedInInput\" data, one is: " +
    "edu.ie3.datamodel.exceptions.FactoryException: edu.ie3.datamodel.exceptions.SourceException: " +
    "Failure due to: Skipping FixedFeedInInput with uuid ")
  }

  def "A CsvSystemParticipantSource should build typed entity from valid and invalid input data as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    def nodeAssetInputEntityData = new NodeAssetInputEntityData(fieldsToAttributes, clazz, operator, node)

    when:
    def typedEntityDataOpt = csvSystemParticipantSource.buildTypedEntityData(nodeAssetInputEntityData, types)

    then:
    typedEntityDataOpt.success == resultIsPresent
    typedEntityDataOpt.data.ifPresent({ typedEntityData ->
      assert (typedEntityData == resultData)
    })

    where:
    types               | node               | operator               | fieldsToAttributes                               | clazz    || resultIsPresent || resultData
    []| sptd.chpInput.node | sptd.chpInput.operator | ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8"] | ChpInput || false           || null
    [sptd.chpTypeInput] | sptd.chpInput.node | sptd.chpInput.operator | ["bla": "foo"]                                   | ChpInput || false           || null
    [sptd.chpTypeInput] | sptd.chpInput.node | sptd.chpInput.operator | [:]                                              | ChpInput || false           || null
    [sptd.chpTypeInput] | sptd.chpInput.node | sptd.chpInput.operator | ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb9"] | ChpInput || false           || null
    [sptd.chpTypeInput] | sptd.chpInput.node | sptd.chpInput.operator | ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8"] | ChpInput || true            || new SystemParticipantTypedEntityData<>([:], clazz, operator, node, sptd.chpTypeInput)
  }

  def "A CsvSystemParticipantSource should build hp input entity from valid and invalid input data as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    def sysPartTypedEntityData = new SystemParticipantTypedEntityData<>(fieldsToAttributes, HpInput, sptd.hpInput.operator, sptd.hpInput.node, sptd.hpTypeInput)

    when:
    def hpInputEntityDataOpt = csvSystemParticipantSource.buildHpEntityData(sysPartTypedEntityData, thermalBuses)

    then:
    hpInputEntityDataOpt.success == resultIsPresent
    hpInputEntityDataOpt.data.ifPresent({ hpInputEntityData ->
      assert (hpInputEntityData == resultData)
    })

    where:
    thermalBuses              | fieldsToAttributes                                     || resultIsPresent || resultData
    [] | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e"] || false           || null
    [sptd.hpInput.thermalBus] | ["bla": "foo"]                                         || false           || null
    [sptd.hpInput.thermalBus] | [:]                                                    || false           || null
    [sptd.hpInput.thermalBus] | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384f"] || false           || null
    [sptd.hpInput.thermalBus] | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e"] || true            || new HpInputEntityData([:], sptd.hpInput.operator, sptd.hpInput.node, sptd.hpTypeInput, sptd.hpInput.thermalBus)
  }

  def "A CsvSystemParticipantSource should build chp input entity from valid and invalid input data as expected"(List<ThermalStorageInput> thermalStorages, List<ThermalBusInput> thermalBuses, Map<String, String> fieldsToAttributes, boolean resultIsPresent, ChpInputEntityData resultData) {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    def sysPartTypedEntityData = new SystemParticipantTypedEntityData<>(fieldsToAttributes, ChpInput, sptd.chpInput.operator, sptd.chpInput.node, sptd.chpTypeInput)

    when:
    def hpInputEntityDataOpt = csvSystemParticipantSource.buildChpEntityData(sysPartTypedEntityData, thermalStorages, thermalBuses)

    then:
    hpInputEntityDataOpt.success == resultIsPresent
    hpInputEntityDataOpt.data.ifPresent({ hpInputEntityData ->
      assert (hpInputEntityData == resultData)
    })

    where:
    thermalStorages                               | thermalBuses                       | fieldsToAttributes                                                                                               || resultIsPresent | resultData
    [] as List                                    | [] as List                         | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e", "thermalStorage": "8851813b-3a7d-4fee-874b-4df9d724e4b3"] || false           | null
    [
      sptd.chpInput.thermalStorage
    ] as List | [sptd.chpInput.thermalBus] as List | ["bla": "foo"]                                                                                                   || false           | null
    [
      sptd.chpInput.thermalStorage
    ] as List | [sptd.chpInput.thermalBus] as List | [:]                                                                                                              || false           | null
    [
      sptd.chpInput.thermalStorage
    ] as List | [sptd.chpInput.thermalBus] as List | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e", "thermalStorage": "8851813b-3a7d-4fee-874b-4df9d724e4b3"] || true            | new ChpInputEntityData([:], sptd.chpInput.operator, sptd.chpInput.node, sptd.chpTypeInput, sptd.chpInput.thermalBus, sptd.chpInput.thermalStorage)
  }

  def "A CsvSystemParticipantSource should return data from a valid heat pump input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def heatPumps = Try.of(() -> csvSystemParticipantSource.getHeatPumps(nodes as Set, operators as Set, types as Set, thermalBuses as Set), SourceException)

    if (heatPumps.success) {
      heatPumps.data().size() == resultingSize
      heatPumps.data() == resultingSet as Set
    } else {
      heatPumps.exception().class == SourceException
    }

    where:
    nodes               | operators               | types               | thermalBuses              || resultingSize || resultingSet
    [sptd.hpInput.node] | [sptd.hpInput.operator] | [sptd.hpInput.type] | [sptd.hpInput.thermalBus] || 1             || [sptd.hpInput]
    [sptd.hpInput.node] | []                      | [sptd.hpInput.type] | [sptd.hpInput.thermalBus] || 1             || [
      new HpInput(sptd.hpInput.uuid, sptd.hpInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.hpInput.operationTime, sptd.hpInput.node, sptd.hpInput.thermalBus, sptd.hpInput.qCharacteristics, sptd.hpInput.type)
    ]
    [] | [] | [] | []                                                        || 0             || []
    [sptd.hpInput.node] | []                      | []                  | [] || 0             || []
    [sptd.hpInput.node] | [sptd.hpInput.operator] | []                  | [] || 0             || []
    [sptd.hpInput.node] | [sptd.hpInput.operator] | [sptd.hpInput.type] | [] || 0             || []
  }

  def "A CsvSystemParticipantSource should return data from a valid chp input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def chpUnits = Try.of(() -> csvSystemParticipantSource.getChpPlants(nodes as Set, operators as Set, types as Set, thermalBuses as Set, thermalStorages as Set), SourceException)

    if (chpUnits.success) {
      chpUnits.data().size() == resultingSize
      chpUnits.data() == resultingSet as Set
    } else {
      chpUnits.exception().class == SourceException
    }

    where:
    nodes                | operators                | types                | thermalBuses               | thermalStorages || resultingSize || resultingSet
    [sptd.chpInput.node]| [sptd.chpInput.operator]| [sptd.chpInput.type]| [sptd.chpInput.thermalBus]| [
      sptd.chpInput.thermalStorage
    ] as List                                                                         || 1             || [sptd.chpInput]
    [sptd.chpInput.node]| []| [sptd.chpInput.type]| [sptd.chpInput.thermalBus]| [
      sptd.chpInput.thermalStorage
    ] as List                                                                         || 1             || [
      new ChpInput(sptd.chpInput.uuid, sptd.chpInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.chpInput.operationTime, sptd.chpInput.node, sptd.chpInput.thermalBus, sptd.chpInput.qCharacteristics, sptd.chpInput.type, sptd.chpInput.thermalStorage, sptd.chpInput.marketReaction)
    ]
    [] | [] | [] | [] | [] as List      || 0             || []
    [sptd.chpInput.node] | [] | [] | [] | [] as List      || 0             || []
    [sptd.chpInput.node] | [sptd.chpInput.operator] | [] | [] | [] as List      || 0             || []
    [sptd.chpInput.node] | [sptd.chpInput.operator] | [sptd.chpInput.type] | [] | [] as List      || 0             || []
  }

  def "A CsvSystemParticipantSource should return data from valid ev input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getEvs(nodes as Set, operators as Set, types as Set), SourceException)

    if (sysParts.success) {
      sysParts.data().size() == resultingSize
      sysParts.data() == resultingSet as Set
    } else {
      sysParts.exception().class == SourceException
    }

    where:
    nodes               | operators               | types               || resultingSize || resultingSet
    [sptd.evInput.node] | [sptd.evInput.operator] | [sptd.evInput.type] || 1             || [sptd.evInput]
    [sptd.evInput.node] | [] | [sptd.evInput.type] || 1             || [
      new EvInput(sptd.evInput.uuid, sptd.evInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.evInput.operationTime, sptd.evInput.node, sptd.evInput.qCharacteristics, sptd.evInput.type)
    ]
    [sptd.evInput.node] | [sptd.evInput.operator] | [] || 0             || []
    [sptd.evInput.node] | [] | [] || 0             || []
    [] | [] | [] || 0             || []
  }

  def "A CsvSystemParticipantSource should return data from valid wec input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getWecPlants(nodes as Set, operators as Set, types as Set), SourceException)

    if (sysParts.success) {
      sysParts.data().size() == resultingSize
      sysParts.data() == resultingSet as Set
    } else {
      sysParts.exception().class == SourceException
    }

    where:
    nodes                | operators                | types                || resultingSize || resultingSet
    [sptd.wecInput.node] | [sptd.wecInput.operator] | [sptd.wecInput.type] || 1             || [sptd.wecInput]
    [sptd.wecInput.node] | [] | [sptd.wecInput.type] || 1             || [
      new WecInput(sptd.wecInput.uuid, sptd.wecInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.wecInput.operationTime, sptd.wecInput.node, sptd.wecInput.qCharacteristics, sptd.wecInput.type, sptd.wecInput.marketReaction)
    ]
    [sptd.wecInput.node] | [sptd.wecInput.operator] | [] || 0             || []
    [sptd.wecInput.node] | [] | [] || 0             || []
    [] | [] | [] || 0             || []
  }

  def "A CsvSystemParticipantSource should return data from valid storage input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getStorages(nodes as Set, operators as Set, types as Set), SourceException)

    if (sysParts.success) {
      sysParts.data().size() == resultingSize
      sysParts.data() == resultingSet as Set
    } else {
      sysParts.exception().class == SourceException
    }

    where:
    nodes                    | operators                    | types                    || resultingSize || resultingSet
    [sptd.storageInput.node] | [sptd.storageInput.operator] | [sptd.storageInput.type] || 1             || [sptd.storageInput]
    [sptd.storageInput.node] | [] | [sptd.storageInput.type] || 1             || [
      new StorageInput(sptd.storageInput.uuid, sptd.storageInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.storageInput.operationTime, sptd.storageInput.node, sptd.storageInput.qCharacteristics, sptd.storageInput.type)
    ]
    [sptd.storageInput.node] | [sptd.storageInput.operator] | [] || 0             || []
    [sptd.storageInput.node] | []                           | [] || 0             || []
    []                       | []                           | [] || 0             || []
  }

  def "A CsvSystemParticipantSource should return data from valid bm input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getBmPlants(nodes as Set, operators as Set, types as Set), SourceException)

    if (sysParts.success) {
      sysParts.data().size() == resultingSize
      sysParts.data() == resultingSet as Set
    } else {
      sysParts.exception().class == SourceException
    }

    where:
    nodes               | operators               | types               || resultingSize || resultingSet
    [sptd.bmInput.node] | [sptd.bmInput.operator] | [sptd.bmInput.type] || 1             || [sptd.bmInput]
    [sptd.bmInput.node] | [] | [sptd.bmInput.type] || 1             || [
      new BmInput(sptd.bmInput.uuid, sptd.bmInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.bmInput.operationTime, sptd.bmInput.node, sptd.bmInput.qCharacteristics, sptd.bmInput.type, sptd.bmInput.marketReaction, sptd.bmInput.costControlled, sptd.bmInput.feedInTariff)
    ]
    [sptd.bmInput.node] | [sptd.bmInput.operator] | [] || 0             || []
    [sptd.bmInput.node] | []                      | [] || 0             || []
    []                  | []                      | [] || 0             || []
  }

  def "A CsvSystemParticipantSource should return data from valid ev charging station input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getEvCS(nodes as Set, operators as Set), SourceException)

    if (sysParts.success) {
      sysParts.data().size() == resultingSize
      sysParts.data() == resultingSet as Set
    } else {
      sysParts.exception().class == SourceException
    }

    where:
    nodes                 | operators                 || resultingSize || resultingSet
    [sptd.evcsInput.node] | [sptd.evcsInput.operator] || 1             || [sptd.evcsInput]
    [sptd.evcsInput.node] | []                        || 1             || [
      new EvcsInput(sptd.evcsInput.uuid, sptd.evcsInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.evcsInput.operationTime, sptd.evcsInput.node, sptd.evcsInput.qCharacteristics, sptd.evcsInput.type, sptd.evcsInput.chargingPoints, sptd.evcsInput.cosPhiRated, sptd.evcsInput.locationType, sptd.evcsInput.v2gSupport)
    ]
    []| [sptd.evcsInput.operator]|| 0             || []
    []| []|| 0             || []
  }

  def "A CsvSystemParticipantSource should return data from valid load input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getLoads(nodes as Set, operators as Set), SourceException)

    if (sysParts.success) {
      sysParts.data().size() == resultingSize
      sysParts.data() == resultingSet as Set
    } else {
      sysParts.exception().class == SourceException
    }

    where:
    nodes                 | operators                 || resultingSize || resultingSet
    [sptd.loadInput.node] | [sptd.loadInput.operator] || 1             || [sptd.loadInput]
    [sptd.loadInput.node] | [] || 1             || [
      new LoadInput(sptd.loadInput.uuid, sptd.loadInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.loadInput.operationTime, sptd.loadInput.node, sptd.loadInput.qCharacteristics, sptd.loadInput.loadProfile, sptd.loadInput.dsm, sptd.loadInput.eConsAnnual, sptd.loadInput.sRated, sptd.loadInput.cosPhiRated)
    ]
    [] | [sptd.loadInput.operator] || 0             || []
    [] | [] || 0             || []
  }

  def "A CsvSystemParticipantSource should return data from valid pv input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getPvPlants(nodes as Set, operators as Set), SourceException)

    if (sysParts.success) {
      sysParts.data().size() == resultingSize
      sysParts.data() == resultingSet as Set
    } else {
      sysParts.exception().class == SourceException
    }

    where:
    nodes               | operators               || resultingSize || resultingSet
    [sptd.pvInput.node] | [sptd.pvInput.operator] || 1             || [sptd.pvInput]
    [sptd.pvInput.node] | [] || 1                  || [
      new PvInput(sptd.pvInput.uuid, sptd.pvInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.pvInput.operationTime, sptd.pvInput.node, sptd.pvInput.qCharacteristics, sptd.pvInput.albedo, sptd.pvInput.azimuth, sptd.pvInput.etaConv, sptd.pvInput.elevationAngle, sptd.pvInput.kG, sptd.pvInput.kT, sptd.pvInput.marketReaction, sptd.pvInput.sRated, sptd.pvInput.cosPhiRated)
    ]
    [] | [sptd.pvInput.operator] || 0             || []
    [] | [] || 0             || []
  }

  def "A CsvSystemParticipantSource should return data from valid fixedFeedIn input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getFixedFeedIns(nodes as Set, operators as Set), SourceException)

    if (sysParts.success) {
      sysParts.data().size() == resultingSize
      sysParts.data() == resultingSet as Set
    } else {
      sysParts.exception().class == SourceException
    }

    where:
    nodes                        | operators        || resultingSize || resultingSet
    [sptd.fixedFeedInInput.node] | [
      sptd.fixedFeedInInput.operator
    ] as List || 1             || [sptd.fixedFeedInInput]
    [sptd.fixedFeedInInput.node] | [] as List       || 1             || [
      new FixedFeedInInput(sptd.fixedFeedInInput.uuid, sptd.fixedFeedInInput.id, OperatorInput.NO_OPERATOR_ASSIGNED, sptd.fixedFeedInInput.operationTime, sptd.fixedFeedInInput.node, sptd.fixedFeedInInput.qCharacteristics, sptd.fixedFeedInInput.sRated, sptd.fixedFeedInInput.cosPhiRated)
    ]
    [] | [
      sptd.fixedFeedInInput.operator
    ] as List || 0             || []
    [] | [] as List       || 0             || []
  }

  def "A CsvSystemParticipantSource should return data from valid em input file as expected"() {
    given:
    def csvSystemParticipantSource = new SystemParticipantSource(
    Mock(TypeSource),
    Mock(ThermalSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def sysParts = Try.of(() -> csvSystemParticipantSource.getEmSystems(nodes as Set, operators as Set), SourceException)

    if (sysParts.success) {
      sysParts.data().size() == resultingSize
      sysParts.data() == resultingSet as Set
    } else {
      sysParts.exception().class == SourceException
    }

    where:
    nodes               | operators               || resultingSize || resultingSet
    [sptd.emInput.node] | [sptd.emInput.operator] || 1             || [sptd.emInput]
    []                  | [sptd.pvInput.operator] || 0             || []
    []					| []					  || 0             || []
  }
}
