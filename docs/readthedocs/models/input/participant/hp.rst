.. _hp_model:

Heat Pump
---------
Model of a heat pump.

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

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

   * - sRated
     - kVA
     - Rated apparent power

   * - cosphiRated
     - --
     - Rated power factor

   * - pThermal
     - kW
     - Rated thermal power (at rated electrical power)



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

   * - em
     - --
     - | UUID reference to an :ref:`Energy Management Unit<em_model>` that is controlling
       | this system participant. Field can be empty or missing, if this participant
       | is not controlled.



Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!
