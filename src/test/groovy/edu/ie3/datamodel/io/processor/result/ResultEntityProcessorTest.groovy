/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor.result

import edu.ie3.datamodel.exceptions.EntityProcessorException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.ResultEntity
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.connector.SwitchResult
import edu.ie3.datamodel.models.result.connector.Transformer2WResult
import edu.ie3.datamodel.models.result.connector.Transformer3WResult
import edu.ie3.datamodel.models.result.system.*
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

import java.time.ZonedDateTime
import javax.measure.Quantity
import javax.measure.quantity.*

class ResultEntityProcessorTest extends Specification {

  // static fields
  @Shared
  UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
  @Shared
  UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
  @Shared
  Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
  @Shared
  Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
  @Shared
  Quantity<Dimensionless> soc = Quantities.getQuantity(50, Units.PERCENT)
  @Shared
  Quantity<Power> qDot = Quantities.getQuantity(1, StandardUnits.Q_DOT_RESULT)
  @Shared
  def expectedStandardResults = [
    inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
    p         : '0.01',
    q         : '0.01',
    time      : '2020-01-30T17:26:44Z']

  @Shared
  def expectedSocResults = [
    inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
    p         : '0.01',
    q         : '0.01',
    soc       : '50.0',
    time      : '2020-01-30T17:26:44Z']

  @Shared
  def expectedQDotResults = [
    inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
    p         : '0.01',
    q         : '0.01',
    time      : '2020-01-30T17:26:44Z',
    qDot      : '1.0']


