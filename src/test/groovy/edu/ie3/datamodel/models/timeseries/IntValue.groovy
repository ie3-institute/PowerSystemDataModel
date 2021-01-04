/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.timeseries

import edu.ie3.datamodel.models.value.Value

class IntValue implements Value {
	private final int value

	IntValue(int value) {
		this.value = value
	}

	int getValue() {
		return value
	}
}