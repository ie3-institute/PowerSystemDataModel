/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.FactoryData
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.utils.options.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import java.time.ZonedDateTime

import static edu.ie3.util.quantities.PowerSystemUnits.PU

class PvInputFactoryTest extends Specification implements FactoryTestHelper {
  def "A PvInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new PvInputFactory()
    def expectedClasses = [PvInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A PvInputFactory should parse a valid PvInput correctly"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new PvInputFactory()
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "albedo"          : "3",
      "azimuth"         : "4",
      "etaconv"         : "5",
      "elevationangle"  : "6",
      "kg"              : "7",
      "kt"              : "8",
      "marketreaction"  : "true",
      "srated"          : "9",
      "cosphirated"          : "10",
    ]
    def inputClass = PvInput
    def nodeInput = Mock(NodeInput)
    def operatorInput = Mock(OperatorInput)

    when:
    Try<PvInput, FactoryException> input = inputFactory.get(
        new NodeAssetInputEntityData(new FactoryData.MapWithRowIndex("-1", parameter), inputClass, operatorInput, nodeInput))

    then:
    input.success
    input.data.getClass() == inputClass
    input.data.with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert albedo == Double.parseDouble(parameter["albedo"])
      assert azimuth == getQuant(parameter["azimuth"], StandardUnits.AZIMUTH)
      assert etaConv == getQuant(parameter["etaconv"], StandardUnits.EFFICIENCY)
      assert elevationAngle == getQuant(parameter["elevationangle"], StandardUnits.SOLAR_ELEVATION_ANGLE)
      assert kG == Double.parseDouble(parameter["kg"])
      assert kT == Double.parseDouble(parameter["kt"])
      assert marketReaction
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosphirated"])
    }
  }
}
