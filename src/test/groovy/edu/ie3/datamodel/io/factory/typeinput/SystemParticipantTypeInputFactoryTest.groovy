/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.typeinput

import static edu.ie3.util.quantities.PowerSystemUnits.METRE_PER_SECOND
import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.models.input.system.type.*
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Speed

class SystemParticipantTypeInputFactoryTest extends Specification implements FactoryTestHelper {

  def "A SystemParticipantTypeInputFactory should contain all expected classes for parsing"() {
    given:
    def typeInputFactory = new SystemParticipantTypeInputFactory()
    def expectedClasses = [
      EvTypeInput,
      HpTypeInput,
      BmTypeInput,
      WecTypeInput,
      ChpTypeInput,
      StorageTypeInput
    ]

    expect:
    typeInputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A SystemParticipantTypeInputFactory should parse a valid EvTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory()
    Map<String, String> parameter = [
      "uuid":	    "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":	    "blablub",
      "capex":    "3",
      "opex":	    "4",
      "sratedac":   "5",
      "cosPhiRated":	"6",

      "estorage":	"7",
      "econs":	"8",
      "srateddc":	"9",
    ]
    def typeInputClass = EvTypeInput

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new EntityData(parameter, typeInputClass))

    then:
    typeInput.success
    typeInput.data.get().getClass() == typeInputClass

    ((EvTypeInput) typeInput.data.get()).with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      assert sRatedAC == getQuant(parameter["sratedac"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])

      assert eStorage == getQuant(parameter["estorage"], StandardUnits.ENERGY_IN)
      assert eCons == getQuant(parameter["econs"], StandardUnits.ENERGY_PER_DISTANCE)
      assert sRatedDC == getQuant(parameter["srateddc"], StandardUnits.ACTIVE_POWER_IN)
    }
  }

  def "A SystemParticipantTypeInputFactory should parse a valid HpTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory()
    Map<String, String> parameter = [
      "uuid":	    "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":	    "blablub",
      "capex":    "3",
      "opex":	    "4",
      "srated":   "5",
      "cosPhiRated":	"6",

      "pthermal":	"7",
    ]
    def typeInputClass = HpTypeInput

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new EntityData(parameter, typeInputClass))

    then:
    typeInput.success
    typeInput.data.get().getClass() == typeInputClass

    ((HpTypeInput) typeInput.data.get()).with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])

      assert pThermal == getQuant(parameter["pthermal"], StandardUnits.ACTIVE_POWER_IN)
    }
  }

  def "A SystemParticipantTypeInputFactory should parse a valid BmTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory()
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
    def typeInputClass = BmTypeInput

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new EntityData(parameter, typeInputClass))

    then:
    typeInput.success
    typeInput.data.get().getClass() == typeInputClass

    ((BmTypeInput) typeInput.data.get()).with {
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

  def "A SystemParticipantTypeInputFactory should parse a valid WecTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory()
    Map<String, String> parameter = [
      "uuid":	        "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":	        "blablub",
      "capex":        "3",
      "opex":	        "4",
      "srated":       "5",
      "cosPhiRated":	    "6",

      "cpCharacteristic": "cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}",
      "etaconv":  	"7",
      "rotorarea":    "8",
      "hubheight":    "9"
    ]
    def typeInputClass = WecTypeInput

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new EntityData(parameter, typeInputClass))

    then:
    typeInput.success
    typeInput.data.get().getClass() == typeInputClass

    ((WecTypeInput) typeInput.data.get()).with {
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

  def "A SystemParticipantTypeInputFactory should parse a valid ChpTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory()
    Map<String, String> parameter = [
      "uuid":	                "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":	                "blablub",
      "capex":                "3",
      "opex":	                "4",
      "srated":               "5",
      "cosPhiRated":	            "6",

      "etael":	            "7",
      "etathermal":           "8",
      "pthermal":	            "9",
      "pown":	                "10"
    ]
    def typeInputClass = ChpTypeInput

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new EntityData(parameter, typeInputClass))

    then:
    typeInput.success
    typeInput.data.get().getClass() == typeInputClass

    ((ChpTypeInput) typeInput.data.get()).with {
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

  def "A SystemParticipantTypeInputFactory should parse a valid StorageTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory()
    Map<String, String> parameter = [
      "uuid"                  : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"                    : "blablub",
      "capex"                 : "3",
      "opex"                  : "4",
      "srated"                : "5",
      "cosPhiRated"                : "6",

      "estorage"              : "6",
      "pmax"                  : "8",
      "activepowergradient"   : "1",
      "eta"                   : "9",
      "dod"                   : "10",
      "lifetime"              : "11",
      "lifecycle"             : "12"
    ]
    def typeInputClass = StorageTypeInput

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new EntityData(parameter, typeInputClass))

    then:
    typeInput.success
    typeInput.data.get().getClass() == typeInputClass

    ((StorageTypeInput) typeInput.data.get()).with {
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
      assert dod == getQuant(parameter["dod"], StandardUnits.DOD)
      assert lifeTime == getQuant(parameter["lifetime"], StandardUnits.LIFE_TIME)
      assert lifeCycle == Integer.parseInt(parameter["lifecycle"])
    }
  }

  def "A SystemParticipantTypeInputFactory should throw an exception on invalid or incomplete data"() {
    given: "a system participant factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory()
    def actualFields = SystemParticipantTypeInputFactory.newSet("uuid", "id", "capex", "opex", "srated", "cosPhiRated", "estorage", "pmin", "pmax", "eta", "dod", "lifetime",)

    when:
    def input = typeInputFactory.validate(actualFields, StorageTypeInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [capex, cosPhiRated, dod, estorage, eta, id, lifetime, opex, pmax, pmin, srated, uuid] are invalid for instance of 'StorageTypeInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'StorageTypeInput' are possible (NOT case-sensitive!):\n" +
        "0: [activePowerGradient, capex, cosPhiRated, dod, eStorage, eta, id, lifeCycle, lifeTime, opex, pMax, sRated, uuid] or [active_power_gradient, capex, cos_phi_rated, dod, e_storage, eta, id, life_cycle, life_time, opex, p_max, s_rated, uuid]\n"
  }
}
