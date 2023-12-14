/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import static edu.ie3.util.quantities.PowerSystemUnits.PU

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

import java.time.ZonedDateTime
import javax.measure.quantity.Dimensionless

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

  def "A FixedFeedInInputFactory should throw an exception on invalid or incomplete data (parameter missing)"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new FixedFeedInInputFactory()
    Map<String, String> parameter = [
      "uuid"       : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"         : "TestID",
      "srated"     : "3",
      "cosphirated": "4"
    ]
    def inputClass = FixedFeedInInput
    def nodeInput = Mock(NodeInput)

    when:
    Try<FixedFeedInInput, FactoryException> input =  inputFactory.get(new NodeAssetInputEntityData(parameter, inputClass, nodeInput))

    then:
    input.failure
    input.exception.get().cause.message == "The provided fields [cosphirated, id, srated, uuid] with data \n" +
        "{cosphirated -> 4,\n" +
        "id -> TestID,\n" +
        "srated -> 3,\n" +
        "uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7} are invalid for instance of FixedFeedInInput. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'FixedFeedInInput' are possible (NOT case-sensitive!):\n" +
        "0: [cosphirated, id, qcharacteristics, srated, uuid]\n" +
        "1: [cosphirated, id, operatesfrom, qcharacteristics, srated, uuid]\n" +
        "2: [cosphirated, id, operatesuntil, qcharacteristics, srated, uuid]\n" +
        "3: [cosphirated, id, operatesfrom, operatesuntil, qcharacteristics, srated, uuid]\n" +
        "4: [cosphirated, em, id, qcharacteristics, srated, uuid]\n" +
        "5: [cosphirated, em, id, operatesfrom, qcharacteristics, srated, uuid]\n" +
        "6: [cosphirated, em, id, operatesuntil, qcharacteristics, srated, uuid]\n" +
        "7: [cosphirated, em, id, operatesfrom, operatesuntil, qcharacteristics, srated, uuid]\n"
  }
}
