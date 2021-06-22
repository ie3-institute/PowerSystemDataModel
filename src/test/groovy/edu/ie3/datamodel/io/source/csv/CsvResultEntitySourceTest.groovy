package edu.ie3.datamodel.io.source.csv

import spock.lang.Specification

import edu.ie3.test.common.ResultEntityTestData as retd

class CsvResultEntitySourceTest extends Specification implements CsvTestDataMeta {

    def "A CsvResultEntitySource should read a csv and extract entities correctly"() {
        given:
        def csvResultEntitySource = new CsvResultEntitySource(csvSep,
                resultEntitiesFolderPath, entityPersistenceNamingStrategy)

        when:
        def wecResults = csvResultEntitySource.getWecResults()

        then:
        wecResults.size() == retd.wecResultsSize
    }
}
