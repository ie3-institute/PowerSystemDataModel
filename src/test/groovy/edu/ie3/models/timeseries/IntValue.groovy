package edu.ie3.models.timeseries

import edu.ie3.models.value.Value

class IntValue implements Value {
    private final int value

    IntValue(int value) {
        this.value = value
    }

    int getValue() {
        return value
    }
}