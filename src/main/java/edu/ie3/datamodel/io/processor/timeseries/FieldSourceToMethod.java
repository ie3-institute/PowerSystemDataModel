/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.timeseries;

import java.lang.reflect.Method;

/**
 * Represent a tuple of {@link FieldSource} to {@link Method} to highlight, where information of a
 * time series can be obtained from
 */
public record FieldSourceToMethod(FieldSource source, Method method) {

  /** @deprecated since 3.0. Use {@link #source()} instead */
  @Deprecated(since = "3.0")
  public FieldSource getSource() {
    return source;
  }

  /** @deprecated since 3.0. Use {@link #method()} instead */
  @Deprecated(since = "3.0")
  public Method getMethod() {
    return method;
  }

  @Override
  public String toString() {
    return "FieldSourceToMethod{" + "source=" + source + ", method=" + method + '}';
  }

  /** Enum to denote, where information can be received from */
  public enum FieldSource {
    TIMESERIES,
    ENTRY,
    VALUE,
    WEATHER_IRRADIANCE,
    WEATHER_TEMPERATURE,
    WEATHER_WIND
  }
}
