(soil-data)=
# Soil Data

Soil data describes the physical properties of the soil surrounding an underground cable. It
consists of two related input entities:

* a **soil type**, which holds the thermal properties of a kind of soil, and
* a **soil layer**, which is a horizontal area (geometry) together with a vertical depth interval
  that is made up of a given soil type.

Together they allow the modelling of a layered soil profile per horizontal position, e.g. to
determine the thermal resistivity of the soil around a cable at a certain over-temperature.

This model is represented by the Java classes `edu.ie3.datamodel.models.input.grid.SoilType` and
`edu.ie3.datamodel.models.input.grid.SoilLayer`. Both are unique input entities. A `SoilLayer` references its `SoilType` via the `soilType` UUID. Both are collected in the
immutable `SoilData` holder, which is attached to a grid container (see
[grid container](/models/input/grid/gridcontainer)).

## Attributes, Units and Remarks

### Soil Type

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1

  * - Attribute
    - Unit
    - Remarks

  * - uuid
    - –
    - unique identifier for the soil type

  * - id
    - –
    - human readable identifier of the soil type

  * - thermalResistivityWet
    - $\mathrm{K}\cdot\mathrm{m}/\mathrm{W}$
    - thermal resistivity of the soil in the wet state

  * - thermalResistivityDry
    - $\mathrm{K}\cdot\mathrm{m}/\mathrm{W}$
    - thermal resistivity of the soil in the dry state

  * - specificHeatCapacity
    - $\mathrm{J}/(\mathrm{m}^3\cdot\mathrm{K})$
    - volumetric specific heat capacity of the soil

  * - criticalTemperatureDifference
    - Kelvin
    - over-temperature of the cable surface relative to the undisturbed soil temperature at which
      the soil transitions from the wet to the dry state
```

The method `currentThermalResistivity(temperatureDifference)` selects the dry thermal resistivity
if the given over-temperature reaches or exceeds `criticalTemperatureDifference`, and the wet
thermal resistivity otherwise.

### Soil Layer

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1

  * - Attribute
    - Unit
    - Remarks

  * - uuid
    - –
    - unique identifier for the soil layer

  * - geometry
    - –
    - horizontal footprint of the layer, e.g. a polygon

  * - zFrom
    - Metre
    - depth at which this layer starts (negative values denote depth below the surface)

  * - zTo
    - Metre
    - depth at which this layer ends

  * - soilType
    - –
    - UUID of the soil type that makes up this layer
```

The method `thickness()` returns the absolute distance between `zFrom` and `zTo`.

### Soil Data Holder

`SoilData` is an immutable holder of a set of soil types and a set of soil layers. `null` arguments
are treated as empty sets. The static factory method `SoilData.empty()` returns an instance holding
neither soil types nor layers.

## Validation

The general validation dispatches soil types and soil layers to `SoilValidationUtils`, which checks
in particular:

* that all required fields of a soil type and a soil layer are present,
* that thermal properties of a soil type are positive,
* that a soil layer has a positive thickness,
* that no two soil layers at the same geometry overlap in depth, and
* that the soil layers at the same geometry do not leave gaps in their depth coverage.

