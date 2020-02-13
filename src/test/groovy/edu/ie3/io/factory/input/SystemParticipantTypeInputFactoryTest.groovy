package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.system.type.*
import spock.lang.Specification

class SystemParticipantTypeInputFactoryTest extends Specification implements FactoryTestHelper {

    def "A SystemParticipantTypeInputFactory should contain all expected classes for parsing"() {
        given:
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        def expectedClasses = [EvTypeInput, HpTypeInput, BmTypeInput, WecTypeInput, ChpTypeInput, StorageTypeInput]

        expect:
        typeInputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A SystemParticipantTypeInputFactory should parse a valid EvTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameter = [
            "uuid":	    "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "id":	    "blablub",
            "capex":    "3",
            "opex":	    "4",
            "srated":   "5",
            "cosphi":	"6",

            "estorage":	"7",
            "econs":	"8",
        ]
        def typeInputClass = EvTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass

        ((EvTypeInput) typeInput.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert id == parameter["id"]
            assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
            assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
            assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphi"])

            assert EStorage == getQuant(parameter["estorage"], StandardUnits.ENERGY_IN)
            assert ECons == getQuant(parameter["econs"], StandardUnits.ENERGY_PER_DISTANCE)
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
            "cosphi":	"6",

            "pthermal":	"7",
        ]
        def typeInputClass = HpTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass

        ((HpTypeInput) typeInput.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert id == parameter["id"]
            assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
            assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
            assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphi"])

            assert PThermal == getQuant(parameter["pthermal"], StandardUnits.ACTIVE_POWER_IN)
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
            "cosphi":	    "6",

            "loadgradient":	"7",
            "etaconv":      "8"
        ]
        def typeInputClass = BmTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass

        ((BmTypeInput) typeInput.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert id == parameter["id"]
            assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
            assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
            assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphi"])

            assert loadGradient == getQuant(parameter["loadgradient"], StandardUnits.LOAD_GRADIENT)
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
            "cosphi":	    "6",

            "etaconv":  	"7",
            "rotorarea":    "8",
            "hubheight":    "9"
        ]
        def typeInputClass = WecTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass

        ((WecTypeInput) typeInput.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert id == parameter["id"]
            assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
            assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
            assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphi"])

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
            "cosphi":	            "6",

            "etael":	            "7",
            "etathermal":           "8",
            "pthermal":	            "9",
            "pown":	                "10"
        ]
        def typeInputClass = ChpTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass

        ((ChpTypeInput) typeInput.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert id == parameter["id"]
            assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
            assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
            assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphi"])

            assert etaEl == getQuant(parameter["etael"], StandardUnits.EFFICIENCY)
            assert etaThermal == getQuant(parameter["etathermal"], StandardUnits.EFFICIENCY)
            assert PThermal == getQuant(parameter["pthermal"], StandardUnits.ACTIVE_POWER_IN)
            assert POwn == getQuant(parameter["pown"], StandardUnits.ACTIVE_POWER_IN)
        }
    }

    def "A SystemParticipantTypeInputFactory should parse a valid StorageTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameter = [
            "uuid":	        "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "id":	        "blablub",
            "capex":        "3",
            "opex":	        "4",
            "srated":       "5",
            "cosphi":	    "6",

            "estorage":	    "6",
            "pmin":	        "7",
            "pmax":	        "8",
            "eta":	        "9",
            "dod":	        "10",
            "lifetime":	    "11",
            "lifecycle":    "12"
        ]
        def typeInputClass = StorageTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))
        
        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        
        ((StorageTypeInput) typeInput.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert id == parameter["id"]
            assert capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
            assert opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
            assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphi"])

            assert EStorage == getQuant(parameter["estorage"], StandardUnits.ENERGY_IN)
            assert PMin == getQuant(parameter["pmin"], StandardUnits.ACTIVE_POWER_IN)
            assert PMax == getQuant(parameter["pmax"], StandardUnits.ACTIVE_POWER_IN)
            assert eta == getQuant(parameter["eta"], StandardUnits.EFFICIENCY)
            assert dod == getQuant(parameter["dod"], StandardUnits.DOD)
            assert lifeTime == getQuant(parameter["lifetime"], StandardUnits.LIFE_TIME)
            assert lifeCycle == Integer.parseInt(parameter["lifecycle"])
        }
    }

    def "A SystemParticipantTypeInputFactory should throw an exception on invalid or incomplete data"() {
        given: "a system participant factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameter = [
            "uuid":	        "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "id":	        "blablub",
            "capex":        "3",
            "opex":	        "4",
            "srated":       "5",
            "cosphi":	    "6",

            "estorage":	    "6",
            "pmin":	        "7",
            "pmax":	        "8",
            "eta":	        "9",
            "dod":	        "10",
            "lifetime":	    "11"
        ]

        when:
        typeInputFactory.getEntity(new SimpleEntityData(parameter, StorageTypeInput))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [capex, cosphi, dod, estorage, eta, id, lifetime, opex, pmax, pmin, srated, uuid] with data {capex -> 3,cosphi -> 6,dod -> 10,estorage -> 6,eta -> 9,id -> blablub,lifetime -> 11,opex -> 4,pmax -> 8,pmin -> 7,srated -> 5,uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7} are invalid for instance of StorageTypeInput. \n" +
                "The following fields to be passed to a constructor of StorageTypeInput are possible:\n" +
                "0: [capex, cosphi, dod, estorage, eta, id, lifecycle, lifetime, opex, pmax, pmin, srated, uuid]\n"
    }
}
