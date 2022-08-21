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

class GridIoIT extends Specification implements CsvTestDataMeta {

  def setup(){
    println "setup in progress..."
  }

  def cleanup(){
    println "cleanup in progress..."
  }

  def "Input JointGridContainer equals Output JointGridContainer."(){

    given:
    // create joint grid container
    def gridname = new String("vn_simona")
    def seperator = new String(",")
    def folderpath = new String(jointGridFolderPath)
    def firstGridContainer = CsvJointGridContainerSource.read(gridname, seperator, folderpath)

    // output: prepare output folder
    def outFolderpath = new String("./exampleGridOut")
    def sink = new CsvFileSink(outFolderpath)
    def outDirectory = new File(outFolderpath)

    when:
    // write files from joint grid container in output directory
    sink.persistJointGrid(firstGridContainer)

    // create second grid container from output folder
    def secondGridContainer = CsvJointGridContainerSource.read(gridname, seperator, outFolderpath)

    // delete files in output directory
    outDirectory.eachFile {
      it.deleteOnExit()
    }

    then:
    //compare input and output grid container participants

    println("Input and output parameters of the joint grid container are identical for:")

    println("Grid Name: " + firstGridContainer.getGridName().equals(secondGridContainer.getGridName()))
    println("RawGrid: " + firstGridContainer.getRawGrid().equals(secondGridContainer.getRawGrid()))
    println("System Participants: " + firstGridContainer.getSystemParticipants().equals(secondGridContainer.getSystemParticipants()))
    println("Graphics: " + firstGridContainer.getGraphics().equals(secondGridContainer.getGraphics()))

    println("System Participants - Fixed Feed Ins: " + firstGridContainer.getSystemParticipants().getFixedFeedIns().equals(secondGridContainer.getSystemParticipants().getFixedFeedIns()))
    println("System Participants - BM Plants: " + firstGridContainer.getSystemParticipants().getBmPlants().equals(secondGridContainer.getSystemParticipants().getBmPlants()))
    println("System Participants - PV Plants: " + firstGridContainer.getSystemParticipants().getPvPlants().equals(secondGridContainer.getSystemParticipants().getPvPlants()))
    println("System Participants - Loads: " + firstGridContainer.getSystemParticipants().getLoads().equals(secondGridContainer.getSystemParticipants().getLoads()))
    println("System Participants - EvCS: " + firstGridContainer.getSystemParticipants().getEvCS().equals(secondGridContainer.getSystemParticipants().getEvCS()))
    println("System Participants - Storages: " + firstGridContainer.getSystemParticipants().getStorages().equals(secondGridContainer.getSystemParticipants().getStorages()))

    println("System Participants - wec Plants: " + firstGridContainer.getSystemParticipants().getWecPlants().equals(secondGridContainer.getSystemParticipants().getWecPlants()))

  }

}
