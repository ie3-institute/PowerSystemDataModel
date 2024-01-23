(storage-model)=

# Electrical Energy Storage

Model of an ideal electrical battery energy storage.

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

   * - eStorage
     - kWh
     - Battery capacity

   * - sRated
     - kVA
     - Rated apparent power

   * - cosPhiRated
     - --
     - Rated power factor

   * - pMax
     - kW
     - | Maximum permissible active power
       | infeed or consumption

   * - activePowerGradient
     - % / h
     - Maximum permissible rate of change of power

   * - eta
     - %
     - Efficiency of the electrical inverter

   * - dod
     - %
     - | Maximum permissible depth of discharge. 80 % dod
       | is equivalent to a state of charge of 20 %.

   * - lifeTime
     - h
     - Permissible hours of full use

   * - lifeCycle
     - --
     - Permissible amount of full cycles

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
     - :ref:`Reactive power characteristic<participant-general-q-characteristic>` to follow

   * - type
     - --
     -

   * - behaviour
     - --
     - | Foreseen operation strategy of the storage.
       | Eligible input: *"market"*, *"grid"*, *"self"*


```

## Caveats

The field {code}`behaviour` will be removed in version 1.x, as this is an information, that is only important to a
smaller sub set of simulation applications.
