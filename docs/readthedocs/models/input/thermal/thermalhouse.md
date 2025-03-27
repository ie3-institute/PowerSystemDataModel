(thermal-house-model)=

# Thermal House Model

Model for the thermal behaviour of a building.
This reflects a simple shoe box with transmission losses

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
     
   * - bus
     -
     - Thermal bus, the model is connected to

   * - operator
     -
     - 

   * - operationTime
     -
     - Timely restriction of operation

   * - ethLosses
     - kW / K
     - Thermal losses

   * - ethCapa
     - kWh / K
     - Thermal capacity

   * - targetTemperature
     - °C
     - Desired target temperature

   * - upperTemperatureLimit
     - °C
     - Upper temperature boundary

   * - lowerTemperatureLimit
     - °C
     - Lower temperature boundary
     
   * - housingType
     - –
     - Type of building can either be house or flat
     
   * - numberInhabitants
     - –
     - Number of people living in the house. Double values to enable modeling based on statistical data sources.

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
