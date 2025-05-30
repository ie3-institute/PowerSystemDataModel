/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static tech.units.indriya.unit.Units.WATT;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.BdewLoadProfileTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import edu.ie3.datamodel.models.value.load.BdewLoadValues.BDEW1999;
import edu.ie3.datamodel.models.value.load.BdewLoadValues.BDEW2025;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public class BdewLoadProfileFactory
    extends LoadProfileFactory<BdewStandardLoadProfile, BdewLoadValues> {
  // 1999 profile scheme
  public static final String SUMMER_SATURDAY = "SuSa";
  public static final String SUMMER_SUNDAY = "SuSu";
  public static final String SUMMER_WEEKDAY = "SuWd";
  public static final String TRANSITION_SATURDAY = "TrSa";
  public static final String TRANSITION_SUNDAY = "TrSu";
  public static final String TRANSITION_WEEKDAY = "TrWd";
  public static final String WINTER_SATURDAY = "WiSa";
  public static final String WINTER_SUNDAY = "WiSu";
  public static final String WINTER_WEEKDAY = "WiWd";

  // 2025 profile scheme
  public static final String JANUARY_SATURDAY = "JanSa";
  public static final String JANUARY_SUNDAY = "JanSu";
  public static final String JANUARY_WEEKDAY = "JanWd";
  public static final String FEBRUARY_SATURDAY = "FebSa";
  public static final String FEBRUARY_SUNDAY = "FebSu";
  public static final String FEBRUARY_WEEKDAY = "FebWd";
  public static final String MARCH_SATURDAY = "MarSa";
  public static final String MARCH_SUNDAY = "MarSu";
  public static final String MARCH_WEEKDAY = "MarWd";
  public static final String APRIL_SATURDAY = "AprSa";
  public static final String APRIL_SUNDAY = "AprSu";
  public static final String APRIL_WEEKDAY = "AprWd";
  public static final String MAY_SATURDAY = "MaySa";
  public static final String MAY_SUNDAY = "MaySu";
  public static final String MAY_WEEKDAY = "MayWd";
  public static final String JUNE_SATURDAY = "JunSa";
  public static final String JUNE_SUNDAY = "JunSu";
  public static final String JUNE_WEEKDAY = "JunWd";
  public static final String JULY_SATURDAY = "JulSa";
  public static final String JULY_SUNDAY = "JulSu";
  public static final String JULY_WEEKDAY = "JulWd";
  public static final String AUGUST_SATURDAY = "AugSa";
  public static final String AUGUST_SUNDAY = "AugSu";
  public static final String AUGUST_WEEKDAY = "AugWd";
  public static final String SEPTEMBER_SATURDAY = "SepSa";
  public static final String SEPTEMBER_SUNDAY = "SepSu";
  public static final String SEPTEMBER_WEEKDAY = "SepWd";
  public static final String OCTOBER_SATURDAY = "OctSa";
  public static final String OCTOBER_SUNDAY = "OctSu";
  public static final String OCTOBER_WEEKDAY = "OctWd";
  public static final String NOVEMBER_SATURDAY = "NovSa";
  public static final String NOVEMBER_SUNDAY = "NovSu";
  public static final String NOVEMBER_WEEKDAY = "NovWd";
  public static final String DECEMBER_SATURDAY = "DecSa";
  public static final String DECEMBER_SUNDAY = "DecSu";
  public static final String DECEMBER_WEEKDAY = "DecWd";

  public BdewLoadProfileFactory() {
    this(BdewLoadValues.class);
  }

  public BdewLoadProfileFactory(Class<BdewLoadValues> valueClass) {
    super(valueClass);
  }

  @Override
  protected LoadProfileEntry<BdewLoadValues> buildModel(LoadProfileData<BdewLoadValues> data) {
    int quarterHour = data.getInt(QUARTER_HOUR);

    boolean is1999Scheme = data.containsKey(SUMMER_SATURDAY);

    BdewLoadValues values;

    if (is1999Scheme) {
      values =
          new BDEW1999(
              data.getDouble(SUMMER_SATURDAY),
              data.getDouble(SUMMER_SUNDAY),
              data.getDouble(SUMMER_WEEKDAY),
              data.getDouble(TRANSITION_SATURDAY),
              data.getDouble(TRANSITION_SUNDAY),
              data.getDouble(TRANSITION_WEEKDAY),
              data.getDouble(WINTER_SATURDAY),
              data.getDouble(WINTER_SUNDAY),
              data.getDouble(WINTER_WEEKDAY));
    } else {
      values =
          new BDEW2025(
              data.getDouble(JANUARY_SATURDAY),
              data.getDouble(JANUARY_SUNDAY),
              data.getDouble(JANUARY_WEEKDAY),
              data.getDouble(FEBRUARY_SATURDAY),
              data.getDouble(FEBRUARY_SUNDAY),
              data.getDouble(FEBRUARY_WEEKDAY),
              data.getDouble(MARCH_SATURDAY),
              data.getDouble(MARCH_SUNDAY),
              data.getDouble(MARCH_WEEKDAY),
              data.getDouble(APRIL_SATURDAY),
              data.getDouble(APRIL_SUNDAY),
              data.getDouble(APRIL_WEEKDAY),
              data.getDouble(MAY_SATURDAY),
              data.getDouble(MAY_SUNDAY),
              data.getDouble(MAY_WEEKDAY),
              data.getDouble(JUNE_SATURDAY),
              data.getDouble(JUNE_SUNDAY),
              data.getDouble(JUNE_WEEKDAY),
              data.getDouble(JULY_SATURDAY),
              data.getDouble(JULY_SUNDAY),
              data.getDouble(JULY_WEEKDAY),
              data.getDouble(AUGUST_SATURDAY),
              data.getDouble(AUGUST_SUNDAY),
              data.getDouble(AUGUST_WEEKDAY),
              data.getDouble(SEPTEMBER_SATURDAY),
              data.getDouble(SEPTEMBER_SUNDAY),
              data.getDouble(SEPTEMBER_WEEKDAY),
              data.getDouble(OCTOBER_SATURDAY),
              data.getDouble(OCTOBER_SUNDAY),
              data.getDouble(OCTOBER_WEEKDAY),
              data.getDouble(NOVEMBER_SATURDAY),
              data.getDouble(NOVEMBER_SUNDAY),
              data.getDouble(NOVEMBER_WEEKDAY),
              data.getDouble(DECEMBER_SATURDAY),
              data.getDouble(DECEMBER_SUNDAY),
              data.getDouble(DECEMBER_WEEKDAY));
    }

    return new LoadProfileEntry<>(values, quarterHour);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return List.of(
        newSet(
            QUARTER_HOUR,
            SUMMER_SATURDAY,
            SUMMER_SUNDAY,
            SUMMER_WEEKDAY,
            TRANSITION_SATURDAY,
            TRANSITION_SUNDAY,
            TRANSITION_WEEKDAY,
            WINTER_SATURDAY,
            WINTER_SUNDAY,
            WINTER_WEEKDAY),
        newSet(
            JANUARY_SATURDAY,
            JANUARY_SUNDAY,
            JANUARY_WEEKDAY,
            FEBRUARY_SATURDAY,
            FEBRUARY_SUNDAY,
            FEBRUARY_WEEKDAY,
            MARCH_SATURDAY,
            MARCH_SUNDAY,
            MARCH_WEEKDAY,
            APRIL_SATURDAY,
            APRIL_SUNDAY,
            APRIL_WEEKDAY,
            MAY_SATURDAY,
            MAY_SUNDAY,
            MAY_WEEKDAY,
            JUNE_SATURDAY,
            JUNE_SUNDAY,
            JUNE_WEEKDAY,
            JULY_SATURDAY,
            JULY_SUNDAY,
            JULY_WEEKDAY,
            AUGUST_SATURDAY,
            AUGUST_SUNDAY,
            AUGUST_WEEKDAY,
            SEPTEMBER_SATURDAY,
            SEPTEMBER_SUNDAY,
            SEPTEMBER_WEEKDAY,
            OCTOBER_SATURDAY,
            OCTOBER_SUNDAY,
            OCTOBER_SUNDAY,
            OCTOBER_WEEKDAY,
            NOVEMBER_SATURDAY,
            NOVEMBER_SUNDAY,
            NOVEMBER_WEEKDAY,
            DECEMBER_SATURDAY,
            DECEMBER_SUNDAY,
            DECEMBER_WEEKDAY,
            QUARTER_HOUR));
  }

  @Override
  public BdewLoadProfileTimeSeries build(
      LoadProfileMetaInformation metaInformation, Set<LoadProfileEntry<BdewLoadValues>> entries) {

    BdewStandardLoadProfile profile = parseProfile(metaInformation.getProfile());
    ComparableQuantity<Power> maxPower = calculateMaxPower(profile, entries);
    ComparableQuantity<Energy> profileEnergyScaling = getLoadProfileEnergyScaling(profile);

    return new BdewLoadProfileTimeSeries(
        metaInformation.getUuid(), profile, entries, maxPower, profileEnergyScaling);
  }

  @Override
  public BdewStandardLoadProfile parseProfile(String profile) {
    try {
      return BdewStandardLoadProfile.get(profile);
    } catch (ParsingException e) {
      throw new FactoryException("An error occurred while parsing the profile: " + profile, e);
    }
  }

  @Override
  public ComparableQuantity<Power> calculateMaxPower(
      BdewStandardLoadProfile loadProfile, Set<LoadProfileEntry<BdewLoadValues>> entries) {
    Function<BdewLoadValues, Stream<Double>> valueExtractor;

    if (loadProfile == BdewStandardLoadProfile.H0) {
      // maximum dynamization factor is on day 366 (leap year) or day 365 (regular year).
      // The difference between day 365 and day 366 is negligible, thus pick 366
      valueExtractor = v -> v.lastDayOfYearValues().map(p -> BdewLoadValues.dynamization(p, 366));
    } else {
      valueExtractor = BdewLoadValues::values;
    }

    double maxPower =
        entries.stream()
            .map(TimeSeriesEntry::getValue)
            .flatMap(valueExtractor)
            .max(Comparator.naturalOrder())
            .orElse(0d);

    return Quantities.getQuantity(maxPower, WATT);
  }

  /** Returns the load profile energy scaling. The default value is 1000 kWh */
  public ComparableQuantity<Energy> getLoadProfileEnergyScaling(
      BdewStandardLoadProfile loadProfile) {

    // the updated profiled are scaled to 1 million kWh -> 1000 MWh
    // old profiles are scaled to 1000 kWh
    return switch (loadProfile) {
      case H25, G25, L25, P25, S25 -> Quantities.getQuantity(1000d, PowerSystemUnits.MEGAWATTHOUR);
      default -> Quantities.getQuantity(1000d, PowerSystemUnits.KILOWATTHOUR);
    };
  }
}
