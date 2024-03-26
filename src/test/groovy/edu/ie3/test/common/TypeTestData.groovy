/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import static edu.ie3.datamodel.models.StandardUnits.*
import static edu.ie3.test.helper.QuantityHelper.*
import static edu.ie3.util.quantities.PowerSystemUnits.*

import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.datamodel.models.input.system.type.*
import tech.units.indriya.quantity.Quantities

class TypeTestData {
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
  0.9,
  Quantities.getQuantity(20d, KILOWATT)
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

  public static final WecCharacteristicInput wecCharacteristicInput = new WecCharacteristicInput("cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}")

  public static final WecTypeInput wecType = new WecTypeInput(
  UUID.fromString("a24fc5b9-a26f-44de-96b8-c9f50b665cb3"),
  "Test wec type",
  Quantities.getQuantity(100d, EURO),
  Quantities.getQuantity(101d, EURO_PER_MEGAWATTHOUR),
  Quantities.getQuantity(2.5d, MEGAVOLTAMPERE),
  0.95,
  wecCharacteristicInput,
  Quantities.getQuantity(0.9, PU),
  Quantities.getQuantity(2000d, SQUARE_METRE),
  Quantities.getQuantity(130d, METRE)
  )

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // asset types

  static Set<LineTypeInput> lineTypes() {
    return[
      lineType380kV_1300,
      lineType20kV_400,
      lineType10kV_300,
      lineType10kV_500,
      lineType400V_170,
      lineType400V_120
    ] as Set
  }

  static Set<Transformer2WTypeInput> transformer2WTypes() {
    return [
      transformerTypeEHV_HV_40,
      transformerTypeEHV_HV_30,
      transformerTypeHV_30kV_40,
      transformerTypeHV_20kV_40,
      transformerTypeHV_10kV_20,
      transformerTypeHV_10kV_10
    ] as Set
  }

  static Set<Transformer3WTypeInput> transformer3WTypes() {
    return [
      transformerTypeEHV_HV_20kV,
      transformerTypeEHV_20kV_10kV,
      transformerTypeHV_20kV_10kV
    ] as Set
  }

  public static final LineTypeInput lineType380kV_1300 = buildLine(380d, 1300)
  public static final LineTypeInput lineType20kV_400 = buildLine(20d, 400)
  public static final LineTypeInput lineType10kV_300 = buildLine(10d, 300)
  public static final LineTypeInput lineType10kV_500 = buildLine(10d, 500)
  public static final LineTypeInput lineType400V_120 = buildLine(0.4d, 120)
  public static final LineTypeInput lineType400V_170 = buildLine(0.4d, 170)


  public static final Transformer2WTypeInput transformerTypeEHV_HV_40 = buildTrafo(380d, 110d, 40000d)
  public static final Transformer2WTypeInput transformerTypeEHV_HV_30 = buildTrafo(380d, 110d, 30000d)
  public static final Transformer2WTypeInput transformerTypeHV_30kV_40 = buildTrafo(110d, 30d, 40000d)
  public static final Transformer2WTypeInput transformerTypeHV_20kV_40 = buildTrafo(110d, 20d, 40000d)
  public static final Transformer2WTypeInput transformerTypeHV_10kV_20 = buildTrafo(110d, 10d, 20000d)
  public static final Transformer2WTypeInput transformerTypeHV_10kV_10 = buildTrafo(110d, 10d, 10000d)

  public static final Transformer3WTypeInput transformerTypeEHV_HV_20kV = buildTrafo(380d, 110d, 20d, 120000d, 60000d, 40000d)
  public static final Transformer3WTypeInput transformerTypeEHV_20kV_10kV = buildTrafo(380d, 20d, 10d, 60000d, 40000d, 20000d)
  public static final Transformer3WTypeInput transformerTypeHV_20kV_10kV = buildTrafo(110d, 20d, 10d, 50000d, 30000d, 20000d)



  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // static utility functions

  protected static LineTypeInput buildLine(
      double vRated,
      double iMax
  ) {
    return new LineTypeInput(
        UUID.randomUUID(),
        vRated+"_"+iMax,
        Quantities.getQuantity(1d, CONDUCTANCE_PER_LENGTH),
        Quantities.getQuantity(0d, SUSCEPTANCE_PER_LENGTH),
        Quantities.getQuantity(1d, OHM_PER_KILOMETRE),
        Quantities.getQuantity(1d, OHM_PER_KILOMETRE),
        current(iMax),
        potential(vRated)
        )
  }

  protected static Transformer2WTypeInput buildTrafo(
      double vRatedA,
      double vRatedB,
      double sRated
  ) {
    return new Transformer2WTypeInput(
        UUID.randomUUID(),
        vRatedA+"-"+vRatedB+"_"+sRated,
        Quantities.getQuantity(45.375d, RESISTANCE),
        Quantities.getQuantity(102.759d, REACTANCE),
        power(sRated),
        potential(vRatedA),
        potential(vRatedB),
        Quantities.getQuantity(0d, CONDUCTANCE),
        Quantities.getQuantity(0d, SUSCEPTANCE),
        Quantities.getQuantity(1.5d, DV_TAP),
        Quantities.getQuantity(0d, DPHI_TAP),
        false,
        0,
        -10,
        10
        )
  }

  protected static Transformer3WTypeInput buildTrafo(
      double vRatedA,
      double vRatedB,
      double vRatedC,
      double sRatedA,
      double sRatedB,
      double sRatedC
  ) {
    return new Transformer3WTypeInput(
        UUID.randomUUID(),
        vRatedA+"-"+vRatedB+"-"+vRatedC+"_"+sRatedA+"_"+sRatedB+"_"+sRatedC,
        power(sRatedA),
        power(sRatedB),
        power(sRatedC),
        potential(vRatedA),
        potential(vRatedB),
        potential(vRatedC),
        Quantities.getQuantity(0.3d, RESISTANCE),
        Quantities.getQuantity(0.025d, RESISTANCE),
        Quantities.getQuantity(0.0008d, RESISTANCE),
        Quantities.getQuantity(1d, REACTANCE),
        Quantities.getQuantity(0.08d, REACTANCE),
        Quantities.getQuantity(0.003d, REACTANCE),
        Quantities.getQuantity(40000d, CONDUCTANCE),
        Quantities.getQuantity(-1000d, SUSCEPTANCE),
        Quantities.getQuantity(1.5d, DV_TAP),
        Quantities.getQuantity(0d, DPHI_TAP),
        0,
        -10,
        10
        )
  }
}