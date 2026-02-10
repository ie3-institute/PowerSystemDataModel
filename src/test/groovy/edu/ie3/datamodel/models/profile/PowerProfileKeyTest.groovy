/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.profile

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE

import spock.lang.Specification

class PowerProfileKeyTest extends Specification {
  def "Load profile keys are parsed correctly from correct input" () {
    when:
    PowerProfileKey actual = new PowerProfileKey(key)

    then:
    actual == expected

    where:
    key      || expected
    "h0"     || BdewStandardLoadProfile.H0.key
    "h-0"    || BdewStandardLoadProfile.H0.key
    "h_0"    || BdewStandardLoadProfile.H0.key
    "H0"     || BdewStandardLoadProfile.H0.key
    "H-0"    || BdewStandardLoadProfile.H0.key
    "H_0"    || BdewStandardLoadProfile.H0.key
    "h25"    || BdewStandardLoadProfile.H25.key
    "h-25"   || BdewStandardLoadProfile.H25.key
    "h_25"   || BdewStandardLoadProfile.H25.key
    "H25"    || BdewStandardLoadProfile.H25.key
    "H-25"   || BdewStandardLoadProfile.H25.key
    "H_25"   || BdewStandardLoadProfile.H25.key
    "l0"     || BdewStandardLoadProfile.L0.key
    "l-0"    || BdewStandardLoadProfile.L0.key
    "l_0"    || BdewStandardLoadProfile.L0.key
    "L0"     || BdewStandardLoadProfile.L0.key
    "L-0"    || BdewStandardLoadProfile.L0.key
    "L_0"    || BdewStandardLoadProfile.L0.key
    "l1"     || BdewStandardLoadProfile.L1.key
    "l-1"    || BdewStandardLoadProfile.L1.key
    "l_1"    || BdewStandardLoadProfile.L1.key
    "L1"     || BdewStandardLoadProfile.L1.key
    "L-1"    || BdewStandardLoadProfile.L1.key
    "L_1"    || BdewStandardLoadProfile.L1.key
    "l2"     || BdewStandardLoadProfile.L2.key
    "l-2"    || BdewStandardLoadProfile.L2.key
    "l_2"    || BdewStandardLoadProfile.L2.key
    "L2"     || BdewStandardLoadProfile.L2.key
    "L-2"    || BdewStandardLoadProfile.L2.key
    "L_2"    || BdewStandardLoadProfile.L2.key
    "l25"    || BdewStandardLoadProfile.L25.key
    "l-25"   || BdewStandardLoadProfile.L25.key
    "l_25"   || BdewStandardLoadProfile.L25.key
    "L25"    || BdewStandardLoadProfile.L25.key
    "L-25"   || BdewStandardLoadProfile.L25.key
    "L_25"   || BdewStandardLoadProfile.L25.key
    "g0"     || BdewStandardLoadProfile.G0.key
    "g-0"    || BdewStandardLoadProfile.G0.key
    "g_0"    || BdewStandardLoadProfile.G0.key
    "G0"     || BdewStandardLoadProfile.G0.key
    "G-0"    || BdewStandardLoadProfile.G0.key
    "G_0"    || BdewStandardLoadProfile.G0.key
    "g1"     || BdewStandardLoadProfile.G1.key
    "g-1"    || BdewStandardLoadProfile.G1.key
    "g_1"    || BdewStandardLoadProfile.G1.key
    "G1"     || BdewStandardLoadProfile.G1.key
    "G-1"    || BdewStandardLoadProfile.G1.key
    "G_1"    || BdewStandardLoadProfile.G1.key
    "g2"     || BdewStandardLoadProfile.G2.key
    "g-2"    || BdewStandardLoadProfile.G2.key
    "g_2"    || BdewStandardLoadProfile.G2.key
    "G2"     || BdewStandardLoadProfile.G2.key
    "G-2"    || BdewStandardLoadProfile.G2.key
    "G_2"    || BdewStandardLoadProfile.G2.key
    "g3"     || BdewStandardLoadProfile.G3.key
    "g-3"    || BdewStandardLoadProfile.G3.key
    "g_3"    || BdewStandardLoadProfile.G3.key
    "G3"     || BdewStandardLoadProfile.G3.key
    "G-3"    || BdewStandardLoadProfile.G3.key
    "G_3"    || BdewStandardLoadProfile.G3.key
    "g4"     || BdewStandardLoadProfile.G4.key
    "g-4"    || BdewStandardLoadProfile.G4.key
    "g_4"    || BdewStandardLoadProfile.G4.key
    "G4"     || BdewStandardLoadProfile.G4.key
    "G-4"    || BdewStandardLoadProfile.G4.key
    "G_4"    || BdewStandardLoadProfile.G4.key
    "g5"     || BdewStandardLoadProfile.G5.key
    "g-5"    || BdewStandardLoadProfile.G5.key
    "g_5"    || BdewStandardLoadProfile.G5.key
    "G5"     || BdewStandardLoadProfile.G5.key
    "G-5"    || BdewStandardLoadProfile.G5.key
    "G_5"    || BdewStandardLoadProfile.G5.key
    "g6"     || BdewStandardLoadProfile.G6.key
    "g-6"    || BdewStandardLoadProfile.G6.key
    "g_6"    || BdewStandardLoadProfile.G6.key
    "G6"     || BdewStandardLoadProfile.G6.key
    "G-6"    || BdewStandardLoadProfile.G6.key
    "G_6"    || BdewStandardLoadProfile.G6.key
    "g25"    || BdewStandardLoadProfile.G25.key
    "g-25"   || BdewStandardLoadProfile.G25.key
    "g_25"   || BdewStandardLoadProfile.G25.key
    "G25"    || BdewStandardLoadProfile.G25.key
    "G-25"   || BdewStandardLoadProfile.G25.key
    "G_25"   || BdewStandardLoadProfile.G25.key
    "p25"    || BdewStandardLoadProfile.P25.key
    "p-25"   || BdewStandardLoadProfile.P25.key
    "p_25"   || BdewStandardLoadProfile.P25.key
    "P25"    || BdewStandardLoadProfile.P25.key
    "P-25"   || BdewStandardLoadProfile.P25.key
    "P_25"   || BdewStandardLoadProfile.P25.key
    "s25"    || BdewStandardLoadProfile.S25.key
    "s-25"   || BdewStandardLoadProfile.S25.key
    "s_25"   || BdewStandardLoadProfile.S25.key
    "S25"    || BdewStandardLoadProfile.S25.key
    "S-25"   || BdewStandardLoadProfile.S25.key
    "S_25"   || BdewStandardLoadProfile.S25.key
    "ep1"    || NbwTemperatureDependantLoadProfile.EP1.key
    "ez2"    || NbwTemperatureDependantLoadProfile.EZ2.key
    "random" || RANDOM_LOAD_PROFILE.key
    ""       || PowerProfileKey.NO_KEY_ASSIGNED
    null     || PowerProfileKey.NO_KEY_ASSIGNED
  }
}
