/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.source.EnergyManagementSource
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.datamodel.models.UniqueEntity
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

import java.util.function.Function
import java.util.stream.Collectors

// TODO test recursion
class CsvEnergyManagementSourceTest extends Specification implements CsvTestDataMeta {

  def "An EnergyManagementSource with csv input should return data from valid em input file as expected"() {
    given:
    def csvEnergyManagementSource = new EnergyManagementSource(
    Mock(TypeSource),
    new CsvDataSource(csvSep, participantsFolderPath, fileNamingStrategy))

    Map<UUID, OperatorInput> operatorMap = [SystemParticipantTestData.emInput.operator].stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()))

    expect:
    def emUnits = Try.of(() -> csvEnergyManagementSource.getEmUnits(operatorMap), SourceException)

    emUnits.success
    emUnits.data.get().size() == 2
    emUnits.data.get() == [SystemParticipantTestData.emInput, SystemParticipantTestData.parentEm].stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()))
  }
}
