(ev-model)=

# Electric Vehicle

Model of an electric vehicle, that is occasionally connected to the grid via an [electric vehicle charging system](evcs.md#electric-vehicle-charging-station).

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
     - Available battery capacity

   * - eCons
     - kWh / km
     - Energy consumption per driven kilometre

   * - sRated
     - kVA
     - Rated apparent power for AC
     
   * - sRatedDC
     - kW
     - power for DC

   * - cosPhiRated
     - --
     - Rated power factor
     
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

   * - em
     - --
     - | UUID reference to an :ref:`Energy Management Unit<em_model>` that is controlling
       | this system participant. Field can be empty or missing, if this participant
       | is not controlled.

```

## Caveats

The {code}`node` attribute only marks the vehicles home connection point.
The actual connection to the grid is always given through {code}`EvcsInput`'s relation.
