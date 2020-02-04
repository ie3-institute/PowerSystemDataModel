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
        Map<String, String> parameterMap = new HashMap<>()
        parameterMap.put("uuid", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7")
        parameterMap.put("id", "blablub")
        parameterMap.put("capex", "3")
        parameterMap.put("opex", "4")
        parameterMap.put("cosphi", "5")
        parameterMap.put("estorage", "6")
        parameterMap.put("econs", "7")
        parameterMap.put("srated", "8")
        def typeInputClass = EvTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameterMap, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameterMap.get("uuid"))
        typeInput.get().id == parameterMap.get("id")
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("capex")), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("opex")), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameterMap.get("cosphi"))

        ((EvTypeInput) typeInput.get()).getEStorage() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("estorage")), StandardUnits.ENERGY)
        ((EvTypeInput) typeInput.get()).getECons() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("econs")), PowerSystemUnits.WATTHOUR_PER_METRE) // TODO
        ((EvTypeInput) typeInput.get()).getSRated() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("srated")), StandardUnits.S_RATED)
    }

    def "A SystemParticipantTypeInputFactory should parse a valid HpTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameterMap = new HashMap<>()
        parameterMap.put("uuid", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8")
        parameterMap.put("id", "blablub1")
        parameterMap.put("capex", "3")
        parameterMap.put("opex", "4")
        parameterMap.put("cosphi", "5")
        parameterMap.put("prated", "6")
        parameterMap.put("pthermal", "7")
        parameterMap.put("pel", "8")
        def typeInputClass = HpTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameterMap, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameterMap.get("uuid"))
        typeInput.get().id == parameterMap.get("id")
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("capex")), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("opex")), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameterMap.get("cosphi"))

        ((HpTypeInput) typeInput.get()).getPRated() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("prated")), StandardUnits.ACTIVE_POWER_IN)
        ((HpTypeInput) typeInput.get()).getPThermal() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("pthermal")), StandardUnits.ACTIVE_POWER_IN)
        ((HpTypeInput) typeInput.get()).getPEl() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("pel")), StandardUnits.ACTIVE_POWER_IN)
    }

    def "A SystemParticipantTypeInputFactory should parse a valid BmTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameterMap = new HashMap<>()
        parameterMap.put("uuid", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8")
        parameterMap.put("id", "blablub1")
        parameterMap.put("capex", "3")
        parameterMap.put("opex", "4")
        parameterMap.put("cosphi", "5")
        parameterMap.put("loadgradient", "6")
        parameterMap.put("srated", "7")
        parameterMap.put("etaconv", "8")
        def typeInputClass = BmTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameterMap, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameterMap.get("uuid"))
        typeInput.get().id == parameterMap.get("id")
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("capex")), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("opex")), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameterMap.get("cosphi"))

        ((BmTypeInput) typeInput.get()).getLoadGradient() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("loadgradient")), StandardUnits.LOAD_GRADIENT)
        ((BmTypeInput) typeInput.get()).getSRated() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("srated")), StandardUnits.S_RATED)
        ((BmTypeInput) typeInput.get()).getEtaConv() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("etaconv")), StandardUnits.EFFICIENCY)
    }

    def "A SystemParticipantTypeInputFactory should parse a valid WecTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameterMap = new HashMap<>()
        parameterMap.put("uuid", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8")
        parameterMap.put("id", "blablub1")
        parameterMap.put("capex", "3")
        parameterMap.put("opex", "4")
        parameterMap.put("cosphi", "5")
        parameterMap.put("etaconv", "6")
        parameterMap.put("srated", "7")
        parameterMap.put("rotorarea", "8")
        parameterMap.put("hubheight", "9")
        def typeInputClass = WecTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameterMap, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameterMap.get("uuid"))
        typeInput.get().id == parameterMap.get("id")
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("capex")), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("opex")), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameterMap.get("cosphi"))

        ((WecTypeInput) typeInput.get()).getEtaConv() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("etaconv")), StandardUnits.EFFICIENCY)
        ((WecTypeInput) typeInput.get()).getSRated() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("srated")), StandardUnits.S_RATED)
        ((WecTypeInput) typeInput.get()).getRotorArea() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("rotorarea")), StandardUnits.ROTOR_AREA)
        ((WecTypeInput) typeInput.get()).getHubHeight() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("hubheight")), StandardUnits.HUB_HEIGHT)
    }

    def "A SystemParticipantTypeInputFactory should parse a valid ChpTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameterMap = new HashMap<>()
        parameterMap.put("uuid", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8")
        parameterMap.put("id", "blablub1")
        parameterMap.put("capex", "3")
        parameterMap.put("opex", "4")
        parameterMap.put("cosphi", "5")
        parameterMap.put("etael", "6")
        parameterMap.put("etathermal", "7")
        parameterMap.put("pel", "8")
        parameterMap.put("pthermal", "9")
        parameterMap.put("pown", "10")
        parameterMap.put("storagevolumelvl", "11")
        parameterMap.put("storagevolumelvlmin", "12")
        parameterMap.put("inlettemp", "13")
        parameterMap.put("returntemp", "14")
        parameterMap.put("c", "15")
        def typeInputClass = ChpTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameterMap, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameterMap.get("uuid"))
        typeInput.get().id == parameterMap.get("id")
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("capex")), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("opex")), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameterMap.get("cosphi"))

        ((ChpTypeInput) typeInput.get()).getEtaEl() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("etael")), StandardUnits.EFFICIENCY)
        ((ChpTypeInput) typeInput.get()).getEtaThermal() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("etathermal")), StandardUnits.EFFICIENCY)
        ((ChpTypeInput) typeInput.get()).getPEl() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("pel")), StandardUnits.ACTIVE_POWER_IN)
        ((ChpTypeInput) typeInput.get()).getPThermal() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("pthermal")), StandardUnits.ACTIVE_POWER_IN)
        ((ChpTypeInput) typeInput.get()).getPOwn() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("pown")), StandardUnits.ACTIVE_POWER_IN)
        // the rest of parameters is not saved in class attributes
    }

    def "A SystemParticipantTypeInputFactory should parse a valid StorageTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameterMap = new HashMap<>()
        parameterMap.put("uuid", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8")
        parameterMap.put("id", "blablub1")
        parameterMap.put("capex", "3")
        parameterMap.put("opex", "4")
        parameterMap.put("cosphi", "5")
        parameterMap.put("estorage", "6")
        parameterMap.put("prated", "7")
        parameterMap.put("pmin", "8")
        parameterMap.put("pmax", "9")
        parameterMap.put("eta", "10")
        parameterMap.put("dod", "11")
        parameterMap.put("lifetime", "12")
        parameterMap.put("lifecycle", "13")
        def typeInputClass = StorageTypeInput

        when:
        Optional<? extends SystemParticipantTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameterMap, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().uuid == UUID.fromString(parameterMap.get("uuid"))
        typeInput.get().id == parameterMap.get("id")
        typeInput.get().capex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("capex")), PowerSystemUnits.EURO) // TODO StandardUnit
        typeInput.get().opex == Quantities.getQuantity(Double.parseDouble(parameterMap.get("opex")), StandardUnits.ENERGY_PRICE)
        typeInput.get().cosphi == Double.parseDouble(parameterMap.get("cosphi"))

        ((StorageTypeInput) typeInput.get()).getEStorage() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("estorage")), StandardUnits.ENERGY)
        ((StorageTypeInput) typeInput.get()).getPRated() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("prated")), StandardUnits.ACTIVE_POWER_IN)
        ((StorageTypeInput) typeInput.get()).getPMin() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("pmin")), StandardUnits.ACTIVE_POWER_IN)
        ((StorageTypeInput) typeInput.get()).getPMax() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("pmax")), StandardUnits.ACTIVE_POWER_IN)
        ((StorageTypeInput) typeInput.get()).getEta() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("eta")), StandardUnits.EFFICIENCY)
        ((StorageTypeInput) typeInput.get()).getDod() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("dod")), StandardUnits.DOD)
        ((StorageTypeInput) typeInput.get()).getLifeTime() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("lifetime")), StandardUnits.LIFE_TIME)
        ((StorageTypeInput) typeInput.get()).getLifeCycle() == Integer.parseInt(parameterMap.get("lifecycle"))
    }

    def "A SystemParticipantTypeInputFactory should throw an exception on invalid or incomplete data"() {
        given: "a system participant factory and model data"
        def typeInputFactory = new SystemParticipantTypeInputFactory()
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("uuid", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c8")
        parameterMap.put("id", "blablub1")
        parameterMap.put("capex", "3")
        parameterMap.put("opex", "4")
        parameterMap.put("cosphi", "5")
        parameterMap.put("estorage", "6")
        parameterMap.put("prated", "7")
        parameterMap.put("pmax", "9")
        parameterMap.put("eta", "10")
        parameterMap.put("dod", "11")
        parameterMap.put("lifetime", "12")
        parameterMap.put("lifecycle", "13")

        when:
        typeInputFactory.getEntity(new SimpleEntityData(parameterMap, StorageTypeInput))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [capex, cosphi, dod, estorage, eta, id, lifecycle, lifetime, opex, pmax, prated, uuid] with data {capex -> 3,cosphi -> 5,dod -> 11,estorage -> 6,eta -> 10,id -> blablub1,lifecycle -> 13,lifetime -> 12,opex -> 4,pmax -> 9,prated -> 7,uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c8} are invalid for instance of StorageTypeInput. \n" +
                "The following fields to be passed to a constructor of StorageTypeInput are possible:\n" +
                "0: [capex, cosphi, dod, estorage, eta, id, lifecycle, lifetime, opex, pmax, pmin, prated, uuid]\n"
    }
}
