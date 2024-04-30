/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.extractor

import edu.ie3.datamodel.exceptions.ExtractorException
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.test.common.GridTestData as gtd
import edu.ie3.test.common.SystemParticipantTestData as sptd
import edu.ie3.test.common.ThermalUnitInputTestData as tutd
import spock.lang.Specification

class ExtractorTest extends Specification {

  private final class InvalidNestedExtensionClass implements NestedEntity {}

  def "An Extractor should be able to extract an entity with nested elements correctly"() {

    expect:
    def result = Extractor.extractElements(nestedEntity) as Set
    result == expectedExtractedEntities as Set

    where:
    nestedEntity               || expectedExtractedEntities

    gtd.lineCtoD               || [
      gtd.lineCtoD.nodeA,
      gtd.lineCtoD.nodeB,
      gtd.lineCtoD.type,
      gtd.lineCtoD.operator
    ]
    gtd.transformerAtoBtoC     || [
      gtd.transformerAtoBtoC.nodeA,
      gtd.transformerAtoBtoC.nodeB,
      gtd.transformerAtoBtoC.nodeC,
      gtd.transformerAtoBtoC.type,
      gtd.transformerAtoBtoC.operator,
      gtd.transformerAtoBtoC.nodeA.operator
    ]
    gtd.transformerCtoG        || [
      gtd.transformerCtoG.nodeA,
      gtd.transformerCtoG.nodeB,
      gtd.transformerCtoG.type,
      gtd.transformerCtoG.operator
    ]
    gtd.switchAtoB             || [
      gtd.switchAtoB.nodeA,
      gtd.switchAtoB.nodeB,
      gtd.switchAtoB.nodeA.operator,
      gtd.switchAtoB.operator
    ]

    sptd.fixedFeedInInput      || [
      sptd.fixedFeedInInput.node,
      sptd.fixedFeedInInput.operator,
      sptd.fixedFeedInInput.node.operator,
      sptd.fixedFeedInInput.controllingEm.get(),
      sptd.fixedFeedInInput.controllingEm.get().controllingEm.get()
    ]

    sptd.wecInput              || [
      sptd.wecInput.node,
      sptd.wecInput.type,
      sptd.wecInput.operator,
      sptd.wecInput.node.operator,
      sptd.wecInput.controllingEm.get(),
      sptd.wecInput.controllingEm.get().controllingEm.get()
    ]
    sptd.chpInput              || [
      sptd.chpInput.node,
      sptd.chpInput.node.operator,
      sptd.chpInput.type,
      sptd.chpInput.thermalBus,
      sptd.chpInput.thermalStorage,
      sptd.chpInput.thermalStorage.thermalBus,
      sptd.chpInput.thermalStorage.thermalBus.operator,
      sptd.chpInput.controllingEm.get(),
      sptd.chpInput.controllingEm.get().controllingEm.get()
    ]
    sptd.bmInput               || [
      sptd.bmInput.node,
      sptd.bmInput.type,
      sptd.bmInput.operator,
      sptd.bmInput.node.operator,
      sptd.bmInput.controllingEm.get(),
      sptd.bmInput.controllingEm.get().controllingEm.get()
    ]
    sptd.evInput               || [
      sptd.evInput.node,
      sptd.evInput.type,
      sptd.evInput.operator,
      sptd.evInput.node.operator,
      sptd.evInput.controllingEm.get(),
      sptd.evInput.controllingEm.get().controllingEm.get()
    ]
    sptd.storageInput          || [
      sptd.storageInput.node,
      sptd.storageInput.type,
      sptd.storageInput.operator,
      sptd.storageInput.node.operator,
      sptd.storageInput.controllingEm.get(),
      sptd.storageInput.controllingEm.get().controllingEm.get()
    ]
    sptd.hpInput               || [
      sptd.hpInput.node,
      sptd.hpInput.type,
      sptd.hpInput.operator,
      sptd.hpInput.thermalBus,
      sptd.hpInput.thermalBus.operator,
      sptd.hpInput.node.operator,
      sptd.hpInput.controllingEm.get(),
      sptd.hpInput.controllingEm.get().controllingEm.get()
    ]

    gtd.lineGraphicCtoD        || [
      gtd.lineGraphicCtoD.line,
      gtd.lineGraphicCtoD.line.nodeB,
      gtd.lineGraphicCtoD.line.nodeA,
      gtd.lineGraphicCtoD.line.type,
      gtd.lineGraphicCtoD.line.operator
    ]

    gtd.nodeGraphicC           || [gtd.nodeGraphicC.node]

    gtd.measurementUnitInput   || [
      gtd.measurementUnitInput.node,
      gtd.measurementUnitInput.operator
    ]

    tutd.thermalBus       || [
      tutd.thermalBus.operator
    ]

    tutd.cylindricStorageInput || [
      tutd.cylindricStorageInput.operator,
      tutd.cylindricStorageInput.thermalBus,
      tutd.cylindricStorageInput.thermalBus.operator
    ]

    tutd.thermalHouseInput     || [
      tutd.thermalHouseInput.operator,
      tutd.thermalHouseInput.thermalBus,
      tutd.thermalHouseInput.thermalBus.operator
    ]
  }

  def "An Extractor should throw an ExtractorException if the provided Nested entity is unknown and or an invalid extension of the 'Nested' interface took place"() {
    when:
    Extractor.extractElements(new InvalidNestedExtensionClass())

    then:
    ExtractorException ex = thrown()
    ex.message == "Unable to extract entity of class 'InvalidNestedExtensionClass'. " +
        "Does this class implements NestedEntity and one of its sub-interfaces correctly?"
  }

  def "An Extractor should not extract an operator that is marked as not assigned"() {
    given:
    def sampleFixedFeedInput = new FixedFeedInInput(UUID.fromString("717af017-cc69-406f-b452-e022d7fb516a"), "test_fixedFeedInInput",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        sptd.fixedFeedInInput.operationTime, sptd.fixedFeedInInput.node, sptd.fixedFeedInInput.qCharacteristics, sptd.fixedFeedInInput.controllingEm.orElse(null),
        sptd.fixedFeedInInput.sRated,sptd.fixedFeedInInput.cosPhiRated)
    expect:
    Extractor.extractElements(sampleFixedFeedInput) as Set == [
      sptd.fixedFeedInInput.node,
      sptd.fixedFeedInInput.node.operator,
      sptd.fixedFeedInInput.controllingEm.get(),
      sptd.fixedFeedInInput.controllingEm.get().controllingEm.get(),
      sptd.fixedFeedInInput.controllingEm.get().operator
    ] as Set
  }

  def "An Extractor should not extract an operator that is marked as not assigned and not throw an exception if the resulting list empty"() {
    given:
    def sampleNodeInput = gtd.nodeB

    expect:
    Extractor.extractElements(sampleNodeInput) == [] as Set
  }
}
