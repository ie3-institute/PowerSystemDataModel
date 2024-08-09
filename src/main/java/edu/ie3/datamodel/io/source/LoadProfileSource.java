/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.LoadProfileKey;
import edu.ie3.datamodel.models.profile.LoadProfileKey.BDEWLoadProfileKey;
import edu.ie3.datamodel.models.timeseries.repetitive.BDEWLoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.BDEWLoadProfileInput;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.utils.Try;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import tech.units.indriya.quantity.Quantities;

public class LoadProfileSource extends EntitySource {

  private final Path bdewLoadProfilePath = Path.of("src", "main", "resources", "load");

  @Override
  public void validate() throws ValidationException {
    /* Nothing to do here */
  }

  public Map<BdewStandardLoadProfile, BDEWLoadProfileInput> getBDEWLoadProfiles()
      throws SourceException {
    CsvFileConnector connector = new CsvFileConnector(bdewLoadProfilePath);

    try (BufferedReader reader = connector.initReader(Path.of("standard_load_profiles"))) {
      return parseBDEWStandardProfiles(reader);
    } catch (IOException | ParsingException e) {
      throw new SourceException("The bdew standard load profiles could not be loaded!", e);
    }
  }

  protected Map<BdewStandardLoadProfile, BDEWLoadProfileInput> parseBDEWStandardProfiles(
      BufferedReader reader) throws IOException, ParsingException, SourceException {

    Map<BDEWLoadProfileKey, List<LoadProfileData<BDEWLoadProfileKey>>> dataMap =
        readLoadProfile(
            reader,
            ",",
            str -> Try.of(() -> LoadProfileKey.parseBDEWProfile(str), ParsingException.class));

    return Arrays.stream(BdewStandardLoadProfile.values())
        .map(
            profile -> {
              Set<BDEWLoadProfileEntry> entries =
                  dataMap.keySet().stream()
                      .filter(e -> e.profile() == profile)
                      .map(dataMap::get)
                      .flatMap(Collection::stream)
                      .map(
                          d ->
                              new BDEWLoadProfileEntry(
                                  d.value,
                                  d.profileKey.season(),
                                  d.profileKey.dayOfWeek(),
                                  d.quarterHour))
                      .collect(Collectors.toSet());

              return Map.entry(profile, new BDEWLoadProfileInput(profile, entries));
            })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private <T extends LoadProfileKey> Map<T, List<LoadProfileData<T>>> readLoadProfile(
      BufferedReader reader, String csvSep, Function<String, Try<T, ParsingException>> keyExtractor)
      throws IOException, SourceException, ParsingException {
    List<String> headline = Arrays.asList(reader.readLine().split(csvSep));

    int quarterHourColumn = headline.indexOf("quarterHour");

    if (quarterHourColumn < 0) {
      throw new SourceException("There is no column for quarter hour values.");
    }

    Map<Integer, T> profileKeys = new HashMap<>();

    for (int i = 0; i < headline.size(); i++) {
      if (i != quarterHourColumn) {
        profileKeys.put(i, keyExtractor.apply(headline.get(i)).getOrThrow());
      }
    }

    return reader
        .lines()
        .map(
            csvRow -> {
              List<String> elements = Arrays.asList(csvRow.split(csvSep));
              int quarterHour = Integer.parseInt(elements.get(quarterHourColumn));

              List<LoadProfileData<T>> loadProfileToValue = new ArrayList<>();

              for (int i = 0; i < elements.size(); i++) {
                if (i != quarterHourColumn) {
                  loadProfileToValue.add(
                      new LoadProfileData<>(
                          profileKeys.get(i),
                          quarterHour,
                          new PValue(
                              Quantities.getQuantity(
                                  Double.parseDouble(elements.get(i)), KILOWATT))));
                }
              }
              return loadProfileToValue;
            })
        .flatMap(Collection::stream)
        .collect(Collectors.groupingBy(LoadProfileData::profileKey));
  }

  public record LoadProfileData<T extends LoadProfileKey>(
      T profileKey, int quarterHour, PValue value) {}
}
