/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import static edu.ie3.util.quantities.PowerSystemUnits.EURO
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR_PER_KILOMETRE
import static edu.ie3.util.quantities.PowerSystemUnits.PU
import static edu.ie3.util.quantities.PowerSystemUnits.EURO_PER_MEGAWATTHOUR
import static edu.ie3.util.quantities.PowerSystemUnits.MEGAVOLTAMPERE
import static edu.ie3.util.quantities.PowerSystemUnits.PU_PER_HOUR
import static tec.uom.se.unit.Units.HOUR
import static tec.uom.se.unit.Units.PERCENT
import static tec.uom.se.unit.Units.SQUARE_METRE
import static tec.uom.se.unit.Units.METRE

import edu.ie3.datamodel.models.input.system.type.BmTypeInput
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput
import edu.ie3.datamodel.models.input.system.type.EvTypeInput
import edu.ie3.datamodel.models.input.system.type.HpTypeInput
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput

import edu.ie3.datamodel.models.input.system.type.WecTypeInput
import tec.uom.se.quantity.Quantities

class TypeTestData extends GridTestData {
	public static final BmTypeInput bmType = new BmTypeInput(
	UUID.fromString("c3bd30f5-1a62-4a37-86e3-074040d965a4"),
	"bm type",
	Quantities.getQuantity(100d, EURO),
	Quantities.getQuantity(101d, EURO_PER_MEGAWATTHOUR),
	Quantities.getQuantity(0.05, PU_PER_HOUR),
	Quantities.getQuantity(800d, KILOVOLTAMPERE),
	0.965,
	Quantities.getQuantity(0.89, PU)
	)

	public static final ChpTypeInput chpType = new ChpTypeInput(
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

	public static final EvTypeInput evType = new EvTypeInput(
	UUID.fromString("66b0db5d-b2fb-41d0-a9bc-990d6b6a36db"),
	"ev type",
	Quantities.getQuantity(100d, EURO),
	Quantities.getQuantity(101d, EURO_PER_MEGAWATTHOUR),
	Quantities.getQuantity(100d, KILOWATTHOUR),
	Quantities.getQuantity(23d, KILOWATTHOUR_PER_KILOMETRE),
	Quantities.getQuantity(22d, KILOWATT),
	0.9
	)

	public static final HpTypeInput hpType = new HpTypeInput(
	UUID.fromString("1059ef51-9e17-4c13-928c-7c1c716d4ee6"),
	"hp type",
	Quantities.getQuantity(100d, EURO),
	Quantities.getQuantity(101d, EURO_PER_MEGAWATTHOUR),
	Quantities.getQuantity(45d, KILOWATT),
	0.975,
	Quantities.getQuantity(26.3, KILOWATT)
	)

	public static final StorageTypeInput storageType = new StorageTypeInput(
	UUID.fromString("fbee4995-24dd-45e4-9c85-7d986fe99ff3"),
	"storage type",
	Quantities.getQuantity(100d, EURO),
	Quantities.getQuantity(101d, EURO_PER_MEGAWATTHOUR),
	Quantities.getQuantity(200d, KILOWATTHOUR),
	Quantities.getQuantity(13d, KILOVOLTAMPERE),
	0.997,
	Quantities.getQuantity(12.961, KILOWATT),
	Quantities.getQuantity(0.03, PU_PER_HOUR),
	Quantities.getQuantity(0.92, PU),
	Quantities.getQuantity(20d, PERCENT),
	Quantities.getQuantity(43800.0, HOUR),
	100000
	)

	public static final WecTypeInput wecType = new WecTypeInput(
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

	//	public static final WecCharacteristicInput wecCharacteristic = new WecCharacteristicInput(
	//	UUID.fromString("ab5ed9e4-62b5-4f40-adf1-286bda97569c"),
	//	wecType,
	//	"{(0.0,0.0), (8.0,0.2), (12.0,0.5), (14.0,1.0), (22.0,0.0)}"
	//	)
}