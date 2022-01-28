package edu.ie3.datamodel.io.connectors

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation
import spock.lang.Shared
import spock.lang.Specification

class SqlIndividualTimeSeriesMetaInformationTest extends Specification {

    @Shared
    private final UUID randomUUID = UUID.fromString("80d1bbec-79df-4ae2-b60a-34a2c3f77408")

    @Shared
    private final ColumnScheme columnScheme = ColumnScheme.ACTIVE_POWER

    @Shared
    private final String tableName = "its_p_80d1bbec-79df-4ae2-b60a-34a2c3f77408"

    def "SqlIndividualTimeSeriesMetaInformation is properly created with standard constructor"() {
        when:
        def sqlMetaInf = new SqlConnector.SqlIndividualTimeSeriesMetaInformation(randomUUID, columnScheme, tableName)

        then:
        sqlMetaInf.getUuid() == randomUUID
        sqlMetaInf.getColumnScheme() == columnScheme
        sqlMetaInf.getTableName() == tableName
    }

    def "SqlIndividualTimeSeriesMetaInformation is properly created with secondary constructor"() {
        given:
        def metaInf = new IndividualTimeSeriesMetaInformation(randomUUID, columnScheme)

        when:
        def sqlMetaInf = new SqlConnector.SqlIndividualTimeSeriesMetaInformation(metaInf, tableName)

        then:
        sqlMetaInf.getUuid() == randomUUID
        sqlMetaInf.getColumnScheme() == columnScheme
        sqlMetaInf.getTableName() == tableName
    }

    def "SqlIndividualTimeSeriesMetaInformation returns a proper String representation"() {
        when:
        def sqlMetaInf = new SqlConnector.SqlIndividualTimeSeriesMetaInformation(randomUUID, columnScheme, tableName)

        then:
        sqlMetaInf.toString() ==
                "SqlIndividualTimeSeriesMetaInformation{uuid=80d1bbec-79df-4ae2-b60a-34a2c3f77408, " +
                "columnScheme=ACTIVE_POWER, tableName='its_p_80d1bbec-79df-4ae2-b60a-34a2c3f77408'}"
    }
}
