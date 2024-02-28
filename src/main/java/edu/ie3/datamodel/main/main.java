package edu.ie3.datamodel.main;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.io.sink.CsvFileSink;
import edu.ie3.datamodel.io.source.csv.CsvJointGridContainerSource;
import edu.ie3.datamodel.models.ControlStrategy;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.EnergyManagementUnits;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.container.ThermalGrid;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed;
import edu.ie3.datamodel.models.input.system.type.HpTypeInput;
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.profile.StandardLoadProfile;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.DimensionlessRate;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.quantity.*;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static edu.ie3.datamodel.models.StandardUnits.*;
import static edu.ie3.datamodel.models.input.OperatorInput.NO_OPERATOR_ASSIGNED;

public class main {
    public static void main(String[] args) throws ParsingException, FileException, InvalidGridException, SourceException, EntityProcessorException {

        String inputDirectory = "C:\\Users\\Johannes\\Documents\\ie3\\IDEA\\PowerSystemDataModel\\input\\recodeSemiurbGrid\\fullGrid";
        String outputDirectory = "C:\\Users\\Johannes\\Documents\\ie3\\IDEA\\PowerSystemDataModel\\output\\recodeSemiurbGrid\\fullGrid";

        // Lade ursprüngliches Simbench-Netz
        JointGridContainer simBenchRaw = CsvJointGridContainerSource.read(
                "simbench_lv5_2",
                ";",
                Path.of(inputDirectory),
                false
        );


        System.out.println("Export grid");

        CsvFileSink csvFileSink = new CsvFileSink(
                Path.of(outputDirectory),
                new ProcessorProvider(),
                new FileNamingStrategy(),
                ",");

        Set<NodeInput> nodes = simBenchRaw.getRawGrid().getNodes();

        NodeInput node81 = null;
        NodeInput node110 = null;
        NodeInput node25 = null;
        for (NodeInput node : nodes) {
            if (node.getUuid().equals(UUID.fromString("dc54bd8a-b7d8-4e99-adb0-d6ee5084241c"))) {
                node81 = node;
            }
            if (node.getUuid().equals(UUID.fromString("3e6be3ac-2b51-4080-b815-391313612fc7"))) {
                node110 = node;
            }
            if (node.getUuid().equals(UUID.fromString("43040a39-8b6c-401f-9dfd-82b42aa6dec6"))) {
                node25 = node;
            }
        }

        //Set<LoadInput> rawLoads = simBenchRaw.getSystemParticipants().getLoads();
        Set<LoadInput> modifiedLoads = new HashSet<>(simBenchRaw.getSystemParticipants().getLoads());

        LoadInput load81 = null;
        LoadInput load110 = null;
        LoadInput load25 = null;
        for (LoadInput load : modifiedLoads) {
            if (load.getUuid().equals(UUID.fromString("ec57d629-040a-46a6-b4dc-7d1db9378516"))) {
                load81 = load.copy().loadprofile(StandardLoadProfile.parse("h0")).build();
            }
            if (load.getUuid().equals(UUID.fromString("976eb5ba-3f07-4c8e-96d9-8453d822b910"))) {

                load110 = load.copy().loadprofile(StandardLoadProfile.parse("h0")).build();
            }
            if (load.getUuid().equals(UUID.fromString("30a27765-27e0-4488-b2a0-a9268db85a53"))) {
                load25 = load.copy().loadprofile(StandardLoadProfile.parse("h0")).build();
            }
        }
        modifiedLoads.removeIf(load -> load.getUuid().equals(UUID.fromString("ec57d629-040a-46a6-b4dc-7d1db9378516")));
        modifiedLoads.add(load81);
        modifiedLoads.removeIf(load -> load.getUuid().equals(UUID.fromString("976eb5ba-3f07-4c8e-96d9-8453d822b910")));
        modifiedLoads.add(load110);
        modifiedLoads.removeIf(load -> load.getUuid().equals(UUID.fromString("30a27765-27e0-4488-b2a0-a9268db85a53")));
        modifiedLoads.add(load25);

        // PV-Inputs für EM-Agent

        double albedo = 0.20000000298023224d;
        ComparableQuantity<Angle> azimuth = Quantities.getQuantity(-8.926613807678223d, AZIMUTH);
        ComparableQuantity<Angle> elevationAngle = Quantities.getQuantity(41.01871871948242d, SOLAR_ELEVATION_ANGLE);
        double kT = 1d;
        double kG = 0.8999999761581421d;
        CosPhiFixed cosPhiFixed = new CosPhiFixed("cosPhiFixed:{(0.0,0.95)}");
        ComparableQuantity<Dimensionless> etaConv = Quantities.getQuantity(98d, EFFICIENCY);
        double cosPhiRated = 0.95;

        ComparableQuantity<Power> sRated81 = Quantities.getQuantity(3.0, ACTIVE_POWER_IN);
        ComparableQuantity<Power> sRated110 = Quantities.getQuantity(4.9, ACTIVE_POWER_IN);
        ComparableQuantity<Power> sRated25 = Quantities.getQuantity(1.0, ACTIVE_POWER_IN);

        PvInput pvInput81 = new PvInput(
                UUID.fromString("a1eb7fc1-3bee-4b65-a387-ef3046644bf0"),
                "PV_Bus_81",
                node81,
                cosPhiFixed,
                albedo,
                azimuth,
                etaConv,
                elevationAngle,
                kG,
                kT,
                false,
                sRated81,
                cosPhiRated
        );

        PvInput pvInput110 = new PvInput(
                UUID.fromString("9d7cd8e2-d859-4f4f-9c01-abba06ef2e2c"),
                "PV_Bus_110",
                node81,
                cosPhiFixed,
                albedo,
                azimuth,
                etaConv,
                elevationAngle,
                kG,
                kT,
                false,
                sRated110,
                cosPhiRated
        );

        PvInput pvInput25 = new PvInput(
                UUID.fromString("e04c6258-0ee5-4928-96c1-8e04acbbcc8f"),
                "PV_Bus_25",
                node81,
                cosPhiFixed,
                albedo,
                azimuth,
                etaConv,
                elevationAngle,
                kG,
                kT,
                false,
                sRated25,
                cosPhiRated
        );

        // Storage-Inputs für EM-Agent

        ComparableQuantity<Power> pMax = Quantities.getQuantity(15, ACTIVE_POWER_IN);
        ComparableQuantity<Dimensionless> eta = Quantities.getQuantity(95, EFFICIENCY);
        ComparableQuantity<Dimensionless> dod = Quantities.getQuantity(10, EFFICIENCY);
        ComparableQuantity<DimensionlessRate> cpRate = Quantities.getQuantity(100, ACTIVE_POWER_GRADIENT);
        ComparableQuantity<Time> lifeTime = Quantities.getQuantity(175316.4, LIFE_TIME);
        int lifeCycle = 100;
        ComparableQuantity<Currency> capex = Quantities.getQuantity(100d, CAPEX);
        ComparableQuantity<EnergyPrice> opex = Quantities.getQuantity(50d, ENERGY_PRICE);
        ComparableQuantity<Energy> eStorage = Quantities.getQuantity(12, ENERGY_IN);
        ComparableQuantity<Power> sRatedStorage = Quantities.getQuantity(20, ACTIVE_POWER_IN);

        StorageTypeInput storageTypeInput = new StorageTypeInput(
                UUID.fromString("569de4c9-9ee4-4edc-96ab-413b7529df7e"),
                "LV5_Storage_Type_1",
                capex,
                opex,
                eStorage,
                sRatedStorage,
                cosPhiRated,
                pMax,
                cpRate,
                eta,
                dod,
                lifeTime,
                lifeCycle
        );
        StorageInput storageInput81 = new StorageInput(
                UUID.fromString("1ed5c49a-1c04-48d1-8fb5-6bcac0286c0b"),
                "LV5_Storage_Bus_81",
                node81,
                cosPhiFixed,
                storageTypeInput
        );
        StorageInput storageInput110 = new StorageInput(
                UUID.fromString("7e585565-a4e2-42f0-85cc-0a75035cbc3c"),
                "LV5_Storage_Bus_110",
                node110,
                cosPhiFixed,
                storageTypeInput
        );
        StorageInput storageInput25 = new StorageInput(
                UUID.fromString("61431e24-a8b2-4b09-9f51-4490a9f7b473"),
                "LV5_Storage_Bus_25",
                node25,
                cosPhiFixed,
                storageTypeInput
        );

        ComparableQuantity<Power> sRatedHp = Quantities.getQuantity(40, ACTIVE_POWER_IN);
        ComparableQuantity<Power> pThermal = Quantities.getQuantity(12, ACTIVE_POWER_IN);


        // HP-Inputs für EM-Agent
        ThermalBusInput thermalBus81 = new ThermalBusInput(
                UUID.fromString("0d95d7f2-49fb-4d49-8636-383a5220384e"),
                "LV5_ThermalBus_81"
        );

        ThermalBusInput thermalBus110 = new ThermalBusInput(
                UUID.fromString("2afe856f-8fa6-484e-bfa8-5c1828644c50"),
                "LV5_ThermalBus_110"
        );

        HpTypeInput hpTypeInput = new HpTypeInput(
                UUID.fromString("0d8df8c2-70bc-4edc-9627-7f6e3c515ada"),
                "LV5_Storage_HP_Type_1",
                capex,
                opex,
                sRatedHp,
                cosPhiRated,
                pThermal
        );

        HpInput hpInput81 = new HpInput(
                UUID.fromString("798028b5-caff-4da7-bcd9-1750fdd8742b"),
                "LV5_HP_Bus_81",
                node81,
                thermalBus81,
                cosPhiFixed,
                hpTypeInput
        );

        HpInput hpInput110 = new HpInput(
                UUID.fromString("139e4e20-5c28-4240-8dc8-30dbcc58aef0"),
                "LV5_HP_Bus_110",
                node81,
                thermalBus110,
                cosPhiFixed,
                hpTypeInput
        );

        // EmInput
        EmInput emInput81 = new EmInput(
                UUID.randomUUID(),
                "EM_HH_Bus_81",
                new UUID[] {
                    UUID.fromString("a1eb7fc1-3bee-4b65-a387-ef3046644bf0"),
                    UUID.fromString("1ed5c49a-1c04-48d1-8fb5-6bcac0286c0b"),
                    UUID.fromString("798028b5-caff-4da7-bcd9-1750fdd8742b")
                },
                "self_optimization"
        );
        EmInput emInput110 = new EmInput(
                UUID.randomUUID(),
                "EM_HH_Bus_110",
                new UUID[] {
                        UUID.fromString("a1eb7fc1-3bee-4b65-a387-ef3046644bf0"),
                        UUID.fromString("1ed5c49a-1c04-48d1-8fb5-6bcac0286c0b"),
                        UUID.fromString("798028b5-caff-4da7-bcd9-1750fdd8742b")
                },
                "self_optimization"
        );
        EmInput emInput25 = new EmInput(
                UUID.randomUUID(),
                "EM_HH_Bus_25",
                new UUID[] {
                        UUID.fromString("e04c6258-0ee5-4928-96c1-8e04acbbcc8f"),
                        UUID.fromString("61431e24-a8b2-4b09-9f51-4490a9f7b473")
                },
                "self_optimization"
        );


        Set<HpInput> hpUnits = new HashSet<>();
        hpUnits.add(hpInput81);
        hpUnits.add(hpInput110);

        Set<PvInput> pvUnits = new HashSet<>();
        pvUnits.add(pvInput81);
        pvUnits.add(pvInput110);
        pvUnits.add(pvInput25);

        Set<StorageInput> storageUnits = new HashSet<>();
        storageUnits.add(storageInput81);
        storageUnits.add(storageInput110);
        storageUnits.add(storageInput25);

        Set<EmInput> emUnits = new HashSet<>();
        emUnits.add(emInput81);
        emUnits.add(emInput110);
        emUnits.add(emInput25);


        ThermalHouseInput thermalHouse81 = new ThermalHouseInput(
                UUID.fromString("e30f3fad-0b77-4b26-a8d8-a021753abf8b"),
                "lv5_thermal_house_bus_81",
                thermalBus81,
                Quantities.getQuantity(10, StandardUnits.THERMAL_TRANSMISSION),
                Quantities.getQuantity(20, StandardUnits.HEAT_CAPACITY),
                Quantities.getQuantity(20, StandardUnits.TEMPERATURE),
                Quantities.getQuantity(25, StandardUnits.TEMPERATURE),
                Quantities.getQuantity(15, StandardUnits.TEMPERATURE));


        ThermalHouseInput thermalHouse110 = new ThermalHouseInput(
                UUID.fromString("d0c2e212-dec5-45f3-97ae-348886f0307f"),
                "lv5_thermal_house_bus_110",
                thermalBus110,
                Quantities.getQuantity(10, StandardUnits.THERMAL_TRANSMISSION),
                Quantities.getQuantity(20, StandardUnits.HEAT_CAPACITY),
                Quantities.getQuantity(20, StandardUnits.TEMPERATURE),
                Quantities.getQuantity(25, StandardUnits.TEMPERATURE),
                Quantities.getQuantity(15, StandardUnits.TEMPERATURE));

        SystemParticipants rawParticipants = simBenchRaw.getSystemParticipants();

        SystemParticipants modifiedSystemParticipants = rawParticipants.copy().pvPlants(
                pvUnits
        ).heatPumps(
                hpUnits
        ).storages(
                storageUnits
        ).loads(modifiedLoads)
        .build();

        JointGridContainer simBenchGrid = simBenchRaw.copy()
                .gridName("simbench_grid_lv5_with_em")
                .systemParticipants(modifiedSystemParticipants)
                .emUnits(new EnergyManagementUnits(emUnits))
                .build();

        csvFileSink.persistJointGrid(simBenchGrid);

        csvFileSink.persistAll(
                Set.of(
                        thermalHouse81,
                        thermalHouse110));




    }
}
