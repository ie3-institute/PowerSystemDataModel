/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.datamodel.io.factory.input.ThermalBusInputFactory
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.csv.CsvDataSource
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.test.common.SystemParticipantTestData as sptd
import edu.ie3.test.common.GridTestData as gtd

import spock.lang.Shared
import spock.lang.Specification

class EntitySourceTest extends Specification {

  private final class DummyEntitySource extends EntitySource {
    DummyEntitySource(CsvDataSource dataSource) {
      this.dataSource = dataSource
    }
  }

  @Shared
  String csvSep = ","
  @Shared
  String testBaseFolderPath = "testBaseFolderPath" // does not have to exist for this test
  @Shared
  FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()

  CsvDataSource csvDataSource = new CsvDataSource(csvSep, testBaseFolderPath, fileNamingStrategy)

  DummyEntitySource dummyEntitySource = new DummyEntitySource(csvDataSource)

  def "A csv data source is able to find the correct first entity by uuid"() {
    given:
    def uuid = UUID.randomUUID()
    def queriedOperator = new OperatorInput(uuid, "b")
    def entities = Arrays.asList(
        new OperatorInput(UUID.randomUUID(), "a"),
        queriedOperator,
        new OperatorInput(UUID.randomUUID(), "c")
        )

    when:
    def actual = dummyEntitySource.findFirstEntityByUuid(uuid.toString(), entities)

    then:
    actual.present
    actual.get() == queriedOperator
  }

  def "A CsvDataSource should always return an operator. Either the found one (if any) or OperatorInput.NO_OPERATOR_ASSIGNED"() {

    expect:
    dummyEntitySource.getFirstOrDefaultOperator(operators, operatorUuid, entityClassName, requestEntityUuid) == expectedOperator

    where:
    operatorUuid                           | operators                | entityClassName   | requestEntityUuid                      || expectedOperator
    "8f9682df-0744-4b58-a122-f0dc730f6510" | [sptd.hpInput.operator]  | "TestEntityClass" | "8f9682df-0744-4b58-a122-f0dc730f6511" || sptd.hpInput.operator
    "8f9682df-0744-4b58-a122-f0dc730f6520" | [sptd.hpInput.operator]  | "TestEntityClass" | "8f9682df-0744-4b58-a122-f0dc730f6511" || OperatorInput.NO_OPERATOR_ASSIGNED
    "8f9682df-0744-4b58-a122-f0dc730f6510" | []                       | "TestEntityClass" | "8f9682df-0744-4b58-a122-f0dc730f6511" || OperatorInput.NO_OPERATOR_ASSIGNED
  }

  def "A CsvDataSource should be able to handle the extraction process of an asset type correctly"() {
    when:
    def assetTypeOpt = dummyEntitySource.getAssetType(types, fieldsToAttributes, "TestClassName")

    then:
    assetTypeOpt.present == resultIsPresent
    assetTypeOpt.ifPresent({ assetType ->
      assert (assetType == resultData)
    })

    where:
    types                       | fieldsToAttributes                               || resultIsPresent || resultData
    []                          | ["type": "202069a7-bcf8-422c-837c-273575220c8a"] || false           || null
    []                          | ["bla": "foo"]                                   || false           || null
    [gtd.transformerTypeBtoD]   | ["type": "202069a7-bcf8-422c-837c-273575220c8a"] || true            || gtd.transformerTypeBtoD
    [sptd.chpTypeInput]         | ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8"] || true            || sptd.chpTypeInput
  }

  def "A CsvDataSource should not throw an exception but assume NO_OPERATOR_ASSIGNED if the operator field is missing in the headline"() {

    given:
    def thermalBusInputFieldsToAttributesMap = [
      "uuid"          : "0d95d7f2-49fb-4d49-8636-383a5220384e",
      "id"            : "test_thermalBusInput",
      "operatesuntil": "2020-03-25T15:11:31Z[UTC]",
      "operatesfrom" : "2020-03-24T15:11:31Z[UTC]"
    ]

    when:
    def thermalBusInputEntity = new ThermalBusInputFactory().get(dummyEntitySource.assetInputEntityDataStream(ThermalBusInput, thermalBusInputFieldsToAttributesMap, Collections.emptyList()))

    then:
    noExceptionThrown() // no NPE should be thrown
    thermalBusInputEntity.success
    thermalBusInputEntity.data().operator.id == OperatorInput.NO_OPERATOR_ASSIGNED.id // operator id should be set accordingly
  }
}
