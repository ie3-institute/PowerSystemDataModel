/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import static edu.ie3.datamodel.models.StandardUnits.*
import static edu.ie3.util.quantities.PowerSystemUnits.OHM_PER_KILOMETRE

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.util.TimeUtil
import edu.ie3.util.quantities.interfaces.SpecificConductance
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.Quantity

class ValidationUtilsTest extends Specification {

  def "Smoke Test: Correct asset throws no exception"() {
    given:
    def asset = GridTestData.nodeA

    when:
    ValidationUtils.check(asset)

    then:
    noExceptionThrown()
  }

  def "The validation check method recognizes all potential errors for an asset"() {
    when:
    ValidationUtils.check(invalidAsset)

    then:
    Exception ex = thrown()
    ex.message.contains(expectedException.message)

    where:
    invalidAsset                                                            	    || expectedException
    null 																			|| new InvalidEntityException("Expected an object, but got nothing. :-(", new NullPointerException())
    GridTestData.nodeA.copy().id(null).build()										|| new InvalidEntityException("No ID assigned", invalidAsset)
    GridTestData.nodeA.copy().operationTime(null).build()							|| new InvalidEntityException("Operation time of the asset is not defined", invalidAsset)
    GridTestData.nodeA.copy().operationTime(OperationTime.builder().
        withStart(TimeUtil.withDefaults.toZonedDateTime("2020-03-26T15:11:31Z")).
        withEnd(TimeUtil.withDefaults.toZonedDateTime("2020-03-25T15:11:31Z")).build()).build() || new InvalidEntityException("Operation start time of the asset has to be before end time", invalidAsset)
  }

  def "The check for negative entities should work as expected"() {
    given:
    def asset = new LineTypeInput(
        UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
        "lineType_AtoB",
        Quantities.getQuantity(0d, SUSCEPTANCE_PER_LENGTH),
        Quantities.getQuantity(0d, CONDUCTANCE_PER_LENGTH),
        Quantities.getQuantity(0.437d, OHM_PER_KILOMETRE),
        Quantities.getQuantity(0.356d, OHM_PER_KILOMETRE),
        Quantities.getQuantity(300d, ELECTRIC_CURRENT_MAGNITUDE),
        Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
        )
    def invalidAsset = new LineTypeInput(
        UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
        "lineType_AtoB",
        Quantities.getQuantity(-1d, SUSCEPTANCE_PER_LENGTH), // invalid value
        Quantities.getQuantity(0d, CONDUCTANCE_PER_LENGTH),
        Quantities.getQuantity(0.437d, OHM_PER_KILOMETRE),
        Quantities.getQuantity(0.356d, OHM_PER_KILOMETRE),
        Quantities.getQuantity(300d, ELECTRIC_CURRENT_MAGNITUDE),
        Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
        )

    when:
    ValidationUtils.detectNegativeQuantities([asset.getB()] as Quantity<SpecificConductance>[], asset)

    then:
    noExceptionThrown()

    when:
    ValidationUtils.detectNegativeQuantities([invalidAsset.getB()] as Quantity<SpecificConductance>[], invalidAsset)

    then:
    InvalidEntityException ex = thrown()
    ex.message == "Entity is invalid because of: \nThe following quantities have to be zero or positive: -1 µS/km [LineTypeInput{uuid=3bed3eb3-9790-4874-89b5-a5434d408088, id=lineType_AtoB, b=-1 µS/km, g=0.0 µS/km, r=0.437 Ω/km, x=0.356 Ω/km, iMax=300 A, vRated=20 kV}]"
  }

  def "The check for zero or negative entities should work as expected"() {
    given:
    def asset = new LineTypeInput(
        UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
        "lineType_AtoB",
        Quantities.getQuantity(1d, SUSCEPTANCE_PER_LENGTH),
        Quantities.getQuantity(0d, CONDUCTANCE_PER_LENGTH),
        Quantities.getQuantity(0.437d, OHM_PER_KILOMETRE),
        Quantities.getQuantity(0.356d, OHM_PER_KILOMETRE),
        Quantities.getQuantity(300d, ELECTRIC_CURRENT_MAGNITUDE),
        Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
        )
    def invalidAsset = new LineTypeInput(
        UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
        "lineType_AtoB",
        Quantities.getQuantity(0d, SUSCEPTANCE_PER_LENGTH), // invalid value
        Quantities.getQuantity(0d, CONDUCTANCE_PER_LENGTH),
        Quantities.getQuantity(0.437d, OHM_PER_KILOMETRE),
        Quantities.getQuantity(0.356d, OHM_PER_KILOMETRE),
        Quantities.getQuantity(300d, ELECTRIC_CURRENT_MAGNITUDE),
        Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
        )

    when:
    ValidationUtils.detectZeroOrNegativeQuantities([asset.getB()] as Quantity<SpecificConductance>[], asset)

    then:
    noExceptionThrown()

    when:
    ValidationUtils.detectZeroOrNegativeQuantities([invalidAsset.getB()] as Quantity<SpecificConductance>[], invalidAsset)

    then:
    InvalidEntityException ex = thrown()
    ex.message == "Entity is invalid because of: \nThe following quantities have to be positive: 0.0 µS/km [LineTypeInput{uuid=3bed3eb3-9790-4874-89b5-a5434d408088, id=lineType_AtoB, b=0.0 µS/km, g=0.0 µS/km, r=0.437 Ω/km, x=0.356 Ω/km, iMax=300 A, vRated=20 kV}]"
  }

  def "Checking an asset type input without an id leads to an exception"() {
    given:
    def invalidAssetType = new InvalidAssetTypeInput(UUID.randomUUID(), null)

    when:
    List<Try<Void, ? extends ValidationException>> exceptions = ValidationUtils.checkAssetType(invalidAssetType).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == 1
    def e = exceptions.get(0).exception.get()
    e.message.startsWith("Entity is invalid because of: \nNo ID assigned [AssetTypeInput")
  }
}
