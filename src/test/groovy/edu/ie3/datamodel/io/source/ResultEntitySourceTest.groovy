/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source


import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.CongestionResult
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.util.TimeUtil
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

class ResultEntitySourceTest extends Specification implements FactoryTestHelper {
  @Shared
  private TimeUtil timeUtil = TimeUtil.withDefaults

  def "A NodeResult should should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "vmag"      : "2",
      "vang"      : "2"
    ]

    when:
    def result = ResultEntitySource.nodeResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert vMag == getQuant(parameter["vmag"], StandardUnits.VOLTAGE_MAGNITUDE)
      assert vAng == getQuant(parameter["vang"], StandardUnits.VOLTAGE_ANGLE)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A SwitchResult should should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "closed"    : "true"
    ]

    when:
    def result = ResultEntitySource.switchResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
      assert closed == Boolean.parseBoolean(parameter["closed"])
    }
  }

  def "A LineResult should should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "iamag"     : "1.0",
      "iaang"     : "90",
      "ibmag"     : "0.98123",
      "ibang"     : "90"
    ]

    when:
    def result =  ResultEntitySource.lineResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
      assert iAAng == getQuant(parameter["iaang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
      assert iAMag == getQuant(parameter["iamag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
      assert iBAng == getQuant(parameter["ibang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
      assert iBMag == getQuant(parameter["ibmag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
    }
  }

  def "A Transformer2WResult should should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "iamag"     : "1.0",
      "iaang"     : "90",
      "ibmag"     : "0.98123",
      "ibang"     : "90",
      "tappos"    : "3"
    ]

    when:
    def result =  ResultEntitySource.transformer2WResultBuilder(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
      assert iAAng == getQuant(parameter["iaang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
      assert iAMag == getQuant(parameter["iamag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
      assert iBAng == getQuant(parameter["ibang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
      assert iBMag == getQuant(parameter["ibmag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
    }
  }

  def "A Transformer3WResult should should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "iamag"     : "1.0",
      "iaang"     : "90",
      "ibmag"     : "0.98123",
      "ibang"     : "90",
      "tappos"    : "3",
      "icmag"     : "1.0",
      "icang"     : "90"
    ]

    when:
    def result = ResultEntitySource.transformer3WResultBuilder(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
      assert iAAng == getQuant(parameter["iaang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
      assert iAMag == getQuant(parameter["iamag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
      assert iBAng == getQuant(parameter["ibang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
      assert iBMag == getQuant(parameter["ibmag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
      assert tapPos == Integer.parseInt(parameter["tappos"])
      assert iCAng == getQuant(parameter["icang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
      assert iCMag == getQuant(parameter["icmag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
    }
  }

  def "A FlexOptionsResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "pref"      : "2",
      "pmin"      : "-1",
      "pmax"      : "10",
    ]

    when:
    def result = ResultEntitySource.flexOptionsResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert pRef == getQuant(parameter["pref"], StandardUnits.ACTIVE_POWER_RESULT)
      assert pMin == getQuant(parameter["pmin"], StandardUnits.ACTIVE_POWER_RESULT)
      assert pMax == getQuant(parameter["pmax"], StandardUnits.ACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A CongestionResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "type"      : "line",
      "subgrid"   : "2",
      "value"     : "110.0",
      "min"       : "0.0",
      "max"       : "100.0"
    ]

    when:
    def result = ResultEntitySource.congestionResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert type == CongestionResult.InputModelType.LINE
      assert subgrid == 2
      assert value == getQuant(parameter["value"], Units.PERCENT)
      assert min == getQuant(parameter["min"], Units.PERCENT)
      assert max == getQuant(parameter["max"], Units.PERCENT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A LoadResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2"
    ]

    when:
    def result = ResultEntitySource.loadResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A PvResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2"
    ]

    when:
    def result = ResultEntitySource.pvResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A FixedFeedInResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2"
    ]

    when:
    def result = ResultEntitySource.fixedFeedInResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A BmResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2"
    ]

    when:
    def result = ResultEntitySource.bmResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A ChpResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2",
      "qDot"      : "1"
    ]

    when:
    def result = ResultEntitySource.chpResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert qDot == getQuant(parameter["qDot"], StandardUnits.Q_DOT_RESULT)
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A WecResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2"
    ]

    when:
    def result = ResultEntitySource.wecResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A StorageResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2",
      "soc"       : "10"
    ]

    when:
    def result = ResultEntitySource.storageResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert soc == getQuant(parameter["soc"], Units.PERCENT)
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A EvcsResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2"
    ]

    when:
    def result = ResultEntitySource.evcsResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A EvResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2",
      "soc"       : "10"
    ]

    when:
    def result = ResultEntitySource.evResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert soc == getQuant(parameter["soc"], Units.PERCENT)
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A HpResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2",
      "qDot"      : "1"
    ]

    when:
    def result = ResultEntitySource.hpResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert qDot == getQuant(parameter["qDot"], StandardUnits.Q_DOT_RESULT)
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A CylindricalStorageResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "qDot"      : "2",
      "energy"    : "3",
      "fillLevel" : "20"
    ]

    when:
    def result = ResultEntitySource.cylindricalStorageResultBuilder(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert time == TIME_UTIL.toZonedDateTime(parameter.get("time"))
      assert inputModel == UUID.fromString(parameter.get("inputModel"))
      assert qDot == Quantities.getQuantity(Double.parseDouble(parameter.get("qDot")), StandardUnits.HEAT_DEMAND)
      assert energy == Quantities.getQuantity(Double.parseDouble(parameter.get("energy")), StandardUnits.ENERGY_RESULT)
      assert fillLevel == Quantities.getQuantity(Double.parseDouble(parameter.get("fillLevel")), StandardUnits.FILL_LEVEL)
    }
  }

  def "A DomesticHotWaterStorageResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "qDot"      : "2",
      "energy"    : "3",
      "fillLevel" : "20"
    ]

    when:
    def result = ResultEntitySource.dhwsResultBuilder(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert time == TIME_UTIL.toZonedDateTime(parameter.get("time"))
      assert inputModel == UUID.fromString(parameter.get("inputModel"))
      assert qDot == Quantities.getQuantity(Double.parseDouble(parameter.get("qDot")), StandardUnits.HEAT_DEMAND)
      assert energy == Quantities.getQuantity(Double.parseDouble(parameter.get("energy")), StandardUnits.ENERGY_RESULT)
      assert fillLevel == Quantities.getQuantity(Double.parseDouble(parameter.get("fillLevel")), StandardUnits.FILL_LEVEL)
    }
  }

  def "A ThermalHouseResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"             : "2020-01-30T17:26:44Z",
      "inputModel"       : "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "qDot"             : "2",
      "indoorTemperature": "21"
    ]

    when:
    def result = ResultEntitySource.thermalHouseResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert time == TIME_UTIL.toZonedDateTime(parameter.get("time"))
      assert inputModel == UUID.fromString(parameter.get("inputModel"))
      assert qDot == Quantities.getQuantity(Double.parseDouble(parameter.get("qDot")), StandardUnits.HEAT_DEMAND)
      assert indoorTemperature == Quantities.getQuantity(Double.parseDouble(parameter.get("indoorTemperature")), StandardUnits.TEMPERATURE)
    }
  }

  def "A EmResult should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "p"         : "2",
      "q"         : "2"
    ]

    when:
    def result = ResultEntitySource.emResultBuildFunction(timeUtil).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    result.success
    result.data.get().with {
      assert p == getQuant(parameter["p"], StandardUnits.ACTIVE_POWER_RESULT)
      assert q == getQuant(parameter["q"], StandardUnits.REACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }
}
