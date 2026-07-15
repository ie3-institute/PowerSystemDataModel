/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input

import edu.ie3.datamodel.io.processor.input.InputEntityProcessor
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.test.common.ThermalUnitInputTestData
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class AdditionalInformationCopyBuilderTest extends Specification {

  @Shared
  List<UniqueInputEntity> sources = [
    GridTestData.profBroccoli,
    GridTestData.energyManagementInput,
    GridTestData.measurementUnitInput,
    GridTestData.nodeA,
    GridTestData.lineCtoD,
    GridTestData.switchAtoB,
    GridTestData.transformerBtoD,
    GridTestData.transformerAtoBtoC,
    GridTestData.lineTypeInputCtoD,
    GridTestData.transformerTypeBtoD,
    GridTestData.transformerTypeAtoBtoC,
    SystemParticipantTestData.fixedFeedInInput,
    SystemParticipantTestData.pvInput,
    SystemParticipantTestData.wecInput,
    SystemParticipantTestData.chpInput,
    SystemParticipantTestData.bmInput,
    SystemParticipantTestData.evInput,
    SystemParticipantTestData.loadInput,
    SystemParticipantTestData.storageInput,
    SystemParticipantTestData.hpInput,
    SystemParticipantTestData.acInput,
    SystemParticipantTestData.evcsInput,
    SystemParticipantTestData.wecType,
    SystemParticipantTestData.chpTypeInput,
    SystemParticipantTestData.bmTypeInput,
    SystemParticipantTestData.evTypeInput,
    SystemParticipantTestData.storageTypeInput,
    SystemParticipantTestData.hpTypeInput,
    SystemParticipantTestData.acTypeInput,
    SystemParticipantTestData.thermalBus,
    ThermalUnitInputTestData.thermalHouseInput,
    ThermalUnitInputTestData.cylindricalStorageInput,
    ThermalUnitInputTestData.domesticHotWaterStorageInput
  ]

  @Unroll
  def "The #source.class.simpleName copy builder should preserve and replace additional information"() {
    given:
    def original = source.copy().additionalInformation([source: "legacy"]).build()
    def replacement = [source: "updated"]

    when:
    def copied = original.copy().build()
    def modifiedBuilder = original.copy().additionalInformation(replacement)
    replacement.source = "changed after configuring builder"
    def modified = modifiedBuilder.build()

    then:
    copied.additionalInformation == [source: "legacy"]
    modified.additionalInformation == [source: "updated"]
    original.additionalInformation == [source: "legacy"]

    when:
    modified.additionalInformation.source = "changed after build"

    then:
    thrown(UnsupportedOperationException)

    where:
    source << sources
  }

  def "The fixture matrix should cover every eligible unique input entity type"() {
    given:
    def coveredTypes = sources.collect { it.class } as Set
    def eligibleTypes = InputEntityProcessor.eligibleEntityClasses.findAll {
      UniqueInputEntity.isAssignableFrom(it)
    } as Set

    expect:
    coveredTypes == eligibleTypes
  }
}
