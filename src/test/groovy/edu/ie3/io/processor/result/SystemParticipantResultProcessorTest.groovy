package edu.ie3.io.processor.result

import edu.ie3.exceptions.FactoryException
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.system.PvInput
import edu.ie3.models.result.system.*
import edu.ie3.util.TimeTools
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.Units

import javax.measure.Quantity
import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Power
import java.time.ZoneId

class SystemParticipantResultProcessorTest extends Specification {

    // initialize TimeTools for parsing
    def setupSpec() {
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
    }

    // static fields
    @Shared
    UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
    @Shared
    UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
    @Shared
    Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
    @Shared
    Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
    @Shared
    Quantity<Dimensionless> soc = Quantities.getQuantity(50, Units.PERCENT)
    @Shared
    def expectedStandardResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                                   inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                                   p         : '0.01',
                                   q         : '0.01',
                                   timestamp : '2020-01-30 17:26:44']

    @Shared
    def expectedSocResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                              inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                              p         : '0.01',
                              q         : '0.01',
                              soc       : '50.0',
                              timestamp : '2020-01-30 17:26:44']


    def "A SystemParticipantResultProcessor should de-serialize a provided SystemParticipantResult correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new SystemParticipantResultProcessor(modelClass)
        def validResult = validSystemParticipantResult

        when:
        def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

        then:
        validProcessedElement.present
        validProcessedElement.get() == expectedResults

        where:
        modelClass        | validSystemParticipantResult                                                                     || expectedResults
        LoadResult        | new LoadResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)         || expectedStandardResults
        FixedFeedInResult | new FixedFeedInResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)  || expectedStandardResults
        BmResult          | new BmResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)           || expectedStandardResults
        EvResult          | new EvResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q, soc)      || expectedSocResults
        PvResult          | new PvResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)           || expectedStandardResults
        EvcsResult        | new EvcsResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)         || expectedStandardResults
        ChpResult         | new ChpResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)          || expectedStandardResults
        WecResult         | new WecResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)          || expectedStandardResults
        StorageResult     | new StorageResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q, soc) || expectedSocResults

    }

    def "A SystemParticipantResultProcessor should de-serialize a provided SystemParticipantResult with null values correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new SystemParticipantResultProcessor(StorageResult)
        def storageResult = new StorageResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q, null)


        when:
        def validProcessedElement = sysPartResProcessor.handleEntity(storageResult)

        then:
        validProcessedElement.present
        validProcessedElement.get() == [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                                        inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                                        p         : '0.01',
                                        q         : '0.01',
                                        soc       : '',
                                        timestamp : '2020-01-30 17:26:44']

    }

    def "A SystemParticipantResultProcessor should throw an exception if the provided class is not registered"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new SystemParticipantResultProcessor(LoadResult)
        def storageResult = new StorageResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q, null)

        when:
        sysPartResProcessor.handleEntity(storageResult)

        then:
        FactoryException ex = thrown()
        ex.message == "Cannot process StorageResult.class with this EntityProcessor. Please either provide an element of LoadResult.class or create a new factory for StorageResult.class!"
    }


    def "A SystemParticipantResultProcessor should determine a ResultEntity correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new SystemParticipantResultProcessor(modelClass)

        expect:
        sysPartResProcessor.resultModel == isResultModel

        where:
        modelClass || isResultModel
        LoadResult || true
        PvInput    || false
    }

}
