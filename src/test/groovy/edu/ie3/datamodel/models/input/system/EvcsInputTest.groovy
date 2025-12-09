/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils
import edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationType
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

class EvcsInputTest extends Specification {

  def "An EvcsInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, EvcsInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'EvcsInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'EvcsInput' are possible (NOT case-sensitive!):\n" +
        "0: [chargingPoints, controllingEm, cosPhiRated, id, locationType, node, qCharacteristics, type, uuid, v2gSupport] or [charging_points, controlling_em, cos_phi_rated, id, location_type, node, q_characteristics, type, uuid, v_2g_support]\n" +
        "1: [chargingPoints, controllingEm, cosPhiRated, id, locationType, node, operator, qCharacteristics, type, uuid, v2gSupport] or [charging_points, controlling_em, cos_phi_rated, id, location_type, node, operator, q_characteristics, type, uuid, v_2g_support]\n" +
        "2: [chargingPoints, controllingEm, cosPhiRated, id, locationType, node, operatesFrom, qCharacteristics, type, uuid, v2gSupport] or [charging_points, controlling_em, cos_phi_rated, id, location_type, node, operates_from, q_characteristics, type, uuid, v_2g_support]\n" +
        "3: [chargingPoints, controllingEm, cosPhiRated, id, locationType, node, operatesFrom, operator, qCharacteristics, type, uuid, v2gSupport] or [charging_points, controlling_em, cos_phi_rated, id, location_type, node, operates_from, operator, q_characteristics, type, uuid, v_2g_support]\n" +
        "4: [chargingPoints, controllingEm, cosPhiRated, id, locationType, node, operatesUntil, qCharacteristics, type, uuid, v2gSupport] or [charging_points, controlling_em, cos_phi_rated, id, location_type, node, operates_until, q_characteristics, type, uuid, v_2g_support]\n" +
        "5: [chargingPoints, controllingEm, cosPhiRated, id, locationType, node, operatesUntil, operator, qCharacteristics, type, uuid, v2gSupport] or [charging_points, controlling_em, cos_phi_rated, id, location_type, node, operates_until, operator, q_characteristics, type, uuid, v_2g_support]\n" +
        "6: [chargingPoints, controllingEm, cosPhiRated, id, locationType, node, operatesFrom, operatesUntil, qCharacteristics, type, uuid, v2gSupport] or [charging_points, controlling_em, cos_phi_rated, id, location_type, node, operates_from, operates_until, q_characteristics, type, uuid, v_2g_support]\n" +
        "7: [chargingPoints, controllingEm, cosPhiRated, id, locationType, node, operatesFrom, operatesUntil, operator, qCharacteristics, type, uuid, v2gSupport] or [charging_points, controlling_em, cos_phi_rated, id, location_type, node, operates_from, operates_until, operator, q_characteristics, type, uuid, v_2g_support]\n"
  }

  def "An EvcsInput copy method should work as expected"() {
    given:
    def evcsInput = SystemParticipantTestData.evcsInput

    when:
    def alteredEntity = evcsInput.copy()
        .type(ChargingPointTypeUtils.TeslaSuperChargerV3)
        .cosPhiRated(0.7d).chargingPoints(1)
        .locationType(EvcsLocationType.CHARGING_HUB_HIGHWAY)
        .v2gSupport(true)
        .build()

    then:
    alteredEntity.with {
      assert uuid == evcsInput.uuid
      assert operationTime == evcsInput.operationTime
      assert operator == evcsInput.operator
      assert id == evcsInput.id
      assert qCharacteristics == evcsInput.qCharacteristics
      assert type == ChargingPointTypeUtils.TeslaSuperChargerV3
      assert cosPhiRated == 0.7d
      assert chargingPoints == 1
      assert locationType == EvcsLocationType.CHARGING_HUB_HIGHWAY
      assert v2gSupport
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling an EvcsInput via builder should work as expected"() {
    given:
    def evcsInput = SystemParticipantTestData.evcsInput

    when:
    def alteredUnit = evcsInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == evcsInput.uuid
      assert operationTime == evcsInput.operationTime
      assert operator == evcsInput.operator
      assert id == evcsInput.id
      assert qCharacteristics == evcsInput.qCharacteristics
      assert type.sRated == evcsInput.type.sRated * 2d
      assert sRated() == evcsInput.type.sRated * 2d
      assert cosPhiRated == evcsInput.cosPhiRated
      assert chargingPoints == evcsInput.chargingPoints
      assert locationType == evcsInput.locationType
      assert v2gSupport == evcsInput.v2gSupport
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
