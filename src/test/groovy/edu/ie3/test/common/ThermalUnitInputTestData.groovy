/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.util.TimeUtil
import edu.ie3.util.quantities.interfaces.HeatCapacity
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity
import edu.ie3.util.quantities.interfaces.ThermalConductance
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Temperature
import javax.measure.quantity.Volume

class ThermalUnitInputTestData extends SystemParticipantTestData {

    // general participant data
    private static final UUID thermalUnitUuid = UUID.fromString("717af017-cc69-406f-b452-e022d7fb516a")
    public static final OperationTime operationTime = OperationTime.builder()
            .withStart(TimeUtil.withDefaults.toZonedDateTime("2020-03-24 15:11:31"))
            .withEnd(TimeUtil.withDefaults.toZonedDateTime("2020-03-25 15:11:31")).build()
    private static final OperatorInput operator = new OperatorInput(
            UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator")

    // thermal house input
    private static final ComparableQuantity<ThermalConductance> thermalConductance = Quantities.getQuantity(10, StandardUnits.THERMAL_TRANSMISSION)
    private static final ComparableQuantity<HeatCapacity> ethCapa = Quantities.getQuantity(20, StandardUnits.HEAT_CAPACITY)
    private static final ComparableQuantity<Temperature> TARGET_TEMPERATURE = Quantities.getQuantity(20, StandardUnits.TEMPERATURE)
    private static final ComparableQuantity<Temperature> UPPER_TEMPERATURE_LIMIT = Quantities.getQuantity(25, StandardUnits.TEMPERATURE)
    private static final ComparableQuantity<Temperature> LOWER_TEMPERATURE_LIMIT = Quantities.getQuantity(15, StandardUnits.TEMPERATURE)
    public static final thermalHouseInput = new ThermalHouseInput(
            thermalUnitUuid,
            "test_thermalHouseInput",
            operator,
            operationTime,
            thermalBus,
            thermalConductance,
            ethCapa,
            TARGET_TEMPERATURE,
            UPPER_TEMPERATURE_LIMIT,
            LOWER_TEMPERATURE_LIMIT)

    // thermal cylindric storage input
    private static final ComparableQuantity<Volume> storageVolumeLvl = Quantities.getQuantity(100, StandardUnits.VOLUME)
    private static final ComparableQuantity<Volume> storageVolumeLvlMin = Quantities.getQuantity(10, StandardUnits.VOLUME)
    private static final ComparableQuantity<Temperature> inletTemp = Quantities.getQuantity(100, StandardUnits.TEMPERATURE)
    private static final ComparableQuantity<Temperature> returnTemp = Quantities.getQuantity(80, StandardUnits.TEMPERATURE)
    private static final ComparableQuantity<SpecificHeatCapacity> c = Quantities.getQuantity(1.05, StandardUnits.SPECIFIC_HEAT_CAPACITY)

    public static final cylindricStorageInput = new CylindricalStorageInput(
            thermalUnitUuid,
            "test_cylindricStorageInput",
            operator,
            operationTime,
            thermalBus,
            storageVolumeLvl,
            storageVolumeLvlMin,
            inletTemp,
            returnTemp,
            c)

}
