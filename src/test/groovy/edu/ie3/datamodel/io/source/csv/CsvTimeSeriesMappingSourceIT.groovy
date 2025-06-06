/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource
import spock.lang.Shared
import spock.lang.Specification

class CsvTimeSeriesMappingSourceIT extends Specification implements CsvTestDataMeta {
  @Shared
  TimeSeriesMappingSource source

  def setupSpec() {
    source = new CsvTimeSeriesMappingSource(";", timeSeriesFolderPath, new FileNamingStrategy())
  }

  def "The csv time series mapping source is able to provide a valid time series mapping from files"() {
    given:
    def expectedMapping = [
      (UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409")) : UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5"),
      (UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8")) : UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26"),
      (UUID.fromString("90a96daa-012b-4fea-82dc-24ba7a7ab81c")) : UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26"),
      (UUID.fromString("7bed7760-c220-4fe6-88b3-47b246f6ef3f")) : UUID.fromString("eeccbe3c-a47e-448e-8eca-1f369d3c24e6")
    ]

    when:
    def actualMapping = source.mapping

    then:
    actualMapping.size() == expectedMapping.size()

    expectedMapping.entrySet().stream().allMatch { entry ->
      actualMapping.containsKey(entry.key) && actualMapping.get(entry.key) == entry.value
    }
  }

  def "The csv time series mapping source returns empty optional on not covered model"() {
    given:
    def modelUuid = UUID.fromString("60b9a3da-e56c-40ff-ace7-8060cea84baf")

    when:
    def actual = source.getTimeSeriesUuid(modelUuid)

    then:
    !actual.present
  }

  def "The csv time series mapping source is able to return the correct time series uuid"() {
    given:
    def modelUuid = UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8")
    def expectedUuid = UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")

    when:
    def actual = source.getTimeSeriesUuid(modelUuid)

    then:
    actual.present
    actual.get() == expectedUuid
  }
}
