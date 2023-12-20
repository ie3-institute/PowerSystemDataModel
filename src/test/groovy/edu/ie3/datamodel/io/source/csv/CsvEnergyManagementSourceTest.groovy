/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.source.EnergyManagementSource
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

// TODO test recursion
class CsvEnergyManagementSourceTest extends Specification implements CsvTestDataMeta {

  def "An EnergyManagementSource with csv input should return data from valid em input file as expected"() {
    given:
    def csvEnergyManagementSource = new EnergyManagementSource(
    Mock(TypeSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    expect:
    def emUnits = Try.of(() -> csvEnergyManagementSource.getEmUnits(operators.toSet()), SourceException)

    emUnits.success
    emUnits.data.get().emUnits.size() == 1
    emUnits.data.get().emUnits == resultingSet as Set

    where:
    operators                                    || resultingSet
    [SystemParticipantTestData.emInput.operator] || [SystemParticipantTestData.emInput]
    []					                         || [SystemParticipantTestData.emInput.copy().operator(OperatorInput.NO_OPERATOR_ASSIGNED).build()]
  }
}
