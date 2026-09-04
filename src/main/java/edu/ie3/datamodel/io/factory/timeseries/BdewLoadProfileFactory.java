/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.models.profile.BdewStandardLoadProfile.*;
import static tech.units.indriya.unit.Units.WATT;

import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.PowerProfileKey;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.BdewLoadProfileTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Function;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public class BdewLoadProfileFactory extends LoadProfileFactory<BdewLoadValues> {
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
      values =
          new BdewLoadValues.Bdew1999(
              data.getDouble(SU_SA),
              data.getDouble(SU_SU),
              data.getDouble(SU_WD),
              data.getDouble(TR_SA),
              data.getDouble(TR_SU),
              data.getDouble(TR_WD),
              data.getDouble(WI_SA),
              data.getDouble(WI_SU),
              data.getDouble(WI_WD));

    } else {
      values =
          new BdewLoadValues.Bdew2025(
              data.getDouble(JAN_SA),
              data.getDouble(JAN_SU),
              data.getDouble(JAN_WD),
              data.getDouble(FEB_SA),
              data.getDouble(FEB_SU),
              data.getDouble(FEB_WD),
              data.getDouble(MAR_SA),
              data.getDouble(MAR_SU),
              data.getDouble(MAR_WD),
              data.getDouble(APR_SA),
              data.getDouble(APR_SU),
              data.getDouble(APR_WD),
              data.getDouble(MAY_SA),
              data.getDouble(MAY_SU),
              data.getDouble(MAY_WD),
              data.getDouble(JUN_SA),
              data.getDouble(JUN_SU),
              data.getDouble(JUN_WD),
              data.getDouble(JUL_SA),
              data.getDouble(JUL_SU),
              data.getDouble(JUL_WD),
              data.getDouble(AUG_SA),
              data.getDouble(AUG_SU),
              data.getDouble(AUG_WD),
              data.getDouble(SEP_SA),
              data.getDouble(SEP_SU),
              data.getDouble(SEP_WD),
              data.getDouble(OCT_SA),
              data.getDouble(OCT_SU),
              data.getDouble(OCT_WD),
              data.getDouble(NOV_SA),
              data.getDouble(NOV_SU),
              data.getDouble(NOV_WD),
              data.getDouble(DEC_SA),
              data.getDouble(DEC_SU),
              data.getDouble(DEC_WD));
    }

    return new LoadProfileEntry<>(values, quarterHour);
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
      valueExtractor = v -> BdewStandardLoadProfile.dynamization(v.getMaxValue(true), 366);
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
