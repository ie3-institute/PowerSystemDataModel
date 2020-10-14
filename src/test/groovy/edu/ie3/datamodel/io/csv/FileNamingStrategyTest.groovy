/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.csv

import edu.ie3.datamodel.models.BdewLoadProfile
import edu.ie3.datamodel.models.input.MeasurementUnitInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.RandomLoadParameters
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput
import edu.ie3.datamodel.models.input.system.*
import edu.ie3.datamodel.models.input.system.characteristic.EvCharacteristicInput
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.datamodel.models.input.system.type.*
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.connector.SwitchResult
import edu.ie3.datamodel.models.result.connector.Transformer2WResult
import edu.ie3.datamodel.models.result.connector.Transformer3WResult
import edu.ie3.datamodel.models.result.system.*
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult
import edu.ie3.datamodel.models.timeseries.IntValue
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput
import edu.ie3.datamodel.models.timeseries.repetitive.RepetitiveTimeSeries
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.file.Paths
import java.time.ZonedDateTime
import java.util.regex.Pattern

class FileNamingStrategyTest extends Specification {

	def "The uuid pattern actually matches a valid uuid"() {
		given:
		def pattern = Pattern.compile(FileNamingStrategy.UUID_STRING)
		def uuidString = UUID.randomUUID().toString()

		when:
		def matcher = pattern.matcher(uuidString)

		then:
		matcher.matches()
	}

	def "The pattern for an individual time series file name actually matches a valid file name and extracts the correct groups"() {
		given:
		def fns = new FileNamingStrategy()
		def validFileName = "its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276"

		when:
		def matcher = fns.individualTimeSeriesPattern.matcher(validFileName)

		then: "the pattern matches"
		matcher.matches()

		then: "it also has correct capturing groups"
		matcher.groupCount() == 2
		matcher.group(1) == "c"
		matcher.group("columnScheme") == "c"
		matcher.group(2) == "4881fda2-bcee-4f4f-a5bb-6a09bf785276"
		matcher.group("uuid") == "4881fda2-bcee-4f4f-a5bb-6a09bf785276"
	}

	def "The pattern for a repetitive load profile time series file name actually matches a valid file name and extracts the correct groups"() {
		given:
		def fns = new FileNamingStrategy()
		def validFileName = "lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304"

		when:
		def matcher = fns.loadProfileTimeSeriesPattern.matcher(validFileName)

		then: "the pattern matches"
		matcher.matches()

		then: "it also has correct capturing groups"
		matcher.groupCount() == 2
		matcher.group(1) == "g3"
		matcher.group(2) == "bee0a8b6-4788-4f18-bf72-be52035f7304"
		matcher.group("profile") == "g3"
		matcher.group("uuid") == "bee0a8b6-4788-4f18-bf72-be52035f7304"
	}

	def "Trying to extract time series meta information throws an Exception, if it is provided a malformed string"() {
		given:
		def fns = new FileNamingStrategy()
		def path = Paths.get("/bla/foo")

		when:
		fns.extractTimeSeriesMetaInformation(path)

		then:
		def ex = thrown(IllegalArgumentException)
		ex.message == "Unknown format of 'foo'. Cannot extract meta information."
	}

	def "Trying to extract individual time series meta information throws an Exception, if it is provided a malformed string"() {
		given:
		def fns = new FileNamingStrategy()
		def fileName = "foo"

		when:
		fns.extractIndividualTimesSeriesMetaInformation(fileName)

		then:
		def ex = thrown(IllegalArgumentException)
		ex.message == "Cannot extract meta information on individual time series from 'foo'."
	}

	def "Trying to extract load profile time series meta information throws an Exception, if it is provided a malformed string"() {
		given:
		def fns = new FileNamingStrategy()
		def fileName = "foo"

		when:
		fns.extractLoadProfileTimesSeriesMetaInformation(fileName)

		then:
		def ex = thrown(IllegalArgumentException)
		ex.message == "Cannot extract meta information on load profile time series from 'foo'."
	}

	def "An unknown column scheme gets not parsed"() {
		given:
		def invalidColumnScheme = "what's this"

		when:
		def actual = FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.parse(invalidColumnScheme)

		then:
		!actual.present
	}

	def "The FileNamingStrategy extracts correct meta information from a valid individual time series file name"() {
		given:
		def fns = new FileNamingStrategy()
		def path = Paths.get(pathString)

		when:
		def metaInformation = fns.extractTimeSeriesMetaInformation(path)

		then:
		FileNamingStrategy.IndividualTimeSeriesMetaInformation.isAssignableFrom(metaInformation.getClass())
		(metaInformation as FileNamingStrategy.IndividualTimeSeriesMetaInformation).with {
			assert it.uuid == UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276")
			assert it.columnScheme == expectedColumnScheme
		}

		where:
		pathString || expectedColumnScheme
		"/bla/foo/its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.ENERGY_PRICE
		"/bla/foo/its_p_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.ACTIVE_POWER
		"/bla/foo/its_pq_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.APPARENT_POWER
		"/bla/foo/its_h_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.HEAT_DEMAND
		"/bla/foo/its_ph_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND
		"/bla/foo/its_pqh_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND
		"/bla/foo/its_weather_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.WEATHER
	}

