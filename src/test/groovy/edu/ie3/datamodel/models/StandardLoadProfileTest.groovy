/*
 * © 2021. TU Dortmund University,
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
		"h0"    || BdewStandardLoadProfile.H0
		"h-0"   || BdewStandardLoadProfile.H0
		"h_0"   || BdewStandardLoadProfile.H0
		"H0"    || BdewStandardLoadProfile.H0
		"H-0"   || BdewStandardLoadProfile.H0
		"H_0"   || BdewStandardLoadProfile.H0
		"l0"    || BdewStandardLoadProfile.L0
		"l-0"   || BdewStandardLoadProfile.L0
		"l_0"   || BdewStandardLoadProfile.L0
		"L0"    || BdewStandardLoadProfile.L0
		"L-0"   || BdewStandardLoadProfile.L0
		"L_0"   || BdewStandardLoadProfile.L0
		"l1"    || BdewStandardLoadProfile.L1
		"l-1"   || BdewStandardLoadProfile.L1
		"l_1"   || BdewStandardLoadProfile.L1
		"L1"    || BdewStandardLoadProfile.L1
		"L-1"   || BdewStandardLoadProfile.L1
		"L_1"   || BdewStandardLoadProfile.L1
		"l2"    || BdewStandardLoadProfile.L2
		"l-2"   || BdewStandardLoadProfile.L2
		"l_2"   || BdewStandardLoadProfile.L2
		"L2"    || BdewStandardLoadProfile.L2
		"L-2"   || BdewStandardLoadProfile.L2
		"L_2"   || BdewStandardLoadProfile.L2
		"g0"    || BdewStandardLoadProfile.G0
		"g-0"   || BdewStandardLoadProfile.G0
		"g_0"   || BdewStandardLoadProfile.G0
		"G0"    || BdewStandardLoadProfile.G0
		"G-0"   || BdewStandardLoadProfile.G0
		"G_0"   || BdewStandardLoadProfile.G0
		"g1"    || BdewStandardLoadProfile.G1
		"g-1"   || BdewStandardLoadProfile.G1
		"g_1"   || BdewStandardLoadProfile.G1
		"G1"    || BdewStandardLoadProfile.G1
		"G-1"   || BdewStandardLoadProfile.G1
		"G_1"   || BdewStandardLoadProfile.G1
		"g2"    || BdewStandardLoadProfile.G2
		"g-2"   || BdewStandardLoadProfile.G2
		"g_2"   || BdewStandardLoadProfile.G2
		"G2"    || BdewStandardLoadProfile.G2
		"G-2"   || BdewStandardLoadProfile.G2
		"G_2"   || BdewStandardLoadProfile.G2
		"g3"    || BdewStandardLoadProfile.G3
		"g-3"   || BdewStandardLoadProfile.G3
		"g_3"   || BdewStandardLoadProfile.G3
		"G3"    || BdewStandardLoadProfile.G3
		"G-3"   || BdewStandardLoadProfile.G3
		"G_3"   || BdewStandardLoadProfile.G3
		"g4"    || BdewStandardLoadProfile.G4
		"g-4"   || BdewStandardLoadProfile.G4
		"g_4"   || BdewStandardLoadProfile.G4
		"G4"    || BdewStandardLoadProfile.G4
		"G-4"   || BdewStandardLoadProfile.G4
		"G_4"   || BdewStandardLoadProfile.G4
		"g5"    || BdewStandardLoadProfile.G5
		"g-5"   || BdewStandardLoadProfile.G5
		"g_5"   || BdewStandardLoadProfile.G5
		"G5"    || BdewStandardLoadProfile.G5
		"G-5"   || BdewStandardLoadProfile.G5
		"G_5"   || BdewStandardLoadProfile.G5
		"g6"    || BdewStandardLoadProfile.G6
		"g-6"   || BdewStandardLoadProfile.G6
		"g_6"   || BdewStandardLoadProfile.G6
		"G6"    || BdewStandardLoadProfile.G6
		"G-6"   || BdewStandardLoadProfile.G6
		"G_6"   || BdewStandardLoadProfile.G6
		"ep1"	|| NbwTemperatureDependantStandardLoadProfile.EP1
		"ez2"	|| NbwTemperatureDependantStandardLoadProfile.EZ2
		""      || StandardLoadProfile.DefaultStandardLoadProfiles.NO_STANDARD_LOAD_PROFILE
		null    || StandardLoadProfile.DefaultStandardLoadProfiles.NO_STANDARD_LOAD_PROFILE
	}
}
