(storage-model)=

# Electrical Energy Storage

Model of an ideal electrical battery energy storage.

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

   * - eStorage
     - kWh
     - Battery capacity

   * - sRated
     - kVA
     - Rated apparent power

   * - cosPhiRated
     -
     - Rated power factor

   * - pMax
     - kW
     - Maximum permissible active power
       infeed or consumption

   * - activePowerGradient
     - % / h
     - Maximum permissible rate of change of power

   * - eta
     - %
     - Efficiency of the electrical inverter

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

   * - behaviour
     -
     - Foreseen operation strategy of the storage.
       Eligible input: *"market"*, *"grid"*, *"self"*
       
   * - controllingEm
     -
     - UUID reference to an [Energy Management Unit](#em_model) that is controlling
       this system participant. Field can be empty or missing, if this participant
       is not controlled.

```

## Caveats

The field {code}`behaviour` will be removed in version 1.x, as this is an information, that is only important to a
smaller sub set of simulation applications.
