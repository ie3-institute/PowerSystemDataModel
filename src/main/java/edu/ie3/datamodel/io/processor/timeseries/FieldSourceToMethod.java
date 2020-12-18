/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.timeseries;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Represent a tuple of {@link FieldSource} to {@link Method} to highlight, where information of a
 * time series can be obtained from
 */
public class FieldSourceToMethod {
  private final FieldSource source;
  private final Method method;

  public FieldSourceToMethod(FieldSource source, Method method) {
    this.source = source;
    this.method = method;
  }

  public FieldSource getSource() {
    return source;
  }

  public Method getMethod() {
    return method;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FieldSourceToMethod that = (FieldSourceToMethod) o;
    return source == that.source && method.equals(that.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, method);
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
