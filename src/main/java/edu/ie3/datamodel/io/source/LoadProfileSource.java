/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.csv.CsvLoadProfileMetaInformation;
import edu.ie3.datamodel.io.factory.timeseries.BDEWLoadProfileFactory;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileData;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileFactory;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.io.source.csv.CsvLoadProfileSource;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.BDEWLoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.BDEWLoadProfileTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.Try;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LoadProfileSource<P extends LoadProfile, E extends LoadProfileEntry>
    extends EntitySource {

  protected final Class<E> entryClass;
  protected final LoadProfileFactory<P, E> entryFactory;

  protected LoadProfileSource(Class<E> entryClass, LoadProfileFactory<P, E> entryFactory) {
    this.entryClass = entryClass;
    this.entryFactory = entryFactory;
  }

  /**
   * Build a list of type {@code E}, whereas the underlying {@link Value} does not need any
   * additional information.
   *
   * @param fieldToValues Mapping from field id to values
   * @return {@link Try} of simple time based value
   */
  protected Try<List<E>, FactoryException> createEntries(Map<String, String> fieldToValues) {
    LoadProfileData<E> factoryData = new LoadProfileData<>(fieldToValues, entryClass);
    return entryFactory.get(factoryData);
  }

  public abstract LoadProfileTimeSeries<E> getTimeSeries();

  /**
   * Method to return all time keys after a given timestamp.
   *
   * @param time given time
   * @return a list of time keys
   */
  public abstract List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time);

  /**
   * Method to read in the build-in {@link BdewStandardLoadProfile}s.
   *
   * @return a map: load profile to time series
   * @throws SourceException if an exception occurred
   */
  public static Map<BdewStandardLoadProfile, BDEWLoadProfileTimeSeries>
      readBDEWStandardLoadProfiles() throws SourceException {
    Path bdewLoadProfilePath = Path.of("src", "main", "resources", "load");
    CsvDataSource dataSource =
        new CsvDataSource(",", bdewLoadProfilePath, new FileNamingStrategy());

    BdewStandardLoadProfile[] implemented = BdewStandardLoadProfile.values();
    Map<BdewStandardLoadProfile, BDEWLoadProfileTimeSeries> loadProfileInputs = new HashMap<>();

    try {
      for (CsvLoadProfileMetaInformation metaInformation :
          dataSource.getCsvLoadProfileMetaInformation(implemented).values()) {
        BdewStandardLoadProfile profile = BdewStandardLoadProfile.get(metaInformation.getProfile());

        Class<BDEWLoadProfileEntry> entryClass = BDEWLoadProfileEntry.class;

        CsvLoadProfileSource<BdewStandardLoadProfile, BDEWLoadProfileEntry> source =
            new CsvLoadProfileSource<>(
                dataSource,
                metaInformation,
                entryClass,
                profile,
                new BDEWLoadProfileFactory(entryClass));

        loadProfileInputs.put(profile, (BDEWLoadProfileTimeSeries) source.getTimeSeries());
      }

      return loadProfileInputs;
    } catch (ParsingException e) {
      throw new SourceException("Unable to read standard load profiles due to: ", e);
    }
  }
}
