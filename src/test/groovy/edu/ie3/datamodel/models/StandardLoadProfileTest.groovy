/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models

import spock.lang.Specification

class StandardLoadProfileTest extends Specification {
	def "Standard load profiles are parsed correctly from correct input" () {
		when:
		StandardLoadProfile actual = StandardLoadProfile.parse(key)

		then:
		actual == expected

		where:
		key     || expected
		"h0"    || BdewLoadProfile.H0
		"h-0"   || BdewLoadProfile.H0
		"h_0"   || BdewLoadProfile.H0
		"H0"    || BdewLoadProfile.H0
		"H-0"   || BdewLoadProfile.H0
		"H_0"   || BdewLoadProfile.H0
		"l0"    || BdewLoadProfile.L0
		"l-0"   || BdewLoadProfile.L0
		"l_0"   || BdewLoadProfile.L0
		"L0"    || BdewLoadProfile.L0
		"L-0"   || BdewLoadProfile.L0
		"L_0"   || BdewLoadProfile.L0
		"l1"    || BdewLoadProfile.L1
		"l-1"   || BdewLoadProfile.L1
		"l_1"   || BdewLoadProfile.L1
		"L1"    || BdewLoadProfile.L1
		"L-1"   || BdewLoadProfile.L1
		"L_1"   || BdewLoadProfile.L1
		"l2"    || BdewLoadProfile.L2
		"l-2"   || BdewLoadProfile.L2
		"l_2"   || BdewLoadProfile.L2
		"L2"    || BdewLoadProfile.L2
		"L-2"   || BdewLoadProfile.L2
		"L_2"   || BdewLoadProfile.L2
		"g0"    || BdewLoadProfile.G0
		"g-0"   || BdewLoadProfile.G0
		"g_0"   || BdewLoadProfile.G0
		"G0"    || BdewLoadProfile.G0
		"G-0"   || BdewLoadProfile.G0
		"G_0"   || BdewLoadProfile.G0
		"g1"    || BdewLoadProfile.G1
		"g-1"   || BdewLoadProfile.G1
		"g_1"   || BdewLoadProfile.G1
		"G1"    || BdewLoadProfile.G1
		"G-1"   || BdewLoadProfile.G1
		"G_1"   || BdewLoadProfile.G1
		"g2"    || BdewLoadProfile.G2
		"g-2"   || BdewLoadProfile.G2
		"g_2"   || BdewLoadProfile.G2
		"G2"    || BdewLoadProfile.G2
		"G-2"   || BdewLoadProfile.G2
		"G_2"   || BdewLoadProfile.G2
		"g3"    || BdewLoadProfile.G3
		"g-3"   || BdewLoadProfile.G3
		"g_3"   || BdewLoadProfile.G3
		"G3"    || BdewLoadProfile.G3
		"G-3"   || BdewLoadProfile.G3
		"G_3"   || BdewLoadProfile.G3
		"g4"    || BdewLoadProfile.G4
		"g-4"   || BdewLoadProfile.G4
		"g_4"   || BdewLoadProfile.G4
		"G4"    || BdewLoadProfile.G4
		"G-4"   || BdewLoadProfile.G4
		"G_4"   || BdewLoadProfile.G4
		"g5"    || BdewLoadProfile.G5
		"g-5"   || BdewLoadProfile.G5
		"g_5"   || BdewLoadProfile.G5
		"G5"    || BdewLoadProfile.G5
		"G-5"   || BdewLoadProfile.G5
		"G_5"   || BdewLoadProfile.G5
		"g6"    || BdewLoadProfile.G6
		"g-6"   || BdewLoadProfile.G6
		"g_6"   || BdewLoadProfile.G6
		"G6"    || BdewLoadProfile.G6
		"G-6"   || BdewLoadProfile.G6
		"G_6"   || BdewLoadProfile.G6
		""      || StandardLoadProfile.DefaultLoadProfiles.NO_STANDARD_LOAD_PROFILE
		null    || StandardLoadProfile.DefaultLoadProfiles.NO_STANDARD_LOAD_PROFILE
	}
}
