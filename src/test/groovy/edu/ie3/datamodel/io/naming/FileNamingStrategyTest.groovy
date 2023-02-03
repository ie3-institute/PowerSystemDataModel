/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.naming

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
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
import edu.ie3.datamodel.models.input.system.BmInput
import edu.ie3.datamodel.models.input.system.ChpInput
import edu.ie3.datamodel.models.input.system.EvInput
import edu.ie3.datamodel.models.input.system.EvcsInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.HpInput
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.StorageInput
import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.system.type.BmTypeInput
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput
import edu.ie3.datamodel.models.input.system.type.EvTypeInput
import edu.ie3.datamodel.models.input.system.type.HpTypeInput
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput
import edu.ie3.datamodel.models.input.system.type.WecTypeInput
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.connector.SwitchResult
import edu.ie3.datamodel.models.result.connector.Transformer2WResult
import edu.ie3.datamodel.models.result.connector.Transformer3WResult
import edu.ie3.datamodel.models.result.system.BmResult
import edu.ie3.datamodel.models.result.system.ChpResult
import edu.ie3.datamodel.models.result.system.EmResult
import edu.ie3.datamodel.models.result.system.EvResult
import edu.ie3.datamodel.models.result.system.EvcsResult
import edu.ie3.datamodel.models.result.system.FixedFeedInResult
import edu.ie3.datamodel.models.result.system.FlexOptionsResult
import edu.ie3.datamodel.models.result.system.LoadResult
import edu.ie3.datamodel.models.result.system.PvResult
import edu.ie3.datamodel.models.result.system.StorageResult
import edu.ie3.datamodel.models.result.system.WecResult
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.ZonedDateTime

class FileNamingStrategyTest extends Specification {

  @Shared
  DefaultDirectoryHierarchy defaultHierarchy
  FlatDirectoryHierarchy flatHierarchy
  EntityPersistenceNamingStrategy simpleEntityNaming

  def setup() {
    def tmpPath = Files.createTempDirectory("psdm_file_naming_strategy")
    defaultHierarchy = new DefaultDirectoryHierarchy(tmpPath, "test_grid")
    flatHierarchy = new FlatDirectoryHierarchy()
    simpleEntityNaming = new EntityPersistenceNamingStrategy()
  }