	def "The FileNamingStrategy extracts correct meta information from a valid individual time series file name with pre- and suffix"() {
		given:
		def fns = new FileNamingStrategy("prefix", "suffix")
		def path = Paths.get(pathString)

		when:
		def metaInformation = fns.extractTimeSeriesMetaInformation(path)

		then:
		FileNamingStrategy.IndividualTimeSeriesMetaInformation.isAssignableFrom(metaInformation.getClass())
		(metaInformation as FileNamingStrategy.IndividualTimeSeriesMetaInformation).with {
			assert it.uuid == UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276")
			assert it.columnScheme == expectedColumnScheme
		}

		where:
		pathString || expectedColumnScheme
		"/bla/foo/prefix_its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.ENERGY_PRICE
		"/bla/foo/prefix_its_p_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.ACTIVE_POWER
		"/bla/foo/prefix_its_pq_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.APPARENT_POWER
		"/bla/foo/prefix_its_h_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.HEAT_DEMAND
		"/bla/foo/prefix_its_ph_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND
		"/bla/foo/prefix_its_pqh_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND
		"/bla/foo/prefix_its_weather_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv" || FileNamingStrategy.IndividualTimeSeriesMetaInformation.ColumnScheme.WEATHER
	}

	def "The FileNamingStrategy throw an IllegalArgumentException, if the column scheme is malformed."() {
		given:
		def fns = new FileNamingStrategy()
		def path = Paths.get("/bla/foo/its_whoops_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv")

		when:
		fns.extractTimeSeriesMetaInformation(path)

		then:
		def ex = thrown(IllegalArgumentException)
		ex.message == "Cannot parse 'whoops' to valid column scheme."
	}

	def "The FileNamingStrategy extracts correct meta information from a valid load profile time series file name"() {
		given:
		def fns = new FileNamingStrategy()
		def path = Paths.get("/bla/foo/lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304.csv")

		when:
		def metaInformation = fns.extractTimeSeriesMetaInformation(path)

		then:
		FileNamingStrategy.LoadProfileTimeSeriesMetaInformation.isAssignableFrom(metaInformation.getClass())
		(metaInformation as FileNamingStrategy.LoadProfileTimeSeriesMetaInformation).with {
			assert uuid == UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304")
			assert profile == "g3"
		}
	}

	def "The FileNamingStrategy extracts correct meta information from a valid load profile time series file name with pre- and suffix"() {
		given:
		def fns = new FileNamingStrategy("prefix", "suffix")
		def path = Paths.get("/bla/foo/prefix_lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304_suffix.csv")

		when:
		def metaInformation = fns.extractTimeSeriesMetaInformation(path)

		then:
		FileNamingStrategy.LoadProfileTimeSeriesMetaInformation.isAssignableFrom(metaInformation.getClass())
		(metaInformation as FileNamingStrategy.LoadProfileTimeSeriesMetaInformation).with {
			assert uuid == UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304")
			assert profile == "g3"
		}
	}

	def "The FileNamingStrategy is able to prepare the prefix properly"() {
		when:
		String actual = FileNamingStrategy.preparePrefix(prefix)

		then:
		actual == expected

		where:
		prefix 		|| expected
		"abc123" 	|| "abc123_"
		"aBc123" 	|| "abc123_"
		"ABC123" 	|| "abc123_"
		"abc123_" 	|| "abc123_"
		"aBc123_"	|| "abc123_"
		"ABC123_" 	|| "abc123_"
	}

	def "The FileNamingStrategy is able to prepare the suffix properly"() {
		when:
		String actual = FileNamingStrategy.prepareSuffix(prefix)

		then:
		actual == suffix

		where:
		prefix || suffix
		"abc123" || "_abc123"
		"aBc123" || "_abc123"
		"ABC123" || "_abc123"
		"_abc123" || "_abc123"
		"_aBc123" || "_abc123"
		"_ABC123" || "_abc123"
	}

	def "A FileNamingStrategy should recognize if empty strings are passed in the prefix/suffix constructor and don't add underlines then"() {
		given: "a file naming strategy"
		FileNamingStrategy strategy = new FileNamingStrategy("", "")

		expect:
		strategy.prefix == ""
		strategy.suffix == ""
	}

