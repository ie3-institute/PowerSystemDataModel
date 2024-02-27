/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.models.input.system.LoadInput
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path

class CsvDataSourceTest extends Specification implements CsvTestDataMeta {

  // Using a groovy bug to gain access to private methods in superclass:
  // by default, we cannot access private methods with parameters from abstract parent classes, introducing a
  // class that extends the abstract parent class and unveils the private methods by calling the parents private
  // methods in a public or protected method makes them available for testing
  private final class DummyCsvSource extends CsvDataSource {

    DummyCsvSource(String csvSep, Path folderPath, FileNamingStrategy fileNamingStrategy) {
      super(csvSep, folderPath, fileNamingStrategy)
    }

    Map<String, String> buildFieldsToAttributes(
        final String csvRow, final String[] headline) {
      return super.buildFieldsToAttributes(csvRow, headline)
    }

    String[] parseCsvRow(
        String csvRow, String csvSep) {
      return super.parseCsvRow(csvRow, csvSep)
    }

    String[] oldFieldVals(
        String csvSep, String csvRow) {
      return super.oldFieldVals(csvSep, csvRow)
    }
  }

  @Shared
  String csvSep = ","
  @Shared
  Path testBaseFolderPath = Path.of("testBaseFolderPath") // does not have to exist for this test
  @Shared
  FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()

  @Shared
  DummyCsvSource dummyCsvSource = new DummyCsvSource(csvSep, testBaseFolderPath, fileNamingStrategy)

  def "A DataSource should contain a valid connector after initialization"() {
    expect:
    dummyCsvSource.connector != null
    dummyCsvSource.connector.baseDirectory == testBaseFolderPath
    dummyCsvSource.connector.fileNamingStrategy == fileNamingStrategy
    dummyCsvSource.connector.entityWriters.isEmpty()
  }

  def "A CsvDataSource should return column names from a valid CSV file as expected"() {
    given:
    DummyCsvSource source = new DummyCsvSource(csvSep, participantsFolderPath, fileNamingStrategy)

    expect:
    source.getSourceFields(LoadInput).get() == [
      "operates_from",
      "node",
      "s_rated",
      "cos_phi_rated",
      "load_profile",
      "id",
      "operates_until",
      "uuid",
      "q_characteristics",
      "e_cons_annual",
      "operator",
      "dsm",
      "em"
    ] as Set
  }

  def "A CsvDataSource should return an empty result when retrieving column names for a non-existing CSV file"() {
    given:
    def path = Path.of("this/path/does-not-exist")

    expect:
    dummyCsvSource.getSourceFields(path).isEmpty()
  }

  def "A CsvDataSource should build a valid fields to attributes map with valid data as expected"() {
    given:
    def validHeadline = [
      "uuid",
      "active_power_gradient",
      "capex",
      "cosphi_rated",
      "eta_conv",
      "id",
      "opex",
      "s_rated",
      "olmcharacteristic",
      "cosPhiFixed"
    ] as String[]
    def validCsvRow = "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8,25.0,100.0,0.95,98.0,test_bmTypeInput,50.0,25.0,\"olm:{(0.0,1.0)}\",\"cosPhiFixed:{(0.0,1.0)}\""

    expect:
    dummyCsvSource.buildFieldsToAttributes(validCsvRow, validHeadline) == [
      activePowerGradient: "25.0",
      capex              : "100.0",
      cosphiRated        : "0.95",
      etaConv            : "98.0",
      id                 : "test_bmTypeInput",
      opex               : "50.0",
      sRated             : "25.0",
      uuid               : "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8",
      olmcharacteristic  : "olm:{(0.0,1.0)}",
      cosPhiFixed        : "cosPhiFixed:{(0.0,1.0)}"
    ]
  }

