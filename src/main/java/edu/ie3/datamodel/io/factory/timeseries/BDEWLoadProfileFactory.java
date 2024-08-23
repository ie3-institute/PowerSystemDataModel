/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT;
import static java.time.DayOfWeek.*;

import edu.ie3.datamodel.models.Season;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.BDEWLoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.BDEWLoadProfileTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.PValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BDEWLoadProfileFactory
    extends LoadProfileFactory<BdewStandardLoadProfile, BDEWLoadProfileEntry> {
  public static final String SUMMER_WEEKDAY = "SuWd";
  public static final String SUMMER_SATURDAY = "SuSa";
  public static final String SUMMER_SUNDAY = "SuSu";
  public static final String WINTER_WEEKDAY = "WiWd";
  public static final String WINTER_SATURDAY = "WiSa";
  public static final String WINTER_SUNDAY = "WiSu";
  public static final String TRANSITION_WEEKDAY = "TrWd";
  public static final String TRANSITION_SATURDAY = "TrSa";
  public static final String TRANSITION_SUNDAY = "TrSu";

  public BDEWLoadProfileFactory(Class<BDEWLoadProfileEntry> valueClass) {
    super(valueClass);
  }

  @Override
  protected List<BDEWLoadProfileEntry> buildModel(LoadProfileData<BDEWLoadProfileEntry> data) {
    List<BDEWLoadProfileEntry> entries = new ArrayList<>();
    int quarterHour = data.getInt(QUARTER_HOUR);

    /* summer */
    entries.add(
        new BDEWLoadProfileEntry(
            new PValue(data.getQuantity(SUMMER_WEEKDAY, KILOWATT)),
            Season.SUMMER,
            MONDAY,
            quarterHour));
    entries.add(
        new BDEWLoadProfileEntry(
            new PValue(data.getQuantity(SUMMER_SATURDAY, KILOWATT)),
            Season.SUMMER,
            SATURDAY,
            quarterHour));
    entries.add(
        new BDEWLoadProfileEntry(
            new PValue(data.getQuantity(SUMMER_SUNDAY, KILOWATT)),
            Season.SUMMER,
            SUNDAY,
            quarterHour));

    /* winter */
    entries.add(
        new BDEWLoadProfileEntry(
            new PValue(data.getQuantity(WINTER_WEEKDAY, KILOWATT)),
            Season.WINTER,
            MONDAY,
            quarterHour));
    entries.add(
        new BDEWLoadProfileEntry(
            new PValue(data.getQuantity(WINTER_SATURDAY, KILOWATT)),
            Season.WINTER,
            SATURDAY,
            quarterHour));
    entries.add(
        new BDEWLoadProfileEntry(
            new PValue(data.getQuantity(WINTER_SUNDAY, KILOWATT)),
            Season.WINTER,
            SUNDAY,
            quarterHour));

    /* transition */
    entries.add(
        new BDEWLoadProfileEntry(
            new PValue(data.getQuantity(TRANSITION_WEEKDAY, KILOWATT)),
            Season.TRANSITION,
            MONDAY,
            quarterHour));
    entries.add(
        new BDEWLoadProfileEntry(
            new PValue(data.getQuantity(TRANSITION_SATURDAY, KILOWATT)),
            Season.TRANSITION,
            SATURDAY,
            quarterHour));
    entries.add(
        new BDEWLoadProfileEntry(
            new PValue(data.getQuantity(TRANSITION_SUNDAY, KILOWATT)),
            Season.TRANSITION,
            SUNDAY,
            quarterHour));

    return entries;
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return List.of(
        newSet(
            SUMMER_WEEKDAY,
            SUMMER_SATURDAY,
            SUMMER_SUNDAY,
            WINTER_WEEKDAY,
            WINTER_SATURDAY,
            WINTER_SUNDAY,
            TRANSITION_WEEKDAY,
            TRANSITION_SATURDAY,
            TRANSITION_SUNDAY));
  }

  @Override
  public LoadProfileTimeSeries<BDEWLoadProfileEntry> build(
      UUID uuid, BdewStandardLoadProfile loadProfile, Set<BDEWLoadProfileEntry> entries) {
    return new BDEWLoadProfileTimeSeries(uuid, loadProfile, entries);
  }
}
