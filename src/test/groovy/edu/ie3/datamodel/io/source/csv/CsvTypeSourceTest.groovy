/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.datamodel.models.input.OperatorInput
import spock.lang.Specification
import edu.ie3.test.common.GridTestData as gtd
import edu.ie3.test.common.SystemParticipantTestData as sptd


class CsvTypeSourceTest extends Specification implements CsvTestDataMeta {

  def "A CsvTypeSource should read and handle valid 2W Transformer type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def transformer2WTypes = typeSource.transformer2WTypes
    def transformerToBeFound = transformer2WTypes.find {trafoType ->
      trafoType.uuid ==gtd.transformerTypeBtoD.uuid
    }
    transformerToBeFound.id == gtd.transformerTypeBtoD.id
    transformerToBeFound.rSc == gtd.transformerTypeBtoD.rSc
    transformerToBeFound.xSc == gtd.transformerTypeBtoD.xSc
    transformerToBeFound.sRated == gtd.transformerTypeBtoD.sRated
    transformerToBeFound.vRatedA == gtd.transformerTypeBtoD.vRatedA
    transformerToBeFound.vRatedB == gtd.transformerTypeBtoD.vRatedB
    transformerToBeFound.gM == gtd.transformerTypeBtoD.gM
    transformerToBeFound.bM == gtd.transformerTypeBtoD.bM
    transformerToBeFound.dV == gtd.transformerTypeBtoD.dV
    transformerToBeFound.dPhi == gtd.transformerTypeBtoD.dPhi
    transformerToBeFound.tapSide == gtd.transformerTypeBtoD.tapSide
    transformerToBeFound.tapNeutr == gtd.transformerTypeBtoD.tapNeutr
    transformerToBeFound.tapMin == gtd.transformerTypeBtoD.tapMin
    transformerToBeFound.tapMax == gtd.transformerTypeBtoD.tapMax
  }

  def "A CsvTypeSource should read and handle valid operator file as expected"() {
    given:
    def firstOperator = new OperatorInput(
        UUID.fromString("f15105c4-a2de-4ab8-a621-4bc98e372d92"), "Univ.-Prof. Dr. rer. hort. Klaus-Dieter Brokkoli")
    def secondOperator = new OperatorInput(
        UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator")
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def operators = typeSource.operators
    operators.first().uuid == firstOperator.uuid
    operators.first().id == firstOperator.id
    operators[1].uuid == secondOperator.uuid
    operators[1].id == secondOperator.id
  }

  def "A CsvTypeSource should read and handle valid line type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def lineTypes = typeSource.lineTypes
    lineTypes.first().uuid == gtd.lineTypeInputCtoD.uuid
    lineTypes.first().id == gtd.lineTypeInputCtoD.id
    lineTypes.first().b == gtd.lineTypeInputCtoD.b
    lineTypes.first().g == gtd.lineTypeInputCtoD.g
    lineTypes.first().r == gtd.lineTypeInputCtoD.r
    lineTypes.first().x == gtd.lineTypeInputCtoD.x
    lineTypes.first().iMax == gtd.lineTypeInputCtoD.iMax
    lineTypes.first().vRated == gtd.lineTypeInputCtoD.vRated
  }

  def "A CsvTypeSource should read and handle valid 3W Transformer type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def transformer3WTypes = typeSource.transformer3WTypes
    transformer3WTypes.first().uuid == gtd.transformerTypeAtoBtoC.uuid
    transformer3WTypes.first().id == gtd.transformerTypeAtoBtoC.id
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
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def bmTypes = typeSource.bmTypes
    bmTypes.first().uuid == sptd.bmTypeInput.uuid
    bmTypes.first().id == sptd.bmTypeInput.id
    bmTypes.first().capex == sptd.bmTypeInput.capex
    bmTypes.first().opex == sptd.bmTypeInput.opex
    bmTypes.first().cosPhiRated == sptd.bmTypeInput.cosPhiRated
    bmTypes.first().activePowerGradient == sptd.bmTypeInput.activePowerGradient
    bmTypes.first().etaConv == sptd.bmTypeInput.etaConv
  }

  def "A CsvTypeSource should read and handle valid chp type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def chpTypes = typeSource.chpTypes
    chpTypes.first().uuid == sptd.chpTypeInput.uuid
    chpTypes.first().id == sptd.chpTypeInput.id
    chpTypes.first().capex == sptd.chpTypeInput.capex
    chpTypes.first().opex == sptd.chpTypeInput.opex
    chpTypes.first().etaEl == sptd.chpTypeInput.etaEl
    chpTypes.first().etaThermal == sptd.chpTypeInput.etaThermal
    chpTypes.first().sRated == sptd.chpTypeInput.sRated
    chpTypes.first().pThermal == sptd.chpTypeInput.pThermal
    chpTypes.first().pOwn == sptd.chpTypeInput.pOwn
  }

  def "A CsvTypeSource should read and handle valid hp type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def hpTypes = typeSource.hpTypes
    hpTypes.first().uuid == sptd.hpTypeInput.uuid
    hpTypes.first().id == sptd.hpTypeInput.id
    hpTypes.first().capex == sptd.hpTypeInput.capex
    hpTypes.first().opex == sptd.hpTypeInput.opex
    hpTypes.first().sRated == sptd.hpTypeInput.sRated
    hpTypes.first().cosPhiRated == sptd.hpTypeInput.cosPhiRated
    hpTypes.first().pThermal == sptd.hpTypeInput.pThermal
  }

  def "A CsvTypeSource should read and handle valid storage type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def storageTypes = typeSource.storageTypes
    storageTypes.first().uuid == sptd.storageTypeInput.uuid
    storageTypes.first().id == sptd.storageTypeInput.id
    storageTypes.first().capex == sptd.storageTypeInput.capex
    storageTypes.first().opex == sptd.storageTypeInput.opex
    storageTypes.first().eStorage == sptd.storageTypeInput.eStorage
    storageTypes.first().sRated == sptd.storageTypeInput.sRated
    storageTypes.first().cosPhiRated == sptd.storageTypeInput.cosPhiRated
    storageTypes.first().pMax == sptd.storageTypeInput.pMax
    storageTypes.first().activePowerGradient == sptd.storageTypeInput.activePowerGradient
    storageTypes.first().eta == sptd.storageTypeInput.eta
  }

  def "A CsvTypeSource should read and handle valid wec type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def wecTypes = typeSource.wecTypes
    wecTypes.first().uuid == sptd.wecType.uuid
    wecTypes.first().id == sptd.wecType.id
    wecTypes.first().capex == sptd.wecType.capex
    wecTypes.first().opex == sptd.wecType.opex
    wecTypes.first().cosPhiRated == sptd.wecType.cosPhiRated
    wecTypes.first().etaConv == sptd.wecType.etaConv
    wecTypes.first().sRated == sptd.wecType.sRated
    wecTypes.first().rotorArea == sptd.wecType.rotorArea
    wecTypes.first().hubHeight == sptd.wecType.hubHeight
    wecTypes.first().cpCharacteristic == sptd.wecType.cpCharacteristic
    //check for the individual points
    if (wecTypes.first().cpCharacteristic.points.iterator().hasNext())
      wecTypes.first().cpCharacteristic.points.iterator().next() == sptd.wecType.cpCharacteristic.points.iterator().next()
  }

  def "A CsvTypeSource should read and handle valid ev type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def evTypes = typeSource.evTypes
    evTypes.first().uuid == sptd.evTypeInput.uuid
    evTypes.first().id == sptd.evTypeInput.id
    evTypes.first().capex == sptd.evTypeInput.capex
    evTypes.first().opex == sptd.evTypeInput.opex
    evTypes.first().eStorage == sptd.evTypeInput.eStorage
    evTypes.first().eCons == sptd.evTypeInput.eCons
    evTypes.first().sRated == sptd.evTypeInput.sRated
    evTypes.first().cosPhiRated == sptd.evTypeInput.cosPhiRated
  }
}
