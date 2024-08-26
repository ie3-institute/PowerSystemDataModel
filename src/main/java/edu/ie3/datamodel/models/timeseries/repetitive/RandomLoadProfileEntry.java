/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT;

import de.lmu.ifi.dbs.elki.math.statistics.distribution.GeneralizedExtremeValueDistribution;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import tech.units.indriya.quantity.Quantities;

/** Unique entry for a {@link RandomLoadProfileTimeSeries}. */
public class RandomLoadProfileEntry extends LoadProfileEntry {
  // distribution for random values
  private final GeneralizedExtremeValueDistribution gev;

  public RandomLoadProfileEntry(
      GeneralizedExtremeValueDistribution gev, DayOfWeek dayOfWeek, int quarterHourOfDay) {
    super(new PValue(null), dayOfWeek, quarterHourOfDay);
    this.gev = gev;
  }

  @Override
  public PValue getValue() {
    double randomValue = gev.nextRandom();

    while (randomValue < 0) {
      randomValue = gev.nextRandom();
    }
    return new PValue(Quantities.getQuantity(randomValue, KILOWATT));
  }
}
