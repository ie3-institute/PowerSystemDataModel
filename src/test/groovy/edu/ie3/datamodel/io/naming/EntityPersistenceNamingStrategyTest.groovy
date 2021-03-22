/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.naming

import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme
import edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation
import edu.ie3.datamodel.io.csv.timeseries.LoadProfileTimeSeriesMetaInformation
import edu.ie3.datamodel.models.BdewLoadProfile
import edu.ie3.datamodel.models.UniqueEntity
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
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput
import edu.ie3.datamodel.models.timeseries.repetitive.RepetitiveTimeSeries
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.file.Paths
import java.time.ZonedDateTime
import java.util.regex.Pattern

class EntityPersistenceNamingStrategyTest extends Specification {

	def "A EntityPersistenceNamingStrategy without pre- or suffix should return valid file name for individual time series" () {
		given:
		EntityPersistenceNamingStrategy strategy = new EntityPersistenceNamingStrategy()
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
		clazz                | uuid                                                    || expectedFileName
		IndividualTimeSeries | UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || "its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276"
	}

	def "A EntityPersistenceNamingStrategy with pre- or suffix should return valid file name for individual time series" () {
		given:
		EntityPersistenceNamingStrategy strategy = new EntityPersistenceNamingStrategy("aa", "zz")
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
		clazz                | uuid                                                    || expectedFileName
		IndividualTimeSeries | UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || "aa_its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276_zz"
	}

	def "A EntityPersistenceNamingStrategy without pre- or suffix should return valid file name for load profile input" () {
		given:
		EntityPersistenceNamingStrategy strategy = new EntityPersistenceNamingStrategy()
		LoadProfileInput timeSeries = Mock(LoadProfileInput)
		timeSeries.uuid >> uuid
		timeSeries.type >> type

		when:
		Optional<String> actual = strategy.getFileName(timeSeries)

		then:
		actual.present
		actual.get() == expectedFileName

		where:
		clazz            | uuid                                                    | type               || expectedFileName
		LoadProfileInput | UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304") | BdewLoadProfile.G3 || "lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304"
	}

	def "A EntityPersistenceNamingStrategy returns empty Optional, when there is no naming defined for a given time series class"() {
		given:
		EntityPersistenceNamingStrategy entityPersistenceNamingStrategy = new EntityPersistenceNamingStrategy()
		RepetitiveTimeSeries timeSeries = Mock(RepetitiveTimeSeries)

		when:
		Optional<String> fileName = entityPersistenceNamingStrategy.getFileName(timeSeries)

		then:
		!fileName.present
	}

	def "A EntityPersistenceNamingStrategy without pre- or suffixes should return valid strings for time series mapping"() {
		given: "a naming strategy without pre- or suffixes"
		EntityPersistenceNamingStrategy strategy = new EntityPersistenceNamingStrategy()

		when:
		Optional<String> res = strategy.getFileName(TimeSeriesMappingSource.MappingEntry)

		then:
		res.present
		res.get() == "time_series_mapping"
	}

	def "A EntityPersistenceNamingStrategy with pre- and suffix should return valid strings for time series mapping"() {
		given: "a naming strategy without pre- or suffixes"
		EntityPersistenceNamingStrategy strategy = new EntityPersistenceNamingStrategy("prefix", "suffix")

		when:
		Optional<String> res = strategy.getFileName(TimeSeriesMappingSource.MappingEntry)

		then:
		res.present
		res.get() == "prefix_time_series_mapping_suffix"
	}

	def "A simple file naming strategy does return empty sub directory path for system type and characteristic model input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def actual = strategy.getDirectoryPath(modelClass as Class<? extends UniqueEntity>)

		then:
		actual == expected

