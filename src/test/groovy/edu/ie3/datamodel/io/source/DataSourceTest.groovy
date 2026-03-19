/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import spock.lang.Specification

class DataSourceTest extends Specification {

  def "A DataSource can return unused fields correctly"() {
    when:
    def unused = DataSource.getUnusedFields(actualFields as Set<String>, validFieldSets)

    then:
    unused == expected as Set<String>

    where:
    actualFields | validFieldSets | expected
    [
      "uuid",
      "id",
      "time",
      "value_1"
    ] | [
      ["uuid", "value_1"] as Set<String>,
      ["id", "time", "value_1"] as Set<String>
    ] | ["uuid"]
    [
      "uuid",
      "id",
      "time",
      "value_1",
      "value_2",
      "value_3"
    ] | [
      ["uuid", "value_1", "value_3"] as Set<String>,
      ["id", "time", "value_1"] as Set<String>
    ] | ["id", "time", "value_2"]
    [
      "uuid",
      "id",
      "time",
      "value_1"
    ] | [
      [
        "uuid",
        "id",
        "time",
        "value_1",
        "value_2"
      ] as Set<String>,
      [
        "uuid",
        "id",
        "time",
        "value_1",
        "value_3"
      ] as Set<String>
    ] | []
  }
}
