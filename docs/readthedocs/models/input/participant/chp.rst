.. _chp_model:

Combined Heat and Power Plant
-----------------------------
Combined heat and power plant.


.. list-table:: Type Model
   :widths: auto
   :header-rows: 1


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

   * - etaEl
     - %
     - Efficiency of the electrical inverter

   * - etaThermal
     - %
     - Thermal efficiency of the system

   * - sRated
     - kVA
     - Rated apparent power

   * - cosPhiRated
     - --
     - Rated power factor

   * - pThermal
     - kW
     - Rated thermal power (at rated electrical power)

   * - pOwn
     - kW
     - Needed self-consumption


.. list-table:: Entity Model
   :widths: auto
   :header-rows: 1


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

   * - thermalBus
     - --
     - Connection point to the thermal system

   * - qCharacteristics
     - --
     - :ref:`Reactive power characteristic<participant_general_q_characteristic>` to follow

   * - type
     - --
     -

   * - thermalStorage
     - --
     - Reference to thermal storage

   * - marketReaction
     - --
     - | Whether to adapt output based on (volatile)
       | market price or not

   * - em
     - --
     - | UUID reference to an :ref:`Energy Management Unit<em_model>` that is controlling
       | this system participant. Field can be empty or missing, if this participant
       | is not controlled.


Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!
