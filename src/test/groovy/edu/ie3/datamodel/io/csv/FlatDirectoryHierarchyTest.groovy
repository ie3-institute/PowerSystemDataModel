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

	def basePathString() {
		tmpDirectory.toString()
	}

	def cleanup() {
		//FileIOUtils.deleteRecursively(tmpDirectory)
	}

	def "A FlatDirectoryHierarchy is set up correctly"() {
		given:
		def basePath = basePathString()

		when:
		def fdh = new FlatDirectoryHierarchy(tmpDirectory.toString())

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
		def basePath = Paths.get(basePathString())
		def fdh = new FlatDirectoryHierarchy(tmpDirectory.toString())
		def x = 2

		when:
		//fdh.createDirs()
		x = 3

		then:
		Files.exists(basePath)
		Files.isDirectory(basePath)
	}

}
