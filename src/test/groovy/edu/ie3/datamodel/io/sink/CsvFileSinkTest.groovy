package edu.ie3.datamodel.io.sink

import edu.ie3.datamodel.exceptions.SinkException
import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.io.processor.ProcessorProvider
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.system.EvResult
import edu.ie3.datamodel.models.result.system.PvResult
import edu.ie3.datamodel.models.result.system.WecResult
import edu.ie3.util.TimeTools
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Power

// todo JH

class CsvFileSinkTest extends Specification {

    @Shared
    String testBaseFolderPath = "test"

    def cleanup() {

        // delete files after each test if they exist
        if (new File(testBaseFolderPath).exists()) {
            FileIOUtils.deleteRecursively(testBaseFolderPath)
        }
    }

    def "A valid CsvFileSink called by simple constructor should not initialize files by default and consist of several default values"() {
        given:
        CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath)

        expect:
        !new File(testBaseFolderPath).exists()
        csvFileSink.csvSep == ","

    }

    def "A valid CsvFileSink with 'initFiles' enabled should create files as expected"() {
        given:
        new CsvFileSink(testBaseFolderPath, new ProcessorProvider([new ResultEntityProcessor(PvResult), new ResultEntityProcessor(EvResult)]), new FileNamingStrategy(), true, ",")

        expect:
        new File(testBaseFolderPath).exists()
        new File(testBaseFolderPath + File.separator + "ev_res.csv").exists()
        new File(testBaseFolderPath + File.separator + "pv_res.csv").exists()

    }

    def "A valid CsvFileSink without 'initFiles' should only persist provided elements correctly but not all files"() {
        given:
        CsvFileSink sink = new CsvFileSink(testBaseFolderPath, new ProcessorProvider([new ResultEntityProcessor(PvResult), new ResultEntityProcessor(WecResult), new ResultEntityProcessor(EvResult)]), new FileNamingStrategy(), false, ",")

        UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
        UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
        Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
        Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
        PvResult pvResult = new PvResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)
        WecResult wecResult = new WecResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)

        when:
        sink.persistAll([pvResult, wecResult])

        then:
        new File(testBaseFolderPath).exists()
        new File(testBaseFolderPath + File.separator + "wec_res.csv").exists()
        new File(testBaseFolderPath + File.separator + "pv_res.csv").exists()

        !new File(testBaseFolderPath + File.separator + "ev_res.csv").exists()

    }

    def "A valid CsvFileSink should throw an exception if the provided entity cannot be handled"() {
        given:
        CsvFileSink sink = new CsvFileSink(testBaseFolderPath, new ProcessorProvider([new ResultEntityProcessor(PvResult)]), new FileNamingStrategy(), false, ",")

        UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
        UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
        Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
        Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
        WecResult wecResult = new WecResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)

        when:
        sink.persist(wecResult)

        then:
        thrown(SinkException)

    }


}
