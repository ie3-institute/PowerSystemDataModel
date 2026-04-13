/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import static edu.ie3.util.quantities.PowerSystemUnits.DEGREE_GEOM
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE
import static tech.units.indriya.unit.Units.PERCENT

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities


class PvInputTest extends Specification {

  def "A PvInput copy method should work as expected"() {
    given:
    def pvInput = SystemParticipantTestData.pvInput

    when:
    def alteredUnit = pvInput.copy().albedo(10).azimuth(Quantities.getQuantity(10, DEGREE_GEOM)).elevationAngle(Quantities.getQuantity(50, DEGREE_GEOM))
        .etaConv(Quantities.getQuantity(50d, PERCENT)).kG(10).kT(5).sRated(Quantities.getQuantity(0d, KILOVOLTAMPERE))
        .cosPhiRated(0.7d).build()

    then:
    alteredUnit.with {
      uuid == pvInput.uuid
      operationTime == pvInput.operationTime
      operator == pvInput.operator
      id == pvInput.id
      qCharacteristics == pvInput.qCharacteristics
      sRated == Quantities.getQuantity(0d, KILOVOLTAMPERE)
      cosPhiRated == 0.7d
      albedo == 10
      azimuth == Quantities.getQuantity(10, DEGREE_GEOM)
      etaConv == Quantities.getQuantity(50, PERCENT)
      elevationAngle == Quantities.getQuantity(50, DEGREE_GEOM)
      kG == 10
      kT == 5
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a PvInput via builder should work as expected"() {
    given:
    def pvInput = SystemParticipantTestData.pvInput

    when:
    def alteredUnit = pvInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == pvInput.uuid
      operationTime == pvInput.operationTime
      operator == pvInput.operator
      id == pvInput.id
      qCharacteristics == pvInput.qCharacteristics
      sRated == pvInput.sRated * 2d
      cosPhiRated == pvInput.cosPhiRated
      albedo == pvInput.albedo
      azimuth == pvInput.azimuth
      etaConv == pvInput.etaConv
      elevationAngle == pvInput.elevationAngle
      kG == pvInput.kG
      kT == pvInput.kT
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
