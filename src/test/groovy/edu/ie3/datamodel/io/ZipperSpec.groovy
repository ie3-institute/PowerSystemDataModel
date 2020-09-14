/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io

import edu.ie3.datamodel.exceptions.FileException
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.io.FilenameUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ZipperSpec extends Specification {
	@Shared
	Path tmpDirectory

	def setup() {
		tmpDirectory = Files.createTempDirectory("psdm_zipper")
	}

	def cleanup() {
		FileIOUtils.deleteRecursively(tmpDirectory)
	}

	def "The Zipper throws an exception, if the target file does not end with '.tar.gz'"() {
		given:
		def filePath = Paths.get(getClass().getResource('/testGridFiles/grid/node_input.csv').toURI())
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "test.bli.blubb"))

		when:
		Zipper.compress(filePath, archiveFile)

		then:
		def ex = thrown(FileException)
		ex.getMessage() == "The target archive path has to end with '.tar.gz'. You provided: '"+ tmpDirectory +"/test.bli.blubb'."
	}

	def "The Zipper throws an exception, if the target file already exists"() {
		given:
		def filePath = Paths.get(getClass().getResource('/testGridFiles/grid/node_input.csv').toURI())
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "test.tar.gz"))
		Files.createFile(archiveFile)

		when:
		Zipper.compress(filePath, archiveFile)

		then:
		def ex = thrown(FileException)
		ex.getMessage() == "The target archive '"+ tmpDirectory +"/test.tar.gz' already exists."
	}

	def "The zipper is able to zip one single file to .tar.gz"() {
		given:
		def filePath = Paths.get(getClass().getResource('/testGridFiles/grid/node_input.csv').toURI())
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "test.tar.gz"))

		when:
		Zipper.compress(filePath, archiveFile)

		then:
		noExceptionThrown()
		Files.exists(archiveFile)
		Files.size(archiveFile) >= 554 && Files.size(archiveFile) <= 588 // Should be around 571 bytes +/- 3 %
	}

	def "The zipper is able to zip the content of a folder to .tar.gz"() {
		given:
		def filePath = Paths.get(getClass().getResource('/testGridFiles/grid').toURI())
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "test.tar.gz"))

		when:
		Zipper.compress(filePath, archiveFile)

		then:
		noExceptionThrown()
		Files.exists(archiveFile)
		Files.size(archiveFile) >= 1330 && Files.size(archiveFile) <= 1412 // Should be around 1371 bytes +/- 3 %
	}

	def "The zipper is able to zip the content of a folder with nested structure to .tar.gz"() {
		given:
		def filePath = Paths.get(getClass().getResource('/testGridFiles/grid_default_hierarchy').toURI())
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "test.tar.gz"))

		when:
		Zipper.compress(filePath, archiveFile)

		then:
		noExceptionThrown()
		Files.exists(archiveFile)
		Files.size(archiveFile) >= 1509 && Files.size(archiveFile) <= 1602 // Should be around 1556 bytes +/- 3 %
	}
}