  // TESTS FOR DEFAULT HIERARCHY

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid directory paths for all result models"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getDirectoryPath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass               || expectedPath
    LoadResult               || Paths.get("test_grid", "results", "participants")
    FixedFeedInResult        || Paths.get("test_grid", "results", "participants")
    BmResult                 || Paths.get("test_grid", "results", "participants")
    PvResult                 || Paths.get("test_grid", "results", "participants")
    ChpResult                || Paths.get("test_grid", "results", "participants")
    WecResult                || Paths.get("test_grid", "results", "participants")
    StorageResult            || Paths.get("test_grid", "results", "participants")
    EvcsResult               || Paths.get("test_grid", "results", "participants")
    EvResult                 || Paths.get("test_grid", "results", "participants")
    EmResult                 || Paths.get("test_grid", "results", "participants")
    FlexOptionsResult        || Paths.get("test_grid", "results", "participants")
    Transformer2WResult      || Paths.get("test_grid", "results", "grid")
    Transformer3WResult      || Paths.get("test_grid", "results", "grid")
    LineResult               || Paths.get("test_grid", "results", "grid")
    SwitchResult             || Paths.get("test_grid", "results", "grid")
    NodeResult               || Paths.get("test_grid", "results", "grid")
    CylindricalStorageResult || Paths.get("test_grid", "results", "thermal")
    ThermalHouseResult       || Paths.get("test_grid", "results", "thermal")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid directory paths for all input assets models"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getDirectoryPath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass              || expectedPath
    FixedFeedInInput        || Paths.get("test_grid", "input", "participants")
    PvInput                 || Paths.get("test_grid", "input", "participants")
    WecInput                || Paths.get("test_grid", "input", "participants")
    ChpInput                || Paths.get("test_grid", "input", "participants")
    BmInput                 || Paths.get("test_grid", "input", "participants")
    EvInput                 || Paths.get("test_grid", "input", "participants")
    EvcsInput               || Paths.get("test_grid", "input", "participants")
    LoadInput               || Paths.get("test_grid", "input", "participants")
    StorageInput            || Paths.get("test_grid", "input", "participants")
    HpInput                 || Paths.get("test_grid", "input", "participants")
    LineInput               || Paths.get("test_grid", "input", "grid")
    SwitchInput             || Paths.get("test_grid", "input", "grid")
    NodeInput               || Paths.get("test_grid", "input", "grid")
    MeasurementUnitInput    || Paths.get("test_grid", "input", "grid")
    Transformer2WInput      || Paths.get("test_grid", "input", "grid")
    Transformer3WInput      || Paths.get("test_grid", "input", "grid")
    CylindricalStorageInput || Paths.get("test_grid", "input", "thermal")
    ThermalHouseInput       || Paths.get("test_grid", "input", "thermal")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid directory paths for all input types models"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getDirectoryPath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass             || expectedPath
    BmTypeInput            || Paths.get("test_grid", "input", "global")
    ChpTypeInput           || Paths.get("test_grid", "input", "global")
    EvTypeInput            || Paths.get("test_grid", "input", "global")
    HpTypeInput            || Paths.get("test_grid", "input", "global")
    StorageTypeInput       || Paths.get("test_grid", "input", "global")
    WecTypeInput           || Paths.get("test_grid", "input", "global")
    LineTypeInput          || Paths.get("test_grid", "input", "global")
    Transformer2WTypeInput || Paths.get("test_grid", "input", "global")
    Transformer3WTypeInput || Paths.get("test_grid", "input", "global")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid directory paths for a graphic input Model"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getDirectoryPath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass       || expectedPath
    NodeGraphicInput || Paths.get("test_grid", "input", "graphics")
    LineGraphicInput || Paths.get("test_grid", "input", "graphics")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffix should return valid directory path for load profile time series"() {
    given:
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)
    def timeSeries = Mock(LoadProfileInput)

    when:
    def actual = strategy.getDirectoryPath(timeSeries)

    then:
    actual.present
    actual.get() == expected

    where:
    clazz            || expected
    LoadProfileInput || Paths.get("test_grid", "input", "global")
  }

  def "A FileNamingStrategy with DefaultHierarchy and should return valid directory path for individual time series"() {
    given:
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)
    IndividualTimeSeries timeSeries = Mock(IndividualTimeSeries)

    when:
    def actual = strategy.getDirectoryPath(timeSeries)

    then:
    actual.present
    actual.get() == expected

    where:
    clazz                || expected
    IndividualTimeSeries || Paths.get("test_grid", "input", "participants", "time_series")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid file paths for all result models"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getFilePath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass               || expectedPath
    LoadResult               || Paths.get("test_grid", "results", "participants", "load_res")
    FixedFeedInResult        || Paths.get("test_grid", "results", "participants", "fixed_feed_in_res")
    BmResult                 || Paths.get("test_grid", "results", "participants", "bm_res")
    PvResult                 || Paths.get("test_grid", "results", "participants", "pv_res")
    ChpResult                || Paths.get("test_grid", "results", "participants", "chp_res")
    WecResult                || Paths.get("test_grid", "results", "participants", "wec_res")
    StorageResult            || Paths.get("test_grid", "results", "participants", "storage_res")
    EvcsResult               || Paths.get("test_grid", "results", "participants", "evcs_res")
    EvResult                 || Paths.get("test_grid", "results", "participants", "ev_res")
    EmResult                 || Paths.get("test_grid", "results", "participants", "em_res")
    FlexOptionsResult        || Paths.get("test_grid", "results", "participants", "flex_options_res")
    Transformer2WResult      || Paths.get("test_grid", "results", "grid", "transformer_2_w_res")
    Transformer3WResult      || Paths.get("test_grid", "results", "grid", "transformer_3_w_res")
    LineResult               || Paths.get("test_grid", "results", "grid", "line_res")
    SwitchResult             || Paths.get("test_grid", "results", "grid", "switch_res")
    NodeResult               || Paths.get("test_grid", "results", "grid", "node_res")
    CylindricalStorageResult || Paths.get("test_grid", "results", "thermal", "cylindrical_storage_res")
    ThermalHouseResult       || Paths.get("test_grid", "results", "thermal", "thermal_house_res")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid file paths for all other input assets models"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getFilePath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass              || expectedPath
    LineInput               || Paths.get("test_grid", "input", "grid", "line_input")
    SwitchInput             || Paths.get("test_grid", "input", "grid", "switch_input")
    NodeInput               || Paths.get("test_grid", "input", "grid", "node_input")
    MeasurementUnitInput    || Paths.get("test_grid", "input", "grid", "measurement_unit_input")
    Transformer2WInput      || Paths.get("test_grid", "input", "grid", "transformer_2_w_input")
    Transformer3WInput      || Paths.get("test_grid", "input", "grid", "transformer_3_w_input")
    CylindricalStorageInput || Paths.get("test_grid", "input", "thermal", "cylindrical_storage_input")
    ThermalHouseInput       || Paths.get("test_grid", "input", "thermal", "thermal_house_input")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid file paths for all system input assets models"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getFilePath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass              || expectedPath
    FixedFeedInInput        || Paths.get("test_grid", "input", "participants", "fixed_feed_in_input")
    PvInput                 || Paths.get("test_grid", "input", "participants", "pv_input")
    WecInput                || Paths.get("test_grid", "input", "participants", "wec_input")
    ChpInput                || Paths.get("test_grid", "input", "participants", "chp_input")
    BmInput                 || Paths.get("test_grid", "input", "participants", "bm_input")
    EvInput                 || Paths.get("test_grid", "input", "participants", "ev_input")
    LoadInput               || Paths.get("test_grid", "input", "participants", "load_input")
    StorageInput            || Paths.get("test_grid", "input", "participants", "storage_input")
    HpInput                 || Paths.get("test_grid", "input", "participants", "hp_input")
    EvcsInput               || Paths.get("test_grid", "input", "participants", "evcs_input")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid file paths for all input types models"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getFilePath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass             || expectedPath
    BmTypeInput            || Paths.get("test_grid", "input", "global", "bm_type_input")
    ChpTypeInput           || Paths.get("test_grid", "input", "global", "chp_type_input")
    EvTypeInput            || Paths.get("test_grid", "input", "global", "ev_type_input")
    HpTypeInput            || Paths.get("test_grid", "input", "global", "hp_type_input")
    LineTypeInput          || Paths.get("test_grid", "input", "global", "line_type_input")
    StorageTypeInput       || Paths.get("test_grid", "input", "global", "storage_type_input")
    Transformer2WTypeInput || Paths.get("test_grid", "input", "global", "transformer_2_w_type_input")
    Transformer3WTypeInput || Paths.get("test_grid", "input", "global", "transformer_3_w_type_input")
    WecTypeInput           || Paths.get("test_grid", "input", "global", "wec_type_input")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid directory path for a Load Parameter Model"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getDirectoryPath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass           || expectedPath
    RandomLoadParameters || Paths.get("test_grid", "input", "global")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid file path for a Load Parameter Model"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getFilePath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass           || expectedPath
    RandomLoadParameters || Paths.get("test_grid", "input", "global", "random_load_parameters_input")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid file paths for a graphic input Model"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getFilePath(modelClass)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass       || expectedPath
    NodeGraphicInput || Paths.get("test_grid", "input", "graphics", "node_graphic_input")
    LineGraphicInput || Paths.get("test_grid", "input", "graphics", "line_graphic_input")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffix should return valid file path for individual time series"() {
    given:
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)
    def entries = [
      new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(500d, PowerSystemUnits.EURO_PER_MEGAWATTHOUR)))
    ] as SortedSet
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
    IndividualTimeSeries | UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || Paths.get("test_grid", "input", "participants", "time_series", "its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276")
  }

  def "A FileNamingStrategy with DefaultHierarchy and with pre- or suffix should return valid file path for individual time series"() {
    given:
    def strategy = new FileNamingStrategy(new EntityPersistenceNamingStrategy("aa", "zz"), defaultHierarchy)
    def entries = [
      new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(500d, PowerSystemUnits.EURO_PER_MEGAWATTHOUR)))
    ] as SortedSet
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
    IndividualTimeSeries | UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || Paths.get("test_grid", "input", "participants", "time_series", "aa_its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276_zz")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffix should return valid file path for load profile time series"() {
    given:
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)
    def timeSeries = Mock(LoadProfileInput)
    timeSeries.uuid >> uuid
    timeSeries.type >> type

    when:
    def actual = strategy.getFilePath(timeSeries)

    then:
    actual.present
    actual.get() == expectedFileName

    where:
    clazz            | uuid                                                    | type                       || expectedFileName
    LoadProfileInput | UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304") | BdewStandardLoadProfile.G3 || Paths.get("test_grid", "input", "global", "lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid directory path for time series mapping"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getDirectoryPath(TimeSeriesMappingSource.MappingEntry)

    then:
    res.present
    res.get() == Paths.get("test_grid", "input", "participants", "time_series")
  }

  def "A FileNamingStrategy with DefaultHierarchy and without pre- or suffixes should return valid file path for time series mapping"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def res = strategy.getFilePath(TimeSeriesMappingSource.MappingEntry)

    then:
    res.present
    res.get() == Paths.get("test_grid", "input", "participants", "time_series", "time_series_mapping")
  }

  def "A FileNamingStrategy with DefaultHierarchy and pre- and suffix should return valid file path for time series mapping"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(new EntityPersistenceNamingStrategy("prefix", "suffix"), defaultHierarchy)

    when:
    def res = strategy.getFilePath(TimeSeriesMappingSource.MappingEntry)

    then:
    res.present
    res.get() == Paths.get("test_grid", "input", "participants", "time_series", "prefix_time_series_mapping_suffix")
  }


  // TESTS FOR FLAT HIERARCHY

  def "A FileNamingStrategy with FlatHierarchy does return empty sub directory path for any result class"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

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
    EmResult                 || Optional.empty()
    FlexOptionsResult        || Optional.empty()
    Transformer2WResult      || Optional.empty()
    Transformer3WResult      || Optional.empty()
    LineResult               || Optional.empty()
    SwitchResult             || Optional.empty()
    NodeResult               || Optional.empty()
    CylindricalStorageResult || Optional.empty()
    ThermalHouseResult       || Optional.empty()
  }

  def "A FileNamingStrategy with FlatHierarchy does return empty sub directory path for all input asset models"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

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
    EvcsInput                || Optional.empty()
    LoadInput                || Optional.empty()
    StorageInput             || Optional.empty()
    HpInput                  || Optional.empty()
    LineInput                || Optional.empty()
    SwitchInput              || Optional.empty()
    NodeInput                || Optional.empty()
    MeasurementUnitInput     || Optional.empty()
    Transformer2WInput       || Optional.empty()
    Transformer3WInput       || Optional.empty()
    CylindricalStorageInput  || Optional.empty()
    ThermalHouseInput        || Optional.empty()
  }

  def "A FileNamingStrategy with FlatHierarchy does return empty sub directory path for system type and model input classes"() {
    given: "a file naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

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
    LineTypeInput            || Optional.empty()
    Transformer2WTypeInput   || Optional.empty()
    Transformer3WTypeInput   || Optional.empty()
  }

  def "A FileNamingStrategy with FlatHierarchy does return empty sub directory path for graphics model input classes"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

    when:
    def actual = strategy.getDirectoryPath(modelClass as Class<? extends UniqueEntity>)

    then:
    actual == expected

    where:
    modelClass               || expected
    NodeGraphicInput         || Optional.empty()
    LineGraphicInput         || Optional.empty()
  }

  def "A FileNamingStrategy with FlatHierarchy does return empty sub directory path for any other model classes"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

    when:
    def actual = strategy.getDirectoryPath(modelClass as Class<? extends UniqueEntity>)

    then:
    actual == expected

    where:
    modelClass               || expected
    RandomLoadParameters     || Optional.empty()
    TimeSeriesMappingSource.MappingEntry  || Optional.empty()
  }

  def "A FileNamingStrategy with FlatHierarchy does return empty sub directory path for load profile time series"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)
    def timeSeries = Mock(LoadProfileInput)

    when:
    def actual = strategy.getDirectoryPath(timeSeries)

    then:
    actual == Optional.empty()
  }

  def "A FileNamingStrategy with FlatHierarchy does return empty sub directory path for individual time series"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)
    def timeSeries = Mock(IndividualTimeSeries)

    when:
    def actual = strategy.getDirectoryPath(timeSeries)

    then:
    actual == Optional.empty()
  }

  def "A FileNamingStrategy with FlatHierarchy and without pre- or suffixes should return valid file paths for all result classes"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

    when:
    def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass               || expectedPath
    LoadResult               || Path.of("load_res")
    FixedFeedInResult        || Path.of("fixed_feed_in_res")
    BmResult                 || Path.of("bm_res")
    PvResult                 || Path.of("pv_res")
    ChpResult                || Path.of("chp_res")
    WecResult                || Path.of("wec_res")
    StorageResult            || Path.of("storage_res")
    EvcsResult               || Path.of("evcs_res")
    EvResult                 || Path.of("ev_res")
    EmResult                 || Path.of("em_res")
    FlexOptionsResult        || Path.of("flex_options_res")
    Transformer2WResult      || Path.of("transformer_2_w_res")
    Transformer3WResult      || Path.of("transformer_3_w_res")
    LineResult               || Path.of("line_res")
    SwitchResult             || Path.of("switch_res")
    NodeResult               || Path.of("node_res")
    CylindricalStorageResult || Path.of("cylindrical_storage_res")
    ThermalHouseResult       || Path.of("thermal_house_res")
  }

  def "A FileNamingStrategy with FlatHierarchy and without pre- or suffixes should return valid file paths for all other system input classes"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

    when:
    def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass               || expectedPath
    FixedFeedInInput         || Path.of("fixed_feed_in_input")
    PvInput                  || Path.of("pv_input")
    WecInput                 || Path.of("wec_input")
    ChpInput                 || Path.of("chp_input")
    BmInput                  || Path.of("bm_input")
    EvInput                  || Path.of("ev_input")
    EvcsInput                || Path.of("evcs_input")
    LoadInput                || Path.of("load_input")
    StorageInput             || Path.of("storage_input")
    HpInput                  || Path.of("hp_input")
    LineInput                || Path.of("line_input")
    SwitchInput              || Path.of("switch_input")
    NodeInput                || Path.of("node_input")
    MeasurementUnitInput     || Path.of("measurement_unit_input")
    Transformer2WInput       || Path.of("transformer_2_w_input")
    Transformer3WInput       || Path.of("transformer_3_w_input")
    CylindricalStorageInput  || Path.of("cylindrical_storage_input")
    ThermalHouseInput        || Path.of("thermal_house_input")
  }

  def "A FileNamingStrategy with FlatHierarchy and without pre- or suffixes should return valid file paths for all system characteristic and type input classes"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

    when:
    def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass               || expectedPath
    BmTypeInput              || Path.of("bm_type_input")
    ChpTypeInput             || Path.of("chp_type_input")
    EvTypeInput              || Path.of("ev_type_input")
    HpTypeInput              || Path.of("hp_type_input")
    StorageTypeInput         || Path.of("storage_type_input")
    WecTypeInput             || Path.of("wec_type_input")
    LineTypeInput            || Path.of("line_type_input")
    Transformer2WTypeInput   || Path.of("transformer_2_w_type_input")
    Transformer3WTypeInput   || Path.of("transformer_3_w_type_input")
  }

  def "A FileNamingStrategy with FlatHierarchy and without pre- or suffixes should return valid file paths for all graphics input classes"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

    when:
    def res = strategy.getFilePath(modelClass as Class<? extends UniqueEntity>)

    then:
    res.present
    res.get() == expectedPath

    where:
    modelClass               || expectedPath
    NodeGraphicInput         || Path.of("node_graphic_input")
    LineGraphicInput         || Path.of("line_graphic_input")
  }

  def "A FileNamingStrategy with FlatHierarchy does return valid file path for load profile time series"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)
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
    LoadProfileInput | UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304") | BdewStandardLoadProfile.G3 || Path.of("lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304")
  }

  def "A FileNamingStrategy with FlatHierarchy does return valid file path for individual time series"() {
    given: "a naming strategy without pre- or suffixes"
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)
    def entries = [
      new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(500d, PowerSystemUnits.EURO_PER_MEGAWATTHOUR)))
    ] as SortedSet
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
    IndividualTimeSeries | UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276") || Path.of("its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276")
  }

  String escapedFileSeparator = File.separator == "\\" ? "\\\\" : File.separator

  def "A FileNamingStrategy with DefaultHierarchy returns correct individual time series file name pattern"() {
    given:
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def actual = strategy.individualTimeSeriesPattern.pattern()

    then:
    actual == "test_grid" + escapedFileSeparator + "input" + escapedFileSeparator + "participants" + escapedFileSeparator + "time_series" + escapedFileSeparator + "its_(?<columnScheme>[a-zA-Z]{1,11})_(?<uuid>[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12})"
  }

  def "A FileNamingStrategy with DefaultHierarchy returns correct load profile time series file name pattern"() {
    given:
    def strategy = new FileNamingStrategy(simpleEntityNaming, defaultHierarchy)

    when:
    def actual = strategy.loadProfileTimeSeriesPattern.pattern()

    then:
    actual == "test_grid" + escapedFileSeparator + "input" + escapedFileSeparator + "global" + escapedFileSeparator + "lpts_(?<profile>[a-zA-Z][0-9])_(?<uuid>[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12})"
  }

  def "A FileNamingStrategy with FlatHierarchy returns correct individual time series file name pattern"() {
    given:
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

    when:
    def actual = strategy.individualTimeSeriesPattern.pattern()

    then:
    actual == "its_(?<columnScheme>[a-zA-Z]{1,11})_(?<uuid>[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12})"
  }

  def "A FileNamingStrategy with FlatHierarchy returns correct load profile time series file name pattern"() {
    given:
    def strategy = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

    when:
    def actual = strategy.loadProfileTimeSeriesPattern.pattern()

    then:
    actual == "lpts_(?<profile>[a-zA-Z][0-9])_(?<uuid>[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12})"
  }

  def "Trying to extract time series meta information throws an Exception, if it is provided a malformed string"() {
    given:
    def fns = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)
    def path = Paths.get("/bla/foo")

    when:
    fns.timeSeriesMetaInformation(path)

    then:
    def ex = thrown(IllegalArgumentException)
    ex.message == "Unknown format of 'foo'. Cannot extract meta information."
  }

  def "The FileNamingStrategy extracts correct meta information from a valid time series file name"() {
    given:
    def fns = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)
    def path = Paths.get(pathString)

    when:
    def metaInformation = fns.timeSeriesMetaInformation(path)

    then:
    IndividualTimeSeriesMetaInformation.isAssignableFrom(metaInformation.getClass())
    (metaInformation as IndividualTimeSeriesMetaInformation).with {
      assert it.uuid == UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276")
      assert it.columnScheme == expectedColumnScheme
    }

    where:
    pathString                                                      || expectedColumnScheme
    "/bla/foo/its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"       || ColumnScheme.ENERGY_PRICE
    "/bla/foo/its_p_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"       || ColumnScheme.ACTIVE_POWER
    "/bla/foo/its_pq_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"      || ColumnScheme.APPARENT_POWER
    "/bla/foo/its_h_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"       || ColumnScheme.HEAT_DEMAND
    "/bla/foo/its_ph_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"      || ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND
    "/bla/foo/its_pqh_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"     || ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND
    "/bla/foo/its_weather_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || ColumnScheme.WEATHER
  }

  def "The FileNamingStrategy extracts correct meta information from a valid time series file name with pre- and suffix"() {
    given:
    def fns = new FileNamingStrategy(new EntityPersistenceNamingStrategy("prefix", "suffix"), flatHierarchy)
    def path = Paths.get(pathString)

    when:
    def metaInformation = fns.timeSeriesMetaInformation(path)

    then:
    IndividualTimeSeriesMetaInformation.isAssignableFrom(metaInformation.getClass())
    (metaInformation as IndividualTimeSeriesMetaInformation).with {
      assert it.uuid == UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276")
      assert it.columnScheme == expectedColumnScheme
    }

    where:
    pathString                                                                    || expectedColumnScheme
    "/bla/foo/prefix_its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv"       || ColumnScheme.ENERGY_PRICE
    "/bla/foo/prefix_its_p_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv"       || ColumnScheme.ACTIVE_POWER
    "/bla/foo/prefix_its_pq_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv"      || ColumnScheme.APPARENT_POWER
    "/bla/foo/prefix_its_h_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv"       || ColumnScheme.HEAT_DEMAND
    "/bla/foo/prefix_its_ph_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv"      || ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND
    "/bla/foo/prefix_its_pqh_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv"     || ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND
    "/bla/foo/prefix_its_weather_4881fda2-bcee-4f4f-a5bb-6a09bf785276_suffix.csv" || ColumnScheme.WEATHER
  }

  def "The FileNamingStrategy extracts correct meta information from a valid individual time series file name"() {
    given:
    def fns = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)

    when:
    def metaInformation = fns.individualTimeSeriesMetaInformation(fileName)

    then:
    IndividualTimeSeriesMetaInformation.isAssignableFrom(metaInformation.getClass())
    (metaInformation as IndividualTimeSeriesMetaInformation).with {
      assert it.uuid == UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276")
      assert it.columnScheme == expectedColumnScheme
    }

    where:
    fileName                                               || expectedColumnScheme
    "its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"       || ColumnScheme.ENERGY_PRICE
    "its_p_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"       || ColumnScheme.ACTIVE_POWER
    "its_pq_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"      || ColumnScheme.APPARENT_POWER
    "its_h_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"       || ColumnScheme.HEAT_DEMAND
    "its_ph_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"      || ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND
    "its_pqh_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"     || ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND
    "its_weather_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || ColumnScheme.WEATHER
  }

  def "The FileNamingStrategy throw an IllegalArgumentException, if the time series file path is malformed."() {
    given:
    def fns = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)
    def path = "erroneous_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv"

    when:
    fns.individualTimeSeriesMetaInformation(path)

    then:
    def ex = thrown(IllegalArgumentException)
    ex.message == "Cannot extract meta information on individual time series from 'erroneous_4881fda2-bcee-4f4f-a5bb-6a09bf785276'."
  }

  def "The FileNamingStrategy throw an IllegalArgumentException, if the column scheme is malformed."() {
    given:
    def fns = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)
    def path = Paths.get("/bla/foo/its_whoops_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv")

    when:
    fns.timeSeriesMetaInformation(path)

    then:
    def ex = thrown(IllegalArgumentException)
    ex.message == "Cannot parse 'whoops' to valid column scheme."
  }

  def "The FileNamingStrategy extracts correct meta information from a valid load profile time series file name"() {
    given:
    def fns = new FileNamingStrategy(simpleEntityNaming, flatHierarchy)
    def path = Paths.get("/bla/foo/lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304.csv")

    when:
    def metaInformation = fns.timeSeriesMetaInformation(path)

    then:
    LoadProfileTimeSeriesMetaInformation.isAssignableFrom(metaInformation.getClass())
    (metaInformation as LoadProfileTimeSeriesMetaInformation).with {
      assert uuid == UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304")
      assert profile == "g3"
    }
  }

  def "The FileNamingStrategy extracts correct meta information from a valid load profile time series file name with pre- and suffix"() {
    given:
    def fns = new FileNamingStrategy(new EntityPersistenceNamingStrategy("prefix", "suffix"), flatHierarchy)
    def path = Paths.get("/bla/foo/prefix_lpts_g3_bee0a8b6-4788-4f18-bf72-be52035f7304_suffix.csv")

    when:
    def metaInformation = fns.timeSeriesMetaInformation(path)

    then:
    LoadProfileTimeSeriesMetaInformation.isAssignableFrom(metaInformation.getClass())
    (metaInformation as LoadProfileTimeSeriesMetaInformation).with {
      assert uuid == UUID.fromString("bee0a8b6-4788-4f18-bf72-be52035f7304")
      assert profile == "g3"
    }
  }


  def "The FileNamingStrategy with FlatHierarchy returns the Id Coordinate file path correctly"() {
    def fns = new FileNamingStrategy(new EntityPersistenceNamingStrategy("prefix", "suffix"), flatHierarchy)

    when:
    def idFilePath = fns.getIdCoordinateFilePath()

    then:
    idFilePath.present
    idFilePath.get() == Path.of("prefix_coordinates_suffix")
  }

  def "The FileNamingStrategy with DefaultHierarchy returns the Id Coordinate file path correctly"() {
    def fns = new FileNamingStrategy(new EntityPersistenceNamingStrategy("prefix", "suffix"), defaultHierarchy)

    when:
    def idFilePath = fns.getIdCoordinateFilePath()

    then:
    idFilePath.present
    idFilePath.get() ==  defaultHierarchy.baseDirectory.get().resolve("prefix_coordinates_suffix")
  }

}