		where:
		modelClass               || expected
		BmTypeInput              || Optional.empty()
		ChpTypeInput             || Optional.empty()
		EvTypeInput              || Optional.empty()
		HpTypeInput              || Optional.empty()
		StorageTypeInput         || Optional.empty()
		WecTypeInput             || Optional.empty()
		WecCharacteristicInput   || Optional.empty()
		EvCharacteristicInput    || Optional.empty()
	}

	def "A simple file naming strategy does return empty sub directory path for other system model input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def actual = strategy.getDirectoryPath(modelClass as Class<? extends UniqueEntity>)

		then:
		actual == expected

		where:
		modelClass               || expected
		FixedFeedInInput         || Optional.empty()
		PvInput                  || Optional.empty()
		WecInput                 || Optional.empty()
		ChpInput                 || Optional.empty()
		BmInput                  || Optional.empty()
		EvInput                  || Optional.empty()
		LoadInput                || Optional.empty()
		StorageInput             || Optional.empty()
		HpInput                  || Optional.empty()
		EvcsInput                || Optional.empty()
	}

	def "A simple file naming strategy does return empty sub directory path for connector model input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def actual = strategy.getDirectoryPath(modelClass as Class<? extends UniqueEntity>)

		then:
		actual == expected

		where:
		modelClass               || expected
		LineInput                || Optional.empty()
		SwitchInput              || Optional.empty()
		Transformer2WInput       || Optional.empty()
		Transformer3WInput       || Optional.empty()
		LineTypeInput            || Optional.empty()
		Transformer2WTypeInput   || Optional.empty()
		Transformer3WTypeInput   || Optional.empty()
	}

	def "A simple file naming strategy does return empty sub directory path for graphics model input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def actual = strategy.getDirectoryPath(modelClass as Class<? extends UniqueEntity>)

		then:
		actual == expected

		where:
		modelClass               || expected
		NodeGraphicInput         || Optional.empty()
		LineGraphicInput         || Optional.empty()
	}

	def "A simple file naming strategy does return empty sub directory path for thermal model input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def actual = strategy.getDirectoryPath(modelClass as Class<? extends UniqueEntity>)

		then:
		actual == expected

		where:
		modelClass               || expected
		CylindricalStorageInput  || Optional.empty()
		ThermalHouseInput        || Optional.empty()
	}

	def "A simple file naming strategy does return empty sub directory path for any other model classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def actual = strategy.getDirectoryPath(modelClass as Class<? extends UniqueEntity>)

		then:
		actual == expected

		where:
		modelClass               || expected
		NodeInput                || Optional.empty()
		MeasurementUnitInput     || Optional.empty()
		RandomLoadParameters     || Optional.empty()
		TimeSeriesMappingSource.MappingEntry  || Optional.empty()
	}

	def "A simple file naming strategy does return empty sub directory path for any result class"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def actual = strategy.getDirectoryPath(modelClass as Class<? extends UniqueEntity>)

		then:
		actual == expected

		where:
		modelClass               || expected
		LoadResult               || Optional.empty()
		FixedFeedInResult        || Optional.empty()
		BmResult                 || Optional.empty()
		PvResult                 || Optional.empty()
		ChpResult                || Optional.empty()
		WecResult                || Optional.empty()
		StorageResult            || Optional.empty()
		EvcsResult               || Optional.empty()
		EvResult                 || Optional.empty()
		Transformer2WResult      || Optional.empty()
		Transformer3WResult      || Optional.empty()
		LineResult               || Optional.empty()
		SwitchResult             || Optional.empty()
		NodeResult               || Optional.empty()
		CylindricalStorageResult || Optional.empty()
		ThermalHouseResult       || Optional.empty()
	}

	def "A simple file naming strategy does return empty sub directory path for load profile time series"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()
		def timeSeries = Mock(LoadProfileInput)

		when:
		def actual = strategy.getDirectoryPath(timeSeries)

		then:
		actual == Optional.empty()
	}

	def "A simple file naming strategy does return empty sub directory path for individual time series"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()
		def timeSeries = Mock(IndividualTimeSeries)

		when:
		def actual = strategy.getDirectoryPath(timeSeries)

		then:
		actual == Optional.empty()
	}

	def "A EntityPersistenceNamingStrategy without pre- or suffixes should return valid file paths for all connector input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass               || expectedString
		LineInput                || "line_input"
		SwitchInput              || "switch_input"
		Transformer2WInput       || "transformer_2_w_input"
		Transformer3WInput       || "transformer_3_w_input"
		LineTypeInput            || "line_type_input"
		Transformer2WTypeInput   || "transformer_2_w_type_input"
		Transformer3WTypeInput   || "transformer_3_w_type_input"
	}

	def "A EntityPersistenceNamingStrategy without pre- or suffixes should return valid file paths for all graphics input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass               || expectedString
		NodeGraphicInput         || "node_graphic_input"
		LineGraphicInput         || "line_graphic_input"
	}

	def "A EntityPersistenceNamingStrategy without pre- or suffixes should return valid file paths for all thermal input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass               || expectedString
		CylindricalStorageInput  || "cylindrical_storage_input"
		ThermalHouseInput        || "thermal_house_input"
	}

	def "A EntityPersistenceNamingStrategy without pre- or suffixes should return valid file paths for all system characteristic and type input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass               || expectedString
		WecCharacteristicInput   || "wec_characteristic_input"
		EvCharacteristicInput    || "ev_characteristic_input"
		BmTypeInput              || "bm_type_input"
		ChpTypeInput             || "chp_type_input"
		EvTypeInput              || "ev_type_input"
		HpTypeInput              || "hp_type_input"
		StorageTypeInput         || "storage_type_input"
		WecTypeInput             || "wec_type_input"
	}

	def "A EntityPersistenceNamingStrategy without pre- or suffixes should return valid file paths for all other system input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass               || expectedString
		FixedFeedInInput         || "fixed_feed_in_input"
		PvInput                  || "pv_input"
		WecInput                 || "wec_input"
		ChpInput                 || "chp_input"
		BmInput                  || "bm_input"
		EvInput                  || "ev_input"
		LoadInput                || "load_input"
		StorageInput             || "storage_input"
		HpInput                  || "hp_input"
		EvcsInput                || "evcs_input"
	}

	def "A EntityPersistenceNamingStrategy without pre- or suffixes should return valid file paths for all other input classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass               || expectedString
		NodeInput                || "node_input"
		MeasurementUnitInput     || "measurement_unit_input"
	}

	def "A EntityPersistenceNamingStrategy without pre- or suffixes should return valid file paths for all result classes"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()

		when:
		def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

		then:
		res.present
		res.get() == expectedString

		where:
		modelClass               || expectedString
		LoadResult               || "load_res"
		FixedFeedInResult        || "fixed_feed_in_res"
		BmResult                 || "bm_res"
		PvResult                 || "pv_res"
		ChpResult                || "chp_res"
		WecResult                || "wec_res"
		StorageResult            || "storage_res"
		EvcsResult               || "evcs_res"
		EvResult                 || "ev_res"
		Transformer2WResult      || "transformer_2_w_res"
		Transformer3WResult      || "transformer_3_w_res"
		LineResult               || "line_res"
		SwitchResult             || "switch_res"
		NodeResult               || "node_res"
		CylindricalStorageResult || "cylindrical_storage_res"
		ThermalHouseResult       || "thermal_house_res"
	}

	def "A simple file naming strategy does return valid file path for load profile time series"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()
		def timeSeries = Mock(LoadProfileInput)
		timeSeries.uuid >> uuid
		timeSeries.type >> type

		when:
		def actual = strategy.getFilePath(timeSeries)

		then:
		actual.present
		actual.get() == expectedFilePath

		where:
		clazz            | uuid                                                    | type               || expectedFilePath
		LoadProfileInput | UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304") | BdewLoadProfile.G3 || "lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304"
	}

	def "A simple file naming strategy does return valid file path for individual time series"() {
		given: "a naming strategy without pre- or suffixes"
		def strategy = new EntityPersistenceNamingStrategy()
		def entries = [
			new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(500d, PowerSystemUnits.EURO_PER_MEGAWATTHOUR)))] as SortedSet
		def timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> uuid
		timeSeries.entries >> entries

		when:
		def actual = strategy.getFilePath(timeSeries)

		then:
		actual.present
		actual.get() == expectedFilePath

		where:
		clazz                | uuid                                                    || expectedFilePath
		IndividualTimeSeries | UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || "its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276"
	}
}
