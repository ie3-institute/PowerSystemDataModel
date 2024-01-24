(ev-result)=

# Electric Vehicle

Result of an electric vehicle, that is occasionally connected to the grid via an [electric vehicle charging system](evcs.md#electric-vehicle-charging-station).

## Attributes, Units and Remarks

```{eval-rst}
.. list-table::
   :widths: 33 33 33
   :header-rows: 0


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     - --
     - uuid for the result entity

   * - time
     - --
     - date and time for the produced result

   * - inputModel
     - --
     - uuid for the associated input model

   * - p
     - MW
     - active power output normally provided in MW

   * - q
     - MVAr
     - reactive power output normally provided in MVAr

   * - soc
     - %
     - the current state of charge of the electric vehicle

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
