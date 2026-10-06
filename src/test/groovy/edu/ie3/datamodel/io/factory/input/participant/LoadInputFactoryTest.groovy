/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import spock.lang.Shared

import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.profile.NbwTemperatureDependantLoadProfile
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless

class LoadInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared  private UUID nodeUuid = UUID.randomUUID()
  @Shared  private def nodeInput = Mock(NodeInput)
  @Shared private UUID operatorUuid = UUID.randomUUID()
  @Shared  private def operatorInput = Mock(OperatorInput)
  @Shared  private UUID emUuid = UUID.randomUUID()
  @Shared  private def emUnit = Mock(EmInput)

  @Shared  private LoadInputFactory inputFactory

  def setupSpec() {
    nodeInput.getUuid() >> nodeUuid
    operatorInput.getUuid() >> operatorUuid
    emUnit.getUuid() >> emUuid

    inputFactory = new LoadInputFactory(map(operatorInput), map(nodeInput), map(emUnit))
  }

  def "A LoadInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [LoadInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A LoadInputFactory should parse a valid LoadInput correctly"() {
    when:
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id" : "TestID",
      "qCharacteristics" : "cosPhiFixed:{(0.0,1.0)}",
      "loadProfile" : profileKey,
      "eConsAnnual" : "3",
      "sRated" : "4",
      "cosPhiRated" : "5",
      "operator": operatorUuid.toString(),
      "node": nodeUuid.toString(),
      "controllingEm": emUuid.toString()
    ]
    Try<LoadInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == LoadInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime == OperationTime.notLimited()
      operator == OperatorInput.NO_OPERATOR_ASSIGNED
      id == parameter["id"]
      node == nodeInput
      qCharacteristics.with {
        uuid != null
        points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      controllingEm == Optional.of(emUnit)
      loadProfile == profile
      eConsAnnual == getQuant(parameter["eConsAnnual"], StandardUnits.ENERGY_IN)
      sRated == getQuant(parameter["sRated"], StandardUnits.S_RATED)
      cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])
    }

    where:
    profileKey || profile
    "G-4" || BdewStandardLoadProfile.G4.key
    "ep1" || NbwTemperatureDependantLoadProfile.EP1.key
  }
}
