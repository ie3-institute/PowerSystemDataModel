(thermal-line-segment-result)=

# Thermal Line Segment

Representation of a thermal line segment.

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

   * - lineSegmentTemperature
     - kelvin
     - temperature of the thermal line segment

   * - groundTemperature
     - kelvin
     - ground temperature at the depth of the line segment
```

## Caveats

The `lineSegmentTemperature` and `groundTemperature` are always converted to Kelvin (`StandardUnits.TEMPERATURE`) upon construction, regardless of the input unit.