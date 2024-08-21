# Time Series
Time series are meant to represent a timely ordered series of values.
Those can either be electrical or non-electrical depending on what one may need for power system simulations.
Our time series models are divided into two subtypes:

## Individual Time Series
Each time instance in this time series has its own value (random duplicates may occur obviously).
They are only applicable for the time frame that is defined by the content of the time series.

## Repetitive Time Series
Those time series do have repetitive values, e.g. each day or at any other period.
Therefore, they can be applied to any time frame, as the mapping from time instant to value is made by information
reduction.
In addition to actual data, a mapping function has to be known.

## Available Classes
To be as flexible, as possible, the actual content of the time series is given as children of the `Value` class.
The following different values are available:

```{eval-rst}
.. list-table::
   :widths: 33 33
   :header-rows: 1
   
   * - Value Class 
     - Purpose
   
   * - `PValue`
     - Electrical active power
   
   * - `SValue`
     - Electrical active and reactive power
     
   * - `HeatAndPValue`
     - | Combination of thermal power (e.g. in kW) 
       | and electrical active power (e.g. in kW)
     
   * - `HeatAndSValue`
     - | Combination of thermal power (e.g. in kW) 
       | and electrical active and reactive power (e.g. in kW and kVAr)
     
   * - `EnergyPriceValue`
     - Wholesale market price (e.g. in € / MWh)
      
   * - `SolarIrradianceValue`
     - Combination of diffuse and direct solar irradiance
     
   * - `TemperatureValue`
     - Temperature information
     
   * - `WindValue`
     - Combination of wind direction and wind velocity
   
   * - `VoltageValue`
     - Combination of voltage magnitude in pu and angle in °
   
   * - `WeatherValue`
     - Combination of irradiance, temperature and wind information
           
```