	def "A FileNamingStrategy should correctly append and prepend underscores"() {
		given: "a file naming strategy"
		FileNamingStrategy strategy = new FileNamingStrategy("bla", "foo")

		expect:
		strategy.prefix == "bla_"
		strategy.suffix == "_foo"
	}

	def "A FileNamingStrategy should correctly append underscore, when only prefix is set"() {
		given: "a file naming strategy"
		FileNamingStrategy strategy = new FileNamingStrategy("bla")

		expect:
		strategy.prefix == "bla_"
		strategy.suffix == ""
	}

	def "A FileNamingStrategy should return an empty optional on a invalid class"() {
		given: "a file naming strategy"
		FileNamingStrategy strategy = new FileNamingStrategy()

		when:
		Optional<String> res = strategy.getFileName(String)

		then:
		!res.present
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for all result models"() {
		given: "a file naming strategy without pre- or suffixes"
		FileNamingStrategy strategy = new FileNamingStrategy()

		when:
		Optional<String> res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass               || expectedString
		LoadResult               || "load_res"
		FixedFeedInResult        || "fixedfeedin_res"
		BmResult                 || "bm_res"
		PvResult                 || "pv_res"
		ChpResult                || "chp_res"
		WecResult                || "wec_res"
		StorageResult            || "storage_res"
		EvcsResult               || "evcs_res"
		EvResult                 || "ev_res"
		Transformer2WResult      || "transformer2w_res"
		Transformer3WResult      || "transformer3w_res"
		LineResult               || "line_res"
		SwitchResult             || "switch_res"
		NodeResult               || "node_res"
		CylindricalStorageResult || "cylindricalstorage_res"
		ThermalHouseResult       || "thermalhouse_res"
	}

	def "A FileNamingStrategy with pre- and suffixes should return valid strings for all result models"() {
		given: "a file naming strategy with pre- or suffixes"
		FileNamingStrategy strategy = new FileNamingStrategy("prefix", "suffix")

		when:
		Optional<String> res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass               || expectedString
		LoadResult               || "prefix_load_res_suffix"
		FixedFeedInResult        || "prefix_fixedfeedin_res_suffix"
		BmResult                 || "prefix_bm_res_suffix"
		PvResult                 || "prefix_pv_res_suffix"
		ChpResult                || "prefix_chp_res_suffix"
		WecResult                || "prefix_wec_res_suffix"
		StorageResult            || "prefix_storage_res_suffix"
		EvcsResult               || "prefix_evcs_res_suffix"
		EvResult                 || "prefix_ev_res_suffix"
		Transformer2WResult      || "prefix_transformer2w_res_suffix"
		Transformer3WResult      || "prefix_transformer3w_res_suffix"
		LineResult               || "prefix_line_res_suffix"
		SwitchResult             || "prefix_switch_res_suffix"
		NodeResult               || "prefix_node_res_suffix"
		CylindricalStorageResult || "prefix_cylindricalstorage_res_suffix"
		ThermalHouseResult       || "prefix_thermalhouse_res_suffix"
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for all input assets models"() {
		given: "a file naming strategy without pre- or suffixes"
		FileNamingStrategy strategy = new FileNamingStrategy()

		when:
		Optional<String> res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass              || expectedString
		FixedFeedInInput        || "fixed_feed_in_input"
		PvInput                 || "pv_input"
		WecInput                || "wec_input"
		ChpInput                || "chp_input"
		BmInput                 || "bm_input"
		EvInput                 || "ev_input"
		LoadInput               || "load_input"
		StorageInput            || "storage_input"
		HpInput                 || "hp_input"
		LineInput               || "line_input"
		SwitchInput             || "switch_input"
		NodeInput               || "node_input"
		MeasurementUnitInput    || "measurement_unit_input"
		EvcsInput               || "evcs_input"
		Transformer2WInput      || "transformer2w_input"
		Transformer3WInput      || "transformer3w_input"
		CylindricalStorageInput || "cylindrical_storage_input"
		ThermalHouseInput       || "thermal_house_input"
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for all asset characteristics models"() {
		given: "a file naming strategy without pre- or suffixes"
		FileNamingStrategy strategy = new FileNamingStrategy()

		when:
		Optional<String> res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass             || expectedString
		WecCharacteristicInput || "wec_characteristic_input"
		EvCharacteristicInput  || "ev_characteristic_input"
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for all input types models"() {
		given: "a file naming strategy without pre- or suffixes"
		FileNamingStrategy strategy = new FileNamingStrategy()

		when:
		Optional<String> res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass             || expectedString
		BmTypeInput            || "bm_type_input"
		ChpTypeInput           || "chp_type_input"
		EvTypeInput            || "ev_type_input"
		HpTypeInput            || "hp_type_input"
		LineTypeInput          || "line_type_input"
		StorageTypeInput       || "storage_type_input"
		Transformer2WTypeInput || "transformer2w_type_input"
		Transformer3WTypeInput || "transformer3w_type_input"
		WecTypeInput           || "wec_type_input"
		WecTypeInput           || "wec_type_input"
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for a Load Parameter Model"() {
		given: "a file naming strategy without pre- or suffixes"
		FileNamingStrategy strategy = new FileNamingStrategy()

		when:
		Optional<String> res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass           || expectedString
		RandomLoadParameters || "random_load_parameters_input"
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for a graphic input Model"() {
		given: "a file naming strategy without pre- or suffixes"
		FileNamingStrategy strategy = new FileNamingStrategy()

		when:
		Optional<String> res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass       || expectedString
		NodeGraphicInput || "node_graphic_input"
		LineGraphicInput || "line_graphic_input"
	}

