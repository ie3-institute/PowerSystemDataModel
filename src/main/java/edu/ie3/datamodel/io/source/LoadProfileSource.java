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
import edu.ie3.datamodel.models.timeseries.repetitive.BDEWLoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.BDEWLoadProfileInput;
import edu.ie3.datamodel.models.value.PValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
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
      BufferedReader reader) throws IOException, ParsingException {
    String[] headline = reader.readLine().split(",");

    Map<Integer, LoadProfileKey> positionToKey = new HashMap<>();
    for (int i = 0; i < headline.length - 1; i++) {
      String header = headline[i];
      positionToKey.put(i, LoadProfileKey.parseBDEWProfile(header));
    }

    Map<LoadProfileKey, List<BDEWLoadProfileEntry>> values =
        reader
            .lines()
            .map(
                row -> {
                  String[] arr = row.split(",");

                  int quarterHour = Integer.parseInt(arr[arr.length - 1]);

                  return IntStream.range(0, arr.length - 1)
                      .boxed()
                      .map(
                          i -> {
                            LoadProfileKey key = positionToKey.get(i);

                            return Pair.of(
                                key,
                                new BDEWLoadProfileEntry(
                                    new PValue(
                                        Quantities.getQuantity(
                                            Double.parseDouble(arr[i]), KILOWATT)),
                                    key.season(),
                                    key.dayOfWeek(),
                                    quarterHour));
                          })
                      .toList();
                })
            .flatMap(Collection::stream)
            .collect(Collectors.groupingBy(Pair::getLeft))
            .entrySet()
            .stream()
            .map(e -> Map.entry(e.getKey(), e.getValue().stream().map(Pair::getValue).toList()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    return Arrays.stream(BdewStandardLoadProfile.values())
        .map(
            profile ->
                Map.entry(
                    profile,
                    new BDEWLoadProfileInput(
                        profile,
                        values.keySet().stream()
                            .filter(key -> key.profile() == profile)
                            .map(values::get)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toSet()))))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
