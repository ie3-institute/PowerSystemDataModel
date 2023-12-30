/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.test.common.GridTestData as gtd
import edu.ie3.test.common.SystemParticipantTestData as sptd
import spock.lang.Specification

class CsvTypeSourceTest extends Specification implements CsvTestDataMeta {

  def "A CsvTypeSource should read and handle valid 2W Transformer type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def transformer2WTypes = typeSource.transformer2WTypes
    def transformerToBeFound = transformer2WTypes.get(gtd.transformerTypeBtoD.uuid)
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
    operators.get(firstOperator.uuid) == firstOperator
    operators.get(secondOperator.uuid) == secondOperator
  }

  def "A CsvTypeSource should read and handle valid line type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def lineType = typeSource.lineTypes.get(gtd.lineTypeInputCtoD.uuid)
    lineType.id == gtd.lineTypeInputCtoD.id
    lineType.b == gtd.lineTypeInputCtoD.b
    lineType.g == gtd.lineTypeInputCtoD.g
    lineType.r == gtd.lineTypeInputCtoD.r
    lineType.x == gtd.lineTypeInputCtoD.x
    lineType.iMax == gtd.lineTypeInputCtoD.iMax
    lineType.vRated == gtd.lineTypeInputCtoD.vRated
  }

  def "A CsvTypeSource should read and handle valid 3W Transformer type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def transformer3WType = typeSource.transformer3WTypes.get(gtd.transformerTypeAtoBtoC.uuid)
    transformer3WType.id == gtd.transformerTypeAtoBtoC.id
    transformer3WType.sRatedA == gtd.transformerTypeAtoBtoC.sRatedA
    transformer3WType.sRatedB == gtd.transformerTypeAtoBtoC.sRatedB
    transformer3WType.sRatedC == gtd.transformerTypeAtoBtoC.sRatedC
    transformer3WType.vRatedA == gtd.transformerTypeAtoBtoC.vRatedA
    transformer3WType.vRatedB == gtd.transformerTypeAtoBtoC.vRatedB
    transformer3WType.vRatedC == gtd.transformerTypeAtoBtoC.vRatedC
    transformer3WType.rScA == gtd.transformerTypeAtoBtoC.rScA
    transformer3WType.rScB == gtd.transformerTypeAtoBtoC.rScB
    transformer3WType.rScC == gtd.transformerTypeAtoBtoC.rScC
    transformer3WType.xScA == gtd.transformerTypeAtoBtoC.xScA
    transformer3WType.xScB == gtd.transformerTypeAtoBtoC.xScB
    transformer3WType.xScC == gtd.transformerTypeAtoBtoC.xScC
    transformer3WType.gM == gtd.transformerTypeAtoBtoC.gM
    transformer3WType.bM == gtd.transformerTypeAtoBtoC.bM
    transformer3WType.dV == gtd.transformerTypeAtoBtoC.dV
    transformer3WType.dPhi == gtd.transformerTypeAtoBtoC.dPhi
    transformer3WType.tapNeutr == gtd.transformerTypeAtoBtoC.tapNeutr
    transformer3WType.tapMin == gtd.transformerTypeAtoBtoC.tapMin
    transformer3WType.tapMax == gtd.transformerTypeAtoBtoC.tapMax
  }

  def "A CsvTypeSource should read and handle valid bm type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def bmType = typeSource.bmTypes.get(sptd.bmTypeInput.uuid)
    bmType.id == sptd.bmTypeInput.id
    bmType.capex == sptd.bmTypeInput.capex
    bmType.opex == sptd.bmTypeInput.opex
    bmType.cosPhiRated == sptd.bmTypeInput.cosPhiRated
    bmType.activePowerGradient == sptd.bmTypeInput.activePowerGradient
    bmType.etaConv == sptd.bmTypeInput.etaConv
  }

  def "A CsvTypeSource should read and handle valid chp type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def chpType = typeSource.chpTypes.get(sptd.chpTypeInput.uuid)
    chpType.id == sptd.chpTypeInput.id
    chpType.capex == sptd.chpTypeInput.capex
    chpType.opex == sptd.chpTypeInput.opex
    chpType.etaEl == sptd.chpTypeInput.etaEl
    chpType.etaThermal == sptd.chpTypeInput.etaThermal
    chpType.sRated == sptd.chpTypeInput.sRated
    chpType.pThermal == sptd.chpTypeInput.pThermal
    chpType.pOwn == sptd.chpTypeInput.pOwn
  }

  def "A CsvTypeSource should read and handle valid hp type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def hpType = typeSource.hpTypes.get(sptd.hpTypeInput.uuid)
    hpType.id == sptd.hpTypeInput.id
    hpType.capex == sptd.hpTypeInput.capex
    hpType.opex == sptd.hpTypeInput.opex
    hpType.sRated == sptd.hpTypeInput.sRated
    hpType.cosPhiRated == sptd.hpTypeInput.cosPhiRated
    hpType.pThermal == sptd.hpTypeInput.pThermal
  }

  def "A CsvTypeSource should read and handle valid storage type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def storageType = typeSource.storageTypes.get(sptd.storageTypeInput.uuid)
    storageType.id == sptd.storageTypeInput.id
    storageType.capex == sptd.storageTypeInput.capex
    storageType.opex == sptd.storageTypeInput.opex
    storageType.eStorage == sptd.storageTypeInput.eStorage
    storageType.sRated == sptd.storageTypeInput.sRated
    storageType.cosPhiRated == sptd.storageTypeInput.cosPhiRated
    storageType.pMax == sptd.storageTypeInput.pMax
    storageType.activePowerGradient == sptd.storageTypeInput.activePowerGradient
    storageType.eta == sptd.storageTypeInput.eta
    storageType.dod == sptd.storageTypeInput.dod
    storageType.lifeTime == sptd.storageTypeInput.lifeTime
    storageType.lifeCycle == sptd.storageTypeInput.lifeCycle
  }

  def "A CsvTypeSource should read and handle valid wec type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def wecType = typeSource.wecTypes.get(sptd.wecType.uuid)
    wecType.id == sptd.wecType.id
    wecType.capex == sptd.wecType.capex
    wecType.opex == sptd.wecType.opex
    wecType.cosPhiRated == sptd.wecType.cosPhiRated
    wecType.etaConv == sptd.wecType.etaConv
    wecType.sRated == sptd.wecType.sRated
    wecType.rotorArea == sptd.wecType.rotorArea
    wecType.hubHeight == sptd.wecType.hubHeight
    wecType.cpCharacteristic == sptd.wecType.cpCharacteristic
    //check for the individual points
    if (wecType.cpCharacteristic.points.iterator().hasNext())
      wecType.cpCharacteristic.points.iterator().next() == sptd.wecType.cpCharacteristic.points.iterator().next()
  }

  def "A CsvTypeSource should read and handle valid ev type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    def evType = typeSource.evTypes.get(sptd.evTypeInput.uuid)
    evType.id == sptd.evTypeInput.id
    evType.capex == sptd.evTypeInput.capex
    evType.opex == sptd.evTypeInput.opex
    evType.eStorage == sptd.evTypeInput.eStorage
    evType.eCons == sptd.evTypeInput.eCons
    evType.sRated == sptd.evTypeInput.sRated
    evType.cosPhiRated == sptd.evTypeInput.cosPhiRated
  }
}
