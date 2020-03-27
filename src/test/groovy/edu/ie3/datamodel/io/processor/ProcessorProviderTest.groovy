package edu.ie3.datamodel.io.processor

import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.EvcsInput
import edu.ie3.datamodel.models.input.MeasurementUnitInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.RandomLoadParameters
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput
import edu.ie3.datamodel.models.input.system.BmInput
import edu.ie3.datamodel.models.input.system.ChpInput
import edu.ie3.datamodel.models.input.system.EvInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.HpInput
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.StorageInput
import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.system.characteristic.EvCharacteristicInput
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.datamodel.models.input.system.type.BmTypeInput
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput
import edu.ie3.datamodel.models.input.system.type.EvTypeInput
import edu.ie3.datamodel.models.input.system.type.HpTypeInput
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput
import edu.ie3.datamodel.models.input.system.type.WecTypeInput
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
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
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Power

class ProcessorProviderTest extends Specification {

    def "A ProcessorProvider should initialize all known EntityProcessors by default"() {
        given:
        ProcessorProvider provider = new ProcessorProvider()

        List knownProcessors = [
                /* InputEntity */
                OperatorInput,
                RandomLoadParameters,
                WecCharacteristicInput,
                EvCharacteristicInput,
                /* - AssetInput */
                NodeInput,
                LineInput,
                Transformer2WInput,
                Transformer3WInput,
                SwitchInput,
                MeasurementUnitInput,
                EvcsInput,
                ThermalBusInput,
                /* -- SystemParticipantInput */
                ChpInput,
                BmInput,
                EvInput,
                FixedFeedInInput,
                HpInput,
                LoadInput,
                PvInput,
                StorageInput,
                WecInput,
                /* -- ThermalUnitInput */
                ThermalHouseInput,
                CylindricalStorageInput,
                /* - GraphicInput */
                NodeGraphicInput,
                LineGraphicInput,
                /* - AssetTypeInput */
                BmTypeInput,
                ChpTypeInput,
                EvTypeInput,
                HpTypeInput,
                LineTypeInput,
                Transformer2WTypeInput,
                Transformer3WTypeInput,
                StorageTypeInput,
                WecTypeInput,
                /* ResultEntity */
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
                LoadResult,
                SwitchResult,
                NodeResult,
                ThermalHouseResult,
                CylindricalStorageResult
        ]
        // currently known processors

        expect:
        provider.registeredClasses.size() == knownProcessors.size()
        provider.registeredClasses.sort() == knownProcessors.sort()

    }

    def "A ProcessorProvider should return the header elements for a class known by one of its processors and do nothing otherwise"() {
        given:
        ProcessorProvider provider = new ProcessorProvider([new ResultEntityProcessor(PvResult), new ResultEntityProcessor(EvResult)])

        when:
        Optional headerResults = provider.getHeaderElements(PvResult)

        then:
        headerResults.present
        headerResults.get() == ["uuid", "inputModel", "p", "q", "timestamp"] as String[]

        when:
        headerResults = provider.getHeaderElements(WecResult)

        then:
        !headerResults.present

    }

    def "A ProcessorProvider should process an entity known by its underlying processors correctly and do nothing otherwise"() {
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
        Optional result = provider.processEntity(new WecResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q))

        then:
        !result.present

    }


}
