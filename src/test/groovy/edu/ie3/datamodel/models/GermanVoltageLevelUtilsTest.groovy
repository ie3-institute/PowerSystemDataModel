/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models

import static edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.*
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT

import edu.ie3.datamodel.models.voltagelevels.CommonVoltageLevel
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class GermanVoltageLevelUtilsTest extends Specification {

  def "The common german voltage level enum should be able to correctly parse different valid inputs"() {
    given:
    CommonVoltageLevel actual = parse(id, vRated)

    expect:
    actual == expected

    where:
    id      || vRated                                   || expected
    "NS"    || Quantities.getQuantity(0.4d, KILOVOLT)   || LV
    "0.4kV" || Quantities.getQuantity(0.4d, KILOVOLT)   || LV
    "10.0kV"|| Quantities.getQuantity(10d, KILOVOLT)    || MV_10KV
    "10kV"  || Quantities.getQuantity(10d, KILOVOLT)    || MV_10KV
    "MS"    || Quantities.getQuantity(15d, KILOVOLT)    || MV_10KV
    "MS"    || Quantities.getQuantity(20d, KILOVOLT)    || MV_20KV
    "20.0kV"|| Quantities.getQuantity(20d, KILOVOLT)    || MV_20KV
    "20kV"  || Quantities.getQuantity(20d, KILOVOLT)    || MV_20KV
    "MS"    || Quantities.getQuantity(35d, KILOVOLT)    || MV_30KV
    "30.0kV"|| Quantities.getQuantity(30d, KILOVOLT)    || MV_30KV
    "30kV"  || Quantities.getQuantity(30d, KILOVOLT)    || MV_30KV
    "HS"    || Quantities.getQuantity(110d, KILOVOLT)   || HV
    "HoeS"  || Quantities.getQuantity(220d, KILOVOLT)   || EHV_220KV
    "HoeS"  || Quantities.getQuantity(380d, KILOVOLT)   || EHV_380KV
  }
}
