/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import static edu.ie3.util.quantities.PowerSystemUnits.*
import static tech.units.indriya.unit.Units.CELSIUS
import static tech.units.indriya.unit.Units.METRE_PER_SECOND

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.timeseries.IntValue
import edu.ie3.datamodel.models.timeseries.TimeSeries
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput
import edu.ie3.datamodel.models.value.*
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import tech.units.indriya.quantity.Quantities

import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime

trait TimeSeriesTestData {
  GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326)
  Point defaultLocation = geometryFactory.createPoint(new Coordinate(7.412152, 51.492758))

  TimeBasedValue<EnergyPriceValue> timeBasedEntry = new TimeBasedValue<>(
  ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
  new EnergyPriceValue(Quantities.getQuantity(5d, EURO_PER_MEGAWATTHOUR)))

  IndividualTimeSeries<EnergyPriceValue> individualEnergyPriceTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("a4bbcb77-b9d0-4b88-92be-b9a14a3e332b"),
  [
    timeBasedEntry,
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new EnergyPriceValue(Quantities.getQuantity(15d, EURO_PER_MEGAWATTHOUR))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new EnergyPriceValue(Quantities.getQuantity(10d, EURO_PER_MEGAWATTHOUR))),
  ] as Set
  )

  IndividualTimeSeriesMetaInformation individualEnergyPriceTimeSeriesMeta = new IndividualTimeSeriesMetaInformation(
  UUID.fromString("a4bbcb77-b9d0-4b88-92be-b9a14a3e332b"),
  ColumnScheme.ENERGY_PRICE
  )

  Set<LinkedHashMap<String, String>>  individualEnergyPriceTimeSeriesProcessed = [
    [
      "time"	: "2020-04-02T10:00:00Z",
      "price"	: "5.0"
    ] as LinkedHashMap,
    [
      "time"	: "2020-04-02T10:15:00Z",
      "price"	: "15.0"
    ] as LinkedHashMap,
    [
      "time"	: "2020-04-02T10:30:00Z",
      "price"	: "10.0"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<IntValue> individualIntTimeSeries = new IndividualTimeSeries<>(
  UUID.randomUUID(),
  [
    new TimeBasedValue<IntValue>(ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")), new IntValue(3)),
    new TimeBasedValue<IntValue>(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), new IntValue(4)),
    new TimeBasedValue<IntValue>(ZonedDateTime.of(1990, 1, 1, 0, 30, 0, 0, ZoneId.of("UTC")), new IntValue(1))
  ] as Set
  )

  IndividualTimeSeries<TemperatureValue> individualTemperatureTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("90da7b7d-2148-4510-a730-31f01a554ace"),
  [
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new TemperatureValue(Quantities.getQuantity(5d, CELSIUS))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new TemperatureValue(Quantities.getQuantity(15d, CELSIUS))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new TemperatureValue(Quantities.getQuantity(10d, CELSIUS))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualTemperatureTimeSeriesProcessed = [
    [
      "time"			: "2020-04-02T10:00:00Z",
      "temperature"	: "5.0"
    ] as LinkedHashMap,
    [
      "time"			: "2020-04-02T10:15:00Z",
      "temperature"	: "15.0"
    ] as LinkedHashMap,
    [
      "time"			: "2020-04-02T10:30:00Z",
      "temperature"	: "10.0"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<WindValue> individualWindTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("3dbfb74f-1fba-4150-95e7-24d22bfca4ac"),
  [
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new WindValue(Quantities.getQuantity(5d, DEGREE_GEOM), Quantities.getQuantity(10d, METRE_PER_SECOND))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new WindValue(Quantities.getQuantity(15d, DEGREE_GEOM), Quantities.getQuantity(20d, METRE_PER_SECOND))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new WindValue(Quantities.getQuantity(10d, DEGREE_GEOM), Quantities.getQuantity(15d, METRE_PER_SECOND))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualWindTimeSeriesProcessed = [
    [
      "direction"	: "5.0",
      "time"		: "2020-04-02T10:00:00Z",
      "velocity"	: "10.0"
    ] as LinkedHashMap,
    [
      "direction"	: "15.0",
      "time"		: "2020-04-02T10:15:00Z",
      "velocity"	: "20.0"
    ] as LinkedHashMap,
    [
      "direction"	: "10.0",
      "time"		: "2020-04-02T10:30:00Z",
      "velocity"	: "15.0"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<SolarIrradianceValue> individualIrradianceTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("fa7fd93b-3d83-4cf6-83d0-85eb1853dcfa"),
  [
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new SolarIrradianceValue(Quantities.getQuantity(5d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new SolarIrradianceValue(Quantities.getQuantity(15d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(20d, StandardUnits.SOLAR_IRRADIANCE))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new SolarIrradianceValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(15d, StandardUnits.SOLAR_IRRADIANCE))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>> individualIrradianceTimeSeriesProcessed = [
    [
      "directIrradiance"		: "5.0",
      "diffuseIrradiance"	: "10.0",
      "time"					: "2020-04-02T10:00:00Z"
    ] as LinkedHashMap,
    [
      "directIrradiance"		: "15.0",
      "diffuseIrradiance"	: "20.0",
      "time"					: "2020-04-02T10:15:00Z"
    ] as LinkedHashMap,
    [
      "directIrradiance"		: "10.0",
      "diffuseIrradiance"	: "15.0",
      "time"					: "2020-04-02T10:30:00Z"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<WeatherValue> individualWeatherTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("4fcbdfcd-4ff0-46dd-b0df-f3af7ae3ed98"),
  [
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new WeatherValue(
    defaultLocation,
    new SolarIrradianceValue(Quantities.getQuantity(5d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE)),
    new TemperatureValue(Quantities.getQuantity(5d, CELSIUS)),
    new WindValue(Quantities.getQuantity(5d, DEGREE_GEOM), Quantities.getQuantity(10d, METRE_PER_SECOND))
    )
    ),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new WeatherValue(
    defaultLocation,
    new SolarIrradianceValue(Quantities.getQuantity(15d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(20d, StandardUnits.SOLAR_IRRADIANCE)),
    new TemperatureValue(Quantities.getQuantity(15d, CELSIUS)),
    new WindValue(Quantities.getQuantity(15d, DEGREE_GEOM), Quantities.getQuantity(20d, METRE_PER_SECOND))
    )
    ),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new WeatherValue(
    defaultLocation,
    new SolarIrradianceValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(15d, StandardUnits.SOLAR_IRRADIANCE)),
    new TemperatureValue(Quantities.getQuantity(10d, CELSIUS)),
    new WindValue(Quantities.getQuantity(10d, DEGREE_GEOM), Quantities.getQuantity(15d, METRE_PER_SECOND))
    )
    ),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualWeatherTimeSeriesProcessed = [
    [
      "coordinate"			: "{\"type\":\"Point\",\"coordinates\":[7.412152,51.492758],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
      "diffuseIrradiance"	: "10.0",
      "directIrradiance"		: "5.0",
      "direction"				: "5.0",
      "temperature"			: "5.0",
      "time"					: "2020-04-02T10:00:00Z",
      "velocity"				: "10.0"
    ] as LinkedHashMap,
    [
      "coordinate"			: "{\"type\":\"Point\",\"coordinates\":[7.412152,51.492758],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
      "diffuseIrradiance"	: "20.0",
      "directIrradiance"		: "15.0",
      "direction"				: "15.0",
      "temperature"			: "15.0",
      "time"					: "2020-04-02T10:15:00Z",
      "velocity"				: "20.0"
    ] as LinkedHashMap,
    [
      "coordinate"			: "{\"type\":\"Point\",\"coordinates\":[7.412152,51.492758],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
      "diffuseIrradiance"	: "15.0",
      "directIrradiance"		: "10.0",
      "direction"				: "10.0",
      "temperature"			: "10.0",
      "time"					: "2020-04-02T10:30:00Z",
      "velocity"				: "15.0"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<HeatDemandValue> individualHeatDemandTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("3c0ebc06-9bd7-44ea-a347-0c52d3dec854"),
  [
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new HeatDemandValue(Quantities.getQuantity(5d, KILOWATT))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new HeatDemandValue(Quantities.getQuantity(15d, KILOWATT))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new HeatDemandValue(Quantities.getQuantity(10d, KILOWATT))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualHeatDemandTimeSeriesProcessed = [
    [
      "heatDemand"	: "5.0",
      "time"			: "2020-04-02T10:00:00Z"
    ] as LinkedHashMap,
    [
      "heatDemand"	: "15.0",
      "time"			: "2020-04-02T10:15:00Z"
    ] as LinkedHashMap,
    [
      "heatDemand"	: "10.0",
      "time"			: "2020-04-02T10:30:00Z"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<PValue> individualPTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("b3d93b08-4985-41a6-b063-00f934a10b28"),
  [
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new PValue(Quantities.getQuantity(5d, KILOWATT))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new PValue(Quantities.getQuantity(15d, KILOWATT))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new PValue(Quantities.getQuantity(10d, KILOWATT))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualPTimeSeriesProcessed = [
    [
      "p"		: "5.0",
      "time"	: "2020-04-02T10:00:00Z"
    ] as LinkedHashMap,
    [
      "p"		: "15.0",
      "time"	: "2020-04-02T10:15:00Z"
    ] as LinkedHashMap,
    [
      "p"		: "10.0",
      "time"	: "2020-04-02T10:30:00Z"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<HeatAndPValue> individualHeatAndPTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("56c20b88-c001-4225-8dac-cd13a75c6b48"),
  [
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new HeatAndPValue(Quantities.getQuantity(5d, KILOWATT), Quantities.getQuantity(10d, KILOWATT))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new HeatAndPValue(Quantities.getQuantity(15d, KILOWATT), Quantities.getQuantity(20d, KILOWATT))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new HeatAndPValue(Quantities.getQuantity(10d, KILOWATT), Quantities.getQuantity(15d, KILOWATT))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualHeatAndPTimeSeriesProcessed = [
    [
      "heatDemand"	: "10.0",
      "p"				: "5.0",
      "time"			: "2020-04-02T10:00:00Z"
    ] as LinkedHashMap,
    [
      "heatDemand"	: "20.0",
      "p"				: "15.0",
      "time"			: "2020-04-02T10:15:00Z"
    ] as LinkedHashMap,
    [
      "heatDemand"	: "15.0",
      "p"				: "10.0",
      "time"			: "2020-04-02T10:30:00Z"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<SValue> individualSTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("7d085fc9-be29-4218-b768-00f885be066b"),
  [
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new SValue(Quantities.getQuantity(5d, KILOWATT), Quantities.getQuantity(10d, KILOWATT))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new SValue(Quantities.getQuantity(15d, KILOWATT), Quantities.getQuantity(20d, KILOWATT))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new SValue(Quantities.getQuantity(10d, KILOWATT), Quantities.getQuantity(15d, KILOWATT))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualSTimeSeriesProcessed = [
    [
      "p"		: "5.0",
      "q"		: "10.0",
      "time"	: "2020-04-02T10:00:00Z"
    ] as LinkedHashMap,
    [
      "p"		: "15.0",
      "q"		: "20.0",
      "time"	: "2020-04-02T10:15:00Z"
    ] as LinkedHashMap,
    [
      "p"		: "10.0",
      "q"		: "15.0",
      "time"	: "2020-04-02T10:30:00Z"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<HeatAndSValue> individualHeatAndSTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("83b577cc-06b1-47a1-bfff-ad648a00784b"),
  [
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new HeatAndSValue(Quantities.getQuantity(5d, KILOWATT), Quantities.getQuantity(10d, KILOWATT), Quantities.getQuantity(15d, KILOWATT))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new HeatAndSValue(Quantities.getQuantity(15d, KILOWATT), Quantities.getQuantity(20d, KILOWATT), Quantities.getQuantity(25d, KILOWATT))),
    new TimeBasedValue<>(
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new HeatAndSValue(Quantities.getQuantity(10d, KILOWATT), Quantities.getQuantity(15d, KILOWATT), Quantities.getQuantity(20d, KILOWATT))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualHeatAndSTimeSeriesProcessed = [
    [
      "heatDemand"	: "15.0",
      "p"				: "5.0",
      "q"				: "10.0",
      "time"			: "2020-04-02T10:00:00Z"
    ] as LinkedHashMap,
    [
      "heatDemand"	: "25.0",
      "p"				: "15.0",
      "q"				: "20.0",
      "time"			: "2020-04-02T10:15:00Z"
    ] as LinkedHashMap,
    [
      "heatDemand"	: "20.0",
      "p"				: "10.0",
      "q"				: "15.0",
      "time"			: "2020-04-02T10:30:00Z"
    ] as LinkedHashMap
  ] as Set

  LoadProfileInput loadProfileInput =  new LoadProfileInput(
  UUID.fromString("b56853fe-b800-4c18-b324-db1878b22a28"),
  BdewStandardLoadProfile.G2,
  [
    new LoadProfileEntry(
    new PValue(Quantities.getQuantity(5d, KILOWATT)),
    DayOfWeek.MONDAY,
    0
    ),
    new LoadProfileEntry(
    new PValue(Quantities.getQuantity(15d, KILOWATT)),
    DayOfWeek.MONDAY,
    1
    ),
    new LoadProfileEntry(
    new PValue(Quantities.getQuantity(10d, KILOWATT)),
    DayOfWeek.MONDAY,
    2
    )
  ] as Set
  )

  Set<LinkedHashMap<String, String>> loadProfileInputProcessed = [
    [
      "dayOfWeek"			: "MONDAY",
      "p"					: "5.0",
      "quarterHourOfDay"	: "0"
    ] as LinkedHashMap,
    [
      "dayOfWeek"			: "MONDAY",
      "p"					: "15.0",
      "quarterHourOfDay"	: "1"
    ] as LinkedHashMap,
    [
      "dayOfWeek"			: "MONDAY",
      "p"					: "10.0",
      "quarterHourOfDay"	: "2"
    ] as LinkedHashMap
  ] as Set

  List<TimeSeries> allTimeSeries = [
    individualPTimeSeries,
    individualEnergyPriceTimeSeries,
    individualHeatAndPTimeSeries,
    individualHeatAndSTimeSeries,
    individualHeatDemandTimeSeries,
    individualPTimeSeries,
    individualSTimeSeries,
    individualWeatherTimeSeries,
    loadProfileInput
  ]
}