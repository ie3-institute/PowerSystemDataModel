/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.naming

import edu.ie3.datamodel.io.csv.DefaultDirectoryHierarchy
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource
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
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput
import edu.ie3.datamodel.models.timeseries.repetitive.RepetitiveTimeSeries
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.file.Files
import java.time.ZonedDateTime

class HierarchicFileNamingStrategyTest extends Specification {
	@Shared
	DefaultDirectoryHierarchy defaultHierarchy

	def setup() {
		def tmpPath = Files.createTempDirectory("psdm_hierarchic_file_naming_strategy")
		defaultHierarchy = new DefaultDirectoryHierarchy(tmpPath.toString(), "test_grid")
	}

	def "A HierarchicFileNamingStrategy should return an empty optional on a invalid class"() {
		given: "a naming strategy"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getEntityName(String)

		then:
		!res.present
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid directory paths for all result models"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getDirectoryPath(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass               || expectedString
		LoadResult               || "test_grid" + File.separator + "results" + File.separator + "participants"
		FixedFeedInResult        || "test_grid" + File.separator + "results" + File.separator + "participants"
		BmResult                 || "test_grid" + File.separator + "results" + File.separator + "participants"
		PvResult                 || "test_grid" + File.separator + "results" + File.separator + "participants"
		ChpResult                || "test_grid" + File.separator + "results" + File.separator + "participants"
		WecResult                || "test_grid" + File.separator + "results" + File.separator + "participants"
		StorageResult            || "test_grid" + File.separator + "results" + File.separator + "participants"
		EvcsResult               || "test_grid" + File.separator + "results" + File.separator + "participants"
		EvResult                 || "test_grid" + File.separator + "results" + File.separator + "participants"
		Transformer2WResult      || "test_grid" + File.separator + "results" + File.separator + "grid"
		Transformer3WResult      || "test_grid" + File.separator + "results" + File.separator + "grid"
		LineResult               || "test_grid" + File.separator + "results" + File.separator + "grid"
		SwitchResult             || "test_grid" + File.separator + "results" + File.separator + "grid"
		NodeResult               || "test_grid" + File.separator + "results" + File.separator + "grid"
		CylindricalStorageResult || "test_grid" + File.separator + "results" + File.separator + "thermal"
		ThermalHouseResult       || "test_grid" + File.separator + "results" + File.separator + "thermal"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid directory paths for all input assets models"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getDirectoryPath(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass              || expectedString
		FixedFeedInInput        || "test_grid" + File.separator + "input" + File.separator + "participants"
		PvInput                 || "test_grid" + File.separator + "input" + File.separator + "participants"
		WecInput                || "test_grid" + File.separator + "input" + File.separator + "participants"
		ChpInput                || "test_grid" + File.separator + "input" + File.separator + "participants"
		BmInput                 || "test_grid" + File.separator + "input" + File.separator + "participants"
		EvInput                 || "test_grid" + File.separator + "input" + File.separator + "participants"
		LoadInput               || "test_grid" + File.separator + "input" + File.separator + "participants"
		StorageInput            || "test_grid" + File.separator + "input" + File.separator + "participants"
		HpInput                 || "test_grid" + File.separator + "input" + File.separator + "participants"
		LineInput               || "test_grid" + File.separator + "input" + File.separator + "grid"
		SwitchInput             || "test_grid" + File.separator + "input" + File.separator + "grid"
		NodeInput               || "test_grid" + File.separator + "input" + File.separator + "grid"
		MeasurementUnitInput    || "test_grid" + File.separator + "input" + File.separator + "grid"
		EvcsInput               || "test_grid" + File.separator + "input" + File.separator + "participants"
		Transformer2WInput      || "test_grid" + File.separator + "input" + File.separator + "grid"
		Transformer3WInput      || "test_grid" + File.separator + "input" + File.separator + "grid"
		CylindricalStorageInput || "test_grid" + File.separator + "input" + File.separator + "thermal"
		ThermalHouseInput       || "test_grid" + File.separator + "input" + File.separator + "thermal"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid file paths for all system input assets models"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFilePath(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass              || expectedString
		FixedFeedInInput        || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "fixed_feed_in_input"
		PvInput                 || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "pv_input"
		WecInput                || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "wec_input"
		ChpInput                || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "chp_input"
		BmInput                 || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "bm_input"
		EvInput                 || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "ev_input"
		LoadInput               || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "load_input"
		StorageInput            || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "storage_input"
		HpInput                 || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "hp_input"
		EvcsInput               || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "evcs_input"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid file paths for all other input assets models"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFilePath(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass              || expectedString
		LineInput               || "test_grid" + File.separator + "input" + File.separator + "grid" + File.separator + "line_input"
		SwitchInput             || "test_grid" + File.separator + "input" + File.separator + "grid" + File.separator + "switch_input"
		NodeInput               || "test_grid" + File.separator + "input" + File.separator + "grid" + File.separator + "node_input"
		MeasurementUnitInput    || "test_grid" + File.separator + "input" + File.separator + "grid" + File.separator + "measurement_unit_input"
		Transformer2WInput      || "test_grid" + File.separator + "input" + File.separator + "grid" + File.separator + "transformer_2_w_input"
		Transformer3WInput      || "test_grid" + File.separator + "input" + File.separator + "grid" + File.separator + "transformer_3_w_input"
		CylindricalStorageInput || "test_grid" + File.separator + "input" + File.separator + "thermal" + File.separator + "cylindrical_storage_input"
		ThermalHouseInput       || "test_grid" + File.separator + "input" + File.separator + "thermal" + File.separator + "thermal_house_input"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid directory paths for all input types models"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getDirectoryPath(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass             || expectedString
		BmTypeInput            || "test_grid" + File.separator + "input" + File.separator + "global"
		ChpTypeInput           || "test_grid" + File.separator + "input" + File.separator + "global"
		EvTypeInput            || "test_grid" + File.separator + "input" + File.separator + "global"
		HpTypeInput            || "test_grid" + File.separator + "input" + File.separator + "global"
		LineTypeInput          || "test_grid" + File.separator + "input" + File.separator + "global"
		StorageTypeInput       || "test_grid" + File.separator + "input" + File.separator + "global"
		Transformer2WTypeInput || "test_grid" + File.separator + "input" + File.separator + "global"
		Transformer3WTypeInput || "test_grid" + File.separator + "input" + File.separator + "global"
		WecTypeInput           || "test_grid" + File.separator + "input" + File.separator + "global"
		WecTypeInput           || "test_grid" + File.separator + "input" + File.separator + "global"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid file paths for all input types models"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFilePath(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass             || expectedString
		BmTypeInput            || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "bm_type_input"
		ChpTypeInput           || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "chp_type_input"
		EvTypeInput            || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "ev_type_input"
		HpTypeInput            || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "hp_type_input"
		LineTypeInput          || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "line_type_input"
		StorageTypeInput       || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "storage_type_input"
		Transformer2WTypeInput || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "transformer_2_w_type_input"
		Transformer3WTypeInput || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "transformer_3_w_type_input"
		WecTypeInput           || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "wec_type_input"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid directory path for a Load Parameter Model"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getDirectoryPath(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass           || expectedString
		RandomLoadParameters || "test_grid" + File.separator + "input" + File.separator + "global"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid file path for a Load Parameter Model"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFilePath(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass           || expectedString
		RandomLoadParameters || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "random_load_parameters_input"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid directory paths for a graphic input Model"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getDirectoryPath(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass       || expectedString
		NodeGraphicInput || "test_grid" + File.separator + "input" + File.separator + "graphics"
		LineGraphicInput || "test_grid" + File.separator + "input" + File.separator + "graphics"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid file paths for a graphic input Model"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFilePath(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass       || expectedString
		NodeGraphicInput || "test_grid" + File.separator + "input" + File.separator + "graphics" + File.separator + "node_graphic_input"
		LineGraphicInput || "test_grid" + File.separator + "input" + File.separator + "graphics" + File.separator + "line_graphic_input"
	}

