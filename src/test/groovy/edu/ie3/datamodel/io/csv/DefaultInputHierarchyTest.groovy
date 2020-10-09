/*
 * © 2020. TU Dortmund University,
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

class DefaultInputHierarchyTest extends Specification {
	@Shared
	Path tmpDirectory

	def setup() {
		tmpDirectory = Files.createTempDirectory("psdm_default_input_hierarchy")
	}

	def basePathString(String gridName) {
		FilenameUtils.concat(tmpDirectory.toString(), gridName)
	}

	def cleanup() {
		FileIOUtils.deleteRecursively(tmpDirectory)
	}

	def "A DefaultFileHierarchy is set up correctly"() {
		given:
		def gridName = "test_grid"
		def basePath = basePathString(gridName)

		when:
		def dfh = new DefaultInputHierarchy(tmpDirectory.toString(), gridName)

		then:
		try {
			dfh.baseDirectory == Paths.get(basePath)
			dfh.subDirectories.get(Paths.get(FilenameUtils.concat(basePath, "grid"))) == true
			dfh.subDirectories.get(Paths.get(FilenameUtils.concat(basePath, "participants"))) == true
			dfh.subDirectories.get(Paths.get(FilenameUtils.concat(FilenameUtils.concat(basePath, "participants"), "time_series"))) == false
			dfh.subDirectories.get(Paths.get(FilenameUtils.concat(basePath, "global"))) == true
			dfh.subDirectories.get(Paths.get(FilenameUtils.concat(basePath, "thermal"))) == false
			dfh.subDirectories.get(Paths.get(FilenameUtils.concat(basePath, "graphics"))) == false
		} catch (TestFailedException e) {
			FileIOUtils.deleteRecursively(tmpDirectory)
			throw e
		}
	}

	def "A DefaultFileHierarchy is able to create a correct hierarchy of mandatory directories"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def dfh = new DefaultInputHierarchy(tmpDirectory.toString(), gridName)

		when:
		dfh.createDirs()

		then:
		Files.exists(basePath)
		Files.isDirectory(basePath)
		dfh.subDirectories.each { path, isMandatory ->
			assert Files.exists(path) == isMandatory
			if (isMandatory) {
				assert Files.isDirectory(path)
			}
		}
		Files.list(basePath).each { path -> assert dfh.subDirectories.containsKey(path) }
	}

	def "A DefaultFileHierarchy is able to create a correct hierarchy of mandatory and optional directories"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def dfh = new DefaultInputHierarchy(tmpDirectory.toString(), gridName)

		when:
		dfh.createDirs(true)

		then:
		Files.exists(basePath)
		Files.isDirectory(basePath)
		dfh.subDirectories.each { path, isMandatory ->
			assert Files.exists(path)
			assert Files.isDirectory(path)
		}
		Files.list(basePath).forEach { path -> assert dfh.subDirectories.containsKey(path) }
	}

	def "A DefaultFileHierarchy is able to validate a correct hierarchy of mandatory and optional directories"() {
		given:
		def gridName = "test_grid"
		def dfh = new DefaultInputHierarchy(tmpDirectory.toString(), gridName)
		dfh.createDirs(true)

		when:
		dfh.validate()

		then:
		noExceptionThrown()
	}

	def "A DefaultFileHierarchy throws an exception when trying to validate a missing hierarchy of mandatory and optional directories"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def dfh = new DefaultInputHierarchy(tmpDirectory.toString(), gridName)

		when:
		dfh.validate()

		then:
		def ex = thrown(FileException)
		ex.message == "The path '" + basePath + "' does not exist."
	}

	def "A DefaultFileHierarchy throws an exception when trying to validate a file instead of a hierarchy"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def dfh = new DefaultInputHierarchy(tmpDirectory.toString(), gridName)
		Files.createFile(basePath)

		when:
		dfh.validate()

		then:
		def ex = thrown(FileException)
		ex.message == "The path '" + basePath + "' has to be a directory."
	}

	def "A DefaultFileHierarchy throws an exception when trying to validate a hierarchy with missing mandatory directory"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def dfh = new DefaultInputHierarchy(tmpDirectory.toString(), gridName)
		dfh.createDirs()
		def globalDirectory = dfh.subDirectories.entrySet().find { entry -> entry.key.toString().endsWith("global") }.key
		Files.delete(globalDirectory)

		when:
		dfh.validate()

		then:
		def ex = thrown(FileException)
		ex.message == "The mandatory directory '" + basePath + "/global' does not exist."
	}

	def "A DefaultFileHierarchy throws an exception when trying to validate a hierarchy with file instead of mandatory directory"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def dfh = new DefaultInputHierarchy(tmpDirectory.toString(), gridName)
		dfh.createDirs()
		def globalDirectory = dfh.subDirectories.entrySet().find { entry -> entry.key.toString().endsWith("global") }.key
		Files.delete(globalDirectory)
		Files.createFile(globalDirectory)

		when:
		dfh.validate()

		then:
		def ex = thrown(FileException)
		ex.message == "The mandatory directory '" + basePath + "/global' is not a directory."
	}

	def "A DefaultFileHierarchy throws an exception when trying to validate a hierarchy with file instead of optional directory"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def dfh = new DefaultInputHierarchy(tmpDirectory.toString(), gridName)
		dfh.createDirs(true)
		def globalDirectory = dfh.subDirectories.entrySet().find { entry -> entry.key.toString().endsWith("thermal") }.key
		Files.delete(globalDirectory)
		Files.createFile(globalDirectory)

		when:
		dfh.validate()

		then:
		def ex = thrown(FileException)
		ex.message == "The optional directory '" + basePath + "/thermal' is not a directory."
	}

	def "A DefaultFileHierarchy throws an exception when trying to validate a hierarchy with unsupported extra directory"() {
		given:
		def gridName = "test_grid"
		def basePath = Paths.get(basePathString(gridName))
		def fifthWheelPath = Paths.get(FilenameUtils.concat(basePathString(gridName), "something_on_top"))
		def dfh = new DefaultInputHierarchy(tmpDirectory.toString(), gridName)
		dfh.createDirs(true)
		Files.createDirectory(fifthWheelPath)

		when:
		dfh.validate()

		then:
		def ex = thrown(FileException)
		ex.message == "There is a directory '" + basePath + "/something_on_top' apparent, that is not supported by the default directory hierarchy."
	}
}
