/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.timeseries;

import edu.ie3.datamodel.io.processor.GetterMethod;

/**
 * Represent a tuple of {@link FieldSource} to {@link GetterMethod} to highlight, where information
 * of a time series can be obtained from
 */
public record FieldSourceToMethod(FieldSource source, GetterMethod method) {
  @Override
  public String toString() {
    return "FieldSourceToMethod{" + "source=" + source + ", method=" + method + '}';
  }

  /** Enum to denote, where information can be received from */
  public enum FieldSource {
    /** Timeseries field source. */
    TIMESERIES,
    /** Entry field source. */
    ENTRY,
    /** Value field source. */
    VALUE,
    /** Weather irradiance field source. */
    WEATHER_IRRADIANCE,
    /** Weather temperature field source. */
    WEATHER_TEMPERATURE,
    /** Weather wind field source. */
    WEATHER_WIND,
  }
}
