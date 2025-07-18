/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import static edu.ie3.datamodel.models.result.CongestionResult.InputModelType.NODE
import static edu.ie3.datamodel.utils.validation.DummyAssetInput.valid
import static edu.ie3.datamodel.utils.validation.UniquenessValidationUtils.*
import static edu.ie3.util.quantities.PowerSystemUnits.DEGREE_GEOM
import static edu.ie3.util.quantities.PowerSystemUnits.PU
import static tech.units.indriya.unit.Units.METRE_PER_SECOND
import static tech.units.indriya.unit.Units.PERCENT

import edu.ie3.datamodel.exceptions.DuplicateEntitiesException
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.AssetInput
import edu.ie3.datamodel.models.result.CongestionResult
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.ResultEntity
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.SolarIrradianceValue
import edu.ie3.datamodel.models.value.TemperatureValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.datamodel.models.value.WindValue
import edu.ie3.datamodel.utils.Try
import edu.ie3.util.geo.GeoUtils
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

import java.time.ZonedDateTime
import javax.measure.Quantity
import javax.measure.quantity.Angle
import javax.measure.quantity.Dimensionless

class UniquenessValidationUtilsTest extends Specification {

  def "Checking if unique entities are unique"() {
    given:
    Set<AssetInput> uniqueEntities = [
      valid("first"),
      valid("second")
    ]

    when:
    checkUniqueEntities(uniqueEntities)

    then:
    noExceptionThrown()
  }

  def "Duplicates in unique entities leads to an exception for UniqueEntity"() {
    given:
    UUID uuid = UUID.fromString("4b931d0a-f564-4555-b576-905c9b9f42d0")

    Set<DummyAssetInput> notUniqueEntities = [
      valid(uuid, "first"),
      valid(uuid, "second")
    ]

    when:
    checkUniqueEntities(notUniqueEntities)

    then:
    DuplicateEntitiesException de = thrown()
    de.message == "'DummyAssetInput' entities with duplicated UUID key, but different field values found! " +
        "Affected primary keys: [4b931d0a-f564-4555-b576-905c9b9f42d0]"
  }

  def "Checking if asset inputs are unique"() {
    given:
    Set<AssetInput> uniqueAssets = [
      valid("first"),
      valid("second"),
      valid("third")
    ]

    when:
    checkAssetUniqueness(uniqueAssets)

    then:
    noExceptionThrown()
  }

  def "Duplicates in asset input ids leads to an exception"() {
    given:
    Set<AssetInput> notUniqueAssets = [
      valid("first"),
      valid("first")
    ]

    when:
    checkAssetUniqueness(notUniqueAssets)

    then:
    DuplicateEntitiesException de = thrown()
    de.message == "The following exception(s) occurred while checking the uniqueness of 'AssetInput' entities: \n" +
        "       'DummyAssetInput' entities with duplicated String key, but different field values found! Affected primary keys: [first]"
  }

  def "Checking if result entities are unique"() {
    given:
    ZonedDateTime time = ZonedDateTime.now()
    UUID uuid = UUID.randomUUID()
    Quantity<Dimensionless> vMag = Quantities.getQuantity(0.95, PU)
    Quantity<Angle> vAng = Quantities.getQuantity(45, StandardUnits.VOLTAGE_ANGLE)

    Set<ResultEntity> uniqueResults = [
      new NodeResult(time, uuid, vMag, vAng),
      new NodeResult(time.plusHours(1), uuid, vMag, vAng)
    ]

    when:
    checkResultUniqueness(uniqueResults)

    then:
    noExceptionThrown()
  }

  def "Duplicates in result inputs lead to an exception"() {
    given:
    ZonedDateTime time = ZonedDateTime.parse("2024-02-15T13:49:44+01:00[Europe/Berlin]")
    UUID uuid1 = UUID.fromString("4f7938ad-3d8f-4d56-a76c-525f2362e8b6")
    UUID uuid2 = UUID.fromString("7b0ac056-3f5a-4cf3-b373-4f19d13981cf")
    Quantity<Dimensionless> vMag = Quantities.getQuantity(0.95, PU)
    Quantity<Angle> vAng = Quantities.getQuantity(45, StandardUnits.VOLTAGE_ANGLE)

    Set<ResultEntity> notUniqueResults = [
      new NodeResult(time, uuid1, vMag, vAng),
      new NodeResult(time, uuid1, vMag, vAng),
      new NodeResult(time.plusHours(1), uuid2, vMag, vAng),
      new NodeResult(time.plusHours(1), uuid2, vMag, vAng)
    ]

    when:
    checkResultUniqueness(notUniqueResults)

    then:
    DuplicateEntitiesException de = thrown()
    de.message.startsWith("'NodeResult' entities with duplicated")
  }

