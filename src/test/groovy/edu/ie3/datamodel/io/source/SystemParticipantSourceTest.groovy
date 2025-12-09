/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source


import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.models.input.system.type.*
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils
import edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationType
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.profile.NbwTemperatureDependantLoadProfile
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import javax.measure.quantity.Dimensionless

class SystemParticipantSourceTest extends Specification implements FactoryTestHelper {

  @Shared
  private UUID operatorUUID = UUID.randomUUID()
  @Shared
  private UUID nodeUUID = UUID.randomUUID()
  @Shared
  private UUID emUUID = UUID.randomUUID()
  @Shared
  private UUID typeUUID = UUID.randomUUID()
  @Shared
  private UUID thermalBusUUID = UUID.randomUUID()
  @Shared
  private UUID thermalStorageUUID = UUID.randomUUID()

  @Shared
  private NodeInput nodeInput = Mock(NodeInput)
  @Shared
  private OperatorInput operatorInput = Mock(OperatorInput)
  @Shared
  private EmInput emUnit = Mock(EmInput)

  @Shared
  private Map<UUID, OperatorInput> operators = [(operatorUUID): operatorInput]
  @Shared
  private Map<UUID, NodeInput> nodes = [(nodeUUID): nodeInput]
  @Shared
  private Map<UUID, EmInput> emUnits = [(emUUID): emUnit]

