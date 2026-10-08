/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.WeatherValue;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.measure.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherValueTimeseriesInterpolation {

  private static final Logger logger =
      LoggerFactory.getLogger(WeatherValueTimeseriesInterpolation.class);

  public static <V extends Quantity<V>> Quantity<V> interpolate(
      IndividualTimeSeries<WeatherValue> timeSeries,
      ZonedDateTime dateTime,
      String typeString,
      Quantity<V> defaultValue) {
    Optional<ValueSet<V>> valueSet = getValueOptions(timeSeries, dateTime, typeString);

    if (valueSet.isPresent()) {
      ValueSet<V> vs = valueSet.get();
      long interval = vs.weight1 + vs.weight2;

      Quantity<V> weighted1 = vs.value1.multiply(vs.weight1);
      Quantity<V> weighted2 = vs.value2.multiply(vs.weight2);

      return weighted1.add(weighted2).divide(interval);
    } else {
      logger.warn(
          "Interpolating value for timestamp {} failed. Using default: {}", dateTime, defaultValue);
      return defaultValue;
    }
  }

  private static <V extends Quantity<V>> Optional<ValueSet<V>> getValueOptions(
      IndividualTimeSeries<WeatherValue> timeSeries, ZonedDateTime dateTime, String typeString) {
    if (timeSeries.getEntries().size() < 3) {
      logger.info(
          "Not enough entries to interpolate. Need at least 3, got {}",
          timeSeries.getEntries().size());
      return Optional.empty();
    }

    ZonedDateTime intervalStart = dateTime.minusHours(2);
    ZonedDateTime intervalEnd = dateTime.plusHours(2);

    Optional<ValueWithWeight<V>> previous =
        getValue(timeSeries, dateTime, intervalStart, dateTime, typeString);
    Optional<ValueWithWeight<V>> next =
        getValue(timeSeries, dateTime, dateTime, intervalEnd, typeString);

    if (previous.isPresent() && next.isPresent()) {
      return Optional.of(
          new ValueSet<>(
              previous.get().value(),
              previous.get().weight(),
              next.get().value(),
              next.get().weight()));
    } else {
      return Optional.empty();
    }
  }

  private static <V extends Quantity<V>> Optional<ValueWithWeight<V>> getValue(
      IndividualTimeSeries<WeatherValue> timeSeries,
      ZonedDateTime timestamp,
      ZonedDateTime intervalStart,
      ZonedDateTime intervalEnd,
      String typeString) {
    List<ValueWithWeight<V>> values =
        (List<ValueWithWeight<V>>)
            timeSeries.getEntries().stream()
                .map(
                    entry -> {
                      ZonedDateTime time = entry.getTime();
                      long weight = Math.abs(ChronoUnit.SECONDS.between(time, timestamp));
                      if (time.isAfter(intervalStart) && time.isBefore(intervalEnd)) {
                        return getTypedValue(entry.getValue(), typeString)
                            .map(v -> new ValueWithWeight<>(v, weight));
                      }
                      return Optional.<ValueWithWeight<V>>empty();
                    })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparingLong(ValueWithWeight::weight))
                .collect(Collectors.toList());

    return values.stream().findFirst();
  }

  private static <V extends Quantity<V>> Optional<Quantity<V>> getTypedValue(
      WeatherValue weatherValue, String typeString) {
    switch (typeString) {
      case "diffIrr":
        return (Optional<Quantity<V>>)
            (Optional<?>) weatherValue.getSolarIrradiance().getDiffuseIrradiance();
      case "dirIrr":
        return (Optional<Quantity<V>>)
            (Optional<?>) weatherValue.getSolarIrradiance().getDirectIrradiance();
      case "temp":
        return (Optional<Quantity<V>>) (Optional<?>) weatherValue.getTemperature().getTemperature();
      case "windVel":
        return (Optional<Quantity<V>>) (Optional<?>) weatherValue.getWind().getVelocity();
      default:
        return Optional.empty();
    }
  }

  private static class ValueSet<V extends Quantity<V>> {
    final Quantity<V> value1;
    final long weight1;
    final Quantity<V> value2;
    final long weight2;

    public ValueSet(Quantity<V> value1, long weight1, Quantity<V> value2, long weight2) {
      this.value1 = value1;
      this.weight1 = weight1;
      this.value2 = value2;
      this.weight2 = weight2;
    }
  }
}
