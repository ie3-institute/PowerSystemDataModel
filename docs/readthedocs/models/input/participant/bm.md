(bm-model)=

# Biomass plant

Model of a biomass power plant.

## Attributes, Units and Remarks

### Type Model

```{eval-rst}
.. list-table::
   :widths: 33 33 33
   :header-rows: 0


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     - --
     -

   * - id
     - --
     - Human readable identifier

   * - capex
     - €
     - Capital expenditure to purchase one entity of this type

   * - opex
     - € / MWh
     - | Operational expenditure to operate one entity of
       | this type

   * - activePowerGradient
     - % / h
     - Maximum permissible rate of change of power

   * - sRated
     - kVA
     - Rated apparent power

   * - cosPhiRated
     - --
     - Rated power factor

   * - etaConv
     - %
     - Efficiency of the assets inverter

```

### Entity Model

```{eval-rst}
.. list-table::
   :widths: 33 33 33
   :header-rows: 0


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     - --
     -

   * - id
     - --
     - Human readable identifier

   * - operator
     - --
     -

   * - operationTime
     - --
     - Timely restriction of operation

   * - node
     - --
     -

   * - qCharacteristics
     - --
     - [Reactive power characteristic](general.md#reactive-power-characteristics) to follow

   * - type
     - --
     -

   * - marketReaction
     - --
     - | Whether to adapt output based on (volatile)
       | market price or not

   * - costControlled
     - --
     - | Whether to adapt output based on the difference
       | between production costs and fixed feed in tariff or not

   * - feedInTariff
     - € / MWh
     - Fixed feed in tariff

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