  def "A BmInput can be build correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "marketreaction"  : "false",
      "costControlled"  : "true",
      "feedintariff"    : "3",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString(),
      "type"            : typeUUID.toString()

    ]
    def typeInput = Mock(BmTypeInput)
    def types = [(typeUUID): typeInput]

    when:
    def input = SystemParticipantSource.bmBuildFunction(operators, nodes, emUnits, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert controllingEm == Optional.of(emUnit)
      assert type == typeInput
      assert !marketReaction
      assert costControlled
      assert feedInTariff == getQuant(parameter["feedintariff"], StandardUnits.ENERGY_PRICE)
    }
  }

  def "A ChpInput can be build correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "marketreaction"  : "true",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString(),
      "type"            : typeUUID.toString(),
      "thermalbus"      : thermalBusUUID.toString(),
      "thermalstorage"  : thermalStorageUUID.toString()
    ]

    def typeInput = Mock(ChpTypeInput)
    def thermalBus = Mock(ThermalBusInput)
    def thermalStorage = Mock(ThermalStorageInput)

    def types = [(typeUUID): typeInput]
    def thermalBusses = [(thermalBusUUID): thermalBus]
    def thermalStorages = [(thermalStorageUUID): thermalStorage]

    when:
    def input = SystemParticipantSource.chpBuildFunction(operators, nodes, emUnits, types, thermalBusses, thermalStorages).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert controllingEm == Optional.of(emUnit)
      assert type == typeInput
      assert marketReaction
    }
  }

  def "A EvcsInput can be build correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "type"            : "Household",
      "chargingpoints"  : "4",
      "cosphirated"     : "0.95",
      "locationtype"    : "CHARGING_HUB_TOWN",
      "v2gsupport"      : "false",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString()
    ]

    when:
    def input = SystemParticipantSource.evcsBuildFunction(operators, nodes, emUnits).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert controllingEm == Optional.of(emUnit)
      assert type == ChargingPointTypeUtils.HouseholdSocket
      assert chargingPoints == Integer.parseInt(parameter["chargingpoints"])
      assert cosPhiRated == Double.parseDouble(parameter["cosphirated"])
      assert locationType == EvcsLocationType.CHARGING_HUB_TOWN
      assert !v2gSupport
    }
  }

  def "A EvcsInput should fail when passing an invalid ChargingPointType"() {
    given:
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "type"            : "-- invalid --",
      "chargingpoints"  : "4",
      "cosphirated"     : "0.95",
      "locationtype"    : "CHARGING_HUB_TOWN",
      "v2gsupport"      : "false",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString()
    ]

    when:
    def input = SystemParticipantSource.evcsBuildFunction(operators, nodes, emUnits).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.failure
    input.exception.get().cause.message == "Provided charging point type string '-- invalid --' is neither a valid custom type string nor can a common charging point type with id '-- invalid --' be found! Please either provide a valid custom string in the format '<Name>(<kVA Value>|<AC|DC>)' (e.g. 'FastCharger(50|DC)') or a common type id (see docs for all available common types)."
  }

  def "A EvcsInput should fail when passing an invalid EvcsLocationType"() {
    given:
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "type"            : "Household",
      "chargingpoints"  : "4",
      "cosphirated"     : "0.95",
      "locationType"    : "-- invalid --",
      "v2gsupport"      : "false",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString()
    ]

    when:
    def input = SystemParticipantSource.evcsBuildFunction(operators, nodes, emUnits).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.failure
    input.exception.get().cause.message == "EvcsLocationType ' invalid ' does not exist."
  }

  def "A EvInput can be build correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString(),
      "type"            : typeUUID.toString()
    ]

    def typeInput = Mock(EvTypeInput)

    def types = [(typeUUID): typeInput]


    when:
    def input = SystemParticipantSource.evBuildFunction(operators, nodes, emUnits, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert controllingEm == Optional.of(emUnit)
      assert type == typeInput
    }
  }

  def "A FixedFeedInInput can be build correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "srated"          : "3",
      "cosphirated"     : "4",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString()
    ]

    when:
    def input = SystemParticipantSource.fixedFeedInBuildFunction(operators, nodes, emUnits).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert controllingEm == Optional.of(emUnit)
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosphirated"])
    }
  }

  def "A HpInput can be build correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString(),
      "type"            : typeUUID.toString(),
      "thermalbus"      : thermalBusUUID.toString()
    ]

    def typeInput = Mock(HpTypeInput)
    def thermalBusInput = Mock(ThermalBusInput)

    def types = [(typeUUID): typeInput]
    def thermalBusses = [(thermalBusUUID): thermalBusInput]

    when:
    def input = SystemParticipantSource.hpBuildFunction(operators, nodes, emUnits, types, thermalBusses).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert controllingEm == Optional.of(emUnit)
      assert type == typeInput
      assert thermalBus == thermalBusInput
    }
  }

  def "A LoadInput can be build correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"               : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"                 : "TestID",
      "qcharacteristics"   : "cosPhiFixed:{(0.0,1.0)}",
      "loadprofile"	       : profileKey,
      "econsannual"        : "3",
      "srated"             : "4",
      "cosphirated"        : "5",
      "operator"           : operatorUUID.toString(),
      "node"               : nodeUUID.toString(),
      "controllingem"      : emUUID.toString()
    ]

    when:
    def input = SystemParticipantSource.loadBuildFunction(operators, nodes, emUnits).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime == OperationTime.notLimited()
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert controllingEm == Optional.of(emUnit)
      assert loadProfile == profile
      assert eConsAnnual == getQuant(parameter["econsannual"], StandardUnits.ENERGY_IN)
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosphirated"])
    }

    where:
    profileKey || profile
    "G-4"      || BdewStandardLoadProfile.G4
    "ep1"      || NbwTemperatureDependantLoadProfile.EP1
  }

  def "A PvInput can be build correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "albedo"          : "3",
      "azimuth"         : "4",
      "etaconv"         : "5",
      "elevationangle"  : "6",
      "kg"              : "7",
      "kt"              : "8",
      "marketreaction"  : "true",
      "srated"          : "9",
      "cosphirated"     : "10",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString()
    ]

    when:
    def input = SystemParticipantSource.pvBuildFunction(operators, nodes, emUnits).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert controllingEm == Optional.of(emUnit)
      assert albedo == Double.parseDouble(parameter["albedo"])
      assert azimuth == getQuant(parameter["azimuth"], StandardUnits.AZIMUTH)
      assert etaConv == getQuant(parameter["etaconv"], StandardUnits.EFFICIENCY)
      assert elevationAngle == getQuant(parameter["elevationangle"], StandardUnits.SOLAR_ELEVATION_ANGLE)
      assert kG == Double.parseDouble(parameter["kg"])
      assert kT == Double.parseDouble(parameter["kt"])
      assert marketReaction
      assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
      assert cosPhiRated == Double.parseDouble(parameter["cosphirated"])
    }
  }

  def "A StorageInput can be build correctly"() {
    given:
    def typeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString(),
      "type"            : typeUUID.toString()
    ]

    def typeInput = Mock(StorageTypeInput)

    def types = [(typeUUID): typeInput]

    when:
    def input = SystemParticipantSource.storageBuildFunction(operators, nodes, emUnits, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert controllingEm == Optional.of(emUnit)
      assert type == typeInput
    }
  }

  def "A WecInputFactory should parse a valid WecInput correctly"() {
    given:
    def typeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "",
      "operatesuntil"   : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "marketreaction"  : "true",
      "operator"        : operatorUUID.toString(),
      "node"            : nodeUUID.toString(),
      "controllingem"   : emUUID.toString(),
      "type"            : typeUUID.toString()
    ]

    def typeInput = Mock(WecTypeInput)

    def types = [(typeUUID): typeInput]

    when:
    def input = SystemParticipantSource.wecBuildFunction(operators, nodes, emUnits, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert !operationTime.startDate.present
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      assert controllingEm == Optional.of(emUnit)
      assert type == typeInput
      assert marketReaction
    }
  }
}
