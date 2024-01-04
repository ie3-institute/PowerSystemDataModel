.. _ev_model:

Electric Vehicle
-----------------------------
Model of an electric vehicle, that is occasionally connected to the grid via an :ref:`electric vehicle charging system<evcs_model>`.


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

   * - eStorage
     - kWh
     - Available battery capacity

   * - eCons
     - kWh / km
     - Energy consumption per driven kilometre

   * - sRated
     - kVA
     - Rated apparent power

   * - cosphiRated
     - --
     - Rated power factor


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
The :code:`node` attribute only marks the vehicles home connection point.
The actual connection to the grid is always given through :code:`EvcsInput`'s relation.
