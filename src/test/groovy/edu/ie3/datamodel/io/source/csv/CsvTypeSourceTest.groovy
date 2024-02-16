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
    with(transformer2WTypes.get(gtd.transformerTypeBtoD.uuid)) {
      id == gtd.transformerTypeBtoD.id
      rSc == gtd.transformerTypeBtoD.rSc
      xSc == gtd.transformerTypeBtoD.xSc
      sRated == gtd.transformerTypeBtoD.sRated
      vRatedA == gtd.transformerTypeBtoD.vRatedA
      vRatedB == gtd.transformerTypeBtoD.vRatedB
      gM == gtd.transformerTypeBtoD.gM
      bM == gtd.transformerTypeBtoD.bM
      dV == gtd.transformerTypeBtoD.dV
      dPhi == gtd.transformerTypeBtoD.dPhi
      tapSide == gtd.transformerTypeBtoD.tapSide
      tapNeutr == gtd.transformerTypeBtoD.tapNeutr
      tapMin == gtd.transformerTypeBtoD.tapMin
      tapMax == gtd.transformerTypeBtoD.tapMax
    }
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
    with(typeSource.lineTypes.get(gtd.lineTypeInputCtoD.uuid)) {
      id == gtd.lineTypeInputCtoD.id
      b == gtd.lineTypeInputCtoD.b
      g == gtd.lineTypeInputCtoD.g
      r == gtd.lineTypeInputCtoD.r
      x == gtd.lineTypeInputCtoD.x
      iMax == gtd.lineTypeInputCtoD.iMax
      vRated == gtd.lineTypeInputCtoD.vRated
    }
  }

  def "A CsvTypeSource should read and handle valid 3W Transformer type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    with(typeSource.transformer3WTypes.get(gtd.transformerTypeAtoBtoC.uuid)) {
      id == gtd.transformerTypeAtoBtoC.id
      sRatedA == gtd.transformerTypeAtoBtoC.sRatedA
      sRatedB == gtd.transformerTypeAtoBtoC.sRatedB
      sRatedC == gtd.transformerTypeAtoBtoC.sRatedC
      vRatedA == gtd.transformerTypeAtoBtoC.vRatedA
      vRatedB == gtd.transformerTypeAtoBtoC.vRatedB
      vRatedC == gtd.transformerTypeAtoBtoC.vRatedC
      rScA == gtd.transformerTypeAtoBtoC.rScA
      rScB == gtd.transformerTypeAtoBtoC.rScB
      rScC == gtd.transformerTypeAtoBtoC.rScC
      xScA == gtd.transformerTypeAtoBtoC.xScA
      xScB == gtd.transformerTypeAtoBtoC.xScB
      xScC == gtd.transformerTypeAtoBtoC.xScC
      gM == gtd.transformerTypeAtoBtoC.gM
      bM == gtd.transformerTypeAtoBtoC.bM
      dV == gtd.transformerTypeAtoBtoC.dV
      dPhi == gtd.transformerTypeAtoBtoC.dPhi
      tapNeutr == gtd.transformerTypeAtoBtoC.tapNeutr
      tapMin == gtd.transformerTypeAtoBtoC.tapMin
      tapMax == gtd.transformerTypeAtoBtoC.tapMax
    }
  }

  def "A CsvTypeSource should read and handle valid bm type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    with(typeSource.bmTypes.get(sptd.bmTypeInput.uuid)) {
      id == sptd.bmTypeInput.id
      capex == sptd.bmTypeInput.capex
      opex == sptd.bmTypeInput.opex
      cosPhiRated == sptd.bmTypeInput.cosPhiRated
      activePowerGradient == sptd.bmTypeInput.activePowerGradient
      etaConv == sptd.bmTypeInput.etaConv
    }
  }

  def "A CsvTypeSource should read and handle valid chp type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    with(typeSource.chpTypes.get(sptd.chpTypeInput.uuid)) {
      id == sptd.chpTypeInput.id
      capex == sptd.chpTypeInput.capex
      opex == sptd.chpTypeInput.opex
      etaEl == sptd.chpTypeInput.etaEl
      etaThermal == sptd.chpTypeInput.etaThermal
      sRated == sptd.chpTypeInput.sRated
      pThermal == sptd.chpTypeInput.pThermal
      pOwn == sptd.chpTypeInput.pOwn
    }
  }

  def "A CsvTypeSource should read and handle valid hp type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    with(typeSource.hpTypes.get(sptd.hpTypeInput.uuid)) {
      id == sptd.hpTypeInput.id
      capex == sptd.hpTypeInput.capex
      opex == sptd.hpTypeInput.opex
      sRated == sptd.hpTypeInput.sRated
      cosPhiRated == sptd.hpTypeInput.cosPhiRated
      pThermal == sptd.hpTypeInput.pThermal
    }
  }

  def "A CsvTypeSource should read and handle valid storage type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    with(typeSource.storageTypes.get(sptd.storageTypeInput.uuid)) {
      id == sptd.storageTypeInput.id
      capex == sptd.storageTypeInput.capex
      opex == sptd.storageTypeInput.opex
      eStorage == sptd.storageTypeInput.eStorage
      sRated == sptd.storageTypeInput.sRated
      cosPhiRated == sptd.storageTypeInput.cosPhiRated
      pMax == sptd.storageTypeInput.pMax
      activePowerGradient == sptd.storageTypeInput.activePowerGradient
      eta == sptd.storageTypeInput.eta
      dod == sptd.storageTypeInput.dod
      lifeTime == sptd.storageTypeInput.lifeTime
      lifeCycle == sptd.storageTypeInput.lifeCycle
    }
  }

  def "A CsvTypeSource should read and handle valid wec type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    with(typeSource.wecTypes.get(sptd.wecType.uuid)) {
      id == sptd.wecType.id
      capex == sptd.wecType.capex
      opex == sptd.wecType.opex
      cosPhiRated == sptd.wecType.cosPhiRated
      etaConv == sptd.wecType.etaConv
      sRated == sptd.wecType.sRated
      rotorArea == sptd.wecType.rotorArea
      hubHeight == sptd.wecType.hubHeight
      cpCharacteristic == sptd.wecType.cpCharacteristic
      //check for the individual points
      if (cpCharacteristic.points.iterator().hasNext()) {
        assert cpCharacteristic.points.iterator().next() == sptd.wecType.cpCharacteristic.points.iterator().next()
      }
    }
  }

  def "A CsvTypeSource should read and handle valid ev type file as expected"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, new FileNamingStrategy()))

    expect:
    with(typeSource.evTypes.get(sptd.evTypeInput.uuid)) {
      id == sptd.evTypeInput.id
      capex == sptd.evTypeInput.capex
      opex == sptd.evTypeInput.opex
      eStorage == sptd.evTypeInput.eStorage
      eCons == sptd.evTypeInput.eCons
      sRatedAC == sptd.evTypeInput.sRatedAC
      cosPhiRated == sptd.evTypeInput.cosPhiRated
      sRatedDC == sptd.evTypeInput.sRatedDC
    }
  }
}