	def "A HierarchicFileNamingStrategy should return valid directory path for individual time series"() {
		given:
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)
		IndividualTimeSeries timeSeries = Mock(IndividualTimeSeries)

		when:
		def actual = strategy.getDirectoryPath(timeSeries)

		then:
		actual.present
		actual.get() == expected

		where:
		clazz                || expected
		IndividualTimeSeries || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "time_series"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffix should return valid file path for individual time series"() {
		given:
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)
		def entries = [
			new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(500d, PowerSystemUnits.EURO_PER_MEGAWATTHOUR)))] as SortedSet
		IndividualTimeSeries timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> uuid
		timeSeries.entries >> entries

		when:
		def actual = strategy.getFilePath(timeSeries)

		then:
		actual.present
		actual.get() == expectedFilePath

		where:
		clazz                | uuid                                                    || expectedFilePath
		IndividualTimeSeries | UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "time_series" + File.separator + "its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276"
	}

	def "A HierarchicFileNamingStrategy with pre- or suffix should return valid file path for individual time series"() {
		given:
		def strategy = new HierarchicFileNamingStrategy("aa", "zz", defaultHierarchy)
		def entries = [
			new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(500d, PowerSystemUnits.EURO_PER_MEGAWATTHOUR)))] as SortedSet
		IndividualTimeSeries timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> uuid
		timeSeries.entries >> entries

		when:
		def actual = strategy.getFilePath(timeSeries)

		then:
		actual.present
		actual.get() == expectedFileName

		where:
		clazz                | uuid                                                    || expectedFileName
		IndividualTimeSeries | UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "time_series" + File.separator + "aa_its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276_zz"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffix should return valid directory path for load profile input"() {
		given:
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)
		def timeSeries = Mock(LoadProfileInput)

		when:
		def actual = strategy.getDirectoryPath(timeSeries)

		then:
		actual.present
		actual.get() == expected

		where:
		clazz            || expected
		LoadProfileInput || "test_grid" + File.separator + "input" + File.separator + "global"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffix should return valid file path for load profile input"() {
		given:
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)
		def timeSeries = Mock(LoadProfileInput)
		timeSeries.uuid >> uuid
		timeSeries.type >> type

		when:
		def actual = strategy.getFilePath(timeSeries)

		then:
		actual.present
		actual.get() == expectedFileName

		where:
		clazz            | uuid                                                    | type               || expectedFileName
		LoadProfileInput | UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304") | BdewLoadProfile.G3 || "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304"
	}

	def "A HierarchicFileNamingStrategy returns empty Optional, when there is no naming defined for a given time series class"() {
		given:
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)
		def timeSeries = Mock(RepetitiveTimeSeries)

		when:
		def fileName = strategy.getEntityName(timeSeries)

		then:
		!fileName.present
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid directory path for time series mapping"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getDirectoryPath(TimeSeriesMappingSource.MappingEntry)

		then:
		res.present
		res.get() == "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "time_series"
	}

	def "A HierarchicFileNamingStrategy without pre- or suffixes should return valid file path for time series mapping"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFilePath(TimeSeriesMappingSource.MappingEntry)

		then:
		res.present
		res.get() == "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "time_series" + File.separator + "time_series_mapping"
	}

	def "A HierarchicFileNamingStrategy with pre- and suffix should return valid file path for time series mapping"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy("prefix", "suffix", defaultHierarchy)

		when:
		def res = strategy.getFilePath(TimeSeriesMappingSource.MappingEntry)

		then:
		res.present
		res.get() == "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "time_series" + File.separator + "prefix_time_series_mapping_suffix"
	}

	def "A hierarchic file naming strategy returns correct individual time series file name pattern"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def actual = strategy.individualTimeSeriesPattern.pattern()

		then:
		actual == "test_grid" + File.separator + "input" + File.separator + "participants" + File.separator + "time_series" + File.separator + "its_(?<columnScheme>[a-zA-Z]{1,11})_(?<uuid>[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12})"
	}

	def "A hierarchic file naming strategy returns correct load profile time series file name pattern"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def actual = strategy.loadProfileTimeSeriesPattern.pattern()

		then:
		actual == "test_grid" + File.separator + "input" + File.separator + "global" + File.separator + "lpts_(?<profile>[a-zA-Z][0-9])_(?<uuid>[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12})"
	}
}