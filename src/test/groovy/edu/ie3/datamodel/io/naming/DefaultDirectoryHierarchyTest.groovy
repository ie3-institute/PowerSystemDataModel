/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.naming

import edu.ie3.datamodel.exceptions.FileException
import edu.ie3.datamodel.io.naming.DefaultDirectoryHierarchy
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

class DefaultDirectoryHierarchyTest extends Specification {
  @Shared
  Path tmpDirectory

  def setup() {
    tmpDirectory = Files.createTempDirectory("psdm_default_input_hierarchy")
  }

  def basePathString(String gridName) {
    tmpDirectory.resolve(gridName)
  }

  def cleanup() {
    FileIOUtils.deleteRecursively(tmpDirectory)
  }

  def "A DefaultFileHierarchy is set up correctly"() {
    given:
    def gridName = "test_grid"
    def basePath = basePathString(gridName)

    when:
    def dfh = new DefaultDirectoryHierarchy(tmpDirectory, gridName)

    then:
    try {
      dfh.baseDirectory.get() == basePath
      dfh.subDirectories.size() == 9
      dfh.subDirectories.get(basePath.resolve(Path.of("input", "grid"))) == true
      dfh.subDirectories.get(basePath.resolve(Path.of("input", "participants"))) == true
      dfh.subDirectories.get(basePath.resolve(Path.of("input", "participants", "time_series"))) == false
      dfh.subDirectories.get(basePath.resolve(Path.of("input", "global"))) == true
      dfh.subDirectories.get(basePath.resolve(Path.of("input", "thermal"))) == false
      dfh.subDirectories.get(basePath.resolve(Path.of("input", "graphics"))) == false
      dfh.subDirectories.get(basePath.resolve(Path.of("results", "grid"))) == false
      dfh.subDirectories.get(basePath.resolve(Path.of("results", "participants"))) == false
      dfh.subDirectories.get(basePath.resolve(Path.of("results", "thermal"))) == false
    } catch (TestFailedException e) {
      FileIOUtils.deleteRecursively(tmpDirectory)
      throw e
    }
  }

  def "A DefaultFileHierarchy is able to create a correct hierarchy of mandatory directories"() {
    given:
    def gridName = "test_grid"
    def basePath = basePathString(gridName)
    def dfh = new DefaultDirectoryHierarchy(tmpDirectory, gridName)

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
    /* Ignore the partial result and input trees, as they are not listed explicitly */
    Files.list(basePath).filter { path ->
      path != Paths.get(Stream.of(basePath.toString(), "input").collect(Collectors.joining(File.separator))) && path != Paths.get(Stream.of(basePath.toString(), "results").collect(Collectors.joining(File.separator)))
    }.each { path ->
      assert dfh.subDirectories.containsKey(path)
    }
  }

  def "A DefaultFileHierarchy is able to create a correct hierarchy of mandatory and optional directories"() {
    given:
    def gridName = "test_grid"
    def basePath = basePathString(gridName)
    def dfh = new DefaultDirectoryHierarchy(tmpDirectory, gridName)

    when:
    dfh.createDirs(true)

    then:
    Files.exists(basePath)
    Files.isDirectory(basePath)
    dfh.subDirectories.each { path, isMandatory ->
      assert Files.exists(path)
      assert Files.isDirectory(path)
    }
    Files.list(basePath).filter { path ->
      path != Paths.get(Stream.of(basePath.toString(), "input").collect(Collectors.joining(File.separator))) && path != Paths.get(Stream.of(basePath.toString(), "results").collect(Collectors.joining(File.separator)))
    }.each { path -> assert dfh.subDirectories.containsKey(path) }
  }

  def "A DefaultFileHierarchy is able to validate a correct hierarchy of mandatory and optional directories"() {
    given:
    def gridName = "test_grid"
    def dfh = new DefaultDirectoryHierarchy(tmpDirectory, gridName)
    dfh.createDirs(true)

    when:
    dfh.validate()

    then:
    noExceptionThrown()
  }

  def "A DefaultFileHierarchy throws an exception when trying to validate a missing hierarchy of mandatory and optional directories"() {
    given:
    def gridName = "test_grid"
    def basePath = basePathString(gridName)
    def dfh = new DefaultDirectoryHierarchy(tmpDirectory, gridName)

    when:
    dfh.validate()

    then:
    def ex = thrown(FileException)
    ex.message == "The path '" + basePath + "' does not exist."
  }

  def "A DefaultFileHierarchy throws an exception when trying to validate a file instead of a hierarchy"() {
    given:
    def gridName = "test_grid"
    def basePath = basePathString(gridName)
    def dfh = new DefaultDirectoryHierarchy(tmpDirectory, gridName)
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
    def basePath = basePathString(gridName)
    def dfh = new DefaultDirectoryHierarchy(tmpDirectory, gridName)
    dfh.createDirs()
    def globalDirectory = dfh.subDirectories.entrySet().find { entry -> entry.key.toString().endsWith("global") }.key
    Files.delete(globalDirectory)

    when:
    dfh.validate()

    then:
    def ex = thrown(FileException)
    ex.message == "The mandatory directory '" + Stream.of(basePath.toString(), "input", "global").collect(Collectors.joining(File.separator)) + "' does not exist."
  }

  def "A DefaultFileHierarchy throws an exception when trying to validate a hierarchy with file instead of mandatory directory"() {
    given:
    def gridName = "test_grid"
    def basePath = basePathString(gridName)
    def dfh = new DefaultDirectoryHierarchy(tmpDirectory, gridName)
    dfh.createDirs()
    def globalDirectory = dfh.subDirectories.entrySet().find { entry -> entry.key.toString().endsWith("global") }.key
    Files.delete(globalDirectory)
    Files.createFile(globalDirectory)

    when:
    dfh.validate()

    then:
    def ex = thrown(FileException)
    ex.message == "The mandatory directory '" + Stream.of(basePath.toString(), "input", "global").collect(Collectors.joining(File.separator)) + "' is not a directory."
  }

  def "A DefaultFileHierarchy throws an exception when trying to validate a hierarchy with file instead of optional directory"() {
    given:
    def gridName = "test_grid"
    def basePath = basePathString(gridName)
    def dfh = new DefaultDirectoryHierarchy(tmpDirectory, gridName)
    dfh.createDirs(true)
    def thermalDirectory = dfh.subDirectories.entrySet().find { entry -> entry.key.toString().endsWith("input" + File.separator + "thermal") }.key
    Files.delete(thermalDirectory)
    Files.createFile(thermalDirectory)

    when:
    dfh.validate()

    then:
    def ex = thrown(FileException)
    ex.message == "The optional directory '" + Stream.of(basePath.toString(), "input", "thermal").collect(Collectors.joining(File.separator)) + "' is not a directory."
  }

  def "A DefaultFileHierarchy throws an exception when trying to validate a hierarchy with unsupported extra directory"() {
    given:
    def gridName = "test_grid"
    def basePath = basePathString(gridName)
    def fifthWheelPath = basePathString(gridName).resolve("something_on_top")
    def dfh = new DefaultDirectoryHierarchy(tmpDirectory, gridName)
    dfh.createDirs(true)
    Files.createDirectory(fifthWheelPath)

    when:
    dfh.validate()

    then:
    def ex = thrown(FileException)
    ex.message == "There is a directory '" + Stream.of(basePath.toString(), "something_on_top").collect(Collectors.joining(File.separator)) + "' apparent, that is not supported by the default directory hierarchy."
  }
}