  def "Duplicates in congestion result inputs lead to an exception"() {
    given:
    ZonedDateTime time = ZonedDateTime.parse("2024-02-15T13:49:44+01:00[Europe/Berlin]")
    UUID inputModel1 = UUID.randomUUID()
    UUID inputModel2 = UUID.randomUUID()
    int subgrid1 = 1
    int subgrid2 = 2
    Quantity<Dimensionless> value = Quantities.getQuantity(120, PERCENT)
    Quantity<Dimensionless> min = Quantities.getQuantity(90, PERCENT)
    Quantity<Dimensionless> max = Quantities.getQuantity(110, PERCENT)

    Set<CongestionResult> notUniqueResults = [
      new CongestionResult(time, inputModel1, NODE, subgrid1, value, min, max),
      new CongestionResult(time, inputModel1, NODE, subgrid2, value, min, max),
      new CongestionResult(time.plusHours(1), inputModel1, NODE, subgrid1, value, min, max),
      new CongestionResult(time.plusHours(1), inputModel2, NODE, subgrid2, value, min, max),
    ]

    when:
    checkResultUniqueness(notUniqueResults)

    then:
    DuplicateEntitiesException de = thrown()
    de.message.startsWith("'CongestionResult' entities with duplicated")
  }

  def "Checking if mapping entries are unique"() {
    given:
    UUID timeSeries = UUID.randomUUID()
    Set<TimeSeriesMappingSource.MappingEntry> uniqueEntries = [
      new TimeSeriesMappingSource.MappingEntry(UUID.randomUUID(), timeSeries),
      new TimeSeriesMappingSource.MappingEntry(UUID.randomUUID(), timeSeries),
    ]

    when:
    checkMappingEntryUniqueness(uniqueEntries)

    then:
    noExceptionThrown()
  }

  def "Duplicates in mapping entries leads to an exception"() {
    given:
    UUID participant = UUID.fromString("1f25eea2-20eb-4b6b-8f05-bdbb0e851e65")

    Set<TimeSeriesMappingSource.MappingEntry> uniqueEntityEntries = [
      new TimeSeriesMappingSource.MappingEntry(participant, UUID.randomUUID()),
      new TimeSeriesMappingSource.MappingEntry(participant, UUID.randomUUID()),
    ]

    when:
    def entityDuplicate = Try.ofVoid(() -> checkMappingEntryUniqueness(uniqueEntityEntries), DuplicateEntitiesException)

    then:

    entityDuplicate.failure
    entityDuplicate.exception.get().message == "'MappingEntry' entities with duplicated UUID key, but different field values found! " +
    "Affected primary keys: [1f25eea2-20eb-4b6b-8f05-bdbb0e851e65]"
  }

  def "Checking if time based weather values are unique"() {
    given:
    ZonedDateTime time = ZonedDateTime.now()
    WeatherValue value = new WeatherValue(
    GeoUtils.buildPoint(50d, 7d),
    new SolarIrradianceValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE)),
    new TemperatureValue(Quantities.getQuantity(5d, Units.CELSIUS)),
    new WindValue(Quantities.getQuantity(5d, DEGREE_GEOM), Quantities.getQuantity(10d, METRE_PER_SECOND))
    )

    Set<TimeBasedValue<WeatherValue>> uniqueValues = [
      new TimeBasedValue<WeatherValue>(time, value),
      new TimeBasedValue<WeatherValue>(time.plusHours(1), value)
    ]

    when:
    checkWeatherUniqueness(uniqueValues)

    then:
    noExceptionThrown()
  }

  def "Duplicates in time based weather values leads to an exception"() {
    given:
    ZonedDateTime time = ZonedDateTime.now()
    WeatherValue value = new WeatherValue(
    GeoUtils.buildPoint(50d, 7d),
    new SolarIrradianceValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE)),
    new TemperatureValue(Quantities.getQuantity(5d, Units.CELSIUS)),
    new WindValue(Quantities.getQuantity(5d, DEGREE_GEOM), Quantities.getQuantity(10d, METRE_PER_SECOND))
    )
    Set<TimeBasedValue<WeatherValue>> notUniqueValues = [
      new TimeBasedValue<WeatherValue>(time, value),
      new TimeBasedValue<WeatherValue>(time, value)
    ]

    when:
    checkWeatherUniqueness(notUniqueValues)

    then:
    DuplicateEntitiesException de = thrown()
    de.message.startsWith("'TimeBasedValue' entities with duplicated")
  }
}
