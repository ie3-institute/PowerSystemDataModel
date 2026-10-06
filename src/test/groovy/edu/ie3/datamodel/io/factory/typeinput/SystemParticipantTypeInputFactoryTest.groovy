/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.typeinput

import static edu.ie3.util.quantities.PowerSystemUnits.METRE_PER_SECOND
import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.source.DataSource
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.models.input.system.type.*
import edu.ie3.datamodel.utils.CollectionUtils
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Speed

class SystemParticipantTypeInputFactoryTest extends Specification implements FactoryTestHelper {

  def "A SystemParticipantTypeInputFactory should contain all expected classes for parsing"() {
    given:
    def typeInputFactory = new SystemParticipantTypeInputFactory(AcTypeInput)
    def expectedClasses = [
      AcTypeInput,
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
    def typeInputFactory = new SystemParticipantTypeInputFactory(EvTypeInput)
    Map<String, String> parameter = [
      "uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id": "blablub",
      "capex": "3",
      "opex": "4",
      "sRated": "5",
      "cosPhiRated": "6",
      "eStorage": "7",
      "eCons": "8",
      "sRatedDC": "9",
    ]

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new HashMap<>(parameter))

    then:
    typeInput.success
    typeInput.data.get().getClass() == EvTypeInput

    ((EvTypeInput) typeInput.data.get()).with {
      uuid == UUID.fromString(parameter["uuid"])
      id == parameter["id"]
      capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      sRated == getQuant(parameter["sRated"], StandardUnits.S_RATED)
      cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])

