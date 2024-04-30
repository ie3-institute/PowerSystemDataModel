(connector-result)=

# Connector

Representation of all kinds of connectors.

## Attributes, Units and Remarks

```{eval-rst}
.. list-table::
   :widths: 33 33 33
   :header-rows: 0


   * - Attribute
     - Unit
     - Remarks

   * - time
     - ZonedDateTime
     - date and time for the produced result

   * - inputModel
     - --
     - uuid for the associated input model

   * - iAMag
     - ampere
     - A stands for sending node

   * - iAAng
     - degree
     -

   * - iBMag
     - ampere
     - B stands for receiving node

   * - iBAng
     - degree
     -

```

## Caveats

Groups all available connectors i.e. lines, switches and transformers
