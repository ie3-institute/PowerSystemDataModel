(congestion-result)=

# Congestion

Representation of a congestion result for a given asset.

## Attributes, Units and Remarks

```{list-table}
   :widths: 33 33 33
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
     
   * - inputModelType
     -
     - the type of the input model (e.g. node, line, etc.)
     
   * - subgrid
     -
     - Sub grid number

   * - min
     - Percent
     - the actual value that was calculated
     
   * - min
     - Percent
     - minimal value that is possible

   * - max
     - Percent
     - maximal value that is possible
```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