      eStorage == getQuant(parameter["eStorage"], StandardUnits.ENERGY_IN)
      eCons == getQuant(parameter["eCons"], StandardUnits.ENERGY_PER_DISTANCE)
      sRatedDC == getQuant(parameter["sRatedDC"], StandardUnits.ACTIVE_POWER_IN)
    }
  }

  def "A SystemParticipantTypeInputFactory should parse a valid HpTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory(HpTypeInput)
    Map<String, String> parameter = [
      "uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id": "blablub",
      "capex": "3",
      "opex": "4",
      "sRated": "5",
      "cosPhiRated": "6",
      "pThermal": "7",
    ]

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new HashMap<>(parameter))

    then:
    typeInput.success
    typeInput.data.get().getClass() == HpTypeInput

    ((HpTypeInput) typeInput.data.get()).with {
      uuid == UUID.fromString(parameter["uuid"])
      id == parameter["id"]
      capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      sRated == getQuant(parameter["sRated"], StandardUnits.S_RATED)
      cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])

      pThermal == getQuant(parameter["pThermal"], StandardUnits.ACTIVE_POWER_IN)
    }
  }

  def "A SystemParticipantTypeInputFactory should parse a valid BmTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory(BmTypeInput)
    Map<String, String> parameter = [
      "uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id": "blablub",
      "capex": "3",
      "opex": "4",
      "sRated": "5",
      "cosPhiRated": "6",
      "activePowerGradient": "7",
      "etaConv": "8"
    ]

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new HashMap<>(parameter))

    then:
    typeInput.success
    typeInput.data.get().getClass() == BmTypeInput

    ((BmTypeInput) typeInput.data.get()).with {
      uuid == UUID.fromString(parameter["uuid"])
      id == parameter["id"]
      capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      sRated == getQuant(parameter["sRated"], StandardUnits.S_RATED)
      cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])

      activePowerGradient == getQuant(parameter["activePowerGradient"], StandardUnits.ACTIVE_POWER_GRADIENT)
      etaConv == getQuant(parameter["etaConv"], StandardUnits.EFFICIENCY)
    }
  }

  def "A SystemParticipantTypeInputFactory should parse a valid WecTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory(WecTypeInput)
    Map<String, String> parameter = [
      "uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id": "blablub",
      "capex": "3",
      "opex": "4",
      "sRated": "5",
      "cosPhiRated": "6",
      "cpCharacteristic": "cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}",
      "etaConv": "7",
      "rotorArea": "8",
      "hubHeight": "9"
    ]

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new HashMap<>(parameter))

    then:
    typeInput.success
    typeInput.data.get().getClass() == WecTypeInput

    ((WecTypeInput) typeInput.data.get()).with {
      uuid == UUID.fromString(parameter["uuid"])
      id == parameter["id"]
      capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      sRated == getQuant(parameter["sRated"], StandardUnits.S_RATED)
      cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])

      cpCharacteristic.with {
        uuid != null
        points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Speed, Dimensionless>(Quantities.getQuantity(10d, METRE_PER_SECOND), Quantities.getQuantity(0.05, PU)),
          new CharacteristicPoint<Speed, Dimensionless>(Quantities.getQuantity(15d, METRE_PER_SECOND), Quantities.getQuantity(0.1, PU)),
          new CharacteristicPoint<Speed, Dimensionless>(Quantities.getQuantity(20d, METRE_PER_SECOND), Quantities.getQuantity(0.2, PU))
        ] as TreeSet)
      }
      etaConv == getQuant(parameter["etaConv"], StandardUnits.EFFICIENCY)
      rotorArea == getQuant(parameter["rotorArea"], StandardUnits.ROTOR_AREA)
      hubHeight == getQuant(parameter["hubHeight"], StandardUnits.HUB_HEIGHT)
    }
  }

  def "A SystemParticipantTypeInputFactory should parse a valid ChpTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory(ChpTypeInput)
    Map<String, String> parameter = [
      "uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id": "blablub",
      "capex": "3",
      "opex": "4",
      "sRated": "5",
      "cosPhiRated": "6",
      "etaEl": "7",
      "etaThermal": "8",
      "pThermal": "9",
      "pOwn": "10"
    ]

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new HashMap<>(parameter))

    then:
    typeInput.success
    typeInput.data.get().getClass() == ChpTypeInput

    ((ChpTypeInput) typeInput.data.get()).with {
      uuid == UUID.fromString(parameter["uuid"])
      id == parameter["id"]
      capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      sRated == getQuant(parameter["sRated"], StandardUnits.S_RATED)
      cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])
      etaEl == getQuant(parameter["etaEl"], StandardUnits.EFFICIENCY)
      etaThermal == getQuant(parameter["etaThermal"], StandardUnits.EFFICIENCY)
      pThermal == getQuant(parameter["pThermal"], StandardUnits.ACTIVE_POWER_IN)
      pOwn == getQuant(parameter["pOwn"], StandardUnits.ACTIVE_POWER_IN)
    }
  }

  def "A SystemParticipantTypeInputFactory should parse a valid StorageTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new SystemParticipantTypeInputFactory(StorageTypeInput)
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id" : "blablub",
      "capex" : "3",
      "opex" : "4",
      "sRated" : "5",
      "cosPhiRated" : "6",
      "eStorage" : "6",
      "pMax" : "8",
      "activePowerGradient" : "1",
      "eta" : "9"
    ]

    when:
    Try<? extends SystemParticipantTypeInput, FactoryException> typeInput = typeInputFactory.get(new HashMap<>(parameter))

    then:
    typeInput.success
    typeInput.data.get().getClass() == StorageTypeInput

    ((StorageTypeInput) typeInput.data.get()).with {
      uuid == UUID.fromString(parameter["uuid"])
      id == parameter["id"]
      capex == getQuant(parameter["capex"], StandardUnits.CAPEX)
      opex == getQuant(parameter["opex"], StandardUnits.ENERGY_PRICE)
      sRated == getQuant(parameter["sRated"], StandardUnits.S_RATED)
      cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])

      eStorage == getQuant(parameter["eStorage"], StandardUnits.ENERGY_IN)
      pMax == getQuant(parameter["pMax"], StandardUnits.ACTIVE_POWER_IN)
      activePowerGradient == getQuant(parameter["activePowerGradient"], StandardUnits.ACTIVE_POWER_GRADIENT)
      eta == getQuant(parameter["eta"], StandardUnits.EFFICIENCY)
    }
  }

  def "A SystemParticipantTypeInputFactory should throw an exception on invalid or incomplete data"() {
    given: "a system participant factory and model data"
    def actualFields = CollectionUtils.newSet("uuid", "id", "capex", "opex", "srated", "cosPhiRated", "estorage", "pmin", "pmax", "eta",)

    when:
    def input = DataSource.validate(actualFields, StorageTypeInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [capex, cosPhiRated, estorage, eta, id, opex, pmax, pmin, srated, uuid] are invalid for instance of 'StorageTypeInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'StorageTypeInput' are possible (NOT case-sensitive!):\n" +
        "0: [activePowerGradient, capex, cosPhiRated, eStorage, eta, id, opex, pMax, sRated, uuid] or [active_power_gradient, capex, cos_phi_rated, e_storage, eta, id, opex, p_max, s_rated, uuid]\n"
  }
}
