package edu.ie3.datamodel.utils

import edu.ie3.datamodel.annotations.FieldName
import edu.ie3.datamodel.models.UniqueEntity
import edu.ie3.datamodel.models.input.AssetInput
import edu.ie3.datamodel.models.input.InputEntity
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel
import edu.ie3.test.common.GridTestData
import spock.lang.Specification
import tec.uom.se.ComparableQuantity

import javax.measure.quantity.ElectricPotential
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.function.Function
import java.util.stream.Collectors

class FieldNameUtilTest extends Specification{

    Class<NodeInput> nodeInputClass = NodeInput.class
    def nodeInputFieldNames = ["uuid", "id", "v_target", "slack", "geo_position", "subnet"]
    def nodeInputFields = [nodeInputClass.getDeclaredFields(), AssetInput.getDeclaredFields(), InputEntity.getDeclaredFields(), UniqueEntity.getDeclaredFields()]
            .flatten().stream().filter(field -> !((Field) field).isSynthetic()).collect(Collectors.toSet())
    def nodeInputNestedFieldNames = ["operator_id", "operator_uuid", "operates_from", "operates_until", "v_rated", "volt_lvl"]


    def "FieldNameUtil can extract all Fields of a class, including superclass fields, excluding nested fields" () {
        when:
        def extractedFields = FieldNameUtil.getAllFields(nodeInputClass)
        then:
        extractedFields == nodeInputFields
    }

    def "FieldNameUtil can map all fields with @FieldName to their name, excluding nested fields" () {
        when:
        def returnedFieldNames = FieldNameUtil.mapFieldToFieldName(nodeInputClass)
        then:
        returnedFieldNames.values().containsAll(nodeInputFieldNames)
        returnedFieldNames.keySet() == nodeInputFields.findAll(field -> ((Field)field).isAnnotationPresent(FieldName.class))
    }

    def "FieldNameUtil can transform a Method to a Function" () {
        given:
        Method subnetGetter = nodeInputClass.getMethod("getSubnet")
        NodeInput node = GridTestData.nodeA
        when:
        Function<NodeInput, Optional<Object>> subnetGetterFunction = FieldNameUtil.toFunction(subnetGetter)
        then:
        subnetGetterFunction != null
        subnetGetterFunction.apply(node).get() == node.invokeMethod("getSubnet", null)
    }

    def "FieldNameUtil can compose getter functions" () {
        given:
        Method voltLevelGetter = nodeInputClass.getMethod("getVoltLvl")
        Method vRatedGetter = VoltageLevel.getMethod("getNominalVoltage")
        Function<NodeInput, Optional<Object>> voltLevelGetterFunction = FieldNameUtil.toFunction(voltLevelGetter)
        Function<VoltageLevel, Optional<Object>> vRatedGetterFunction = FieldNameUtil.toFunction(vRatedGetter)
        NodeInput node = GridTestData.nodeA
        ComparableQuantity<ElectricPotential> vRated = node.getVoltLvl().getNominalVoltage()
        when:
        Function<NodeInput, Optional<Object>> composedFunction = FieldNameUtil.composeFunctions(voltLevelGetterFunction, vRatedGetterFunction)
        then:
        composedFunction.apply(node).get() == vRated
    }

    def "FieldNameUtil produces a map of FieldNames to GetterFunctions, including nested Fields for a class" () {
        given:
        NodeInput node = GridTestData.nodeA
        when:
        Map<String, Function<NodeInput, Optional<Object>>> fieldNameToFunction = FieldNameUtil.mapFieldNameToFunction(nodeInputClass)
        then:
        fieldNameToFunction.keySet().containsAll([nodeInputFieldNames, nodeInputNestedFieldNames].flatten())
        fieldNameToFunction.get("uuid").apply(node).get() == node.getUuid()
        fieldNameToFunction.get("id").apply(node).get() == node.getId()
        fieldNameToFunction.get("v_target").apply(node).get() == node.getvTarget()
        fieldNameToFunction.get("slack").apply(node).get() == node.isSlack()
        fieldNameToFunction.get("geo_position").apply(node).get() == node.getGeoPosition()
        fieldNameToFunction.get("subnet").apply(node).get() == node.getSubnet()
        fieldNameToFunction.get("operator_id").apply(node).get() == node.getOperator().getId()
        fieldNameToFunction.get("operator_uuid").apply(node).get() == node.getOperator().getUuid()
        fieldNameToFunction.get("operates_from").apply(node).get() == node.getOperationTime().getStartDate()
        fieldNameToFunction.get("operates_until").apply(node).get() == node.getOperationTime().getEndDate()
        fieldNameToFunction.get("v_rated").apply(node).get() == node.getVoltLvl().getNominalVoltage()
        fieldNameToFunction.get("volt_lvl").apply(node).get() == node.getVoltLvl().getId()
    }

}
