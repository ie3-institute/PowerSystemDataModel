(fixed-feed-in-model)=

# Fixed Feed In Facility

Model of a facility, that provides constant power feed in, as no further information about the actual behaviour of the
model can be derived.

## Attributes, Units and Remarks

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

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
