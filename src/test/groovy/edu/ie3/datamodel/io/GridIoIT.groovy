/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io

import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.datamodel.io.source.csv.CsvJointGridContainerSource
import edu.ie3.datamodel.io.source.csv.CsvTestDataMeta
import spock.lang.Specification

import java.nio.file.Files

class GridIoIT extends Specification implements CsvTestDataMeta {

  def "Input JointGridContainer equals Output JointGridContainer."(){

    given:
    // create joint grid container
    def gridname = new String("vn_simona")
    def seperator = new String(",")
    def folderpath = new String(jointGridFolderPath)
    def firstGridContainer = CsvJointGridContainerSource.read(gridname, seperator, folderpath)

    // output: prepare output folder
    def tempDirectory = Files.createTempDirectory("GridIoIT")
    def outDirectoryPath = tempDirectory.toAbsolutePath().toString()
    def sink = new CsvFileSink(outDirectoryPath)

    when:
    // write files from joint grid container in output directory
    sink.persistJointGrid(firstGridContainer)

    // create second grid container from output folder
    def secondGridContainer = CsvJointGridContainerSource.read(gridname, seperator, outDirectoryPath)

    // delete files in output directory
    tempDirectory.toFile().eachFile {
      it.deleteOnExit()
    }

    then:
    //compare input and output joint grid container

    firstGridContainer.getProperties().equals(secondGridContainer.getProperties())


  }

}
