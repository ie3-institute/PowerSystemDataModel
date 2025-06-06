(fixed-feed-in-model)=

# Fixed Feed In Facility

Model of a facility, that provides constant power feed in, as no further information about the actual behaviour of the
model can be derived.

## Attributes, Units and Remarks

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

   * - sRated
     - kVA
     - Rated apparent power

   * - cosPhiRated
     -
     - Rated power factor

   * - controllingEm
     -
     - UUID reference to an [Energy Management Unit](#em_model) that is controlling
       this system participant. Field can be empty or missing, if this participant
       is not controlled.

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
