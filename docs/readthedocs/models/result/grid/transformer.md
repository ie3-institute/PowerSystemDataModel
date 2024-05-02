(transformer-result)=

# Transformer

Representation of transformers.

## Attributes, Units and Remarks

```{eval-rst}
.. list-table::
   :widths: 33 33 33
   :header-rows: 1


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

   * - tapPos
     - --
     -

```

## Caveats

Groups common information to both 2W and 3W transformers.
