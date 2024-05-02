(measurement-unit-model)=

# Measurement Unit

Representation of a measurement unit placed at a node.
It can be used to mark restrictive access to simulation results to e.g. control algorithms.
The measured information are indicated by boolean fields.

## Attributes, Units and Remarks

```{eval-rst}
.. list-table::
   :widths: 33 33 33
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

   * - vMag
     - --
     - Voltage magnitude measurements are available

   * - vAng
     - --
     - Voltage angle measurements are available

   * - p
     - --
     - Active power measurements are available

   * - q
     - --
     - Reactive power measurements are available

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
