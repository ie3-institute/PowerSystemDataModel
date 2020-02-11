package edu.ie3.io

import edu.ie3.models.input.EvcsInput
import edu.ie3.models.input.MeasurementUnitInput
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.RandomLoadParameters
import edu.ie3.models.input.connector.LineInput
import edu.ie3.models.input.connector.SwitchInput
import edu.ie3.models.input.connector.Transformer2WInput
import edu.ie3.models.input.connector.Transformer3WInput
import edu.ie3.models.input.connector.type.LineTypeInput
import edu.ie3.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.models.input.graphics.LineGraphicInput
import edu.ie3.models.input.graphics.NodeGraphicInput
import edu.ie3.models.input.system.BmInput
import edu.ie3.models.input.system.ChpInput
import edu.ie3.models.input.system.EvInput
import edu.ie3.models.input.system.FixedFeedInInput
import edu.ie3.models.input.system.HpInput
import edu.ie3.models.input.system.LoadInput
import edu.ie3.models.input.system.PvInput
import edu.ie3.models.input.system.StorageInput
import edu.ie3.models.input.system.WecInput
import edu.ie3.models.input.system.characteristic.EvCharacteristicInput
import edu.ie3.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.models.input.system.type.BmTypeInput
import edu.ie3.models.input.system.type.ChpTypeInput
import edu.ie3.models.input.system.type.EvTypeInput
import edu.ie3.models.input.system.type.HpTypeInput
import edu.ie3.models.input.system.type.StorageTypeInput
import edu.ie3.models.input.system.type.WecTypeInput
import edu.ie3.models.input.thermal.CylindricalStorageInput
import edu.ie3.models.input.thermal.ThermalHouseInput
import edu.ie3.models.result.NodeResult
import edu.ie3.models.result.thermal.CylindricalStorageResult
import edu.ie3.models.result.thermal.ThermalHouseResult
import edu.ie3.models.result.connector.LineResult
import edu.ie3.models.result.connector.SwitchResult
import edu.ie3.models.result.connector.Transformer2WResult
import edu.ie3.models.result.connector.Transformer3WResult
import edu.ie3.models.result.system.BmResult
import edu.ie3.models.result.system.ChpResult
import edu.ie3.models.result.system.EvResult
import edu.ie3.models.result.system.EvcsResult
import edu.ie3.models.result.system.FixedFeedInResult
import edu.ie3.models.result.system.LoadResult
import edu.ie3.models.result.system.PvResult
import edu.ie3.models.result.system.StorageResult
import edu.ie3.models.result.system.WecResult
import spock.lang.Specification


class FileNamingStrategyTest extends Specification {

    def "A FileNamingStrategy should return an empty optional on a invalid class"() {
        given: "a file naming strategy"
        FileNamingStrategy strategy = new FileNamingStrategy()

        when:
        Optional<String> res = strategy.getFileName(String)

        then:
        !res.present
    }

    def "A FileNamingStrategy should recognize if empty strings are passed in the prefix/suffix constructor and don't add underlines then"() {
        given: "a file naming strategy"
        FileNamingStrategy strategy = new FileNamingStrategy("", "")

        expect:
        strategy.prefix == ""
        strategy.suffix == ""
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
        FixedFeedInInput        || "fixedfeedin_input"
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
        MeasurementUnitInput    || "measurementunit_input"
        EvcsInput               || "evcs_input"
        Transformer2WInput      || "transformer2w_input"
        Transformer3WInput      || "transformer3w_input"
        CylindricalStorageInput || "cylindricalstorage_input"
        ThermalHouseInput       || "thermalhouse_input"
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
        WecCharacteristicInput || "weccharacteristic_input"
        EvCharacteristicInput  || "evcharacteristic_input"
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
        RandomLoadParameters || "randomloadparameters_input"
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

}
