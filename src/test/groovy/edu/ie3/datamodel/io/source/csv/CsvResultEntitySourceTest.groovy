/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import spock.lang.Specification

import edu.ie3.test.common.ResultEntityTestData as retd

class CsvResultEntitySourceTest extends Specification implements CsvTestDataMeta {

  def "A CsvResultEntitySource should read a csv and extract entities correctly"() {
    given:
    def csvResultEntitySource = new CsvResultEntitySource(csvSep, resultEntitiesFolderPath, fileNamingStrategy)

    when:
    // existent
    def wecResults = csvResultEntitySource.wecResults
    def pvResults = csvResultEntitySource.pvResults
    def bmResults = csvResultEntitySource.bmResults
    def fixedFeedInResults = csvResultEntitySource.fixedFeedInResults
    // non-existent (empty)
    def chpResults = csvResultEntitySource.chpResults
    def hpResults = csvResultEntitySource.hpResults
    def evResults = csvResultEntitySource.evResults
    def evcsResults = csvResultEntitySource.evcsResults
    def loadResults = csvResultEntitySource.loadResults
    def storageResults = csvResultEntitySource.storageResults
    def thermalHouseResults = csvResultEntitySource.thermalHouseResults

    then:
    wecResults.size() == retd.WEC_RESULT_SIZE
    pvResults.size() == retd.PV_RESULT_SIZE
    bmResults.size() == retd.BM_RESULT_SIZE
    fixedFeedInResults.size() == retd.FIXED_FEED_IN_SIZE
    chpResults.empty && hpResults.empty && evResults.empty && evcsResults.empty &&
        loadResults.empty && storageResults.empty && thermalHouseResults.empty

    bmResults.first().uuid == retd.BM_UUID
    bmResults.first().inputModel == retd.BM_INPUT_MODEL
    bmResults.first().p == retd.BM_ACTIVE_POWER
    bmResults.first().q == retd.BM_REACTIVE_POWER
    bmResults.first().time == retd.BM_TIME
  }
}
