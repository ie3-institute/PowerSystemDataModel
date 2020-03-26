package edu.ie3.test.common

import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput

import static edu.ie3.util.quantities.PowerSystemUnits.EURO
import static edu.ie3.util.quantities.PowerSystemUnits.PU
import static edu.ie3.util.quantities.PowerSystemUnits.EURO_PER_MEGAWATTHOUR
import static edu.ie3.util.quantities.PowerSystemUnits.MEGAVOLTAMPERE
import static tec.uom.se.unit.Units.SQUARE_METRE
import static tec.uom.se.unit.Units.METRE

import edu.ie3.datamodel.models.input.system.type.WecTypeInput
import tec.uom.se.quantity.Quantities

class TypeTestData extends GridTestData {
    public static WecTypeInput wecType = new WecTypeInput(
            UUID.fromString("a24fc5b9-a26f-44de-96b8-c9f50b665cb3"),
            "Test wec type",
            Quantities.getQuantity(100d, EURO),
            Quantities.getQuantity(101d, EURO_PER_MEGAWATTHOUR),
            0.95,
            Quantities.getQuantity(0.9, PU),
            Quantities.getQuantity(2500d, MEGAVOLTAMPERE),
            Quantities.getQuantity(2000d, SQUARE_METRE),
            Quantities.getQuantity(130d, METRE)
    )

    public static WecCharacteristicInput wecCharacteristic = new WecCharacteristicInput(
            UUID.fromString("ab5ed9e4-62b5-4f40-adf1-286bda97569c"),
            wecType,
            "{(0.0,0.0), (8.0,0.2), (12.0,0.5), (14.0,1.0), (22.0,0.0)}"
    )
}