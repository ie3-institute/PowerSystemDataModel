/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.BdewLoadProfileTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import java.util.List;
import java.util.Set;

public class BdewLoadProfileFactory
    extends LoadProfileFactory<BdewStandardLoadProfile, BdewLoadValues> {
  public static final String SUMMER_SATURDAY = "SuSa";
  public static final String SUMMER_SUNDAY = "SuSu";
  public static final String SUMMER_WEEKDAY = "SuWd";
  public static final String TRANSITION_SATURDAY = "TrSa";
  public static final String TRANSITION_SUNDAY = "TrSu";
  public static final String TRANSITION_WEEKDAY = "TrWd";
  public static final String WINTER_SATURDAY = "WiSa";
  public static final String WINTER_SUNDAY = "WiSu";
  public static final String WINTER_WEEKDAY = "WiWd";

  public BdewLoadProfileFactory() {
    this(BdewLoadValues.class);
  }

  public BdewLoadProfileFactory(Class<BdewLoadValues> valueClass) {
    super(valueClass);
  }

  @Override
  protected LoadProfileEntry<BdewLoadValues> buildModel(LoadProfileData<BdewLoadValues> data) {
    int quarterHour = data.getInt(QUARTER_HOUR);

    return new LoadProfileEntry<>(
        new BdewLoadValues(
            data.getDouble(SUMMER_SATURDAY),
            data.getDouble(SUMMER_SUNDAY),
            data.getDouble(SUMMER_WEEKDAY),
            data.getDouble(TRANSITION_SATURDAY),
            data.getDouble(TRANSITION_SUNDAY),
            data.getDouble(TRANSITION_WEEKDAY),
            data.getDouble(WINTER_SATURDAY),
            data.getDouble(WINTER_SUNDAY),
            data.getDouble(WINTER_WEEKDAY)),
        quarterHour);
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
            WINTER_WEEKDAY));
  }

  @Override
  public LoadProfileTimeSeries<BdewLoadValues> build(
      LoadProfileTimeSeriesMetaInformation metaInformation,
      Set<LoadProfileEntry<BdewLoadValues>> entries) {
    return new BdewLoadProfileTimeSeries(
        metaInformation.getUuid(), parseProfile(metaInformation.getProfile()), entries);
  }

  @Override
  public BdewStandardLoadProfile parseProfile(String profile) {
    try {
      return BdewStandardLoadProfile.get(profile);
    } catch (ParsingException e) {
      throw new FactoryException("An error occurred while parsing the profile: " + profile, e);
    }
  }
}
