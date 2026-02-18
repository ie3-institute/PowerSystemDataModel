/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.exceptions.FileException
import edu.ie3.datamodel.io.naming.DefaultDirectoryHierarchy
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

/**
 * Testing whether PSDM CSV grids are equal when serialized and deserialized sequentially.
 * Grid data should <strong>not</strong> change when written out or parsed.
 */
class GridIoIT extends Specification implements CsvTestDataMeta {

  Path tempDirectory
  CsvFileSink sinkFlat
  CsvFileSink sinkHierarchic

  def setup() {
    FileNamingStrategy hierarchicNamingStrategy = new FileNamingStrategy(
        new EntityPersistenceNamingStrategy(),
        new DefaultDirectoryHierarchy(Path.of("output"), "vn_simona"))
    tempDirectory = Files.createTempDirectory("GridIoIT")
    sinkFlat = new CsvFileSink(tempDirectory.toAbsolutePath())
    sinkHierarchic = new CsvFileSink(tempDirectory.toAbsolutePath(), hierarchicNamingStrategy, ",")
  }

  def cleanup() {
    sinkFlat.shutdown()
    sinkHierarchic.shutdown()
    FileIOUtils.deleteRecursively(tempDirectory)
  }

  def "Input flat JointGridContainer equals Output flat JointGridContainer."() {

    given:
    // create joint grid container
    def gridName = "vn_simona"
    def separator = ","
    def firstGridContainer = CsvJointGridContainerSource.read(gridName, separator, jointGridFolderPath, false)

    when:
    // write files from joint grid container in output directory
    sinkFlat.persistJointGrid(firstGridContainer)
    // create second grid container from output folder
    def secondGridContainer = CsvJointGridContainerSource.read(gridName, separator, tempDirectory.toAbsolutePath(), false)

    then:
    // compare input and output joint grid container
    firstGridContainer == secondGridContainer
  }

  def "Input flat JointGridContainer equals Output hierarchic JointGridContainer."() {
    given:
    // create joint grid container
    def gridName = "vn_simona"
    def separator = ","
    def firstGridContainer = CsvJointGridContainerSource.read(gridName, separator, jointGridFolderPath, false)

    when:
    sinkHierarchic.persistJointGrid(firstGridContainer)
    def secondGridContainer = CsvJointGridContainerSource.read(gridName, separator, tempDirectory.toAbsolutePath(), true)

    then:
    // compare input and output joint grid container
    firstGridContainer == secondGridContainer
  }

  def "CsvJointGridContainerSource throws exception if a hierarchic grid is expected but a flat grid is presented."() {
    given:
    def gridName = "vn_simona"
    def separator = ","

    when:
    CsvJointGridContainerSource.read(gridName, separator, jointGridFolderPath, true)

    then:
    thrown(FileException)
  }

  def "Input flat JointGridContainer with EmInput equals Output flat JointGridContainer."() {
    given:
    def gridName = "vn_simona"
    def separator = ","
    def baseGrid = CsvJointGridContainerSource.read(gridName, separator, jointGridFolderPath, false)

    def emInput = new EmInput(
        UUID.randomUUID(),
        "test_em",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        "control_strategy_test",
        null
        )

    def participants = baseGrid.systemParticipants
    def oldPv = participants.pvPlants.first()
    def newPv = oldPv.copy().em(emInput).build()

    def newPvs = (participants.pvPlants - oldPv + newPv)

    def newParticipants = new SystemParticipants(
        participants.bmPlants,
        participants.chpPlants,
        participants.evcs,
        participants.evs,
        participants.fixedFeedIns,
        participants.heatPumps,
        participants.loads,
        newPvs,
        participants.storages,
        participants.wecPlants
        )

    def gridWithEm = new JointGridContainer(
        baseGrid.gridName,
        baseGrid.rawGrid,
        newParticipants,
        baseGrid.graphics
        )

    when:
    sinkFlat.persistJointGrid(gridWithEm)
    def readGrid = CsvJointGridContainerSource.read(gridName, separator, tempDirectory.toAbsolutePath(), false)

    then:
    gridWithEm == readGrid
  }
}
