(cylindricalstorage-model)=

# Cylindrical Thermal Storage

Model of a cylindrical thermal storage using a fluent to store thermal energy.

## Attributes, Units and Remarks

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     -
     -

   * - id
     -
     - Human readable identifier

   * - operator
     -
     -

   * - operationTime
     -
     - Timely restriction of operation

   * - thermalBus
     -
     - Connection point to the thermal system

   * - storageVolumeLvl
     - m³
     - Overall usable storage volume

   * - inletTemp
     - °C
     - Temperature of the inlet

   * - returnTemp
     - °C
     - Temperature of the outlet

   * - c
     - kWh / (K :math:`\cdot` m³)
     - Specific heat capacity of the storage medium

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
