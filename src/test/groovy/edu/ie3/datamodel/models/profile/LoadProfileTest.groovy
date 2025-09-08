/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.profile

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE

import edu.ie3.datamodel.exceptions.ParsingException
import spock.lang.Specification

class LoadProfileTest extends Specification {
  def "Load profiles are parsed correctly from correct input" () {
    when:
    LoadProfile actual = LoadProfile.parse(key)

    then:
    actual == expected

    where:
    key      || expected
    "h0"     || BdewStandardLoadProfile.H0
    "h-0"    || BdewStandardLoadProfile.H0
    "h_0"    || BdewStandardLoadProfile.H0
    "H0"     || BdewStandardLoadProfile.H0
    "H-0"    || BdewStandardLoadProfile.H0
    "H_0"    || BdewStandardLoadProfile.H0
    "h25"    || BdewStandardLoadProfile.H25
    "h-25"   || BdewStandardLoadProfile.H25
    "h_25"   || BdewStandardLoadProfile.H25
    "H25"    || BdewStandardLoadProfile.H25
    "H-25"   || BdewStandardLoadProfile.H25
    "H_25"   || BdewStandardLoadProfile.H25
    "l0"     || BdewStandardLoadProfile.L0
    "l-0"    || BdewStandardLoadProfile.L0
    "l_0"    || BdewStandardLoadProfile.L0
    "L0"     || BdewStandardLoadProfile.L0
    "L-0"    || BdewStandardLoadProfile.L0
    "L_0"    || BdewStandardLoadProfile.L0
    "l1"     || BdewStandardLoadProfile.L1
    "l-1"    || BdewStandardLoadProfile.L1
    "l_1"    || BdewStandardLoadProfile.L1
    "L1"     || BdewStandardLoadProfile.L1
    "L-1"    || BdewStandardLoadProfile.L1
    "L_1"    || BdewStandardLoadProfile.L1
    "l2"     || BdewStandardLoadProfile.L2
    "l-2"    || BdewStandardLoadProfile.L2
    "l_2"    || BdewStandardLoadProfile.L2
    "L2"     || BdewStandardLoadProfile.L2
    "L-2"    || BdewStandardLoadProfile.L2
    "L_2"    || BdewStandardLoadProfile.L2
    "l25"    || BdewStandardLoadProfile.L25
    "l-25"   || BdewStandardLoadProfile.L25
    "l_25"   || BdewStandardLoadProfile.L25
    "L25"    || BdewStandardLoadProfile.L25
    "L-25"   || BdewStandardLoadProfile.L25
    "L_25"   || BdewStandardLoadProfile.L25
    "g0"     || BdewStandardLoadProfile.G0
    "g-0"    || BdewStandardLoadProfile.G0
    "g_0"    || BdewStandardLoadProfile.G0
    "G0"     || BdewStandardLoadProfile.G0
    "G-0"    || BdewStandardLoadProfile.G0
    "G_0"    || BdewStandardLoadProfile.G0
    "g1"     || BdewStandardLoadProfile.G1
    "g-1"    || BdewStandardLoadProfile.G1
    "g_1"    || BdewStandardLoadProfile.G1
    "G1"     || BdewStandardLoadProfile.G1
    "G-1"    || BdewStandardLoadProfile.G1
    "G_1"    || BdewStandardLoadProfile.G1
    "g2"     || BdewStandardLoadProfile.G2
    "g-2"    || BdewStandardLoadProfile.G2
    "g_2"    || BdewStandardLoadProfile.G2
    "G2"     || BdewStandardLoadProfile.G2
    "G-2"    || BdewStandardLoadProfile.G2
    "G_2"    || BdewStandardLoadProfile.G2
    "g3"     || BdewStandardLoadProfile.G3
    "g-3"    || BdewStandardLoadProfile.G3
    "g_3"    || BdewStandardLoadProfile.G3
    "G3"     || BdewStandardLoadProfile.G3
    "G-3"    || BdewStandardLoadProfile.G3
    "G_3"    || BdewStandardLoadProfile.G3
    "g4"     || BdewStandardLoadProfile.G4
    "g-4"    || BdewStandardLoadProfile.G4
    "g_4"    || BdewStandardLoadProfile.G4
    "G4"     || BdewStandardLoadProfile.G4
    "G-4"    || BdewStandardLoadProfile.G4
    "G_4"    || BdewStandardLoadProfile.G4
    "g5"     || BdewStandardLoadProfile.G5
    "g-5"    || BdewStandardLoadProfile.G5
    "g_5"    || BdewStandardLoadProfile.G5
    "G5"     || BdewStandardLoadProfile.G5
    "G-5"    || BdewStandardLoadProfile.G5
    "G_5"    || BdewStandardLoadProfile.G5
    "g6"     || BdewStandardLoadProfile.G6
    "g-6"    || BdewStandardLoadProfile.G6
    "g_6"    || BdewStandardLoadProfile.G6
    "G6"     || BdewStandardLoadProfile.G6
    "G-6"    || BdewStandardLoadProfile.G6
    "G_6"    || BdewStandardLoadProfile.G6
    "g25"    || BdewStandardLoadProfile.G25
    "g-25"   || BdewStandardLoadProfile.G25
    "g_25"   || BdewStandardLoadProfile.G25
    "G25"    || BdewStandardLoadProfile.G25
    "G-25"   || BdewStandardLoadProfile.G25
    "G_25"   || BdewStandardLoadProfile.G25
    "p25"    || BdewStandardLoadProfile.P25
    "p-25"   || BdewStandardLoadProfile.P25
    "p_25"   || BdewStandardLoadProfile.P25
    "P25"    || BdewStandardLoadProfile.P25
    "P-25"   || BdewStandardLoadProfile.P25
    "P_25"   || BdewStandardLoadProfile.P25
    "s25"    || BdewStandardLoadProfile.S25
    "s-25"   || BdewStandardLoadProfile.S25
    "s_25"   || BdewStandardLoadProfile.S25
    "S25"    || BdewStandardLoadProfile.S25
    "S-25"   || BdewStandardLoadProfile.S25
    "S_25"   || BdewStandardLoadProfile.S25
    "ep1"    || NbwTemperatureDependantLoadProfile.EP1
    "ez2"    || NbwTemperatureDependantLoadProfile.EZ2
    "random" || RANDOM_LOAD_PROFILE
    ""       || LoadProfile.DefaultLoadProfiles.NO_LOAD_PROFILE
    null     || LoadProfile.DefaultLoadProfiles.NO_LOAD_PROFILE
  }

