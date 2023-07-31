/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.exceptions.NotImplementedException
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.datamodel.models.input.system.type.*
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.util.quantities.interfaces.Currency
import edu.ie3.util.quantities.interfaces.DimensionlessRate
import edu.ie3.util.quantities.interfaces.EnergyPrice
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.*

import static edu.ie3.datamodel.models.StandardUnits.*
import static tech.units.indriya.unit.Units.PERCENT

class SystemParticipantValidationUtilsTest extends Specification {

  def "Instantiating a ConnectorValidationUtil leads to an exception"() {
    when:
    new SystemParticipantValidationUtils()

    then:
    def e = thrown(IllegalStateException)
    e.message == "Don't try and instantiate a Utility class."
  }

  def "Smoke Test: Correct system participant throws no exception"() {
    given:
    def systemParticipant = SystemParticipantTestData.bmInput

    when:
    ValidationUtils.check(systemParticipant)

    then:
    noExceptionThrown()
  }

  def "SystemParticipantValidationUtils.check() recognizes all potential errors for a system participant"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = SystemParticipantValidationUtils.check(invalidSystemParticipant).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidSystemParticipant                                                || expectedSize || expectedException
    SystemParticipantTestData.bmInput.copy().qCharacteristics(null).build() || 1            || new InvalidEntityException("Reactive power characteristics of system participant is not defined", invalidSystemParticipant)
  }

  // Common data for all system participant types
  private static final UUID uuid = UUID.fromString("92ec3bcf-1327-4d38-af67-0bf7a9fa73c7")
  private static final String id = "test_id"
  private static final ComparableQuantity<Currency> capex = Quantities.getQuantity(100d, CAPEX)
  private static final ComparableQuantity<EnergyPrice> opex = Quantities.getQuantity(50d, ENERGY_PRICE)
  private static final ComparableQuantity<Power> sRated = Quantities.getQuantity(25d, ACTIVE_POWER_IN)
  private static final cosPhiRated = 0.95

  // Common data for some (but not all) system participant types
  private static final ComparableQuantity<Dimensionless> etaConv = Quantities.getQuantity(98d, EFFICIENCY)

  // Specific data for bm type
  private static final ComparableQuantity<DimensionlessRate> activePowerGradient = Quantities.getQuantity(25, ACTIVE_POWER_GRADIENT)

  // Specific data for CHP type (and HP type)
  private static final ComparableQuantity<Dimensionless> etaEl = Quantities.getQuantity(19, EFFICIENCY)
  private static final ComparableQuantity<Dimensionless> etaThermal = Quantities.getQuantity(76, EFFICIENCY)
  private static final ComparableQuantity<Power> pOwn = Quantities.getQuantity(0, ACTIVE_POWER_IN)
  private static final ComparableQuantity<Power> pThermal = Quantities.getQuantity(9, ACTIVE_POWER_IN)

  // Specific data for storage type
  private static final ComparableQuantity<Energy> eStorage = Quantities.getQuantity(100, ENERGY_IN)
  private static final ComparableQuantity<Power> pMax = Quantities.getQuantity(15, ACTIVE_POWER_IN)
  private static final ComparableQuantity<Dimensionless> eta = Quantities.getQuantity(95, EFFICIENCY)
  private static final ComparableQuantity<Dimensionless> dod = Quantities.getQuantity(10, EFFICIENCY)
  private static final ComparableQuantity<DimensionlessRate> cpRate = Quantities.getQuantity(100, ACTIVE_POWER_GRADIENT)
  private static final ComparableQuantity<Time> lifeTime = Quantities.getQuantity(175316.4, LIFE_TIME)
  private static final int lifeCycle = 100

  // Specific data for wec type
  private static final WecCharacteristicInput wecCharacteristic = new WecCharacteristicInput("cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}")
  private static final ComparableQuantity<Area> rotorArea = Quantities.getQuantity(20, ROTOR_AREA)
  private static final ComparableQuantity<Length> hubHeight = Quantities.getQuantity(200, HUB_HEIGHT)


  def "SystemParticipantValidationUtils.checkType() recognizes all potential errors for a system participant type"() {
    when:
    SystemParticipantValidationUtils.check(invalidType)

    then:
    Throwable topEx = thrown()
    Throwable ex = topEx.cause
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidType                                                                                                                                                                                           || expectedException
    new BmTypeInput(uuid, id, null, opex, activePowerGradient, sRated, cosPhiRated, etaConv)                                                                                                              || new InvalidEntityException("At least one of capex, opex, or sRated is null", invalidType)
    new BmTypeInput(uuid, id, Quantities.getQuantity(-100d, CAPEX), Quantities.getQuantity(-50d, ENERGY_PRICE), activePowerGradient, Quantities.getQuantity(-25d, ACTIVE_POWER_IN), cosPhiRated, etaConv) || new InvalidEntityException("The following quantities have to be zero or positive: -100 EUR, -50 EUR/MWh, -25 kVA", invalidType)
    new BmTypeInput(uuid, id, capex, opex, activePowerGradient, sRated, 2, etaConv)                                                                                                                       || new InvalidEntityException("Rated power factor of BmTypeInput must be between 0 and 1", invalidType)
  }

  // BM

  def "Smoke Test: Correct biomass power plant throws no exception"() {
    given:
    def bmInput = SystemParticipantTestData.bmInput

    when:
    ValidationUtils.check(bmInput)

    then:
    noExceptionThrown()
  }

  // No tests for "SystemParticipantValidationUtils.checkBm() recognizes all potential errors for a biomass power plant"

  def "Smoke Test: Correct biomass power plant type throws no exception"() {
    given:
    def bmType = SystemParticipantTestData.bmTypeInput

    when:
    ValidationUtils.check(bmType)

    then:
    noExceptionThrown()
  }

  def "SystemParticipantValidationUtils.checkBmType() recognizes all potential errors for a biomass power plant type"() {
    when:
    ValidationUtils.check(invalidBmType)

    then:
    Throwable topEx = thrown()
    Throwable ex = topEx.cause
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidBmType                                                                                                             || expectedException
    new BmTypeInput(uuid, id, capex, opex, Quantities.getQuantity(-25, ACTIVE_POWER_GRADIENT), sRated, cosPhiRated, etaConv)  || new InvalidEntityException("The following quantities have to be zero or positive: -25 %/h", invalidBmType)
    new BmTypeInput(uuid, id, capex, opex, activePowerGradient, sRated, cosPhiRated, Quantities.getQuantity(1000d, PERCENT))  || new InvalidEntityException("Efficiency of inverter of BmTypeInput must be between 0% and 100%", invalidBmType)
  }

  // CHP

  def "Smoke Test: Correct CHP plant throws no exception"() {
    given:
    def chpInput = SystemParticipantTestData.chpInput

    when:
    ValidationUtils.check(chpInput)

    then:
    noExceptionThrown()
  }

  // No tests for "SystemParticipantValidationUtils.checkChp() recognizes all potential errors for a CHP plant, as there is nothing to test here"

  def "Smoke Test: Correct CHP type throws no exception"() {
    given:
    def chpType = SystemParticipantTestData.chpTypeInput

    when:
    ValidationUtils.check(chpType)

    then:
    noExceptionThrown()
  }

  def "SystemParticipantValidationUtils.checkChpType() recognizes all potential errors for a CHP type"() {
    when:
    SystemParticipantValidationUtils.check(invalidChpType)

    then:
    Throwable topEx = thrown()
    Throwable ex = topEx.cause
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidChpType                                                                                                                           || expectedException
    new ChpTypeInput(uuid, id, capex, opex, etaEl, etaThermal, sRated, cosPhiRated, pThermal, Quantities.getQuantity(-100, ACTIVE_POWER_IN)) || new InvalidEntityException("The following quantities have to be zero or positive: -100 kW", invalidChpType)
    new ChpTypeInput(uuid, id, capex, opex, etaEl, etaThermal, sRated, cosPhiRated, Quantities.getQuantity(0, ACTIVE_POWER_IN), pOwn)        || new InvalidEntityException("The following quantities have to be positive: 0 kW", invalidChpType)
    new ChpTypeInput(uuid, id, capex, opex, Quantities.getQuantity(110, EFFICIENCY), etaThermal, sRated, cosPhiRated, pThermal, pOwn)        || new InvalidEntityException("Electrical efficiency of ChpTypeInput must be between 0% and 100%", invalidChpType)
    new ChpTypeInput(uuid, id, capex, opex, etaEl, Quantities.getQuantity(110, EFFICIENCY), sRated, cosPhiRated, pThermal, pOwn)             || new InvalidEntityException("Thermal efficiency of ChpTypeInput must be between 0% and 100%", invalidChpType)
  }

  // EV

  def "Smoke Test: Correct EV throws no exception"() {
    given:
    def evInput = SystemParticipantTestData.evInput

    when:
    ValidationUtils.check(evInput)

    then:
    noExceptionThrown()
  }

  // No tests for "SystemParticipantValidationUtils.checkEv() recognizes all potential errors for an EV"

  def "Smoke Test: Correct EV type throws no exception"() {
    given:
    def evType = SystemParticipantTestData.evTypeInput

    when:
    ValidationUtils.check(evType)

    then:
    noExceptionThrown()
  }

  def "SystemParticipantValidationUtils.checkEvType() recognizes all potential errors for an EV type"() {
    when:
    SystemParticipantValidationUtils.check(invalidEvType)

    then:
    Throwable topEx = thrown()
    Throwable ex = topEx.cause
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidEvType                                                                                                                                     || expectedException
    new EvTypeInput(uuid, id, capex, opex, Quantities.getQuantity(0, ENERGY_IN), Quantities.getQuantity(0, ENERGY_PER_DISTANCE), sRated, cosPhiRated) || new InvalidEntityException("The following quantities have to be positive: 0 kWh, 0 kWh/km", invalidEvType)
  }

  // Fixed Feed In

  def "Smoke Test: Correct Fixed Feed-In throws no exception"() {
    given:
    def fixedFeedInInput = SystemParticipantTestData.fixedFeedInInput

    when:
    ValidationUtils.check(fixedFeedInInput)

    then:
    noExceptionThrown()
  }

  def "SystemParticipantValidationUtils.checkFixedFeedIn() recognizes all potential errors for an a Fixed Feed-In"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = SystemParticipantValidationUtils.check(invalidFixedFeedIn).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidFixedFeedIn                                                                                               || expectedSize || expectedException
    SystemParticipantTestData.fixedFeedInInput.copy().sRated(Quantities.getQuantity(-100d, ACTIVE_POWER_IN)).build() || 1            || new InvalidEntityException("The following quantities have to be zero or positive: -100 kVA", invalidFixedFeedIn)
    SystemParticipantTestData.fixedFeedInInput.copy().cosPhiRated(-1d).build()                                       || 1            || new InvalidEntityException("Rated power factor of FixedFeedInInput must be between 0 and 1", invalidFixedFeedIn)
  }

  // HP

  def "Smoke Test: Correct HP throws no exception"() {
    given:
    def hpInput = SystemParticipantTestData.hpInput

    when:
    ValidationUtils.check(hpInput)

    then:
    noExceptionThrown()
  }

  // No tests for "SystemParticipantValidationUtils.checkHp() recognizes all potential errors for an HP"

  def "Smoke Test: Correct HP type throws no exception"() {
    given:
    def hpType = SystemParticipantTestData.hpTypeInput

    when:
    ValidationUtils.check(hpType)

    then:
    noExceptionThrown()
  }

  def "SystemParticipantValidationUtils.checkHpType() recognizes all potential errors for an HP type"() {
    when:
    SystemParticipantValidationUtils.check(invalidHpType)

    then:
    Throwable topEx = thrown()
    Throwable ex = topEx.cause
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidHpType                                                                                           || expectedException
    new HpTypeInput(uuid, id, capex, opex, sRated, cosPhiRated, Quantities.getQuantity(0, ACTIVE_POWER_IN)) || new InvalidEntityException("The following quantities have to be positive: 0 kW", invalidHpType)
  }

  // Load

  def "Smoke Test: Correct load throws no exception"() {
    given:
    def load = SystemParticipantTestData.loadInput

    when:
    ValidationUtils.check(load)

    then:
    noExceptionThrown()
  }

  def "SystemParticipantValidationUtils.checkLoad() recognizes all potential errors for a load"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = SystemParticipantValidationUtils.check(invalidLoad).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidLoad                                                                                                                                                    || expectedSize || expectedException
    SystemParticipantTestData.loadInput.copy().loadprofile(null).build() 																						   || 1            || new InvalidEntityException("No standard load profile defined for load", invalidLoad)
    SystemParticipantTestData.loadInput.copy().sRated(Quantities.getQuantity(-25d, ACTIVE_POWER_IN)).eConsAnnual(Quantities.getQuantity(-4000, ENERGY_IN)).build() || 1            || new InvalidEntityException("The following quantities have to be zero or positive: -25 kVA, -4000 kWh", invalidLoad)
    SystemParticipantTestData.loadInput.copy().cosPhiRated(2).build()                                                                                              || 1            || new InvalidEntityException("Rated power factor of LoadInput must be between 0 and 1", invalidLoad)
  }

  // PV

  def "Smoke Test: Correct PV throws no exception"() {
    given:
    def pvInput = SystemParticipantTestData.pvInput

    when:
    ValidationUtils.check(pvInput)

    then:
    noExceptionThrown()
  }

  def "SystemParticipantValidationUtils.checkPV() recognizes all potential errors for a PV"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = SystemParticipantValidationUtils.check(invalidPV).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidPV                                                                                                            || expectedSize || expectedException
    SystemParticipantTestData.pvInput.copy().sRated(Quantities.getQuantity(-25d, ACTIVE_POWER_IN)).build()               || 1            || new InvalidEntityException("The following quantities have to be zero or positive: -25 kVA", invalidPV)
    SystemParticipantTestData.pvInput.copy().albedo(2).build()                                                           || 1            || new InvalidEntityException("Albedo of the plant's surrounding of PvInput must be between 0 and 1", invalidPV)
    SystemParticipantTestData.pvInput.copy().azimuth(Quantities.getQuantity(-100d, AZIMUTH)).build()                     || 1            || new InvalidEntityException("Azimuth angle of PvInput must be between -90° (east) and 90° (west)", invalidPV)
    SystemParticipantTestData.pvInput.copy().etaConv(Quantities.getQuantity(110d, EFFICIENCY)).build()                   || 1            || new InvalidEntityException("Efficiency of the converter of PvInput must be between 0% and 100%", invalidPV)
    SystemParticipantTestData.pvInput.copy().elevationAngle(Quantities.getQuantity(100d, SOLAR_ELEVATION_ANGLE)).build() || 1            || new InvalidEntityException("Tilted inclination from horizontal of PvInput must be between 0° and 90°", invalidPV)
    SystemParticipantTestData.pvInput.copy().cosPhiRated(2).build()                                                      || 1            || new InvalidEntityException("Rated power factor of PvInput must be between 0 and 1", invalidPV)
  }

  // Storage

  def "Smoke Test: Correct storage throws no exception"() {
    given:
    def storage = SystemParticipantTestData.storageInput

    when:
    ValidationUtils.check(storage)

    then:
    noExceptionThrown()
  }

  // No tests for "SystemParticipantValidationUtils.checkStorage() recognizes all potential errors for a storage"

  def "Smoke Test: Correct storage type throws no exception"() {
    given:
    def storageType = SystemParticipantTestData.storageTypeInput

    when:
    ValidationUtils.check(storageType)

    then:
    noExceptionThrown()
  }

  def "SystemParticipantValidationUtils.checkStorageType() recognizes all potential errors for a storage type"() {
    when:
    SystemParticipantValidationUtils.check(invalidStorageType)

    then:
    Throwable topEx = thrown()
    Throwable ex = topEx.cause
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidStorageType                                                                                                                                                                                                                           || expectedException
    new StorageTypeInput(uuid, id, capex, opex, eStorage, sRated, cosPhiRated, pMax, cpRate, eta, dod, lifeTime, -1)                                                                                                                             || new InvalidEntityException("Permissible amount of life cycles of the storage type must be zero or positive", invalidStorageType)
    new StorageTypeInput(uuid, id, capex, opex, eStorage, sRated, cosPhiRated, pMax, cpRate, Quantities.getQuantity(110, EFFICIENCY), dod, lifeTime, lifeCycle)                                                                                  || new InvalidEntityException("Efficiency of the electrical converter of StorageTypeInput must be between 0% and 100%", invalidStorageType)
    new StorageTypeInput(uuid, id, capex, opex, eStorage, sRated, cosPhiRated, pMax, cpRate, eta, Quantities.getQuantity(-10, EFFICIENCY), lifeTime, lifeCycle)                                                                                  || new InvalidEntityException("Maximum permissible depth of discharge of StorageTypeInput must be between 0% and 100%", invalidStorageType)
    new StorageTypeInput(uuid, id, capex, opex, eStorage, sRated, cosPhiRated, Quantities.getQuantity(-15, ACTIVE_POWER_IN), Quantities.getQuantity(-100, ACTIVE_POWER_GRADIENT), eta, dod, Quantities.getQuantity(-10.5, LIFE_TIME), lifeCycle) || new InvalidEntityException("The following quantities have to be zero or positive: -15 kW, -100 %/h, -10.5 h", invalidStorageType)
    new StorageTypeInput(uuid, id, capex, opex, Quantities.getQuantity(0, ENERGY_IN), sRated, cosPhiRated, pMax, cpRate, eta, dod, lifeTime, lifeCycle)                                                                                          || new InvalidEntityException("The following quantities have to be positive: 0 kWh", invalidStorageType)
  }

  // WEC

  def "Smoke Test: Correct WEC throws no exception"() {
    given:
    def wec = SystemParticipantTestData.wecInput

    when:
    ValidationUtils.check(wec)

    then:
    noExceptionThrown()
  }

  // No tests for "SystemParticipantValidationUtils.checkWec() recognizes all potential errors for a WEC"

  def "Smoke Test: Correct WEC type throws no exception"() {
    given:
    def wecType = SystemParticipantTestData.wecType

    when:
    ValidationUtils.check(wecType)

    then:
    noExceptionThrown()
  }

  def "SystemParticipantValidationUtils.checkWecType() recognizes all potential errors for a wec type"() {
    when:
    SystemParticipantValidationUtils.check(invalidWecType)

    then:
    Throwable topEx = thrown()
    Throwable ex = topEx.cause
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidWecType                                                                                                                                                              || expectedException
    new WecTypeInput(uuid, id, capex, opex, sRated, cosPhiRated, wecCharacteristic, Quantities.getQuantity(110d, EFFICIENCY), rotorArea, hubHeight)                             || new InvalidEntityException("Efficiency of the converter of WecTypeInput must be between 0% and 100%", invalidWecType)
    new WecTypeInput(uuid, id, capex, opex, sRated, cosPhiRated, wecCharacteristic, etaConv, Quantities.getQuantity(-10, ROTOR_AREA), Quantities.getQuantity(-200, HUB_HEIGHT)) || new InvalidEntityException("The following quantities have to be zero or positive: -10 m², -200 m", invalidWecType)
  }

  def "Checking an unsupported asset leads to an exception"() {
    given:
    def node = Mock(NodeInput)
    node.getUuid() >> UUID.randomUUID()
    def invalidParticipant = new InvalidSystemParticipantInput(node)

    when:
    List<Try<Void, InvalidEntityException>> exceptions = SystemParticipantValidationUtils.check(invalidParticipant).stream().filter { it -> it.failure }.toList()

    then:
    def e = exceptions.get(0).exception.get().cause
    e.message == "Cannot validate object of class 'InvalidSystemParticipantInput', as no routine is implemented."
  }

  def "Checking an unsupported asset type leads to an exception"() {
    given:
    def invalidParticipantInput = new InvalidSystemParticipantTypeInput()

    when:
    SystemParticipantValidationUtils.check(invalidParticipantInput)

    then:
    Throwable topEx = thrown()
    Throwable e = topEx.cause
    e.message.contains "Cannot validate object of class 'InvalidSystemParticipantTypeInput', as no routine is implemented."
  }

  def "Checking electric vehicle charging stations leads to an exception"() {
    when:
    SystemParticipantValidationUtils.checkEvcs()

    then:
    def e = thrown(NotImplementedException)
    e.message == "Validation of 'EvcsInput' is currently not supported."
  }
}
