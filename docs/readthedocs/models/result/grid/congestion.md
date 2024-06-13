(congestion-result)=

# Congestion

Representation of a congestion result for a given subnet.

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
   
   * - subgrid
     - --
     - Sub grid number

   * - vMin
     - p.u.
     - minimal voltage of the subnet

   * - vMax
     - p.u.
     - maximal voltage of the subnet

   * - voltage
     - --
     - Boolean indicator, if a voltage congestion occurred

   * - line
     - --
     - Boolean indicator, if a line congestion occurred

   * - transformer
     - --
     - Boolean indicator, if a transformer congestion occurred
```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
