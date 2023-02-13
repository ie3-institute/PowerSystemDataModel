/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io

import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.datamodel.io.source.csv.CsvJointGridContainerSource
import edu.ie3.datamodel.io.source.csv.CsvTestDataMeta
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class GridIoIT extends Specification implements CsvTestDataMeta {

  @Shared
  Path tempDirectory

  @Shared
  CsvFileSink sink

  def setupSpec() {
    tempDirectory = Files.createTempDirectory("GridIoIT")
    sink = new CsvFileSink(tempDirectory.toAbsolutePath().toString())
  }

  def cleanupSpec() {
    sink.shutdown()
    FileIOUtils.deleteRecursively(tempDirectory)
  }

  def "Input JointGridContainer equals Output JointGridContainer."() {

    given:
    // create joint grid container
    def gridName = "vn_simona"
    def separator = ","
    def firstGridContainer = CsvJointGridContainerSource.read(gridName, separator, jointGridFolderPath)

    when:
    // write files from joint grid container in output directory
    sink.persistJointGrid(firstGridContainer)

    // create second grid container from output folder
    def secondGridContainer = CsvJointGridContainerSource.read(gridName, separator, tempDirectory.toAbsolutePath().toString())

    then:
    // compare input and output joint grid container
    firstGridContainer == secondGridContainer

  }
}
