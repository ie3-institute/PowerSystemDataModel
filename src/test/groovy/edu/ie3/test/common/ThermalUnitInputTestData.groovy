package edu.ie3.test.common

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.util.TimeTools
import edu.ie3.util.quantities.interfaces.HeatCapacity
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity
import edu.ie3.util.quantities.interfaces.ThermalConductance
import tec.uom.se.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Temperature
import javax.measure.quantity.Volume

class ThermalUnitInputTestData {

    // general participant data
    private static final UUID thermalUnitUuid = UUID.fromString("717af017-cc69-406f-b452-e022d7fb516a")
    private static final OperationTime operationTime = OperationTime.builder()
            .withStart(TimeTools.toZonedDateTime("2020-03-24 15:11:31"))
            .withEnd(TimeTools.toZonedDateTime("2020-03-25 15:11:31")).build()
    private static final OperatorInput operator = new OperatorInput(
            UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "SystemParticipantOperator")


    // thermal bus input
    public static final thermalBusInput = new ThermalBusInput(thermalUnitUuid, operationTime, operator, "test_thermalBus")

    // thermal house input
    private static final Quantity<ThermalConductance> thermalConductance = Quantities.getQuantity(10, StandardUnits.THERMAL_TRANSMISSION)
    private static final Quantity<HeatCapacity> ethCapa = Quantities.getQuantity(20, StandardUnits.HEAT_CAPACITY)
    public static final thermalHouseInput = new ThermalHouseInput(thermalUnitUuid, "test_thermalHouseInput", thermalBusInput, thermalConductance, ethCapa)

    // thermal cylindric storage input
    private static final Quantity<Volume> storageVolumeLvl = Quantities.getQuantity(100, StandardUnits.VOLUME)
    private static final Quantity<Volume> storageVolumeLvlMin = Quantities.getQuantity(10, StandardUnits.VOLUME)
    private static final Quantity<Temperature> inletTemp = Quantities.getQuantity(100, StandardUnits.TEMPERATURE)
    private static final Quantity<Temperature> returnTemp = Quantities.getQuantity(80, StandardUnits.TEMPERATURE)
    private static final Quantity<SpecificHeatCapacity> c = Quantities.getQuantity(1.05, StandardUnits.SPECIFIC_HEAT_CAPACITY)

    public static final cylindricStorageInput = new CylindricalStorageInput(thermalUnitUuid,
            "test_cylindricStorageInput", thermalBusInput, storageVolumeLvl, storageVolumeLvlMin, inletTemp, returnTemp, c)

}
