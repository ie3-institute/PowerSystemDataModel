package edu.ie3.datamodel.io

import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme
import edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation
import spock.lang.Specification

import java.nio.file.Paths

class FileNamingStrategyTest extends Specification{

    def "The FileNamingStrategy extracts correct meta information from a valid individual time series file name"() {
        given:
        def fns = new edu.ie3.datamodel.io.csv.FileNamingStrategy()
        def path = Paths.get(pathString)

        when:
        def metaInformation = fns.extractTimeSeriesMetaInformation(path)

        then:
        IndividualTimeSeriesMetaInformation.isAssignableFrom(metaInformation.getClass())
        (metaInformation as IndividualTimeSeriesMetaInformation).with {
            assert it.uuid == UUID.fromString("4881fda2-bcee-4f4f-a5bb-6a09bf785276")
            assert it.columnScheme == expectedColumnScheme
        }

        where:
        pathString || expectedColumnScheme
        "/bla/foo/its_c_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || ColumnScheme.ENERGY_PRICE
        "/bla/foo/its_p_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || ColumnScheme.ACTIVE_POWER
        "/bla/foo/its_pq_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || ColumnScheme.APPARENT_POWER
        "/bla/foo/its_h_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || ColumnScheme.HEAT_DEMAND
        "/bla/foo/its_ph_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND
        "/bla/foo/its_pqh_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND
        "/bla/foo/its_weather_4881fda2-bcee-4f4f-a5bb-6a09bf785276.csv" || ColumnScheme.WEATHER
}
