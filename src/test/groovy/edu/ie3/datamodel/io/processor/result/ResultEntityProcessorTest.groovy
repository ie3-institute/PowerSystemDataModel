package edu.ie3.datamodel.io.processor.result

import edu.ie3.datamodel.exceptions.EntityProcessorException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.ResultEntity
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.connector.SwitchResult
import edu.ie3.datamodel.models.result.connector.Transformer2WResult
import edu.ie3.datamodel.models.result.connector.Transformer3WResult
import edu.ie3.datamodel.models.result.system.*
import edu.ie3.util.TimeTools
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.Units

import javax.measure.Quantity
import javax.measure.quantity.Angle
import javax.measure.quantity.Dimensionless
import javax.measure.quantity.ElectricCurrent
import javax.measure.quantity.Energy
import javax.measure.quantity.Power
import java.time.ZoneId
import java.time.ZonedDateTime

class ResultEntityProcessorTest extends Specification {

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


    def "A ResultEntityProcessor should de-serialize a provided SystemParticipantResult correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new ResultEntityProcessor(modelClass)
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

    def "A ResultEntityProcessor should de-serialize a provided SystemParticipantResult with null values correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new ResultEntityProcessor(StorageResult)
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

    def "A ResultEntityProcessor should throw an exception if the provided class is not registered"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new ResultEntityProcessor(LoadResult)
        def storageResult = new StorageResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q, null)

        when:
        sysPartResProcessor.handleEntity(storageResult)

        then:
        EntityProcessorException ex = thrown()
        ex.message == "Cannot process StorageResult.class with this EntityProcessor. Please either provide an element of LoadResult.class or create a new factory for StorageResult.class!"
    }

    def "A ResultEntityProcessor should de-serialize a NodeResult correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new ResultEntityProcessor(NodeResult)

        Quantity<Dimensionless> vMag = Quantities.getQuantity(0.95, PowerSystemUnits.PU)
        Quantity<Angle> vAng = Quantities.getQuantity(45, StandardUnits.VOLTAGE_ANGLE)

        def validResult = new NodeResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, vMag, vAng)

        def expectedResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                               inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                               vAng      : '45.0',
                               vMag      : '0.95',
                               timestamp : '2020-01-30 17:26:44']

        when:
        def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

        then:
        validProcessedElement.present
        validProcessedElement.get() == expectedResults

    }

    @Shared
    def expectedLineResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                               inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                               iAMag     : '100.0',
                               iAAng     : '45.0',
                               iBMag     : '150.0',
                               iBAng     : '30.0',
                               timestamp : '2020-01-30 17:26:44']

    @Shared
    def expectedTrafo2WResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                                  inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                                  iAMag     : '100.0',
                                  iAAng     : '45.0',
                                  iBMag     : '150.0',
                                  iBAng     : '30.0',
                                  tapPos    : '5',
                                  timestamp : '2020-01-30 17:26:44']


    @Shared
    def expectedTrafo3WResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                                  inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                                  iAMag     : '100.0',
                                  iAAng     : '45.0',
                                  iBMag     : '150.0',
                                  iBAng     : '30.0',
                                  iCMag     : '300.0',
                                  iCAng     : '70.0',
                                  tapPos    : '5',
                                  timestamp : '2020-01-30 17:26:44']

    @Shared
    def expectedSwitchResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                                 inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                                 iAMag     : '100.0',
                                 iAAng     : '45.0',
                                 iBMag     : '150.0',
                                 iBAng     : '30.0',
                                 closed    : 'true',
                                 timestamp : '2020-01-30 17:26:44']


    @Shared
    Quantity<ElectricCurrent> iAMag = Quantities.getQuantity(100, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
    @Shared
    Quantity<Angle> iAAng = Quantities.getQuantity(45, StandardUnits.ELECTRIC_CURRENT_ANGLE)
    @Shared
    Quantity<ElectricCurrent> iBMag = Quantities.getQuantity(150, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
    @Shared
    Quantity<Angle> iBAng = Quantities.getQuantity(30, StandardUnits.ELECTRIC_CURRENT_ANGLE)
    @Shared
    Quantity<ElectricCurrent> iCMag = Quantities.getQuantity(300, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
    @Shared
    Quantity<Angle> iCAng = Quantities.getQuantity(70, StandardUnits.ELECTRIC_CURRENT_ANGLE)
    @Shared
    int tapPos = 5
    @Shared
    boolean closed = true


    def "A ResultEntityProcessor should de-serialize all ConnectorResults correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new ResultEntityProcessor(modelClass)

        def validResult = validConnectorResult

        when:
        def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

        then:
        validProcessedElement.present
        validProcessedElement.get() == expectedResults

        where:
        modelClass          | validConnectorResult                                                                                                                          || expectedResults
        LineResult          | new LineResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, iAMag, iAAng, iBMag, iBAng)                                || expectedLineResults
        SwitchResult        | new SwitchResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, iAMag, iAAng, iBMag, iBAng, closed)                      || expectedSwitchResults
        Transformer2WResult | new Transformer2WResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, iAMag, iAAng, iBMag, iBAng, tapPos)               || expectedTrafo2WResults
        Transformer3WResult | new Transformer3WResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, iAMag, iAAng, iBMag, iBAng, iCMag, iCAng, tapPos) || expectedTrafo3WResults
    }

    def "A ResultEntityProcessor should de-serialize a CylindricalStorageResult correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new ResultEntityProcessor(CylindricalStorageResult)

        Quantity<Power> qDot = Quantities.getQuantity(2, StandardUnits.Q_DOT_RESULT)
        Quantity<Energy> energy = Quantities.getQuantity(3, StandardUnits.ENERGY_RESULT)
        Quantity<Dimensionless> fillLevel = Quantities.getQuantity(20, Units.PERCENT)

        def validResult = new CylindricalStorageResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, energy, qDot, fillLevel)

        def expectedResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                               energy    : '3.0',
                               fillLevel : '20.0',
                               inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
                               qDot      : '2.0',
                               timestamp : '2020-01-30 17:26:44']

        when:
        def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

        then:
        validProcessedElement.present
        validProcessedElement.get() == expectedResults

    }

    def "A ResultEntityProcessor should throw an EntityProcessorException when it receives an entity result that is not eligible"() {

        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new ResultEntityProcessor(ResultEntityProcessor.eligibleEntityClasses.get(0))

        def invalidClassResult = new InvalidTestResult(TimeTools.toZonedDateTime("2020-01-30 17:26:44"), uuid)

        when:
        sysPartResProcessor.handleEntity(invalidClassResult)

        then:
        final EntityProcessorException exception = thrown()
        exception.getMessage() == "Cannot process InvalidTestResult.class with this EntityProcessor. " +
                "Please either provide an element of LoadResult.class or create a new factory for InvalidTestResult.class!"

    }

    def "The list of eligible entity classes for a ResultEntityProcessor should be valid"() {
        given:
        int noOfElements = 16 // number of all currently implemented entity results

        expect:
        ResultEntityProcessor.eligibleEntityClasses.size() == noOfElements
    }

    private class InvalidTestResult extends ResultEntity {

        InvalidTestResult(ZonedDateTime timestamp, UUID inputModel) {
            super(timestamp, inputModel)
        }

        InvalidTestResult(UUID uuid, ZonedDateTime timestamp, UUID inputModel) {
            super(uuid, timestamp, inputModel)
        }
    }

}

