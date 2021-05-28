/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.csv

import edu.ie3.datamodel.models.input.system.BmInput
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

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
		FileIOUtils.deleteRecursively(tmpDirectory)
	}

	def "A FlatDirectoryHierarchy is set up correctly"() {
		given:
		def basePath = Paths.get(basePathString())

		when:
		def fdh = new FlatDirectoryHierarchy()

		then:
		Files.exists(basePath)
		Files.isDirectory(basePath)

		fdh.getSubDirectory(BmInput) == Optional.empty();
		}

}
