/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesMappingFactory;
import edu.ie3.datamodel.io.source.TimeSeriesSource;
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping;
import java.util.Set;
import java.util.stream.Collectors;

/** Source that is capable of providing information around time series from csv files. */
public class CsvTimeSeriesSource extends CsvDataSource implements TimeSeriesSource {
  private final TimeSeriesMappingFactory mappingFactory = new TimeSeriesMappingFactory();

  public CsvTimeSeriesSource(
      String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy) {
    super(csvSep, folderPath, fileNamingStrategy);
  }

  @Override
  public Set<TimeSeriesMapping.Entry> getMapping() {
    return filterEmptyOptionals(
            buildStreamWithFieldsToAttributesMap(TimeSeriesMapping.Entry.class, connector)
                .map(
                    fieldToValues -> {
                      SimpleEntityData entityData =
                          new SimpleEntityData(fieldToValues, TimeSeriesMapping.Entry.class);
                      return mappingFactory.getEntity(entityData);
                    }))
        .collect(Collectors.toSet());
  }
}