  def "A ResultEntityProcessor should serialize a provided SystemParticipantResult correctly"() {
    given:
    def sysPartResProcessor = new ResultEntityProcessor(modelClass)
    def validResult = validSystemParticipantResult

    when:
    def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

    then:
    validProcessedElement == expectedResults

    where:
    modelClass        | validSystemParticipantResult                                                                     || expectedResults
    LoadResult        | new LoadResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q)         || expectedStandardResults
    FixedFeedInResult | new FixedFeedInResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q)  || expectedStandardResults
    BmResult          | new BmResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q)           || expectedStandardResults
    EvResult          | new EvResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q, soc)      || expectedSocResults
    PvResult          | new PvResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q)           || expectedStandardResults
    EvcsResult        | new EvcsResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q)         || expectedStandardResults
    ChpResult         | new ChpResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q, qDot)    || expectedQDotResults
    WecResult         | new WecResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q)          || expectedStandardResults
    StorageResult     | new StorageResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q, soc) || expectedSocResults
    HpResult          | new HpResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q, qDot)     || expectedQDotResults
    EmResult          | new EmResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q)           || expectedStandardResults
  }

  def "A ResultEntityProcessor should throw an exception if the provided class is not registered"() {
    given:
    def sysPartResProcessor = new ResultEntityProcessor(LoadResult)
    def storageResult = new StorageResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, p, q, Quantities.getQuantity(10d, StandardUnits.SOC))

    when:
    sysPartResProcessor.handleEntity(storageResult)

    then:
    EntityProcessorException ex = thrown()
    ex.message == "Cannot process StorageResult.class with this EntityProcessor. Please either provide an element of LoadResult.class or create a new processor for StorageResult.class!"
  }

  def "A ResultEntityProcessor should serialize a NodeResult correctly"() {
    given:
    def sysPartResProcessor = new ResultEntityProcessor(NodeResult)

    Quantity<Dimensionless> vMag = Quantities.getQuantity(0.95, PowerSystemUnits.PU)
    Quantity<Angle> vAng = Quantities.getQuantity(45, StandardUnits.VOLTAGE_ANGLE)

    def validResult = new NodeResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, vMag, vAng)

    def expectedResults = [
      inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
      vAng      : '45.0',
      vMag      : '0.95',
      time      : '2020-01-30T17:26:44Z']

    when:
    def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

    then:
    validProcessedElement == expectedResults
  }

  def "A ResultEntityProcessor should serialize a FlexOptionsResult correctly"() {
    given:
    def sysPartResProcessor = new ResultEntityProcessor(FlexOptionsResult)

    // take wrong unit for pRef on purpose, should get converted
    Quantity<Power> pRef = Quantities.getQuantity(5100, PowerSystemUnits.KILOWATT)
    Quantity<Power> pMin = Quantities.getQuantity(-6, StandardUnits.ACTIVE_POWER_RESULT)
    Quantity<Power> pMax = Quantities.getQuantity(6, StandardUnits.ACTIVE_POWER_RESULT)

    def validResult = new FlexOptionsResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, pRef, pMin, pMax)

    def expectedResults = [
      inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
      time      : '2020-01-30T17:26:44Z',
      pMax      : '6.0',
      pMin      : '-6.0',
      pRef      : '5.1',
    ]

    when:
    def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

    then:
    validProcessedElement == expectedResults
  }

  @Shared
  def expectedLineResults = [
    inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
    iAMag     : '100.0',
    iAAng     : '45.0',
    iBMag     : '150.0',
    iBAng     : '30.0',
    time      : '2020-01-30T17:26:44Z']

  @Shared
  def expectedTrafo2WResults = [
    inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
    iAMag     : '100.0',
    iAAng     : '45.0',
    iBMag     : '150.0',
    iBAng     : '30.0',
    tapPos    : '5',
    time      : '2020-01-30T17:26:44Z']


  @Shared
  def expectedTrafo3WResults = [
    inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
    iAMag     : '100.0',
    iAAng     : '45.0',
    iBMag     : '150.0',
    iBAng     : '30.0',
    iCMag     : '300.0',
    iCAng     : '70.0',
    tapPos    : '5',
    time      : '2020-01-30T17:26:44Z']

  @Shared
  def expectedSwitchResults = [
    inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
    closed    : 'true',
    time      : '2020-01-30T17:26:44Z']


  @Shared
  Quantity<ElectricCurrent> iAMag = Quantities.getQuantity(100, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
  @Shared
  Quantity<Angle> iAAng = Quantities.getQuantity(45, StandardUnits.ELECTRIC_CURRENT_ANGLE)
  @Shared
  Quantity<ElectricCurrent> iBMag = Quantities.getQuantity(150, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
  @Shared
  Quantity<Angle> iBAng = Quantities.getQuantity(30, StandardUnits.ELECTRIC_CURRENT_ANGLE)
  @Shared
  Quantity<ElectricCurrent> iCMag = Quantities.getQuantity(300, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
  @Shared
  Quantity<Angle> iCAng = Quantities.getQuantity(70, StandardUnits.ELECTRIC_CURRENT_ANGLE)
  @Shared
  int tapPos = 5
  @Shared
  boolean closed = true


  def "A ResultEntityProcessor should serialize all ConnectorResults correctly"() {
    given:
    def sysPartResProcessor = new ResultEntityProcessor(modelClass)

    def validResult = validConnectorResult

    when:
    def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

    then:
    validProcessedElement == expectedResults

    where:
    modelClass          | validConnectorResult                                                                                                                          || expectedResults
    LineResult          | new LineResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, iAMag, iAAng, iBMag, iBAng)                                || expectedLineResults
    SwitchResult        | new SwitchResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, closed)                    								|| expectedSwitchResults
    Transformer2WResult | new Transformer2WResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, iAMag, iAAng, iBMag, iBAng, tapPos)               || expectedTrafo2WResults
    Transformer3WResult | new Transformer3WResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, iAMag, iAAng, iBMag, iBAng, iCMag, iCAng, tapPos) || expectedTrafo3WResults
  }

  def "A ResultEntityProcessor should serialize a CylindricalStorageResult correctly"() {
    given:
    def sysPartResProcessor = new ResultEntityProcessor(CylindricalStorageResult)

    Quantity<Power> qDot = Quantities.getQuantity(2, StandardUnits.Q_DOT_RESULT)
    Quantity<Energy> energy = Quantities.getQuantity(3, StandardUnits.ENERGY_RESULT)
    Quantity<Dimensionless> fillLevel = Quantities.getQuantity(20, Units.PERCENT)

    def validResult = new CylindricalStorageResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputModel, energy, qDot, fillLevel)

    def expectedResults = [
      energy    : '3.0',
      fillLevel : '20.0',
      inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
      qDot      : '2.0',
      time      : '2020-01-30T17:26:44Z']

    when:
    def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

    then:
    validProcessedElement == expectedResults
  }

  def "A ResultEntityProcessor should throw an EntityProcessorException when it receives an entity result that is not eligible"() {

    given:
    def sysPartResProcessor = new ResultEntityProcessor(ResultEntityProcessor.eligibleEntityClasses.get(0))

    def invalidClassResult = new InvalidTestResult(ZonedDateTime.parse("2020-01-30T17:26:44Z"), uuid)

    when:
    sysPartResProcessor.handleEntity(invalidClassResult)

    then:
    EntityProcessorException exception = thrown()
    exception.message == "Cannot process InvalidTestResult.class with this EntityProcessor. " +
        "Please either provide an element of LoadResult.class or create a new processor for InvalidTestResult.class!"
  }

  def "The list of eligible entity classes for a ResultEntityProcessor should be valid"() {
    given:
    int noOfElements = 20 // number of all currently implemented entity results

    expect:
    ResultEntityProcessor.eligibleEntityClasses.size() == noOfElements
  }

  def "ResultEntityProcessor should throw an exception if an invalid class is passed into the constructor"() {

    when:
    new ResultEntityProcessor(InvalidTestResult)

    then:
    thrown(EntityProcessorException)
  }

  private static class InvalidTestResult extends ResultEntity {

    InvalidTestResult(ZonedDateTime time, UUID inputModel) {
      super(time, inputModel)
    }
  }
}
