(wec-model)=

# Wind Energy Converter

Model of a wind energy converter.

## Attributes, Units and Remarks

### Type Model

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     -
     -

   * - id
     -
     - Human readable identifier

   * - capex
     - €
     - Capital expenditure to purchase one entity of this type

   * - opex
     - € / MWh
     - Operational expenditure to operate one entity of
       this type

   * - sRated
     - kVA
     - Rated apparent power

   * - cosPhiRated
     -
     - Rated power factor

   * - cpCharacteristic
     -
     - Wind velocity dependent [Betz factors](#wec-cp-characteristic).

   * - etaConv
     - %
     - Efficiency of the assets inverter

   * - rotorArea
     - m²
     - Area the rotor covers

   * - hubHeight
     - m
     - Height of the rotor hub

```

### Entity Model

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     -
     -

   * - id
     -
     - Human readable identifier

   * - operator
     -
     -

   * - operationTime
     -
     - Timely restriction of operation

   * - node
     -
     -

   * - qCharacteristics
     -
     - [Reactive power characteristic](#participant-general-q-characteristic) to follow

   * - type
     -
     -

   * - marketReaction
     -
     - Whether to adapt output based on (volatile)
       market price or not

   * - controllingEm
     -
     - UUID reference to an [Energy Management Unit](#em_model) that is controlling
       this system participant. Field can be empty or missing, if this participant
       is not controlled.

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!

(wec-cp-characteristic)=

## Betz Characteristic

A collection of wind velocity to Betz factor pairs to be applied in
[Betz's law](https://en.wikipedia.org/wiki/Betz's_law) to determine the wind energy coming onto the rotor area.