  def "Standard load profiles can be parsed correctly"() {
    when:
    StandardLoadProfile actual = StandardLoadProfile.parse(key)

    then:
    actual == expected

    where:
    key     || expected
    "h0"    || BdewStandardLoadProfile.H0
    "h-0"   || BdewStandardLoadProfile.H0
    "h_0"   || BdewStandardLoadProfile.H0
    "H0"    || BdewStandardLoadProfile.H0
    "H-0"   || BdewStandardLoadProfile.H0
    "H_0"   || BdewStandardLoadProfile.H0
  }

  def "Temperature dependent load profiles can be parsed correctly"() {
    when:
    TemperatureDependantLoadProfile actual = TemperatureDependantLoadProfile.parse(key)

    then:
    actual == expected

    where:
    key       || expected
    "ep1"     || NbwTemperatureDependantLoadProfile.EP1
    "ep_1"    || NbwTemperatureDependantLoadProfile.EP1
    "ep_1"    || NbwTemperatureDependantLoadProfile.EP1
    "ez2"     || NbwTemperatureDependantLoadProfile.EZ2
    "ez-2"    || NbwTemperatureDependantLoadProfile.EZ2
    "ez_2"    || NbwTemperatureDependantLoadProfile.EZ2
  }

  def "BDEW load profiles can be gotten by their key"() {
    when:
    BdewStandardLoadProfile actual = BdewStandardLoadProfile.get(key)

    then:
    actual == expected

    where:
    key      || expected
    "h0"     || BdewStandardLoadProfile.H0
    "h-0"    || BdewStandardLoadProfile.H0
    "h_0"    || BdewStandardLoadProfile.H0
    "l0"     || BdewStandardLoadProfile.L0
    "l-0"    || BdewStandardLoadProfile.L0
    "l_0"    || BdewStandardLoadProfile.L0
    "g1"     || BdewStandardLoadProfile.G1
    "g-1"    || BdewStandardLoadProfile.G1
    "g_1"    || BdewStandardLoadProfile.G1
  }

  def "Nbw temperature dependant load profiles can be parsed correctly"() {
    when:
    NbwTemperatureDependantLoadProfile actual = NbwTemperatureDependantLoadProfile.get(key)

    then:
    actual == expected

    where:
    key       || expected
    "ep1"     || NbwTemperatureDependantLoadProfile.EP1
    "ep_1"    || NbwTemperatureDependantLoadProfile.EP1
    "ep_1"    || NbwTemperatureDependantLoadProfile.EP1
    "ez2"     || NbwTemperatureDependantLoadProfile.EZ2
    "ez-2"    || NbwTemperatureDependantLoadProfile.EZ2
    "ez_2"    || NbwTemperatureDependantLoadProfile.EZ2
  }

  def "Nbw temperature dependant load profiles can be gotten by their key"() {
    when:
    NbwTemperatureDependantLoadProfile actual = NbwTemperatureDependantLoadProfile.get(key)

    then:
    actual == expected

    where:
    key       || expected
    "ep1"     || NbwTemperatureDependantLoadProfile.EP1
    "ep_1"    || NbwTemperatureDependantLoadProfile.EP1
    "ep_1"    || NbwTemperatureDependantLoadProfile.EP1
    "ez2"     || NbwTemperatureDependantLoadProfile.EZ2
    "ez-2"    || NbwTemperatureDependantLoadProfile.EZ2
    "ez_2"    || NbwTemperatureDependantLoadProfile.EZ2
  }

  def "Throws an exception when encountering an unknown key"() {
    when:
    LoadProfile.parse("not_a_key")

    then:
    def e = thrown(ParsingException)
    e.message == "No predefined load profile with key 'not_a_key' found. Please provide one of the following keys: h0, h25, l0, l1, l2, l25, g0, g1, g2, g3, g4, g5, g6, g25, p25, s25, ep1, ez2, random"
  }
}
