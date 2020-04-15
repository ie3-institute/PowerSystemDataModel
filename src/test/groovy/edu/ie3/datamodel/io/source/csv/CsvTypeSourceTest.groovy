/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.models.input.OperatorInput
import spock.lang.Specification
import edu.ie3.test.common.GridTestData as gtd
import edu.ie3.test.common.SystemParticipantTestData as sptd


class CsvTypeSourceTest extends Specification implements CsvTestDataMeta {

	def "A CsvTypeSource should read and handle valid 2W Transformer type file as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def transformer2WTypes = typeSource.transformer2WTypes
		transformer2WTypes.size() == 1
		transformer2WTypes.first().rSc == gtd.transformerTypeBtoD.rSc
		transformer2WTypes.first().xSc == gtd.transformerTypeBtoD.xSc
		transformer2WTypes.first().sRated == gtd.transformerTypeBtoD.sRated
		transformer2WTypes.first().vRatedA == gtd.transformerTypeBtoD.vRatedA
		transformer2WTypes.first().vRatedB == gtd.transformerTypeBtoD.vRatedB
		transformer2WTypes.first().gM == gtd.transformerTypeBtoD.gM
		transformer2WTypes.first().bM == gtd.transformerTypeBtoD.bM
		transformer2WTypes.first().dV == gtd.transformerTypeBtoD.dV
		transformer2WTypes.first().dPhi == gtd.transformerTypeBtoD.dPhi
		transformer2WTypes.first().tapSide == gtd.transformerTypeBtoD.tapSide
		transformer2WTypes.first().tapNeutr == gtd.transformerTypeBtoD.tapNeutr
		transformer2WTypes.first().tapMin == gtd.transformerTypeBtoD.tapMin
		transformer2WTypes.first().tapMax == gtd.transformerTypeBtoD.tapMax
	}

	def "A CsvTypeSource should read and handle valid operator file as expected"() {
		given:
		def operator = new OperatorInput(
				UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator")
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def operators = typeSource.operators
		operators.size() == 1
		operators.first().uuid == operator.uuid
		operators.first().id == operator.id
	}

	def "A CsvTypeSource should read and handle valid line type file as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def lineTypes = typeSource.lineTypes
		lineTypes.size() == 1
		lineTypes.first().b == gtd.lineTypeInputCtoD.b
		lineTypes.first().g == gtd.lineTypeInputCtoD.g
		lineTypes.first().r == gtd.lineTypeInputCtoD.r
		lineTypes.first().x == gtd.lineTypeInputCtoD.x
		lineTypes.first().iMax == gtd.lineTypeInputCtoD.iMax
		lineTypes.first().vRated == gtd.lineTypeInputCtoD.vRated
	}

	def "A CsvTypeSource should read and handle valid 3W Transformer type file as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def transformer3WTypes = typeSource.transformer3WTypes
		transformer3WTypes.size() == 1
		transformer3WTypes.first().sRatedA == gtd.transformerTypeAtoBtoC.sRatedA
		transformer3WTypes.first().sRatedB == gtd.transformerTypeAtoBtoC.sRatedB
		transformer3WTypes.first().sRatedC == gtd.transformerTypeAtoBtoC.sRatedC
		transformer3WTypes.first().vRatedA == gtd.transformerTypeAtoBtoC.vRatedA
		transformer3WTypes.first().vRatedB == gtd.transformerTypeAtoBtoC.vRatedB
		transformer3WTypes.first().vRatedC == gtd.transformerTypeAtoBtoC.vRatedC
		transformer3WTypes.first().rScA == gtd.transformerTypeAtoBtoC.rScA
		transformer3WTypes.first().rScB == gtd.transformerTypeAtoBtoC.rScB
		transformer3WTypes.first().rScC == gtd.transformerTypeAtoBtoC.rScC
		transformer3WTypes.first().xScA == gtd.transformerTypeAtoBtoC.xScA
		transformer3WTypes.first().xScB == gtd.transformerTypeAtoBtoC.xScB
		transformer3WTypes.first().xScC == gtd.transformerTypeAtoBtoC.xScC
		transformer3WTypes.first().gM == gtd.transformerTypeAtoBtoC.gM
		transformer3WTypes.first().bM == gtd.transformerTypeAtoBtoC.bM
		transformer3WTypes.first().dV == gtd.transformerTypeAtoBtoC.dV
		transformer3WTypes.first().dPhi == gtd.transformerTypeAtoBtoC.dPhi
		transformer3WTypes.first().tapNeutr == gtd.transformerTypeAtoBtoC.tapNeutr
		transformer3WTypes.first().tapMin == gtd.transformerTypeAtoBtoC.tapMin
		transformer3WTypes.first().tapMax == gtd.transformerTypeAtoBtoC.tapMax
	}

	def "A CsvTypeSource should read and handle valid bm type file as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def bmTypes = typeSource.bmTypes
		bmTypes.size() == 1
		bmTypes.first() == sptd.bmTypeInput
	}

	def "A CsvTypeSource should read and handle valid chp type file as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def chpTypes = typeSource.chpTypes
		chpTypes.size() == 1
		chpTypes.first() == sptd.chpTypeInput
	}

	def "A CsvTypeSource should read and handle valid hp type file as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def hpTypes = typeSource.hpTypes
		hpTypes.size() == 1
		hpTypes.first() == sptd.hpTypeInput
	}

	def "A CsvTypeSource should read and handle valid storage type file as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def storageTypes = typeSource.storageTypes
		storageTypes.size() == 1
		storageTypes.first() == sptd.storageTypeInput
	}

	def "A CsvTypeSource should read and handle valid wec type file as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def wecTypes = typeSource.wecTypes
		wecTypes.size() == 1
		//if (wecTypes.first().cpCharacteristic.points.iterator().hasNext())
		//wecTypes.first().cpCharacteristic.points.iterator().next() == sptd.wecType.cpCharacteristic.points.iterator().next()
		wecTypes.first() == sptd.wecType
	}

	def "A CsvTypeSource should read and handle valid ev type file as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def evTypes = typeSource.evTypes
		evTypes.size() == 1
		evTypes.first() == sptd.evTypeInput
	}
}
