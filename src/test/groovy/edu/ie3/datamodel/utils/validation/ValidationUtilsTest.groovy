/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import static edu.ie3.datamodel.models.StandardUnits.*
import static edu.ie3.datamodel.utils.validation.DummyAssetInput.invalid
import static edu.ie3.datamodel.utils.validation.DummyAssetInput.valid
import static edu.ie3.util.quantities.PowerSystemUnits.OHM_PER_KILOMETRE
import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.DuplicateEntitiesException
import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.UniqueEntity
import edu.ie3.datamodel.models.input.AssetInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
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

  def "The validation utils should check for duplicates as expected"() {
    expect:
    def tries = ValidationUtils.checkForDuplicates(collection, UniqueEntity::getUuid)

    if (!tries.isEmpty()) {
      tries.get(0).exception.map {
        it.message
      } == checkResult
    }

    where:
    collection                         || checkResult
    [
      new NodeInput(
      UUID.fromString("9e37ce48-9650-44ec-b888-c2fd182aff01"), "node_f", GridTestData.profBroccoli,
      OperationTime.notLimited()
      ,
      Quantities.getQuantity(1d, PU),
      false,
      null,
      GermanVoltageLevelUtils.LV,
      6),
      new NodeInput(
      UUID.fromString("9e37ce48-9650-44ec-b888-c2fd182aff01"), "node_g", GridTestData.profBroccoli,
      OperationTime.notLimited()
      ,
      Quantities.getQuantity(1d, PU),
      false,
      null,
      GermanVoltageLevelUtils.LV,
      6)
    ] as Set         || Optional.of("The following entities have duplicate 'UUID': " +
    "{NodeInput{uuid=9e37ce48-9650-44ec-b888-c2fd182aff01, id='node_f', operator=f15105c4-a2de-4ab8-a621-4bc98e372d92, operationTime=OperationTime{startDate=null, endDate=null, isLimited=false}, vTarget=1 p.u., slack=false, geoPosition=null, voltLvl=CommonVoltageLevel{id='Niederspannung', nominalVoltage=0.4 kV, synonymousIds=[Niederspannung, lv, ns], voltageRange=Interval [0.0 kV, 10 kV)}, subnet=6}, " +
    "NodeInput{uuid=9e37ce48-9650-44ec-b888-c2fd182aff01, id='node_g', operator=f15105c4-a2de-4ab8-a621-4bc98e372d92, operationTime=OperationTime{startDate=null, endDate=null, isLimited=false}, vTarget=1 p.u., slack=false, geoPosition=null, voltLvl=CommonVoltageLevel{id='Niederspannung', nominalVoltage=0.4 kV, synonymousIds=[Niederspannung, lv, ns], voltageRange=Interval [0.0 kV, 10 kV)}, subnet=6}}")
    [
      GridTestData.nodeD,
      GridTestData.nodeE
    ] as Set || Optional.empty()
    [] as Set                          || Optional.empty()
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

  def "Checking if asset input ids are unique"() {
    given:
    Set<AssetInput> validAssetIds = [
      valid("first"),
      valid("second"),
      valid("third")
    ]

    when:
    List<Try<Void, DuplicateEntitiesException>> exceptions = ValidationUtils.checkForDuplicates(validAssetIds, AssetInput::getId)

    then:
    exceptions.every {
      ex -> ex.success
    }
  }

  def "Duplicate asset input ids leads to an exception"() {
    given:
    Set<AssetInput> invalidAssetIds = [
      invalid(),
      invalid()
    ]

    when:
    List<Try<Void, DuplicateEntitiesException>> exceptions = ValidationUtils.checkForDuplicates(invalidAssetIds, AssetInput::getId)

    then:
    exceptions.size() == 1
    exceptions.get(0).failure
    exceptions.get(0).exception.get().message.startsWith("The following entities have duplicate 'String': {AssetInput{uuid=")
  }
}