  def "A CsvDataSource should be able to handle a variety of different csvRows correctly"() {
    expect:
    dummyCsvSource.parseCsvRow(csvRow, csvSep) as List == resultingArray

    where:
    csvSep | csvRow                                                                                                                                                                                                                                                                                                                                                                                                              || resultingArray
    ","    | "\"4ca90220-74c2-4369-9afa-a18bf068840d\",\"{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}\",\"node_a\",\"2020-03-25T15:11:31Z[UTC]\",\"2020-03-24T15:11:31Z[UTC]\",\"8f9682df-0744-4b58-a122-f0dc730f6510\",\"true\",\"1\",\"1.0\",\"Höchstspannung\",\"380.0\",\"olm:{(0.00,1.00)}\",\"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}\"" || [
      "4ca90220-74c2-4369-9afa-a18bf068840d",
      "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
      "node_a",
      "2020-03-25T15:11:31Z[UTC]",
      "2020-03-24T15:11:31Z[UTC]",
      "8f9682df-0744-4b58-a122-f0dc730f6510",
      "true",
      "1",
      "1.0",
      "Höchstspannung",
      "380.0",
      "olm:{(0.00,1.00)}",
      "cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"
    ]
    ";"    | "\"4ca90220-74c2-4369-9afa-a18bf068840d\";\"{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}\";\"node_a\";\"2020-03-25T15:11:31Z[UTC]\";\"2020-03-24T15:11:31Z[UTC]\";\"8f9682df-0744-4b58-a122-f0dc730f6510\";\"true\";\"1\";\"1.0\";\"Höchstspannung\";\"380.0\";\"olm:{(0.00,1.00)}\";\"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}\"" || [
      "4ca90220-74c2-4369-9afa-a18bf068840d",
      "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
      "node_a",
      "2020-03-25T15:11:31Z[UTC]",
      "2020-03-24T15:11:31Z[UTC]",
      "8f9682df-0744-4b58-a122-f0dc730f6510",
      "true",
      "1",
      "1.0",
      "Höchstspannung",
      "380.0",
      "olm:{(0.00,1.00)}",
      "cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"
    ]
    ","    | "1,abc,def,\"He said \"\"run, run\"\"\", 6.0, \"thats \"\"good\"\"\""                                                                                                                                                                                                                                                                                                                                               || [
      "1",
      "abc",
      "def",
      "He said \"run, run\"",
      "6.0",
      "thats \"good\""
    ]
    ";"    | "1;abc;def;\"He said \"\"run, run\"\"\"; 6.0; \"thats \"\"good\"\"\""                                                                                                                                                                                                                                                                                                                                               || [
      "1",
      "abc",
      "def",
      "He said \"run, run\"",
      "6.0",
      "thats \"good\""
    ]
    ";"    | "1;abc;def;\"He said \"\"run; run\"\"\"; 6.0; \"thats \"\"good\"\"\""                                                                                                                                                                                                                                                                                                                                               || [
      "1",
      "abc",
      "def",
      "He said \"run; run\"",
      "6.0",
      "thats \"good\""
    ]
    ","    | "1,abc,def,\"He said \"\"test, test\"\" and was happy\", 5.0"                                                                                                                                                                                                                                                                                                                                                       || [
      "1",
      "abc",
      "def",
      "He said \"test, test\" and was happy",
      "5.0"
    ]
    ","    | "1,abc,def,\"He said \"\"test, test\"\" and was happy\",\"obviously, yet.\", 5.0"                                                                                                                                                                                                                                                                                                                                   || [
      "1",
      "abc",
      "def",
      "He said \"test, test\" and was happy",
      "obviously, yet.",
      "5.0"
    ]
    ","    | "1,abc,def,\"He said \"\"test, test\"\"\", 5.0"                                                                                                                                                                                                                                                                                                                                                                     || [
      "1",
      "abc",
      "def",
      "He said \"test, test\"",
      "5.0"
    ]
    ","    | "1,abc,def,\"He said \"\"test, test\"\"\""                                                                                                                                                                                                                                                                                                                                                                          || [
      "1",
      "abc",
      "def",
      "He said \"test, test\""
    ]
    ","    | "1,abc,def,\"He said \"\"test, test\"\" and was happy\", 5.0, \"... and felt like a \"\"genius\"\" with this.\""                                                                                                                                                                                                                                                                                                    || [
      "1",
      "abc",
      "def",
      "He said \"test, test\" and was happy",
      "5.0",
      "... and felt like a \"genius\" with this."
    ]
    ","    | "1,abc,def,\"He said \"\"test, test\"\" and was happy\", 5.0, \"... and felt like a \"\"genius\"\" with this.\","                                                                                                                                                                                                                                                                                                   || [
      "1",
      "abc",
      "def",
      "He said \"test, test\" and was happy",
      "5.0",
      "... and felt like a \"genius\" with this.",
      ""
    ]
  }


  def "A CsvDataSource should build a valid fields to attributes map with valid data and empty value fields as expected"() {
    given:
    def validHeadline = [
      "uuid",
      "active_power_gradient",
      "capex",
      "cosphi_rated",
      "eta_conv",
      "id",
      "opex",
      "s_rated",
      "olmcharacteristic",
      "cosPhiFixed"
    ] as String[]
    def validCsvRow = "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8,25.0,100.0,0.95,98.0,test_bmTypeInput,50.0,25.0,\"olm:{(0.0,1.0)}\","

    expect:
    dummyCsvSource.buildFieldsToAttributes(validCsvRow, validHeadline) == [
      activePowerGradient: "25.0",
      capex              : "100.0",
      cosphiRated        : "0.95",
      etaConv            : "98.0",
      id                 : "test_bmTypeInput",
      opex               : "50.0",
      sRated             : "25.0",
      uuid               : "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8",
      olmcharacteristic  : "olm:{(0.0,1.0)}",
      cosPhiFixed        : ""
    ]
  }

  def "A CsvDataSource should be able to handle several errors when the csvRow is invalid or cannot be processed"() {
    given:
    def validHeadline = [
      "uuid",
      "active_power_gradient",
      "capex",
      "cosphi_rated",
      "eta_conv",
      "id",
      "opex",
      "s_rated"
    ] as String[]

    expect:
    dummyCsvSource.buildFieldsToAttributes(invalidCsvRow, validHeadline) == [:]

    where:
    invalidCsvRow                                                                          || explaination
    "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8;25.0;100.0;0.95;98.0;test_bmTypeInput;50.0;25.0" || "wrong separator"
    "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8,25.0,100.0,0.95,98.0,test_bmTypeInput"           || "too less columns"
    "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8,25.0,100.0,0.95,98.0,test_bmTypeInput,,,,"       || "too much columns"
  }
}
