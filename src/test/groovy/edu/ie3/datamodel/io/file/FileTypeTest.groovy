/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.file

import edu.ie3.datamodel.exceptions.ParsingException
import spock.lang.Specification

class FileTypeTest extends Specification {

  def "getFileType resolves CSV and JSON endings"() {
    expect:
    FileType.getFileType(fileName) == expected

    where:
    fileName      || expected
    "data.csv"    || FileType.CSV
    "model.json"  || FileType.JSON
  }

  def "getFileType throws ParsingException on unknown ending"() {
    when:
    FileType.getFileType("unknown.txt")

    then:
    thrown(ParsingException)
  }
}
