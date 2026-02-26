/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.models.profile.BdewStandardLoadProfile.*;
import static tech.units.indriya.unit.Units.WATT;

import edu.ie3.datamodel.models.profile.PowerProfileKey;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.BdewLoadProfileTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import edu.ie3.datamodel.models.value.load.BdewLoadValues.BdewKey;
import edu.ie3.datamodel.models.value.load.BdewLoadValues.BdewScheme;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public class BdewLoadProfileFactory extends LoadProfileFactory<BdewLoadValues> {
  // 1999 profile scheme
  public static final BdewLoadValues.BdewMap<String> BDEW1999_FIELDS =
      BdewKey.toMap(BdewScheme.BDEW1999);

  // 2025 profile scheme
  public static final BdewLoadValues.BdewMap<String> BDEW2025_FIELDS =
      BdewKey.toMap(BdewScheme.BDEW2025);

  public BdewLoadProfileFactory() {
    super(BdewLoadValues.class);
  }

  @Override
  protected LoadProfileEntry<BdewLoadValues> buildModel(LoadProfileData<BdewLoadValues> data) {
    int quarterHour = data.getInt(QUARTER_HOUR);

    boolean is1999Scheme =
        data.containsKey("SuSa") || data.containsKey("su_sa") || data.containsKey("suSa");

    BdewLoadValues values;

    if (is1999Scheme) {
      values = new BdewLoadValues(BdewScheme.BDEW1999, BDEW1999_FIELDS.map(data::getDouble));

    } else {
      values = new BdewLoadValues(BdewScheme.BDEW2025, BDEW2025_FIELDS.map(data::getDouble));
    }

    return new LoadProfileEntry<>(values, quarterHour);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return List.of(
        expandSet(new HashSet<>(BDEW1999_FIELDS.values()), QUARTER_HOUR),
        expandSet(new HashSet<>(BDEW2025_FIELDS.values()), QUARTER_HOUR));
  }

  @Override
  public BdewLoadProfileTimeSeries build(
      PowerProfileKey powerProfileKey, Set<LoadProfileEntry<BdewLoadValues>> entries) {
    ComparableQuantity<Power> maxPower = calculateMaxPower(powerProfileKey, entries);
    ComparableQuantity<Energy> profileEnergyScaling = getLoadProfileEnergyScaling(powerProfileKey);

    return new BdewLoadProfileTimeSeries(powerProfileKey, entries, maxPower, profileEnergyScaling);
  }

  @Override
  public ComparableQuantity<Power> calculateMaxPower(
      PowerProfileKey powerProfileKey, Set<LoadProfileEntry<BdewLoadValues>> entries) {
    Function<BdewLoadValues, Double> valueExtractor;

    if (powerProfileKey.equalsAny(H0, H25, P25, S25)) {
      // maximum dynamization factor is on day 366 (leap year) or day 365 (regular year).
      // The difference between day 365 and day 366 is negligible, thus pick 366
      valueExtractor = v -> BdewLoadValues.dynamization(v.getMaxValue(true), 366);
    } else {
      valueExtractor = v -> v.getMaxValue(false);
    }

    double maxPower =
        entries.stream()
            .map(TimeSeriesEntry::getValue)
            .map(valueExtractor)
            .max(Comparator.naturalOrder())
            .orElse(0d);

    return Quantities.getQuantity(maxPower, WATT);
  }

  /** Returns the load profile energy scaling. The default value is 1000 kWh */
  @Override
  public ComparableQuantity<Energy> getLoadProfileEnergyScaling(PowerProfileKey powerProfileKey) {
    // the updated profiled are scaled to 1 million kWh -> 1000 MWh
    // old profiles are scaled to 1000 kWh
    if (powerProfileKey.equalsAny(H25, G25, L25, P25, S25)) {
      return Quantities.getQuantity(1000d, PowerSystemUnits.MEGAWATTHOUR);
    } else {
      return Quantities.getQuantity(1000d, PowerSystemUnits.KILOWATTHOUR);
    }
  }
}
