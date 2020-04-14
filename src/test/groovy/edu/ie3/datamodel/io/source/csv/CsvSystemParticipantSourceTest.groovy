/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.ChpInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.HpInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.SystemParticipantTypedEntityData
import edu.ie3.datamodel.io.source.RawGridSource
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.ChpInput
import edu.ie3.datamodel.models.input.system.HpInput
import edu.ie3.test.common.SystemParticipantTestData as sptd
import spock.lang.Specification

class CsvSystemParticipantSourceTest extends Specification implements CsvTestDataMeta {

	// todo

	def "A CsvSystemParticipantSource should provide an instance of SystemParticipants based on valid input data correctly"() {
		// todo
	}

	def "A CsvSystemParticipantSource should process invalid input data as expected when requested to provide an instance of SystemParticipants"() {
		// todo
	}

	def "A CsvSystemParticipantSource should build typed entity from valid and invalid input data as expected"() {
		given:
		def csvSystemParticipantSource = new CsvSystemParticipantSource(csvSep,
				participantsFolderPath, fileNamingStrategy, Mock(CsvTypeSource),
				Mock(CsvThermalSource), Mock(CsvRawGridSource))

		def nodeAssetInputEntityData = new NodeAssetInputEntityData(fieldsToAttributes, clazz, operator, node)

		when:
		def typedEntityDataOpt = csvSystemParticipantSource.buildTypedEntityData(nodeAssetInputEntityData, types)

		then:
		typedEntityDataOpt.present == resultIsPresent
		typedEntityDataOpt.ifPresent({ typedEntityData ->
			assert (typedEntityData == resultData)
		})

		where:
		types               | node               | operator               | fieldsToAttributes                               | clazz    || resultIsPresent || resultData
		[]| sptd.chpInput.node | sptd.chpInput.operator | ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8"] | ChpInput || false           || null
		[sptd.chpTypeInput]| sptd.chpInput.node | sptd.chpInput.operator | ["bla": "foo"]                                   | ChpInput || false           || null
		[sptd.chpTypeInput]| sptd.chpInput.node | sptd.chpInput.operator | [:]                                              | ChpInput || false           || null
		[sptd.chpTypeInput]| sptd.chpInput.node | sptd.chpInput.operator | ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb9"] | ChpInput || false           || null
		[sptd.chpTypeInput]| sptd.chpInput.node | sptd.chpInput.operator | ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8"] | ChpInput || true            || new SystemParticipantTypedEntityData<>([:], clazz, operator, node, sptd.chpTypeInput)

	}

	def "A CsvSystemParticipantSource should build hp input entity from valid and invalid input data as expected"() {
		given:
		def csvSystemParticipantSource = new CsvSystemParticipantSource(csvSep,
				participantsFolderPath, fileNamingStrategy, Mock(CsvTypeSource),
				Mock(CsvThermalSource), Mock(CsvRawGridSource))

		def sysPartTypedEntityData = new SystemParticipantTypedEntityData<>(fieldsToAttributes, HpInput, sptd.hpInput.operator, sptd.hpInput.node, sptd.hpTypeInput)

		when:
		def hpInputEntityDataOpt = csvSystemParticipantSource.buildHpEntityData(sysPartTypedEntityData, thermalBuses)

		then:
		hpInputEntityDataOpt.present == resultIsPresent
		hpInputEntityDataOpt.ifPresent({ hpInputEntityData ->
			assert (hpInputEntityData == resultData)
		})

		where:
		thermalBuses              | fieldsToAttributes                                     || resultIsPresent || resultData
		[]| ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e"] || false           || null
		[sptd.hpInput.thermalBus]| ["bla": "foo"]                                         || false           || null
		[sptd.hpInput.thermalBus]| [:]                                                    || false           || null
		[sptd.hpInput.thermalBus]| ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384f"] || false           || null
		[sptd.hpInput.thermalBus]| ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e"] || true            || new HpInputEntityData([:], sptd.hpInput.operator, sptd.hpInput.node, sptd.hpTypeInput, sptd.hpInput.thermalBus)

	}

	def "A CsvSystemParticipantSource should build chp input entity from valid and invalid input data as expected"() {
		given:
		def csvSystemParticipantSource = new CsvSystemParticipantSource(csvSep,
				participantsFolderPath, fileNamingStrategy, Mock(CsvTypeSource),
				Mock(CsvThermalSource), Mock(CsvRawGridSource))

		def sysPartTypedEntityData = new SystemParticipantTypedEntityData<>(fieldsToAttributes, ChpInput, sptd.chpInput.operator, sptd.chpInput.node, sptd.chpTypeInput)

		when:
		def hpInputEntityDataOpt = csvSystemParticipantSource.buildChpEntityData(sysPartTypedEntityData, thermalStorages, thermalBuses)

		then:
		hpInputEntityDataOpt.present == resultIsPresent
		hpInputEntityDataOpt.ifPresent({ hpInputEntityData ->
			assert (hpInputEntityData == resultData)
		})

		where:
		thermalStorages               | thermalBuses               | fieldsToAttributes                                                                                               || resultIsPresent || resultData
		[] as List                    | [] as List                 | ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e", "thermalStorage": "8851813b-3a7d-4fee-874b-4df9d724e4b3"] || false           || null
		[sptd.chpInput.thermalStorage]| [sptd.chpInput.thermalBus]| ["bla": "foo"]                                                                                                   || false           || null
		[sptd.chpInput.thermalStorage]| [sptd.chpInput.thermalBus]| [:]                                                                                                              || false           || null
		[sptd.chpInput.thermalStorage]| [sptd.chpInput.thermalBus]| ["thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e", "thermalStorage": "8851813b-3a7d-4fee-874b-4df9d724e4b3"] || true            || new ChpInputEntityData([:], sptd.chpInput.operator, sptd.chpInput.node, sptd.chpTypeInput, sptd.chpInput.thermalBus, sptd.chpInput.thermalStorage)
	}


}
