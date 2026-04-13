/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static tech.units.indriya.unit.Units.WATT;

import edu.ie3.datamodel.models.profile.PowerProfileKey;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.Set;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public class RandomLoadProfileFactory extends LoadProfileFactory<RandomLoadValues> {

  public RandomLoadProfileFactory() {
    super(RandomLoadValues.class);
  }

  @Override
  protected LoadProfileEntry<RandomLoadValues> buildModel(LoadProfileData<RandomLoadValues> data) {
    int quarterHour = data.getInt(QUARTER_HOUR);

    return new LoadProfileEntry<>(
        new RandomLoadValues(
            data.getDouble(K_SATURDAY),
            data.getDouble(K_SUNDAY),
            data.getDouble(K_WEEKDAY),
            data.getDouble(MY_SATURDAY),
            data.getDouble(MY_SUNDAY),
            data.getDouble(MY_WEEKDAY),
            data.getDouble(SIGMA_SATURDAY),
            data.getDouble(SIGMA_SUNDAY),
            data.getDouble(SIGMA_WEEKDAY)),
        quarterHour);
  }

  @Override
  public RandomLoadProfileTimeSeries build(
      PowerProfileKey powerProfileKey, Set<LoadProfileEntry<RandomLoadValues>> entries) {
    ComparableQuantity<Power> maxPower = calculateMaxPower(powerProfileKey, entries);
    ComparableQuantity<Energy> profileEnergyScaling = getLoadProfileEnergyScaling(powerProfileKey);

    return new RandomLoadProfileTimeSeries(
        powerProfileKey, entries, maxPower, profileEnergyScaling);
  }

  /**
   * This is the 95 % quantile resulting from 10,000 evaluations of the year 2019. It is only
   * needed, when the load is meant to be scaled to rated active power.
   *
   * @return Reference active power to use for later model calculations
   */
  @Override
  public ComparableQuantity<Power> calculateMaxPower(
      PowerProfileKey powerProfileKey, Set<LoadProfileEntry<RandomLoadValues>> loadProfileEntries) {
    return Quantities.getQuantity(159d, WATT);
  }

  /**
   * Returns the profile energy scaling factor, the random profile is scaled to.
   *
   * <p>It is said in 'Kays - Agent-based simulation environment for improving the planning of
   * distribution grids', that the Generalized Extreme Value distribution's parameters are sampled
   * from input data, that is normalized to 1,000 kWh annual energy consumption. However, due to
   * inaccuracies in random data reproduction, the sampled values will lead to an average annual
   * energy consumption of approx. this value. It has been found by 1,000 evaluations of the year
   * 2019.
   */
  @Override
  public ComparableQuantity<Energy> getLoadProfileEnergyScaling(PowerProfileKey powerProfileKey) {
    return Quantities.getQuantity(716.5416966513656, PowerSystemUnits.KILOWATTHOUR);
  }
}
