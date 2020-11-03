/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.*;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Point;

/** Container class to hold different types of individual time series */
public class TimeSeriesContainer {
  private final Map<Point, IndividualTimeSeries<WeatherValue>> weather;
  private final Set<IndividualTimeSeries<EnergyPriceValue>> energyPrice;
  private final Set<IndividualTimeSeries<HeatAndSValue>> heatAndApparentPower;
  private final Set<IndividualTimeSeries<HeatAndPValue>> heatAndActivePower;
  private final Set<IndividualTimeSeries<HeatDemandValue>> heat;
  private final Set<IndividualTimeSeries<SValue>> apparentPower;
  private final Set<IndividualTimeSeries<PValue>> activePower;

  public TimeSeriesContainer(
      Map<Point, IndividualTimeSeries<WeatherValue>> weather,
      Set<IndividualTimeSeries<EnergyPriceValue>> energyPrice,
      Set<IndividualTimeSeries<HeatAndSValue>> heatAndApparentPower,
      Set<IndividualTimeSeries<HeatAndPValue>> heatAndActivePower,
      Set<IndividualTimeSeries<HeatDemandValue>> heat,
      Set<IndividualTimeSeries<SValue>> apparentPower,
      Set<IndividualTimeSeries<PValue>> activePower) {
    this.weather = Objects.requireNonNull(weather, "Weather time series may not be null.");
    this.energyPrice =
        Objects.requireNonNull(energyPrice, "Energy price time series may not be null.");
    this.heatAndApparentPower =
        Objects.requireNonNull(
            heatAndApparentPower, "Heat and apparent power time series may not be null.");
    this.heatAndActivePower =
        Objects.requireNonNull(
            heatAndActivePower, "Heat and active power time series may not be null.");
    this.heat = Objects.requireNonNull(heat, "Heat time series may not be null.");
    this.apparentPower =
        Objects.requireNonNull(apparentPower, "Apparent power time series may not be null.");
    this.activePower =
        Objects.requireNonNull(activePower, "Active power time series may not be null.");
  }

  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather() {
    return weather;
  }

  public Set<IndividualTimeSeries<EnergyPriceValue>> getEnergyPrice() {
    return energyPrice;
  }

  public Set<IndividualTimeSeries<HeatAndSValue>> getHeatAndApparentPower() {
    return heatAndApparentPower;
  }

  public Set<IndividualTimeSeries<HeatAndPValue>> getHeatAndActivePower() {
    return heatAndActivePower;
  }

  public Set<IndividualTimeSeries<HeatDemandValue>> getHeat() {
    return heat;
  }

  public Set<IndividualTimeSeries<SValue>> getApparentPower() {
    return apparentPower;
  }

  public Set<IndividualTimeSeries<PValue>> getActivePower() {
    return activePower;
  }

  public Set<IndividualTimeSeries<Value>> getAll() {
    return Stream.of(
            weather.values(),
            energyPrice,
            heatAndApparentPower,
            heatAndActivePower,
            heat,
            apparentPower,
            activePower)
        .flatMap(set -> set.parallelStream().map(ts -> (IndividualTimeSeries<Value>) ts))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TimeSeriesContainer)) return false;
    TimeSeriesContainer that = (TimeSeriesContainer) o;
    return weather.equals(that.weather)
        && energyPrice.equals(that.energyPrice)
        && heatAndApparentPower.equals(that.heatAndApparentPower)
        && heatAndActivePower.equals(that.heatAndActivePower)
        && heat.equals(that.heat)
        && apparentPower.equals(that.apparentPower)
        && activePower.equals(that.activePower);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        weather,
        energyPrice,
        heatAndApparentPower,
        heatAndActivePower,
        heat,
        apparentPower,
        activePower);
  }

  @Override
  public String toString() {
    return "TimeSeriesContainer{"
        + "#weather="
        + weather.size()
        + ", #energyPrice="
        + energyPrice.size()
        + ", #heatAndApparentPower="
        + heatAndApparentPower.size()
        + ", #heatAndActivePower="
        + heatAndActivePower.size()
        + ", #heat="
        + heat.size()
        + ", #apparentPower="
        + apparentPower.size()
        + ", #activePower="
        + activePower.size()
        + '}';
  }
}
