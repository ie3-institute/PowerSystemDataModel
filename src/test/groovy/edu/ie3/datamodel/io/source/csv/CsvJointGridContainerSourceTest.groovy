/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import spock.lang.Specification

class CsvJointGridContainerSourceTest extends Specification implements CsvTestDataMeta {

  def "The CsvJointGridContainer is able to read a grid with flat structure"() {
    when:
    def gridName = "vn_simona"
    def separator = ","
    def inputGridContainer = CsvJointGridContainerSource.read(gridName, separator, jointGridFolderPath, false)

    then:
    inputGridContainer.getGraphics().allEntitiesAsList().size() == 590
    inputGridContainer.getSystemParticipants().allEntitiesAsList().size() == 566
    inputGridContainer.getRawGrid().allEntitiesAsList().size() == 898
  }

  def "The CsvJointGridContainer is able to read a grid with hierarchic structure"() {
    when:
    def gridName = "vn_simona"
    def separator = ";"
    def inputGridContainer = CsvJointGridContainerSource.read(gridName, separator, hierarchicGridFolderPath, true)

    then:
    inputGridContainer.getGraphics().allEntitiesAsList().size() == 0
    inputGridContainer.getSystemParticipants().allEntitiesAsList().size() == 198
    inputGridContainer.getRawGrid().allEntitiesAsList().size() == 202
  }
}