(em_model)=

# Energy Management Unit

A model of an Energy Management Unit that controls the power of connected system participants. 
Participants are connected to an EM each via their `em` field.


```{eval-rst}
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

   * - controlStrategy
     - --
     - String representation (e.g. name) of a control strategy

   * - parentEm
     - --
     - | Reference to a superior Energy Management Unit that is controlling this EM.
       | Field can be empty or missing, if this EM itself is not controlled.

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
