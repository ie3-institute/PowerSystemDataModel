(domestichotwaterstorage-model)=

# Domestic Hot Water Storage

Model of a domestic hot water storage using a fluent to store thermal energy.

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

   * - thermalBus
     - --
     - Connection point to the thermal system

   * - storageVolumeLvl
     - m³
     - Overall available storage volume

   * - inletTemp
     - °C
     - Temperature of the inlet

   * - returnTemp
     - °C
     - Temperature of the outlet

   * - c
     - kWh / (K :math:`\cdot` m³)
     - Specific heat capacity of the storage medium
     
   * - pThermalMax
     - kW
     - Maximum permissible thermal power of the storage

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
