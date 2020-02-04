package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.system.type.*
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class SystemParticipantTypeInputFactoryTest extends Specification {

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
        Map<String, String> parameter = [:]
        parameter["uuid"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
        parameter["id"] = "blablub"
        parameter["capex"] = "3"
        parameter["opex"] = "4"
        parameter["cosphi"] = "5"
        parameter["estorage"] = "6"
        parameter["econs"] = "7"
        parameter["srated"] = "8"
        def typeInputClass = EvTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameter["uuid"])
        typeInput.get().id == parameter["id"]
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameter["capex"]), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameter["opex"]), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameter["cosphi"])

        ((EvTypeInput) typeInput.get()).EStorage == Quantities.getQuantity(Double.parseDouble(parameter["estorage"]), StandardUnits.ENERGY)
        ((EvTypeInput) typeInput.get()).ECons == Quantities.getQuantity(Double.parseDouble(parameter["econs"]), PowerSystemUnits.WATTHOUR_PER_METRE) // TODO
        ((EvTypeInput) typeInput.get()).SRated == Quantities.getQuantity(Double.parseDouble(parameter["srated"]), StandardUnits.S_RATED)
    }

    def "A SystemParticipantTypeInputFactory should parse a valid HpTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameter = [:]
        parameter["uuid"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8"
        parameter["id"] = "blablub1"
        parameter["capex"] = "3"
        parameter["opex"] = "4"
        parameter["cosphi"] = "5"
        parameter["prated"] = "6"
        parameter["pthermal"] = "7"
        parameter["pel"] = "8"
        def typeInputClass = HpTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameter["uuid"])
        typeInput.get().id == parameter["id"]
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameter["capex"]), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameter["opex"]), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameter["cosphi"])

        ((HpTypeInput) typeInput.get()).PRated == Quantities.getQuantity(Double.parseDouble(parameter["prated"]), StandardUnits.ACTIVE_POWER_IN)
        ((HpTypeInput) typeInput.get()).PThermal == Quantities.getQuantity(Double.parseDouble(parameter["pthermal"]), StandardUnits.ACTIVE_POWER_IN)
        ((HpTypeInput) typeInput.get()).PEl == Quantities.getQuantity(Double.parseDouble(parameter["pel"]), StandardUnits.ACTIVE_POWER_IN)
    }

    def "A SystemParticipantTypeInputFactory should parse a valid BmTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameter = [:]
        parameter["uuid"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8"
        parameter["id"] = "blablub1"
        parameter["capex"] = "3"
        parameter["opex"] = "4"
        parameter["cosphi"] = "5"
        parameter["loadgradient"] = "6"
        parameter["srated"] = "7"
        parameter["etaconv"] = "8"
        def typeInputClass = BmTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameter["uuid"])
        typeInput.get().id == parameter["id"]
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameter["capex"]), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameter["opex"]), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameter["cosphi"])

        ((BmTypeInput) typeInput.get()).loadGradient == Quantities.getQuantity(Double.parseDouble(parameter["loadgradient"]), StandardUnits.LOAD_GRADIENT)
        ((BmTypeInput) typeInput.get()).SRated == Quantities.getQuantity(Double.parseDouble(parameter["srated"]), StandardUnits.S_RATED)
        ((BmTypeInput) typeInput.get()).etaConv == Quantities.getQuantity(Double.parseDouble(parameter["etaconv"]), StandardUnits.EFFICIENCY)
    }

    def "A SystemParticipantTypeInputFactory should parse a valid WecTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameter = [:]
        parameter["uuid"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8"
        parameter["id"] = "blablub1"
        parameter["capex"] = "3"
        parameter["opex"] = "4"
        parameter["cosphi"] = "5"
        parameter["etaconv"] = "6"
        parameter["srated"] = "7"
        parameter["rotorarea"] = "8"
        parameter["hubheight"] = "9"
        def typeInputClass = WecTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameter["uuid"])
        typeInput.get().id == parameter["id"]
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameter["capex"]), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameter["opex"]), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameter["cosphi"])

        ((WecTypeInput) typeInput.get()).etaConv == Quantities.getQuantity(Double.parseDouble(parameter["etaconv"]), StandardUnits.EFFICIENCY)
        ((WecTypeInput) typeInput.get()).SRated == Quantities.getQuantity(Double.parseDouble(parameter["srated"]), StandardUnits.S_RATED)
        ((WecTypeInput) typeInput.get()).rotorArea == Quantities.getQuantity(Double.parseDouble(parameter["rotorarea"]), StandardUnits.ROTOR_AREA)
        ((WecTypeInput) typeInput.get()).hubHeight == Quantities.getQuantity(Double.parseDouble(parameter["hubheight"]), StandardUnits.HUB_HEIGHT)
    }

    def "A SystemParticipantTypeInputFactory should parse a valid ChpTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameter = [:]
        parameter["uuid"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8"
        parameter["id"] = "blablub1"
        parameter["capex"] = "3"
        parameter["opex"] = "4"
        parameter["cosphi"] = "5"
        parameter["etael"] = "6"
        parameter["etathermal"] = "7"
        parameter["pel"] = "8"
        parameter["pthermal"] = "9"
        parameter["pown"] = "10"
        parameter["storagevolumelvl"] = "11"
        parameter["storagevolumelvlmin"] = "12"
        parameter["inlettemp"] = "13"
        parameter["returntemp"] = "14"
        parameter["c"] = "15"
        def typeInputClass = ChpTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameter["uuid"])
        typeInput.get().id == parameter["id"]
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameter["capex"]), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameter["opex"]), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameter["cosphi"])

        ((ChpTypeInput) typeInput.get()).etaEl == Quantities.getQuantity(Double.parseDouble(parameter["etael"]), StandardUnits.EFFICIENCY)
        ((ChpTypeInput) typeInput.get()).etaThermal == Quantities.getQuantity(Double.parseDouble(parameter["etathermal"]), StandardUnits.EFFICIENCY)
        ((ChpTypeInput) typeInput.get()).PEl == Quantities.getQuantity(Double.parseDouble(parameter["pel"]), StandardUnits.ACTIVE_POWER_IN)
        ((ChpTypeInput) typeInput.get()).PThermal == Quantities.getQuantity(Double.parseDouble(parameter["pthermal"]), StandardUnits.ACTIVE_POWER_IN)
        ((ChpTypeInput) typeInput.get()).POwn == Quantities.getQuantity(Double.parseDouble(parameter["pown"]), StandardUnits.ACTIVE_POWER_IN)
        // the rest of parameters is not saved in class attributes
    }

    def "A SystemParticipantTypeInputFactory should parse a valid StorageTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameter = [:]
        parameter["uuid"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8"
        parameter["id"] = "blablub1"
        parameter["capex"] = "3"
        parameter["opex"] = "4"
        parameter["cosphi"] = "5"
        parameter["estorage"] = "6"
        parameter["prated"] = "7"
        parameter["pmin"] = "8"
        parameter["pmax"] = "9"
        parameter["eta"] = "10"
        parameter["dod"] = "11"
        parameter["lifetime"] = "12"
        parameter["lifecycle"] = "13"
        def typeInputClass = StorageTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameter["uuid"])
        typeInput.get().id == parameter["id"]
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameter["capex"]), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameter["opex"]), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameter["cosphi"])

        ((StorageTypeInput) typeInput.get()).EStorage == Quantities.getQuantity(Double.parseDouble(parameter["estorage"]), StandardUnits.ENERGY)
        ((StorageTypeInput) typeInput.get()).PRated == Quantities.getQuantity(Double.parseDouble(parameter["prated"]), StandardUnits.ACTIVE_POWER_IN)
        ((StorageTypeInput) typeInput.get()).PMin == Quantities.getQuantity(Double.parseDouble(parameter["pmin"]), StandardUnits.ACTIVE_POWER_IN)
        ((StorageTypeInput) typeInput.get()).PMax == Quantities.getQuantity(Double.parseDouble(parameter["pmax"]), StandardUnits.ACTIVE_POWER_IN)
        ((StorageTypeInput) typeInput.get()).eta == Quantities.getQuantity(Double.parseDouble(parameter["eta"]), StandardUnits.EFFICIENCY)
        ((StorageTypeInput) typeInput.get()).dod == Quantities.getQuantity(Double.parseDouble(parameter["dod"]), StandardUnits.DOD)
        ((StorageTypeInput) typeInput.get()).lifeTime == Quantities.getQuantity(Double.parseDouble(parameter["lifetime"]), StandardUnits.LIFE_TIME)
        ((StorageTypeInput) typeInput.get()).lifeCycle == Integer.parseInt(parameter["lifecycle"])
    }

    def "A SystemParticipantTypeInputFactory should throw an exception on invalid or incomplete data"() {
        given: "a system participant factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameter = [:]
        parameter["uuid"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8"
        parameter["id"] = "blablub1"
        parameter["capex"] = "3"
        parameter["opex"] = "4"
        parameter["cosphi"] = "5"
        parameter["estorage"] = "6"
        parameter["prated"] = "7"
        parameter["pmax"] = "9"
        parameter["eta"] = "10"
        parameter["dod"] = "11"
        parameter["lifetime"] = "12"
        parameter["lifecycle"] = "13"

        when:
        typeInputFactory.getEntity(new SimpleEntityData(parameter, StorageTypeInput))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [capex, cosphi, dod, estorage, eta, id, lifecycle, lifetime, opex, pmax, prated, uuid] with data {capex -> 3,cosphi -> 5,dod -> 11,estorage -> 6,eta -> 10,id -> blablub1,lifecycle -> 13,lifetime -> 12,opex -> 4,pmax -> 9,prated -> 7,uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c8} are invalid for instance of StorageTypeInput. \n" +
                "The following fields to be passed to a constructor of StorageTypeInput are possible:\n" +
                "0: [capex, cosphi, dod, estorage, eta, id, lifecycle, lifetime, opex, pmax, pmin, prated, uuid]\n"
    }
}
