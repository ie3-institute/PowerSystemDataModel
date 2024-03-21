/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.models.Entity

import java.util.stream.Stream

class DummyDataSource implements DataSource {

  private final Map<String, String> data

  private DummyDataSource(Map<String, String> data) {
    this.data = data
  }

  static DummyDataSource of(Map<String, String> data) {
    return new DummyDataSource(data)
  }

  @Override
  Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass) throws SourceException {
    return Optional.empty()
  }

  @Override
  Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass) throws SourceException {
    return Stream.of(data)
  }
}
