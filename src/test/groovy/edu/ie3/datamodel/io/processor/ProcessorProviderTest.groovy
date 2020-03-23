package edu.ie3.datamodel.io.processor

import edu.ie3.datamodel.exceptions.ProcessorProviderException
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.connector.SwitchResult
import edu.ie3.datamodel.models.result.connector.Transformer2WResult
import edu.ie3.datamodel.models.result.connector.Transformer3WResult
import edu.ie3.datamodel.models.result.system.BmResult
import edu.ie3.datamodel.models.result.system.ChpResult
import edu.ie3.datamodel.models.result.system.EvResult
import edu.ie3.datamodel.models.result.system.EvcsResult
import edu.ie3.datamodel.models.result.system.FixedFeedInResult
import edu.ie3.datamodel.models.result.system.LoadResult
import edu.ie3.datamodel.models.result.system.PvResult
import edu.ie3.datamodel.models.result.system.StorageResult
import edu.ie3.datamodel.models.result.system.WecResult
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult
import edu.ie3.util.TimeTools
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Power

/**
 * //ToDo: Class Description JH
 * // todo JH
 *
 * @version 0.1* @since 23.03.20
 */
class ProcessorProviderTest extends Specification {

    def "A ProcessorProvider should initialize all known EntityProcessors by default"() {
        given:
        ProcessorProvider provider = new ProcessorProvider()

        // currently known processors
        List knownProcessors = [LoadResult,
                                FixedFeedInResult,
                                BmResult,
                                PvResult,
                                ChpResult,
                                WecResult,
                                StorageResult,
                                EvcsResult,
                                EvResult,
                                Transformer2WResult,
                                Transformer3WResult,
                                LineResult,
                                SwitchResult,
                                NodeResult,
                                ThermalHouseResult,
                                CylindricalStorageResult]

        expect:
        provider.getRegisteredClasses().size() == knownProcessors.size()
        provider.getRegisteredClasses().sort() == knownProcessors.sort()

    }

    def "A ProcessorProvider should return the header elements for a class known by one of its processors and throw an exception otherwise"() {
        given:
        ProcessorProvider provider = new ProcessorProvider([new ResultEntityProcessor(PvResult), new ResultEntityProcessor(EvResult)])

        when:
        String[] headerResults = provider.getHeaderElements(PvResult)

        then:
        headerResults == ["uuid", "inputModel", "p", "q", "timestamp"] as String[]

        when:
        provider.getHeaderElements(WecResult)

        then:
        thrown(ProcessorProviderException)

    }

    def "A ProcessorProvider should process an entity known by its underlying processors correctly and throw an exception otherwise"() {
        given:
        ProcessorProvider provider = new ProcessorProvider([new ResultEntityProcessor(PvResult), new ResultEntityProcessor(EvResult)])

        Map expectedMap = ["uuid"      : "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
                           "inputModel": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
                           "p"         : "0.01",
                           "q"         : "0.01",
                           "timestamp" : "2020-01-30 17:26:44"]

        when:
        UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
        UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
        Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
        Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
        PvResult pvResult = new PvResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)

        and:
        Optional processorResult = provider.processEntity(pvResult)

        then:
        processorResult.present
        Map resultMap = processorResult.get()
        resultMap.size() == 5
        resultMap == expectedMap

        when:
        provider.processEntity(new WecResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q))

        then:
        thrown(ProcessorProviderException)

    }


}
