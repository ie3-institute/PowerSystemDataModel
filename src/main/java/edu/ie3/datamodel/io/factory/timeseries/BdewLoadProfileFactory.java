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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

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
  public BdewLoadProfileTimeSeries build(
      LoadProfileMetaInformation metaInformation, Set<LoadProfileEntry<BdewLoadValues>> entries) {

    BdewStandardLoadProfile profile = parseProfile(metaInformation.getProfile());
    Optional<ComparableQuantity<Power>> maxPower = calculateMaxPower(profile, entries);

    return new BdewLoadProfileTimeSeries(metaInformation.getUuid(), profile, entries, maxPower);
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
  public Optional<ComparableQuantity<Power>> calculateMaxPower(
      BdewStandardLoadProfile loadProfile, Set<LoadProfileEntry<BdewLoadValues>> entries) {
    Function<BdewLoadValues, Stream<Double>> valueExtractor;

    if (loadProfile == BdewStandardLoadProfile.H0) {
      // maximum dynamization factor is on day 366 (leap year) or day 365 (regular year).
      // The difference between day 365 and day 366 is negligible, thus pick 366
      valueExtractor =
          v ->
              Stream.of(v.getWiSa(), v.getWiSu(), v.getWiWd())
                  .map(p -> BdewLoadValues.dynamization(p, 366));
    } else {
      valueExtractor = v -> v.values().stream();
    }
    return entries.stream()
        .map(TimeSeriesEntry::getValue)
        .flatMap(valueExtractor)
        .max(Comparator.naturalOrder())
        .map(p -> Quantities.getQuantity(p, WATT));
  }

  @Override
  public double getLoadProfileEnergyScaling() {
    return 1000d;
  }
}
