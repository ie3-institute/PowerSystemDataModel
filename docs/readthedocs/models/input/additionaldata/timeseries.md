# Time Series
Time series are meant to represent a timely ordered series of values.
Those can either be electrical or non-electrical depending on what one may need for power system simulations.
Our time series models are divided into two subtypes:

## Individual Time Series
Each time instance in this time series has its own value (random duplicates may occur obviously).
They are only applicable for the time frame that is defined by the content of the time series.

Let's spend a few more words on the individual time series:
Those files are meant to carry different types of content - one might give information about wholesale market prices,
the other is a record of power values provided by a real system.
To be able to understand, what's inside the file, the *columnScheme* part of the file name gives insight of its
content.

For example, you have an IndividualTimeSeries CSV file for energy prices, then you use the key `c` from the table below
for columnScheme `its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1.csv`.
The CSV file must then have the appropriate format for the key `c` :

```text
   time,price
   2020-01-01T00:00:00Z,100.0
```

The CSV file requires a unique identification number.
The UUID (Universally Unique Identifier) can be created [here](https://www.uuidgenerator.net/).
You can also use the Method `java.util.UUID#randomUUID` to create a UUID.
This is the UUID from the example above `2fcb3e53-b94a-4b96-bea4-c469e499f1a1`.


## Repetitive Time Series
Those time series do have repetitive values, e.g. each day or at any other period.
Therefore, they can be applied to any time frame, as the mapping from time instant to value is made by information
reduction.
In addition to actual data, a mapping function has to be known.

## Available Classes
To be as flexible, as possible, the actual content of the time series is given as children of the `Value` class.
The following different values are available:

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1
   
   * - Value Class 
     - Purpose
   
   * - `PValue`
     - Electrical active power
   
   * - `SValue`
     - Electrical active and reactive power
     
   * - `HeatAndPValue`
     - Combination of thermal power (e.g. in kW) <br> and electrical active power (e.g. in kW)
     
   * - `HeatAndSValue`
     -  Combination of thermal power (e.g. in kW) <br> and electrical active and reactive power (e.g. in kW and kVAr)
     
   * - `EnergyPriceValue`
     - Wholesale market price (e.g. in € / MWh)
      
   * - `SolarIrradianceValue`
     - Combination of diffuse and direct solar irradiance
     
   * - `TemperatureValue`
     - Temperature information
     
   * - `WindValue`
     - Combination of wind direction and wind velocity
   
   * - `VoltageValue`
     - Combination of voltage magnitude in p.u. and angle in °
   
   * - `WeatherValue`
     - Combination of irradiance, temperature and wind information
           
   * - `BdewLoadValues`
     - Values for combination of seasons and day types

   * - `RandomLoadValues`
     - Parameters for a probability density function to draw random power consumptions
```
