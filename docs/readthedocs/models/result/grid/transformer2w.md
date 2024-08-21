(transformer2w-result)=

# Two Winding Transformer

Representation of two winding transformers.

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

   * - tapPos
     -
     -

```

## Caveats

Assumption: Node A is the node at higher voltage.
