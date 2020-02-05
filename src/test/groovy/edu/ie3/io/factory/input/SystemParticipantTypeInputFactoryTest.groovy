package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.io.factory.FactorySpecification
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.system.type.*
import edu.ie3.util.quantities.PowerSystemUnits

class SystemParticipantTypeInputFactoryTest extends FactorySpecification {

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

        def evTI = (EvTypeInput) typeInput.get()
        evTI.uuid == UUID.fromString(parameter["uuid"])
        evTI.id == parameter["id"]
        evTI.capex == getQuant(parameter["capex"], PowerSystemUnits.EURO) // TODO StandardUnit
        evTI.opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
        evTI.cosphi == Double.parseDouble(parameter["cosphi"])

        evTI.EStorage == getQuant(parameter["estorage"], StandardUnits.ENERGY)
        evTI.ECons == getQuant(parameter["econs"], PowerSystemUnits.WATTHOUR_PER_METRE) // TODO
        evTI.SRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
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

        def hpTI = (HpTypeInput) typeInput.get()
        hpTI.uuid == UUID.fromString(parameter["uuid"])
        hpTI.id == parameter["id"]
        hpTI.capex == getQuant(parameter["capex"], PowerSystemUnits.EURO) // TODO StandardUnit
        hpTI.opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
        hpTI.cosphi == Double.parseDouble(parameter["cosphi"])

        hpTI.PRated == getQuant(parameter["prated"], StandardUnits.ACTIVE_POWER_IN)
        hpTI.PThermal == getQuant(parameter["pthermal"], StandardUnits.ACTIVE_POWER_IN)
        hpTI.PEl == getQuant(parameter["pel"], StandardUnits.ACTIVE_POWER_IN)
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

        def bmTI = (BmTypeInput) typeInput.get()
        bmTI.uuid == UUID.fromString(parameter["uuid"])
        bmTI.id == parameter["id"]
        bmTI.capex == getQuant(parameter["capex"], PowerSystemUnits.EURO) // TODO StandardUnit
        bmTI.opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
        bmTI.cosphi == Double.parseDouble(parameter["cosphi"])

        bmTI.loadGradient == getQuant(parameter["loadgradient"], StandardUnits.LOAD_GRADIENT)
        bmTI.SRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
        bmTI.etaConv == getQuant(parameter["etaconv"], StandardUnits.EFFICIENCY)
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

        def wecTI = (WecTypeInput) typeInput.get()
        wecTI.uuid == UUID.fromString(parameter["uuid"])
        wecTI.id == parameter["id"]
        wecTI.capex == getQuant(parameter["capex"], PowerSystemUnits.EURO) // TODO StandardUnit
        wecTI.opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
        wecTI.cosphi == Double.parseDouble(parameter["cosphi"])

        wecTI.etaConv == getQuant(parameter["etaconv"], StandardUnits.EFFICIENCY)
        wecTI.SRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
        wecTI.rotorArea == getQuant(parameter["rotorarea"], StandardUnits.ROTOR_AREA)
        wecTI.hubHeight == getQuant(parameter["hubheight"], StandardUnits.HUB_HEIGHT)
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

        def chpTI = (ChpTypeInput) typeInput.get()
        chpTI.uuid == UUID.fromString(parameter["uuid"])
        chpTI.id == parameter["id"]
        chpTI.capex == getQuant(parameter["capex"], PowerSystemUnits.EURO) // TODO StandardUnit
        chpTI.opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
        chpTI.cosphi == Double.parseDouble(parameter["cosphi"])

        chpTI.etaEl == getQuant(parameter["etael"], StandardUnits.EFFICIENCY)
        chpTI.etaThermal == getQuant(parameter["etathermal"], StandardUnits.EFFICIENCY)
        chpTI.PEl == getQuant(parameter["pel"], StandardUnits.ACTIVE_POWER_IN)
        chpTI.PThermal == getQuant(parameter["pthermal"], StandardUnits.ACTIVE_POWER_IN)
        chpTI.POwn == getQuant(parameter["pown"], StandardUnits.ACTIVE_POWER_IN)
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
        
        def storageTI = (StorageTypeInput) typeInput.get()
        storageTI.uuid == UUID.fromString(parameter["uuid"])
        storageTI.id == parameter["id"]
        storageTI.capex == getQuant(parameter["capex"], PowerSystemUnits.EURO) // TODO StandardUnit
        storageTI.opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
        storageTI.cosphi == Double.parseDouble(parameter["cosphi"])

        storageTI.EStorage == getQuant(parameter["estorage"], StandardUnits.ENERGY)
        storageTI.PRated == getQuant(parameter["prated"], StandardUnits.ACTIVE_POWER_IN)
        storageTI.PMin == getQuant(parameter["pmin"], StandardUnits.ACTIVE_POWER_IN)
        storageTI.PMax == getQuant(parameter["pmax"], StandardUnits.ACTIVE_POWER_IN)
        storageTI.eta == getQuant(parameter["eta"], StandardUnits.EFFICIENCY)
        storageTI.dod == getQuant(parameter["dod"], StandardUnits.DOD)
        storageTI.lifeTime == getQuant(parameter["lifetime"], StandardUnits.LIFE_TIME)
        storageTI.lifeCycle == Integer.parseInt(parameter["lifecycle"])
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
