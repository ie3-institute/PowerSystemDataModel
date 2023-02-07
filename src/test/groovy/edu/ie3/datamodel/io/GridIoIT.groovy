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

  def "Input JointGridContainer equals Output JointGridContainer."() {

    given:
    // create joint grid container
    def gridname = "vn_simona"
    def seperator = ","
    def firstGridContainer = CsvJointGridContainerSource.read(gridname, seperator, jointGridFolderPath)

    // output: prepare output folder
    //def outDirectoryPath = tempDirectory.toAbsolutePath().toString()
    //def sink = new CsvFileSink(outDirectoryPath)

    when:
    // write files from joint grid container in output directory
    sink.persistJointGrid(firstGridContainer)

    // create second grid container from output folder
    def secondGridContainer = CsvJointGridContainerSource.read(gridname, seperator, tempDirectory.toAbsolutePath().toString())

    then:
    //compare input and output joint grid container

    firstGridContainer == secondGridContainer

  }

  def cleanupSpec() {
    FileIOUtils.deleteRecursively(tempDirectory)
    sink.shutdown()
  }

}
