.. _fixed_feed_in_model:

Fixed Feed In Facility
----------------------
Model of a facility, that provides constant power feed in, as no further information about the actual behaviour of the
model can be derived.

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

   * - qCharacteristics
     - --
     - :ref:`Reactive power characteristic<participant_general_q_characteristic>` to follow

   * - sRated
     - kVA
     - Rated apparent power

   * - cosPhiRated
     - --
     - Rated power factor

   * - em
     - --
     - | UUID reference to an :ref:`Energy Management Unit<em_model>` that is controlling
       | this system participant. Field can be empty or missing, if this participant
       | is not controlled.


Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!
