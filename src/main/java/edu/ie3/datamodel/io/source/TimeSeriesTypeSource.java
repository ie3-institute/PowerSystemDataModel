/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.models.input.InputEntity;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public interface TimeSeriesTypeSource extends DataSource {

  /**
   * Get a mapping from time series {@link UUID} to its meta information {@link
   * IndividualTimeSeriesMetaInformation}
   *
   * @return That mapping
   */
  Map<UUID, ? extends IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation();

  /** Class to represent one entry within the participant to time series mapping */
  class TypeEntry extends InputEntity {
    private final ColumnScheme columnScheme;

    public TypeEntry(UUID timeSeries, ColumnScheme columnScheme) {
      super(timeSeries);
      this.columnScheme = columnScheme;
    }

    public UUID getTimeSeries() {
      return getUuid();
    }

    public ColumnScheme getColumnScheme() {
      return columnScheme;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      TypeEntry typeEntry = (TypeEntry) o;
      return columnScheme == typeEntry.columnScheme;
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), columnScheme);
    }

    @Override
    public String toString() {
      return "TypeEntry{" + "uuid=" + getUuid() + ", columnScheme=" + columnScheme + '}';
    }
  }
}
