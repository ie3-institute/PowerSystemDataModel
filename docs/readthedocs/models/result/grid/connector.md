(connector-result)=

# Connector

Representation of all kinds of connectors.

## Attributes, Units and Remarks

```{list-table}
   :widths: auto
   :header-rows: 1


   * - Attribute
     - Unit
     - Remarks

   * - time
     - ZonedDateTime
     - date and time for the produced result

   * - inputModel
     -
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
