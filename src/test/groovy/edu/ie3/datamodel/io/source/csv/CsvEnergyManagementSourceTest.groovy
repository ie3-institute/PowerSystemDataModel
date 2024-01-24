/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.source.EnergyManagementSource
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.EnergyManagementTestData
import spock.lang.Specification

class CsvEnergyManagementSourceTest extends Specification implements CsvTestDataMeta {

  def "An EnergyManagementSource with csv input should return data from valid em input file as expected"() {
    given:
    def csvEnergyManagementSource = new EnergyManagementSource(
    Mock(TypeSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    Map<UUID, OperatorInput> operatorMap = map([EnergyManagementTestData.emInput.operator])

    expect:
    def emUnits = Try.of(() -> csvEnergyManagementSource.getEmUnits(operatorMap), SourceException)

    emUnits.success
    emUnits.data.get().emUnits.size() == 1
    emUnits.data.get().emUnits == [EnergyManagementTestData.emInput] as Set
  }
}
