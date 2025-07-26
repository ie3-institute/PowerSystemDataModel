# WeatherSource

The **`WeatherSource`** is an abstract class that provides a standardized interface for accessing time-series weather data. It serves as a foundation for concrete implementations that fetch data from various providers (e.g., CSV files or SQL databases).

A `WeatherSource` relies on two key components:
* **[`IdCoordinateSource`](idcoordinatesource.md)**: Used to resolve a numeric **coordinate ID** from the source data into a geographic `Point` object.
* **`TimeBasedWeatherValueFactory`**: Used to construct `WeatherValue` objects from the raw data fields.

***

## Information

The source data for any `WeatherSource` implementation is expected to contain the following information.

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1

   * - Attribute
     - Remarks

   * - **`coordinateid`**
     - An **integer ID** for a specific geographic coordinate. This ID is used to look up the actual `Point` via the `IdCoordinateSource`.

   * - **Time**
     - The specific timestamp of the weather data, typically as a `ZonedDateTime`.

   * - **Weather Data**
     - The meteorological values, such as solar irradiance (`direct` and `diffuse`), temperature, and wind data (`speed` and `direction`).