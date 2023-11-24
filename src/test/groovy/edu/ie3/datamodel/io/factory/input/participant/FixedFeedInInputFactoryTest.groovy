/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import java.time.ZonedDateTime

import static edu.ie3.util.quantities.PowerSystemUnits.PU

class FixedFeedInInputFactoryTest extends Specification implements FactoryTestHelper {
  def "A FixedFeedInInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new FixedFeedInInputFactory()
    def expectedClasses = [FixedFeedInInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A FixedFeedInInputFactory should parse a valid FixedFeedInInput correctly"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new FixedFeedInInputFactory()
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "srated"          : "3",
      "cosphirated"     : "4"
    ]
    def inputClass = FixedFeedInInput
    def nodeInput = Mock(NodeInput)
    def operatorInput = Mock(OperatorInput)

    when:
    Try<FixedFeedInInput, FactoryException> input = inputFactory.get(new NodeAssetInputEntityData(parameter, inputClass, operatorInput, nodeInput))

    then:
    input.success
    input.data.get().getClass() == inputClass
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosphirated"])
    }
  }

  def "A FixedFeedInInputFactory should throw an exception on invalid or incomplete data fields"() {
    given:
    def inputFactory = new FixedFeedInInputFactory()
    def foundFields = inputFactory.newSet("uuid", "id", "s_rated", "cosphi_rated")

    when:
    Try<Void, FactoryException> input = Try.ofVoid(() -> inputFactory.validate(foundFields, FixedFeedInInput), FactoryException)

    then:
    input.failure
    input.exception.get().message == "The provided fields [cosphi_rated, id, s_rated, uuid] are invalid for instance of 'FixedFeedInInput'. \n" +
    "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'FixedFeedInInput' are possible (NOT case-sensitive!):\n" +
    "0: [cosPhiRated, id, qCharacteristics, sRated, uuid] or [cos_phi_rated, id, q_characteristics, s_rated, uuid]\n" +
    "1: [cosPhiRated, id, operatesFrom, qCharacteristics, sRated, uuid] or [cos_phi_rated, id, operates_from, q_characteristics, s_rated, uuid]\n" +
    "2: [cosPhiRated, id, operatesUntil, qCharacteristics, sRated, uuid] or [cos_phi_rated, id, operates_until, q_characteristics, s_rated, uuid]\n" +
    "3: [cosPhiRated, id, operatesFrom, operatesUntil, qCharacteristics, sRated, uuid] or [cos_phi_rated, id, operates_from, operates_until, q_characteristics, s_rated, uuid]\n"
  }
}
