/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.csv

import edu.ie3.datamodel.exceptions.SinkException
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.io.FilenameUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class BufferedCsvWriterTest extends Specification {
	@Shared
	Path tmpDirectory

	def setup() {
		tmpDirectory = Files.createTempDirectory("psdm_csv_buffered_writer_")
	}

	def cleanup() {
		FileIOUtils.deleteRecursively(tmpDirectory)
	}

	def "The convenience constructor of the BufferedCsvWriter class works as expected."() {
		given:
		def baseDirectory = tmpDirectory.toString()
		def fileDefinition = new CsvFileDefinition("test.csv", ["a", "b", "c"] as String[], ",")
		def expectedFile = new File(FilenameUtils.concat(tmpDirectory.toString(), fileDefinition.filePath))

		when:
		def actual = new BufferedCsvWriter(baseDirectory, fileDefinition, false, false)

		then:
		actual.with {
			assert it.headLineElements == ["a", "b", "c"] as String[]
			assert it.csvSep == ","
		}
		expectedFile.exists()
		expectedFile.file // is it a file?
	}

	def "The buffered csv writer refuses to write entries, if their length does not conform the needed length of head line elements"() {
		given:
		def targetFile = FilenameUtils.concat(tmpDirectory.toString(), "test.csv")
		def writer = new BufferedCsvWriter(targetFile, ["a", "b", "c"] as String[], "c,", false, false)
		def malFormedInput = [
			"a": "z",
			"b": "y"
		]

		when:
		writer.write(malFormedInput)

		then:
		def e = thrown(SinkException)
		e.message == "The provided data does not meet the pre-defined head line elements 'a,b,c'."
	}

	def "The buffered csv writer refuses to write entries, if keys do not match the required head line"() {
		given:
		def targetFile = FilenameUtils.concat(tmpDirectory.toString(), "test.csv")
		def writer = new BufferedCsvWriter(targetFile, ["a", "b", "c"] as String[], "c,", false, false)
		def malFormedInput = [
			"a": "z",
			"b": "y",
			"d": "w"
		]

		when:
		writer.write(malFormedInput)

		then:
		def e = thrown(SinkException)
		e.message == "The provided data does not meet the pre-defined head line elements 'a,b,c'."
	}
}
