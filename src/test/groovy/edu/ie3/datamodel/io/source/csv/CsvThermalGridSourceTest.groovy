/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import spock.lang.Specification

class CsvThermalGridSourceTest extends Specification implements CsvTestDataMeta {

  def "CsvThermalGridSource can read a valid thermal grid"() {
    when:
    def thermalGrids = CsvThermalGridSource.read(",", thermalFolderPath)

    then:
    thermalGrids.size() == 1
    thermalGrids.get(0).with {
      def bus = it.bus()
      assert it.houses().every { it.thermalBus == bus }
      assert it.heatStorages().every { it.thermalBus == bus }
      assert it.domesticHotWaterStorages().every { it.thermalBus == bus }
    }
  }
}
