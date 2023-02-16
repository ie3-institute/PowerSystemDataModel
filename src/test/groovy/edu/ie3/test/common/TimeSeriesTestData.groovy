/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.StandardUnits
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

import static edu.ie3.util.quantities.PowerSystemUnits.*
import static tech.units.indriya.unit.Units.CELSIUS
import static tech.units.indriya.unit.Units.METRE_PER_SECOND

trait TimeSeriesTestData {
  GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326)
  Point defaultLocation = geometryFactory.createPoint(new Coordinate(7.412152, 51.492758))

  TimeBasedValue<EnergyPriceValue> timeBasedEntry = new TimeBasedValue<>(
  UUID.fromString("9e4dba1b-f3bb-4e40-bd7e-2de7e81b7704"),
  ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
  new EnergyPriceValue(Quantities.getQuantity(5d, EURO_PER_MEGAWATTHOUR)))

  IndividualTimeSeries<EnergyPriceValue> individualEnergyPriceTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("a4bbcb77-b9d0-4b88-92be-b9a14a3e332b"),
  [
    timeBasedEntry,
    new TimeBasedValue<>(
    UUID.fromString("520d8e37-b842-40fd-86fb-32007e88493e"),
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new EnergyPriceValue(Quantities.getQuantity(15d, EURO_PER_MEGAWATTHOUR))),
    new TimeBasedValue<>(
    UUID.fromString("593d006c-ef76-46a9-b8db-f8666f69c5db"),
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new EnergyPriceValue(Quantities.getQuantity(10d, EURO_PER_MEGAWATTHOUR))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualEnergyPriceTimeSeriesProcessed = [
    [
      "uuid" 	: "9e4dba1b-f3bb-4e40-bd7e-2de7e81b7704",
      "time"	: "2020-04-02T10:00Z[UTC]",
      "price"	: "5.0"
    ] as LinkedHashMap,
    [
      "uuid" 	: "520d8e37-b842-40fd-86fb-32007e88493e",
      "time"	: "2020-04-02T10:15Z[UTC]",
      "price"	: "15.0"
    ] as LinkedHashMap,
    [
      "uuid" 	: "593d006c-ef76-46a9-b8db-f8666f69c5db",
      "time"	: "2020-04-02T10:30Z[UTC]",
      "price"	: "10.0"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<IntValue> individualIntTimeSeries = new IndividualTimeSeries<>(
  UUID.randomUUID(),
  [
    new TimeBasedValue<IntValue>(UUID.fromString("52ccf570-53a5-490e-85d4-7a57082ebdfc"), ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")), new IntValue(3)),
    new TimeBasedValue<IntValue>(UUID.fromString("23727eb1-e108-4187-99b2-bef959797078"), ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), new IntValue(4)),
    new TimeBasedValue<IntValue>(UUID.fromString("21b1a544-2961-4488-a9f6-0e94a706a68f"), ZonedDateTime.of(1990, 1, 1, 0, 30, 0, 0, ZoneId.of("UTC")), new IntValue(1))
  ] as Set
  )

  IndividualTimeSeries<TemperatureValue> individualTemperatureTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("90da7b7d-2148-4510-a730-31f01a554ace"),
  [
    new TimeBasedValue<>(
    UUID.fromString("48962a4a-b169-41f4-b0fe-e4bd8539b281"),
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new TemperatureValue(Quantities.getQuantity(5d, CELSIUS))),
    new TimeBasedValue<>(
    UUID.fromString("38e8188d-17dc-4b49-9827-68ba1eeac1e3"),
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new TemperatureValue(Quantities.getQuantity(15d, CELSIUS))),
    new TimeBasedValue<>(
    UUID.fromString("e332cae2-785d-47db-941a-3c400fa8518b"),
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new TemperatureValue(Quantities.getQuantity(10d, CELSIUS))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualTemperatureTimeSeriesProcessed = [
    [
      "uuid" 			: "48962a4a-b169-41f4-b0fe-e4bd8539b281",
      "time"			: "2020-04-02T10:00Z[UTC]",
      "temperature"	: "5.0"
    ] as LinkedHashMap,
    [
      "uuid" 			: "38e8188d-17dc-4b49-9827-68ba1eeac1e3",
      "time"			: "2020-04-02T10:15Z[UTC]",
      "temperature"	: "15.0"
    ] as LinkedHashMap,
    [
      "uuid" 			: "e332cae2-785d-47db-941a-3c400fa8518b",
      "time"			: "2020-04-02T10:30Z[UTC]",
      "temperature"	: "10.0"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<WindValue> individualWindTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("3dbfb74f-1fba-4150-95e7-24d22bfca4ac"),
  [
    new TimeBasedValue<>(
    UUID.fromString("3453d88d-50f6-4124-b2d0-807a9b7dbf54"),
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new WindValue(Quantities.getQuantity(5d, DEGREE_GEOM), Quantities.getQuantity(10d, METRE_PER_SECOND))),
    new TimeBasedValue<>(
    UUID.fromString("870e8e22-5667-4681-96ad-5ab6ac9cf25b"),
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new WindValue(Quantities.getQuantity(15d, DEGREE_GEOM), Quantities.getQuantity(20d, METRE_PER_SECOND))),
    new TimeBasedValue<>(
    UUID.fromString("cb7da21b-59af-4579-9352-2aa6b3020627"),
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new WindValue(Quantities.getQuantity(10d, DEGREE_GEOM), Quantities.getQuantity(15d, METRE_PER_SECOND))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualWindTimeSeriesProcessed = [
    [
      "uuid" 		: "3453d88d-50f6-4124-b2d0-807a9b7dbf54",
      "direction"	: "5.0",
      "time"		: "2020-04-02T10:00Z[UTC]",
      "velocity"	: "10.0"
    ] as LinkedHashMap,
    [
      "uuid" 		: "870e8e22-5667-4681-96ad-5ab6ac9cf25b",
      "direction"	: "15.0",
      "time"		: "2020-04-02T10:15Z[UTC]",
      "velocity"	: "20.0"
    ] as LinkedHashMap,
    [
      "uuid" 		: "cb7da21b-59af-4579-9352-2aa6b3020627",
      "direction"	: "10.0",
      "time"		: "2020-04-02T10:30Z[UTC]",
      "velocity"	: "15.0"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<SolarIrradianceValue> individualIrradianceTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("fa7fd93b-3d83-4cf6-83d0-85eb1853dcfa"),
  [
    new TimeBasedValue<>(
    UUID.fromString("e397cf20-43ae-4601-a6cd-0ee85c63cec3"),
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new SolarIrradianceValue(Quantities.getQuantity(5d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE))),
    new TimeBasedValue<>(
    UUID.fromString("94400577-83ac-4dd5-818d-8d62edcd4ee2"),
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new SolarIrradianceValue(Quantities.getQuantity(15d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(20d, StandardUnits.SOLAR_IRRADIANCE))),
    new TimeBasedValue<>(
    UUID.fromString("d7523ef9-f8d7-449f-834f-7b92bf51fd9e"),
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new SolarIrradianceValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(15d, StandardUnits.SOLAR_IRRADIANCE))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>> individualIrradianceTimeSeriesProcessed = [
    [
      "uuid" 					: "e397cf20-43ae-4601-a6cd-0ee85c63cec3",
      "directIrradiance"		: "5.0",
      "diffuseIrradiance"	: "10.0",
      "time"					: "2020-04-02T10:00Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 					: "94400577-83ac-4dd5-818d-8d62edcd4ee2",
      "directIrradiance"		: "15.0",
      "diffuseIrradiance"	: "20.0",
      "time"					: "2020-04-02T10:15Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 					: "d7523ef9-f8d7-449f-834f-7b92bf51fd9e",
      "directIrradiance"		: "10.0",
      "diffuseIrradiance"	: "15.0",
      "time"					: "2020-04-02T10:30Z[UTC]"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<WeatherValue> individualWeatherTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("4fcbdfcd-4ff0-46dd-b0df-f3af7ae3ed98"),
  [
    new TimeBasedValue<>(
    UUID.fromString("edb872a0-7421-4283-b072-91b9a729dabf"),
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new WeatherValue(
    defaultLocation,
    new SolarIrradianceValue(Quantities.getQuantity(5d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE)),
    new TemperatureValue(Quantities.getQuantity(5d, CELSIUS)),
    new WindValue(Quantities.getQuantity(5d, DEGREE_GEOM), Quantities.getQuantity(10d, METRE_PER_SECOND))
    )
    ),
    new TimeBasedValue<>(
    UUID.fromString("b264057c-bc38-4f49-ab27-c7dc5dd51b4c"),
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new WeatherValue(
    defaultLocation,
    new SolarIrradianceValue(Quantities.getQuantity(15d, StandardUnits.SOLAR_IRRADIANCE), Quantities.getQuantity(20d, StandardUnits.SOLAR_IRRADIANCE)),
    new TemperatureValue(Quantities.getQuantity(15d, CELSIUS)),
    new WindValue(Quantities.getQuantity(15d, DEGREE_GEOM), Quantities.getQuantity(20d, METRE_PER_SECOND))
    )
    ),
    new TimeBasedValue<>(
    UUID.fromString("79eff66e-a910-4ba8-b2c6-ac622bef55b3"),
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
      "uuid" 					: "edb872a0-7421-4283-b072-91b9a729dabf",
      "coordinate"			: "{\"type\":\"Point\",\"coordinates\":[7.412152,51.492758],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
      "diffuseIrradiance"	: "10.0",
      "directIrradiance"		: "5.0",
      "direction"				: "5.0",
      "temperature"			: "5.0",
      "time"					: "2020-04-02T10:00Z[UTC]",
      "velocity"				: "10.0"
    ] as LinkedHashMap,
    [
      "uuid" 					: "b264057c-bc38-4f49-ab27-c7dc5dd51b4c",
      "coordinate"			: "{\"type\":\"Point\",\"coordinates\":[7.412152,51.492758],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
      "diffuseIrradiance"	: "20.0",
      "directIrradiance"		: "15.0",
      "direction"				: "15.0",
      "temperature"			: "15.0",
      "time"					: "2020-04-02T10:15Z[UTC]",
      "velocity"				: "20.0"
    ] as LinkedHashMap,
    [
      "uuid" 					: "79eff66e-a910-4ba8-b2c6-ac622bef55b3",
      "coordinate"			: "{\"type\":\"Point\",\"coordinates\":[7.412152,51.492758],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
      "diffuseIrradiance"	: "15.0",
      "directIrradiance"		: "10.0",
      "direction"				: "10.0",
      "temperature"			: "10.0",
      "time"					: "2020-04-02T10:30Z[UTC]",
      "velocity"				: "15.0"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<HeatDemandValue> individualHeatDemandTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("3c0ebc06-9bd7-44ea-a347-0c52d3dec854"),
  [
    new TimeBasedValue<>(
    UUID.fromString("89ae0ccd-04fd-47a7-a49f-a882cab66784"),
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new HeatDemandValue(Quantities.getQuantity(5d, KILOWATT))),
    new TimeBasedValue<>(
    UUID.fromString("56e4295b-740a-45dd-9a0d-f5ec8b3bec13"),
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new HeatDemandValue(Quantities.getQuantity(15d, KILOWATT))),
    new TimeBasedValue<>(
    UUID.fromString("996a0ffa-548b-4f5e-825a-25b1452bc9c0"),
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new HeatDemandValue(Quantities.getQuantity(10d, KILOWATT))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualHeatDemandTimeSeriesProcessed = [
    [
      "uuid" 			: "89ae0ccd-04fd-47a7-a49f-a882cab66784",
      "heatDemand"	: "5.0",
      "time"			: "2020-04-02T10:00Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 			: "56e4295b-740a-45dd-9a0d-f5ec8b3bec13",
      "heatDemand"	: "15.0",
      "time"			: "2020-04-02T10:15Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 			: "996a0ffa-548b-4f5e-825a-25b1452bc9c0",
      "heatDemand"	: "10.0",
      "time"			: "2020-04-02T10:30Z[UTC]"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<PValue> individualPTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("b3d93b08-4985-41a6-b063-00f934a10b28"),
  [
    new TimeBasedValue<>(
    UUID.fromString("cb3c12e9-7b54-4066-8e51-30aed8ea05ff"),
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new PValue(Quantities.getQuantity(5d, KILOWATT))),
    new TimeBasedValue<>(
    UUID.fromString("07937986-c7b6-48b8-852d-8579a4de0f3f"),
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new PValue(Quantities.getQuantity(15d, KILOWATT))),
    new TimeBasedValue<>(
    UUID.fromString("43fcb651-94ff-4491-9994-5ce5980b51f8"),
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new PValue(Quantities.getQuantity(10d, KILOWATT))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualPTimeSeriesProcessed = [
    [
      "uuid" 	: "cb3c12e9-7b54-4066-8e51-30aed8ea05ff",
      "p"		: "5.0",
      "time"	: "2020-04-02T10:00Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 	: "07937986-c7b6-48b8-852d-8579a4de0f3f",
      "p"		: "15.0",
      "time"	: "2020-04-02T10:15Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 	: "43fcb651-94ff-4491-9994-5ce5980b51f8",
      "p"		: "10.0",
      "time"	: "2020-04-02T10:30Z[UTC]"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<HeatAndPValue> individualHeatAndPTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("56c20b88-c001-4225-8dac-cd13a75c6b48"),
  [
    new TimeBasedValue<>(
    UUID.fromString("d0dd9b16-6561-45cd-989f-2f9f2d623285"),
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new HeatAndPValue(Quantities.getQuantity(5d, KILOWATT), Quantities.getQuantity(10d, KILOWATT))),
    new TimeBasedValue<>(
    UUID.fromString("76cdb572-db19-4731-a51d-f88d60ac23bf"),
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new HeatAndPValue(Quantities.getQuantity(15d, KILOWATT), Quantities.getQuantity(20d, KILOWATT))),
    new TimeBasedValue<>(
    UUID.fromString("85d34be8-9672-4382-bb28-6c526e061979"),
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new HeatAndPValue(Quantities.getQuantity(10d, KILOWATT), Quantities.getQuantity(15d, KILOWATT))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualHeatAndPTimeSeriesProcessed = [
    [
      "uuid" 			: "d0dd9b16-6561-45cd-989f-2f9f2d623285",
      "heatDemand"	: "10.0",
      "p"				: "5.0",
      "time"			: "2020-04-02T10:00Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 			: "76cdb572-db19-4731-a51d-f88d60ac23bf",
      "heatDemand"	: "20.0",
      "p"				: "15.0",
      "time"			: "2020-04-02T10:15Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 			: "85d34be8-9672-4382-bb28-6c526e061979",
      "heatDemand"	: "15.0",
      "p"				: "10.0",
      "time"			: "2020-04-02T10:30Z[UTC]"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<SValue> individualSTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("7d085fc9-be29-4218-b768-00f885be066b"),
  [
    new TimeBasedValue<>(
    UUID.fromString("1db6d265-40b3-4c02-bee9-ffc74574af65"),
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new SValue(Quantities.getQuantity(5d, KILOWATT), Quantities.getQuantity(10d, KILOWATT))),
    new TimeBasedValue<>(
    UUID.fromString("c4fe02c4-1a11-4975-8641-7c3daf452475"),
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new SValue(Quantities.getQuantity(15d, KILOWATT), Quantities.getQuantity(20d, KILOWATT))),
    new TimeBasedValue<>(
    UUID.fromString("90082474-af4c-44ea-8b38-f7c6fb48907c"),
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new SValue(Quantities.getQuantity(10d, KILOWATT), Quantities.getQuantity(15d, KILOWATT))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualSTimeSeriesProcessed = [
    [
      "uuid"	: "1db6d265-40b3-4c02-bee9-ffc74574af65",
      "p"		: "5.0",
      "q"		: "10.0",
      "time"	: "2020-04-02T10:00Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 	: "c4fe02c4-1a11-4975-8641-7c3daf452475",
      "p"		: "15.0",
      "q"		: "20.0",
      "time"	: "2020-04-02T10:15Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 	: "90082474-af4c-44ea-8b38-f7c6fb48907c",
      "p"		: "10.0",
      "q"		: "15.0",
      "time"	: "2020-04-02T10:30Z[UTC]"
    ] as LinkedHashMap
  ] as Set

  IndividualTimeSeries<HeatAndSValue> individualHeatAndSTimeSeries =  new IndividualTimeSeries<>(
  UUID.fromString("83b577cc-06b1-47a1-bfff-ad648a00784b"),
  [
    new TimeBasedValue<>(
    UUID.fromString("79a9e03e-2645-410b-9de4-def4795e7d77"),
    ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
    new HeatAndSValue(Quantities.getQuantity(5d, KILOWATT), Quantities.getQuantity(10d, KILOWATT), Quantities.getQuantity(15d, KILOWATT))),
    new TimeBasedValue<>(
    UUID.fromString("adb6a248-c57f-4ca5-9feb-d0ca3296f0c7"),
    ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
    new HeatAndSValue(Quantities.getQuantity(15d, KILOWATT), Quantities.getQuantity(20d, KILOWATT), Quantities.getQuantity(25d, KILOWATT))),
    new TimeBasedValue<>(
    UUID.fromString("07feb8fa-1ee0-4a40-bca0-cf831db0b745"),
    ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
    new HeatAndSValue(Quantities.getQuantity(10d, KILOWATT), Quantities.getQuantity(15d, KILOWATT), Quantities.getQuantity(20d, KILOWATT))),
  ] as Set
  )

  Set<LinkedHashMap<String, String>>  individualHeatAndSTimeSeriesProcessed = [
    [
      "uuid"			: "79a9e03e-2645-410b-9de4-def4795e7d77",
      "heatDemand"	: "15.0",
      "p"				: "5.0",
      "q"				: "10.0",
      "time"			: "2020-04-02T10:00Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 			: "adb6a248-c57f-4ca5-9feb-d0ca3296f0c7",
      "heatDemand"	: "25.0",
      "p"				: "15.0",
      "q"				: "20.0",
      "time"			: "2020-04-02T10:15Z[UTC]"
    ] as LinkedHashMap,
    [
      "uuid" 			: "07feb8fa-1ee0-4a40-bca0-cf831db0b745",
      "heatDemand"	: "20.0",
      "p"				: "10.0",
      "q"				: "15.0",
      "time"			: "2020-04-02T10:30Z[UTC]"
    ] as LinkedHashMap
  ] as Set

  LoadProfileInput loadProfileInput =  new LoadProfileInput(
  UUID.fromString("b56853fe-b800-4c18-b324-db1878b22a28"),
  BdewStandardLoadProfile.G2,
  [
    new LoadProfileEntry(
    UUID.fromString("587b71d8-84ac-4dc1-a30a-aff82d4d6d25"),
    new PValue(Quantities.getQuantity(5d, KILOWATT)),
    DayOfWeek.MONDAY,
    0
    ),
    new LoadProfileEntry(
    UUID.fromString("90403b41-bfe9-4264-9fe9-2bf9ed7b9e61"),
    new PValue(Quantities.getQuantity(15d, KILOWATT)),
    DayOfWeek.MONDAY,
    1
    ),
    new LoadProfileEntry(
    UUID.fromString("2c8fdecd-7527-40f2-a64e-08944f1ff568"),
    new PValue(Quantities.getQuantity(10d, KILOWATT)),
    DayOfWeek.MONDAY,
    2
    )
  ] as Set
  )

  Set<LinkedHashMap<String, String>> loadProfileInputProcessed = [
    [
      "uuid"				: "587b71d8-84ac-4dc1-a30a-aff82d4d6d25",
      "dayOfWeek"			: "MONDAY",
      "p"					: "5.0",
      "quarterHourOfDay"	: "0"
    ] as LinkedHashMap,
    [
      "uuid"				: "90403b41-bfe9-4264-9fe9-2bf9ed7b9e61",
      "dayOfWeek"			: "MONDAY",
      "p"					: "15.0",
      "quarterHourOfDay"	: "1"
    ] as LinkedHashMap,
    [
      "uuid"				: "2c8fdecd-7527-40f2-a64e-08944f1ff568",
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