/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.csv

import edu.ie3.datamodel.exceptions.FileException
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.io.FilenameUtils
import org.testcontainers.shaded.org.bouncycastle.util.test.TestFailedException
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.stream.Stream

class FlatDirectoryHierarchyTest extends Specification {
	@Shared
	Path tmpDirectory

	def setup() {
		tmpDirectory = Files.createTempDirectory("psdm_flat_input_hierarchy")
	}

	def basePathString(String gridName) {
		FilenameUtils.concat(tmpDirectory.toString(), gridName)
	}

	def cleanup() {
		FileIOUtils.deleteRecursively(tmpDirectory)
	}

	def "A FlatDirectoryHierarchy is set up correctly"() {
		given:
		def gridName = "test_grid"
		def basePath = basePathString(gridName)

		when:
		def fdh = new FlatDirectoryHierarchy(tmpDirectory.toString(), gridName)

		then:
		try {
			fdh.projectDirectory == Paths.get(basePath)
		} catch (TestFailedException e) {
			FileIOUtils.deleteRecursively(tmpDirectory)
			throw e
		}
	}

	def "A FlatDirectoryHierarchy is able to create a correct flat hierarchy of directories"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def fdh = new FlatDirectoryHierarchy(tmpDirectory.toString(), gridName)

		when:
		fdh.createDirs()

		then:
		Files.exists(basePath)
		Files.isDirectory(basePath)
	}

	def "A FlatDirectoryHierarchy is able to validate a correct hierarchy of mandatory and optional directories"() {
		given:
		def gridName = "test_grid"
		def fdh = new FlatDirectoryHierarchy(tmpDirectory.toString(), gridName)
		fdh.createDirs()

		when:
		fdh.validate()

		then:
		noExceptionThrown()
	}

	def "A FlatDirectoryHierarchy throws an exception when trying to validate a missing hierarchy of mandatory and optional directories"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def fdh = new FlatDirectoryHierarchy(tmpDirectory.toString(), gridName)

		when:
		fdh.validate()

		then:
		def ex = thrown(FileException)
		ex.message == "The path '" + basePath + "' does not exist."
	}

	def "A FlatDirectoryHierarchy throws an exception when trying to validate a file instead of a hierarchy"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def fdh = new FlatDirectoryHierarchy(tmpDirectory.toString(), gridName)
		Files.createFile(basePath)

		when:
		fdh.validate()

		then:
		def ex = thrown(FileException)
		ex.message == "The path '" + basePath + "' has to be a directory."
	}
}
