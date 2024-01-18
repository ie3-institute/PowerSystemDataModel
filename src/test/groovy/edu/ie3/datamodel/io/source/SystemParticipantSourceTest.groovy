/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.ChpInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.HpInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.SystemParticipantTypedEntityData
import edu.ie3.datamodel.models.input.system.ChpInput
import edu.ie3.datamodel.models.input.system.HpInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

import java.util.stream.Stream

class SystemParticipantSourceTest extends Specification {

  def "A SystemParticipantSource should build typed entity from valid and invalid input data as expected"() {
    given:
    def systemParticipantEntityData = Stream.of(new Try.Success<>(new NodeAssetInputEntityData(fieldsToAttributes, ChpInput, SystemParticipantTestData.chpInput.operator, SystemParticipantTestData.chpInput.node)))

    when:
    def typedEntityDataStream = SystemParticipantSource.typedSystemParticipantEntityStream(systemParticipantEntityData, map(types))

    then:
    def element = typedEntityDataStream.findFirst().get()
    element.success == resultIsPresent
    element.data.ifPresent({
      typedEntityData ->
      assert (typedEntityData == resultData)
    })

    where:
    types                                    | fieldsToAttributes                               || resultIsPresent || resultData
    []                                       | ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8"] || false           || null
    [SystemParticipantTestData.chpTypeInput] | ["bla": "foo"]                                   || false           || null
    [SystemParticipantTestData.chpTypeInput] | [:]                                              || false           || null
    [SystemParticipantTestData.chpTypeInput] | ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb9"] || false           || null
    [SystemParticipantTestData.chpTypeInput] | ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8"] || true            || new SystemParticipantTypedEntityData<>([:], ChpInput, SystemParticipantTestData.chpInput.operator, SystemParticipantTestData.chpInput.node, SystemParticipantTestData.chpTypeInput)
  }

  def "A SystemParticipantSource should build hp input entity from valid and invalid input data as expected"() {
    given:
    def sysPartTypedEntityData = Stream.of(new Try.Success<>(new SystemParticipantTypedEntityData<>(fieldsToAttributes, HpInput, SystemParticipantTestData.hpInput.operator, SystemParticipantTestData.hpInput.node, SystemParticipantTestData.hpTypeInput)))

    when:
    def hpInputEntityDataOpt = SystemParticipantSource.hpEntityStream(sysPartTypedEntityData, map(thermalBuses))

    then:
    def element = hpInputEntityDataOpt.findFirst().get()
    element.success == resultIsPresent
    element.data.ifPresent({
      hpInputEntityData ->
      assert (hpInputEntityData == resultData)
    })

    where:
    thermalBuses                                   | fieldsToAttributes                                     || resultIsPresent || resultData
    []                                             | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e"] || false           || null
    [SystemParticipantTestData.hpInput.thermalBus] | ["bla": "foo"]                                         || false           || null
    [SystemParticipantTestData.hpInput.thermalBus] | [:]                                                    || false           || null
    [SystemParticipantTestData.hpInput.thermalBus] | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384f"] || false           || null
    [SystemParticipantTestData.hpInput.thermalBus] | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e"] || true            || new HpInputEntityData([:], SystemParticipantTestData.hpInput.operator, SystemParticipantTestData.hpInput.node, SystemParticipantTestData.hpTypeInput, SystemParticipantTestData.hpInput.thermalBus)
  }

  def "A SystemParticipantSource should build chp input entity from valid and invalid input data as expected"(List<ThermalStorageInput> thermalStorages, List<ThermalBusInput> thermalBuses, Map<String, String> fieldsToAttributes, boolean resultIsPresent, ChpInputEntityData resultData) {
    given:
    def sysPartTypedEntityData = Stream.of(new Try.Success<>(new SystemParticipantTypedEntityData<>(fieldsToAttributes, ChpInput, SystemParticipantTestData.chpInput.operator, SystemParticipantTestData.chpInput.node, SystemParticipantTestData.chpTypeInput)))

    when:
    def hpInputEntityDataOpt = SystemParticipantSource.chpEntityStream(sysPartTypedEntityData, map(thermalStorages), map(thermalBuses))

    then:
    def element = hpInputEntityDataOpt.findFirst().get()
    element.success == resultIsPresent
    element.data.ifPresent({
      hpInputEntityData ->
      assert (hpInputEntityData == resultData)
    })

    where:
    thermalStorages                                     | thermalBuses                                    | fieldsToAttributes                                                                                               || resultIsPresent | resultData
    []                                                  | []                                              | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e", "thermalStorage": "8851813b-3a7d-4fee-874b-4df9d724e4b3"] || false           | null
    [SystemParticipantTestData.chpInput.thermalStorage] | [SystemParticipantTestData.chpInput.thermalBus] | ["bla": "foo"]                                                                                                   || false           | null
    [SystemParticipantTestData.chpInput.thermalStorage] | [SystemParticipantTestData.chpInput.thermalBus] | [:]                                                                                                              || false           | null
    [SystemParticipantTestData.chpInput.thermalStorage] | [SystemParticipantTestData.chpInput.thermalBus] | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e", "thermalStorage": "8851813b-3a7d-4fee-874b-4df9d724e4b3"] || true            | new ChpInputEntityData([:], SystemParticipantTestData.chpInput.operator, SystemParticipantTestData.chpInput.node, SystemParticipantTestData.chpTypeInput, SystemParticipantTestData.chpInput.thermalBus, SystemParticipantTestData.chpInput.thermalStorage)
  }
}
