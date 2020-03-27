package edu.ie3.test.common

import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput
import edu.ie3.datamodel.models.input.system.type.EvTypeInput
import edu.ie3.datamodel.models.input.system.type.HpTypeInput

import static edu.ie3.util.quantities.PowerSystemUnits.EURO
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR_PER_KILOMETRE
import static edu.ie3.util.quantities.PowerSystemUnits.PU
import static edu.ie3.util.quantities.PowerSystemUnits.EURO_PER_MEGAWATTHOUR
import static edu.ie3.util.quantities.PowerSystemUnits.MEGAVOLTAMPERE
import static tec.uom.se.unit.Units.SQUARE_METRE
import static tec.uom.se.unit.Units.METRE

import edu.ie3.datamodel.models.input.system.type.WecTypeInput
import tec.uom.se.quantity.Quantities

class TypeTestData extends GridTestData {
    public static EvTypeInput evType = new EvTypeInput(
            UUID.fromString("66b0db5d-b2fb-41d0-a9bc-990d6b6a36db"),
            "ev type",
            Quantities.getQuantity(100d, EURO),
            Quantities.getQuantity(101d, EURO_PER_MEGAWATTHOUR),
            Quantities.getQuantity(100d, KILOWATTHOUR),
            Quantities.getQuantity(23d, KILOWATTHOUR_PER_KILOMETRE),
            Quantities.getQuantity(22d, KILOWATT),
            0.9
    )

    public static ChpTypeInput chpType = new ChpTypeInput(
            UUID.fromString("1c027d3e-5409-4e52-a0e2-f8a23d5d0af0"),
            "chp type",
            Quantities.getQuantity(100d, EURO),
            Quantities.getQuantity(101d, EURO_PER_MEGAWATTHOUR),
            Quantities.getQuantity(0.95, PU),
            Quantities.getQuantity(0.9, PU),
            Quantities.getQuantity(58d, KILOVOLTAMPERE),
            0.98,
            Quantities.getQuantity(49.59, KILOWATT),
            Quantities.getQuantity(5d, KILOWATT)
    )

    public static HpTypeInput hpType = new HpTypeInput(
            UUID.fromString("1059ef51-9e17-4c13-928c-7c1c716d4ee6"),
            "hp type",
            Quantities.getQuantity(100d, EURO),
            Quantities.getQuantity(101d, EURO_PER_MEGAWATTHOUR),
            Quantities.getQuantity(45d, KILOWATT),
            0.975,
            Quantities.getQuantity(26.3, KILOWATT)
    )

    public static WecTypeInput wecType = new WecTypeInput(
            UUID.fromString("a24fc5b9-a26f-44de-96b8-c9f50b665cb3"),
            "Test wec type",
            Quantities.getQuantity(100d, EURO),
            Quantities.getQuantity(101d, EURO_PER_MEGAWATTHOUR),
            0.95,
            Quantities.getQuantity(0.9, PU),
            Quantities.getQuantity(2.5d, MEGAVOLTAMPERE),
            Quantities.getQuantity(2000d, SQUARE_METRE),
            Quantities.getQuantity(130d, METRE)
    )

    public static WecCharacteristicInput wecCharacteristic = new WecCharacteristicInput(
            UUID.fromString("ab5ed9e4-62b5-4f40-adf1-286bda97569c"),
            wecType,
            "{(0.0,0.0), (8.0,0.2), (12.0,0.5), (14.0,1.0), (22.0,0.0)}"
    )
}