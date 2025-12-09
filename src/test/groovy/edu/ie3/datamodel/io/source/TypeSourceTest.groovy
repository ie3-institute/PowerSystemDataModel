/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.util.quantities.PowerSystemUnits.PU
import static tech.units.indriya.unit.Units.METRE_PER_SECOND

import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Speed

class TypeSourceTest extends Specification implements FactoryTestHelper {

  def "A OperatorInput should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid":     "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":       "blablub",
    ]

    when:
    def typeInput = TypeSource.operatorBuildFunction.apply(new Try.Success<>(new EntityData(parameter)))

    then:
    typeInput.success
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
    }
  }

  def "A LineTypeInput should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid":     "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":       "blablub",
      "b":        "3",
      "g":        "4",
      "r":        "5",
      "x":        "6",
      "imax":     "7",
      "vrated":   "8"
    ]

    when:
    def typeInput = TypeSource.lineTypeBuildFunction.apply(new Try.Success<>(new EntityData(parameter)))

    then:
    typeInput.success
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert b == getQuant(parameter["b"], StandardUnits.SUSCEPTANCE_PER_LENGTH)
      assert g == getQuant(parameter["g"], StandardUnits.CONDUCTANCE_PER_LENGTH)
      assert r == getQuant(parameter["r"], StandardUnits.RESISTANCE_PER_LENGTH)
      assert x == getQuant(parameter["x"], StandardUnits.REACTANCE_PER_LENGTH)
      assert iMax == getQuant(parameter["imax"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
      assert vRated == getQuant(parameter["vrated"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
    }
  }

  def "A Transformer2WTypeInput should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid":     "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":       "blablub",
      "rsc":      "3",
      "xsc":      "4",
      "srated":   "5",
      "vrateda":  "6",
      "vratedb":  "7",
      "gm":       "8",
      "bm":       "9",
      "dv":       "10",
      "dphi":     "11",
      "tapside":  "1",
      "tapneutr": "12",
      "tapmin":   "13",
      "tapmax":   "14"
    ]

    when:
    def typeInput = TypeSource.transformer2WTypeBuildFunction.apply(new Try.Success<>(new EntityData(parameter)))

    then:
    typeInput.success
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert rSc == getQuant(parameter["rsc"], StandardUnits.RESISTANCE)
      assert xSc == getQuant(parameter["xsc"], StandardUnits.REACTANCE)
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert vRatedA == getQuant(parameter["vrateda"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      assert vRatedB == getQuant(parameter["vratedb"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      assert gM == getQuant(parameter["gm"], StandardUnits.CONDUCTANCE)
      assert bM == getQuant(parameter["bm"], StandardUnits.SUSCEPTANCE)
      assert dV == getQuant(parameter["dv"], StandardUnits.DV_TAP)
      assert dPhi == getQuant(parameter["dphi"], StandardUnits.DPHI_TAP)
      assert tapSide == (parameter["tapside"].trim() == "1") || parameter["tapside"].trim() == "true"
      assert tapNeutr == Integer.parseInt(parameter["tapneutr"])
      assert tapMin == Integer.parseInt(parameter["tapmin"])
      assert tapMax == Integer.parseInt(parameter["tapmax"])
    }
  }

  def "A Transformer3WTypeInput should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid":	    "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":   	"blablub",
      "srateda":	"3",
      "sratedb":	"4",
      "sratedc":	"5",
      "vrateda":	"6",
      "vratedb":	"7",
      "vratedc":	"8",
      "rsca":	    "9",
      "rscb":	    "10",
      "rscc":	    "11",
      "xsca":	    "12",
      "xscb":	    "13",
      "xscc":	    "14",
      "gm":	    "15",
      "bm":	    "16",
      "dv":   	"17",
      "dphi":	    "18",
      "tapneutr":	"19",
      "tapmin":	"20",
      "tapmax":	"21"
    ]

    when:
    def typeInput = TypeSource.transformer3WTypeBuildFunction.apply(new Try.Success<>(new EntityData(parameter)))

    then:
    typeInput.success
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert sRatedA == getQuant(parameter["srateda"], StandardUnits.S_RATED)
      assert sRatedB == getQuant(parameter["sratedb"], StandardUnits.S_RATED)
      assert sRatedC == getQuant(parameter["sratedc"], StandardUnits.S_RATED)
      assert vRatedA == getQuant(parameter["vrateda"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      assert vRatedB == getQuant(parameter["vratedb"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      assert vRatedC == getQuant(parameter["vratedc"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      assert rScA == getQuant(parameter["rsca"], StandardUnits.RESISTANCE)
      assert rScB == getQuant(parameter["rscb"], StandardUnits.RESISTANCE)
      assert rScC == getQuant(parameter["rscc"], StandardUnits.RESISTANCE)
      assert xScA == getQuant(parameter["xsca"], StandardUnits.REACTANCE)
      assert xScB == getQuant(parameter["xscb"], StandardUnits.REACTANCE)
      assert xScC == getQuant(parameter["xscc"], StandardUnits.REACTANCE)
      assert gM == getQuant(parameter["gm"], StandardUnits.CONDUCTANCE)
      assert bM == getQuant(parameter["bm"], StandardUnits.SUSCEPTANCE)
      assert dV == getQuant(parameter["dv"], StandardUnits.DV_TAP)
      assert dPhi == getQuant(parameter["dphi"], StandardUnits.DPHI_TAP)
      assert tapNeutr == Integer.parseInt(parameter["tapneutr"])
      assert tapMin == Integer.parseInt(parameter["tapmin"])
      assert tapMax == Integer.parseInt(parameter["tapmax"])
    }
  }

  def "A BmTypeInput should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid":	        "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":	        "blablub",
      "capex":        "3",
      "opex":	        "4",
      "srated":       "5",
      "cosPhiRated":	    "6",
      "activepowergradient":	"7",
      "etaconv":      "8"
    ]

    when:
    def typeInput = TypeSource.bmTypeBuildFunction.apply(new Try.Success<>(new EntityData(parameter)))

    then:
    typeInput.success
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])
      assert activePowerGradient == getQuant(parameter["activepowergradient"], StandardUnits.ACTIVE_POWER_GRADIENT)
      assert etaConv == getQuant(parameter["etaconv"], StandardUnits.EFFICIENCY)
    }
  }

  def "A ChpTypeInput should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid":	                "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":	                "blablub",
      "capex":                "3",
      "opex":	                "4",
      "srated":               "5",
      "cosPhiRated":	        "6",
      "etael":	            "7",
      "etathermal":           "8",
      "pthermal":	            "9",
      "pown":	                "10"
    ]

    when:
    def typeInput = TypeSource.chpTypeBuildFunction.apply(new Try.Success<>(new EntityData(parameter)))

    then:
    typeInput.success
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])
      assert etaEl == getQuant(parameter["etael"], StandardUnits.EFFICIENCY)
      assert etaThermal == getQuant(parameter["etathermal"], StandardUnits.EFFICIENCY)
      assert pThermal == getQuant(parameter["pthermal"], StandardUnits.ACTIVE_POWER_IN)
      assert pOwn == getQuant(parameter["pown"], StandardUnits.ACTIVE_POWER_IN)
    }
  }

  def "A HpTypeInput should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid":	        "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":	        "blablub",
      "capex":        "3",
      "opex":	        "4",
      "srated":       "5",
      "cosPhiRated":	"6",
      "pthermal":	    "7",
    ]

    when:
    def typeInput = TypeSource.hpTypeBuildFunction.apply(new Try.Success<>(new EntityData(parameter)))

    then:
    typeInput.success
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])
      assert pThermal == getQuant(parameter["pthermal"], StandardUnits.ACTIVE_POWER_IN)
    }
  }

  def "A StorageTypeInput should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"                  : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"                    : "blablub",
      "capex"                 : "3",
      "opex"                  : "4",
      "srated"                : "5",
      "cosPhiRated"           : "6",
      "estorage"              : "6",
      "pmax"                  : "8",
      "activepowergradient"   : "1",
      "eta"                   : "9"
    ]

    when:
    def typeInput = TypeSource.storageBuildFunction.apply(new Try.Success<>(new EntityData(parameter)))

    then:
    typeInput.success
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])
      assert eStorage == getQuant(parameter["estorage"], StandardUnits.ENERGY_IN)
      assert pMax == getQuant(parameter["pmax"], StandardUnits.ACTIVE_POWER_IN)
      assert activePowerGradient == getQuant(parameter["activepowergradient"], StandardUnits.ACTIVE_POWER_GRADIENT)
      assert eta == getQuant(parameter["eta"], StandardUnits.EFFICIENCY)
    }
  }

  def "A WecTypeInput should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid":	            "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":	            "blablub",
      "capex":            "3",
      "opex":	            "4",
      "srated":           "5",
      "cosPhiRated":	    "6",
      "cpCharacteristic": "cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}",
      "etaconv":  	    "7",
      "rotorarea":        "8",
      "hubheight":        "9"
    ]

    when:
    def typeInput = TypeSource.wecTypeBuildFunction.apply(new Try.Success<>(new EntityData(parameter)))

    then:
    typeInput.success
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])
      cpCharacteristic.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Speed, Dimensionless>(Quantities.getQuantity(10d, METRE_PER_SECOND), Quantities.getQuantity(0.05, PU)),
          new CharacteristicPoint<Speed, Dimensionless>(Quantities.getQuantity(15d, METRE_PER_SECOND), Quantities.getQuantity(0.1, PU)),
          new CharacteristicPoint<Speed, Dimensionless>(Quantities.getQuantity(20d, METRE_PER_SECOND), Quantities.getQuantity(0.2, PU))
        ] as TreeSet)
      }
      assert etaConv == getQuant(parameter["etaconv"], StandardUnits.EFFICIENCY)
      assert rotorArea == getQuant(parameter["rotorarea"], StandardUnits.ROTOR_AREA)
      assert hubHeight == getQuant(parameter["hubheight"], StandardUnits.HUB_HEIGHT)
    }
  }

  def "A EvTypeInput should be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid":	        "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":	        "blablub",
      "capex":        "3",
      "opex":	        "4",
      "srated":       "5",
      "cosPhiRated":  "6",
      "estorage": 	"7",
      "econs":	    "8",
      "srateddc":	    "9",
    ]

    when:
    def typeInput = TypeSource.evTypeBuildFunction.apply(new Try.Success<>(new EntityData(parameter)))

    then:
    typeInput.success
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])

      assert eStorage == getQuant(parameter["estorage"], StandardUnits.ENERGY_IN)
      assert eCons == getQuant(parameter["econs"], StandardUnits.ENERGY_PER_DISTANCE)
      assert sRatedDC == getQuant(parameter["srateddc"], StandardUnits.ACTIVE_POWER_IN)
    }
  }
}
