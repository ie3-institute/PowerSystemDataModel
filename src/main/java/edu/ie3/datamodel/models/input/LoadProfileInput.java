/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import static java.time.temporal.ChronoUnit.HOURS;

import edu.ie3.datamodel.models.LoadProfileType;
import edu.ie3.datamodel.models.timeseries.RepetitiveTimeSeries;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// TODO This is a sample implementation, please implement a real scenario
public class LoadProfileInput extends RepetitiveTimeSeries<PValue> {

  private final LoadProfileType type;
  private final Map<DayOfWeek, Map<Integer, PValue>> dayOfWeekToHourlyValues;

  public LoadProfileInput(
      UUID uuid,
      LoadProfileType type,
      Map<DayOfWeek, Map<Integer, PValue>> dayOfWeekToHourlyValues) {
    super(uuid);
    this.type = type;
    this.dayOfWeekToHourlyValues = dayOfWeekToHourlyValues;
  }

  public LoadProfileInput(
      LoadProfileType type, Map<DayOfWeek, Map<Integer, PValue>> dayOfWeekToHourlyValues) {
    super();
    this.type = type;
    this.dayOfWeekToHourlyValues = dayOfWeekToHourlyValues;
  }

  @Override
  public PValue calc(ZonedDateTime time) {
    return dayOfWeekToHourlyValues.get(time.getDayOfWeek()).get(time.getHour());
  }

  @Override
  protected Optional<ZonedDateTime> getPreviousZonedDateTime(ZonedDateTime time) {
    return Optional.of(time.minus(1, HOURS));
  }

  @Override
  protected Optional<ZonedDateTime> getNextZonedDateTime(ZonedDateTime time) {
    return Optional.of(time.plus(1, HOURS));
  }

  public LoadProfileType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "LoadProfileInput{" +
            "type=" + type +
            ", dayOfWeekToHourlyValues=" + dayOfWeekToHourlyValues +
            '}';
  }
}