	def "A FileNamingStrategy without pre- or suffix should return empty Optional, if the content of the time series is not covered"() {
		given:
		FileNamingStrategy strategy = new FileNamingStrategy()
		def entries = [
			new TimeBasedValue(ZonedDateTime.now(), new IntValue(5))
		] as SortedSet
		IndividualTimeSeries timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> UUID.randomUUID()
		timeSeries.entries >> entries

		when:
		Optional<String> actual = strategy.getFileName(timeSeries)

		then:
		!actual.present
	}

	def "A FileNamingStrategy without pre- or suffix should return empty Optional, if the time series is empty"() {
		given:
		FileNamingStrategy strategy = new FileNamingStrategy()
		def entries = [] as SortedSet
		IndividualTimeSeries timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> UUID.randomUUID()
		timeSeries.entries >> entries

		when:
		Optional<String> actual = strategy.getFileName(timeSeries)

		then:
		!actual.present
	}

	def "A FileNamingStrategy without pre- or suffix should return valid file name for individual time series" () {
		given:
		FileNamingStrategy strategy = new FileNamingStrategy()
		def entries = [
			new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(500d, PowerSystemUnits.EURO_PER_MEGAWATTHOUR)))] as SortedSet
		IndividualTimeSeries timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> uuid
		timeSeries.entries >> entries

		when:
		Optional<String> actual = strategy.getFileName(timeSeries)

		then:
		actual.present
		actual.get() == expectedFileName

		where:
		clazz                || uuid 													|| expectedFileName
		IndividualTimeSeries || UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || "its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276"
	}

	def "A FileNamingStrategy with pre- or suffix should return valid file name for individual time series" () {
		given:
		FileNamingStrategy strategy = new FileNamingStrategy("aa", "zz")
		def entries = [] as SortedSet
		entries.add(new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(500d, PowerSystemUnits.EURO_PER_MEGAWATTHOUR))))
		IndividualTimeSeries timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> uuid
		timeSeries.entries >> entries

		when:
		Optional<String> actual = strategy.getFileName(timeSeries)

		then:
		actual.present
		actual.get() == expectedFileName

		where:
		clazz                || uuid 													|| expectedFileName
		IndividualTimeSeries || UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || "aa_its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276_zz"
	}

	def "A FileNamingStrategy without pre- or suffix should return valid file name for load profile input" () {
		given:
		FileNamingStrategy strategy = new FileNamingStrategy()
		LoadProfileInput timeSeries = Mock(LoadProfileInput)
		timeSeries.uuid >> uuid
		timeSeries.getType() >> type

		when:
		Optional<String> actual = strategy.getFileName(timeSeries)

		then:
		actual.present
		actual.get() == expectedFileName

		where:
		clazz                || uuid 													|| type 				|| expectedFileName
		LoadProfileInput     || UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304") || BdewLoadProfile.G3 	|| "lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304"
	}

	def "A FileNamingStrategy returns empty Optional, when there is no naming defined for a given time series class"() {
		given:
		FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()
		RepetitiveTimeSeries timeSeries = Mock(RepetitiveTimeSeries)

		when:
		Optional<String> fileName = fileNamingStrategy.getFileName(timeSeries)

		then:
		!fileName.present
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for time series mapping"() {
		given: "a file naming strategy without pre- or suffixes"
		FileNamingStrategy strategy = new FileNamingStrategy()

		when:
		Optional<String> res = strategy.getFileName(TimeSeriesMapping.Entry)

		then:
		res.present
		res.get() == "time_series_mapping"
	}

	def "A FileNamingStrategy with pre- and suffix should return valid strings for time series mapping"() {
		given: "a file naming strategy without pre- or suffixes"
		FileNamingStrategy strategy = new FileNamingStrategy("prefix", "suffix")

		when:
		Optional<String> res = strategy.getFileName(TimeSeriesMapping.Entry)

		then:
		res.present
		res.get() == "prefix_time_series_mapping_suffix"
	}
}
