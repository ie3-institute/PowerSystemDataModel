/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.deserialize;

import edu.ie3.datamodel.exceptions.DeserializationException;
import edu.ie3.datamodel.io.CsvFileDefinition;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.models.input.LoadProfileInput;
import edu.ie3.datamodel.models.value.PValue;
import java.util.UUID;

public class LoadProfileInputDeserializer extends TimeSeriesDeserializer<LoadProfileInput, PValue> {
  private final String[] headLineElements;

  public LoadProfileInputDeserializer(Class<? extends PValue> valueClass, String baseFolderPath) {
    super(valueClass, baseFolderPath);
    this.headLineElements = determineHeadLineElements();
  }

  @Override
  protected CsvFileDefinition determineFileDefinition(UUID uuid) throws DeserializationException {
    FileNamingStrategy fileNamingStrategy = new FileNamingStrategy();
    String fileName =
        fileNamingStrategy
            .getLoadProfileInputFileName(uuid)
            .orElseThrow(
                () ->
                    new DeserializationException(
                        "Cannot determine file name for load profile time series with uuid="
                            + uuid));
    return new CsvFileDefinition(fileName, headLineElements);
  }

  @Override
  protected String[] determineHeadLineElements() {
    return new String[0];
  }

  @Override
  protected void deserialize(LoadProfileInput timeSeries) throws DeserializationException {
    throw new DeserializationException(
        "The deserialisation of LoadProleInput is not implemented, yet.", timeSeries);

    /*
     * Steps to implement
     *   1) Determine the "unique" table entries as a combination of "credentials"
     *      and edu.ie3.datamodel.models.value.Value
     *   2) Build field name to value mapping for credentials and values independently
     *   3) Combine the mapping
     *   4) Write the result
     */
  }
}
