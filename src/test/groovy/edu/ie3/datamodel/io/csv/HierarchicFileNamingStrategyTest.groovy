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
import edu.ie3.datamodel.models.result.system.BmResult
import edu.ie3.datamodel.models.result.system.ChpResult
import edu.ie3.datamodel.models.result.system.EvResult
import edu.ie3.datamodel.models.result.system.EvcsResult
import edu.ie3.datamodel.models.result.system.FixedFeedInResult
import edu.ie3.datamodel.models.result.system.LoadResult
import edu.ie3.datamodel.models.result.system.PvResult
import edu.ie3.datamodel.models.result.system.StorageResult
import edu.ie3.datamodel.models.result.system.WecResult
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping
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
	DefaultInputHierarchy defaultHierarchy

	def setup() {
		def tmpPath = Files.createTempDirectory("psdm_hierarchic_file_naming_strategy")
		defaultHierarchy = new DefaultInputHierarchy(tmpPath.toString(), "test_grid")
	}

	def "A FileNamingStrategy should return an empty optional on a invalid class"() {
		given: "a file naming strategy"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFileName(String)

		then:
		!res.present
	}

	def "A FileNamingStrategy without pre- or suffixes should return empty optionals for all result models"() {
		given: "a file naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFileName(modelClass)

		then:
		!res.present

		where:
		modelClass               || expectedString
		LoadResult               || ""
		FixedFeedInResult        || ""
		BmResult                 || ""
		PvResult                 || ""
		ChpResult                || ""
		WecResult                || ""
		StorageResult            || ""
		EvcsResult               || ""
		EvResult                 || ""
		Transformer2WResult      || ""
		Transformer3WResult      || ""
		LineResult               || ""
		SwitchResult             || ""
		NodeResult               || ""
		CylindricalStorageResult || ""
		ThermalHouseResult       || ""
	}

	def "A FileNamingStrategy with pre- and suffixes should return empty optionals for all result models"() {
		given: "a file naming strategy with pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy("prefix", "suffix", defaultHierarchy)

		when:
		def res = strategy.getFileName(modelClass)

		then:
		!res.present

		where:
		modelClass               || expectedString
		LoadResult               || ""
		FixedFeedInResult        || ""
		BmResult                 || ""
		PvResult                 || ""
		ChpResult                || ""
		WecResult                || ""
		StorageResult            || ""
		EvcsResult               || ""
		EvResult                 || ""
		Transformer2WResult      || ""
		Transformer3WResult      || ""
		LineResult               || ""
		SwitchResult             || ""
		NodeResult               || ""
		CylindricalStorageResult || ""
		ThermalHouseResult       || ""
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for all input assets models"() {
		given: "a file naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass              || expectedString
		FixedFeedInInput        || "test_grid/participants/fixed_feed_in_input"
		PvInput                 || "test_grid/participants/pv_input"
		WecInput                || "test_grid/participants/wec_input"
		ChpInput                || "test_grid/participants/chp_input"
		BmInput                 || "test_grid/participants/bm_input"
		EvInput                 || "test_grid/participants/ev_input"
		LoadInput               || "test_grid/participants/load_input"
		StorageInput            || "test_grid/participants/storage_input"
		HpInput                 || "test_grid/participants/hp_input"
		LineInput               || "test_grid/grid/line_input"
		SwitchInput             || "test_grid/grid/switch_input"
		NodeInput               || "test_grid/grid/node_input"
		MeasurementUnitInput    || "test_grid/grid/measurement_unit_input"
		EvcsInput               || "test_grid/participants/evcs_input"
		Transformer2WInput      || "test_grid/grid/transformer2w_input"
		Transformer3WInput      || "test_grid/grid/transformer3w_input"
		CylindricalStorageInput || "test_grid/thermal/cylindrical_storage_input"
		ThermalHouseInput       || "test_grid/thermal/thermal_house_input"
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for all asset characteristics models"() {
		given: "a file naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass             || expectedString
		WecCharacteristicInput || "test_grid/global/wec_characteristic_input"
		EvCharacteristicInput  || "test_grid/global/ev_characteristic_input"
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for all input types models"() {
		given: "a file naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass             || expectedString
		BmTypeInput            || "test_grid/global/bm_type_input"
		ChpTypeInput           || "test_grid/global/chp_type_input"
		EvTypeInput            || "test_grid/global/ev_type_input"
		HpTypeInput            || "test_grid/global/hp_type_input"
		LineTypeInput          || "test_grid/global/line_type_input"
		StorageTypeInput       || "test_grid/global/storage_type_input"
		Transformer2WTypeInput || "test_grid/global/transformer2w_type_input"
		Transformer3WTypeInput || "test_grid/global/transformer3w_type_input"
		WecTypeInput           || "test_grid/global/wec_type_input"
		WecTypeInput           || "test_grid/global/wec_type_input"
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for a Load Parameter Model"() {
		given: "a file naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass           || expectedString
		RandomLoadParameters || "test_grid/global/random_load_parameters_input"
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for a graphic input Model"() {
		given: "a file naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFileName(modelClass)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass       || expectedString
		NodeGraphicInput || "test_grid/graphics/node_graphic_input"
		LineGraphicInput || "test_grid/graphics/line_graphic_input"
	}

	def "A FileNamingStrategy without pre- or suffix should return valid file name for individual time series"() {
		given:
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)
		def entries = [
			new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(500d, PowerSystemUnits.EURO_PER_MEGAWATTHOUR)))] as SortedSet
		IndividualTimeSeries timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> uuid
		timeSeries.entries >> entries

		when:
		def actual = strategy.getFileName(timeSeries)

		then:
		actual.present
		actual.get() == expectedFileName

		where:
		clazz                || uuid                                                    || expectedFileName
		IndividualTimeSeries || UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || "test_grid/participants/time_series/its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276"
	}

	def "A FileNamingStrategy with pre- or suffix should return valid file name for individual time series"() {
		given:
		def strategy = new HierarchicFileNamingStrategy("aa", "zz", defaultHierarchy)
		def entries = [
			new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(500d, PowerSystemUnits.EURO_PER_MEGAWATTHOUR)))] as SortedSet
		IndividualTimeSeries timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> uuid
		timeSeries.entries >> entries

		when:
		def actual = strategy.getFileName(timeSeries)

		then:
		actual.present
		actual.get() == expectedFileName

		where:
		clazz                || uuid                                                    || expectedFileName
		IndividualTimeSeries || UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || "test_grid/participants/time_series/aa_its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276_zz"
	}

	def "A FileNamingStrategy without pre- or suffix should return valid file name for load profile input"() {
		given:
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)
		def timeSeries = Mock(LoadProfileInput)
		timeSeries.uuid >> uuid
		timeSeries.type >> type

		when:
		def actual = strategy.getFileName(timeSeries)

		then:
		actual.present
		actual.get() == expectedFileName

		where:
		clazz            || uuid                                                    || type               || expectedFileName
		LoadProfileInput || UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304") || BdewLoadProfile.G3 || "test_grid/global/lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304"
	}

	def "A FileNamingStrategy returns empty Optional, when there is no naming defined for a given time series class"() {
		given:
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)
		def timeSeries = Mock(RepetitiveTimeSeries)

		when:
		def fileName = strategy.getFileName(timeSeries)

		then:
		!fileName.present
	}

	def "A FileNamingStrategy without pre- or suffixes should return valid strings for time series mapping"() {
		given: "a file naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def res = strategy.getFileName(TimeSeriesMapping.Entry)

		then:
		res.present
		res.get() == "test_grid/participants/time_series/time_series_mapping"
	}

	def "A FileNamingStrategy with pre- and suffix should return valid strings for time series mapping"() {
		given: "a file naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy("prefix", "suffix", defaultHierarchy)

		when:
		def res = strategy.getFileName(TimeSeriesMapping.Entry)

		then:
		res.present
		res.get() == "test_grid/participants/time_series/prefix_time_series_mapping_suffix"
	}

	def "A hierarchic file naming strategy returns correct individual time series file name pattern"() {
		given: "a file naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def actual = strategy.individualTimeSeriesPattern.pattern()

		then:
		actual == "test_grid/participants/time_series/its_(?<columnScheme>[a-zA-Z]{1,11})_(?<uuid>[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12})"
	}

	def "A hierarchic file naming strategy returns correct load profile time series file name pattern"() {
		given: "a file naming strategy without pre- or suffixes"
		def strategy = new HierarchicFileNamingStrategy(defaultHierarchy)

		when:
		def actual = strategy.loadProfileTimeSeriesPattern.pattern()

		then:
		actual == "test_grid/global/lpts_(?<profile>[a-zA-Z][0-9])_(?<uuid>[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12})"
	}
}
