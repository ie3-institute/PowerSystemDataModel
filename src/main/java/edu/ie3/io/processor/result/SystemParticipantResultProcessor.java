/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.processor.result;

import edu.ie3.exceptions.EntityProcessorException;
import edu.ie3.io.factory.result.SystemParticipantResultFactory;
import edu.ie3.io.processor.EntityProcessor;
import edu.ie3.models.result.system.SystemParticipantResult;

import javax.measure.Quantity;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * 'De-serializer' for {@link SystemParticipantResult}s into a fieldName -> value representation to
 * allow for an easy processing into a database or file sink e.g. .csv It is important that the
 * units used in this class are equal to the units used {@link SystemParticipantResultFactory} to
 * prevent invalid interpretation of unit prefixes!
 *
 * @version 0.1
 * @since 31.01.20
 */
public class SystemParticipantResultProcessor extends EntityProcessor<SystemParticipantResult> {

  public SystemParticipantResultProcessor(
      Class<? extends SystemParticipantResult> registeredClass) {
    super(registeredClass);
  }

  @Override
  protected Optional<LinkedHashMap<String, String>> processEntity(SystemParticipantResult entity) {

    Optional<LinkedHashMap<String, String>> resultMapOpt;

    try {
      LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
      for (String fieldName : headerElements) {
        Method method = fieldNameToMethod.get(fieldName);
        Optional<Object> methodReturnObjectOpt = Optional.ofNullable(method.invoke(entity));

        if (methodReturnObjectOpt.isPresent()) {
          resultMap.put(
              fieldName, processMethodResult(methodReturnObjectOpt.get(), method, fieldName));
        } else {
          resultMap.put(fieldName, "");
        }
      }
      resultMapOpt = Optional.of(resultMap);
    } catch (Exception e) {
      log.error("Error during entity processing in SystemParticipantResultProcessor:", e);
      resultMapOpt = Optional.empty();
    }
    return resultMapOpt;
  }

  private String processMethodResult(Object methodReturnObject, Method method, String fieldName) {

    StringBuilder resultStringBuilder = new StringBuilder();

    switch (method.getReturnType().getSimpleName()) {
        // primitives (Boolean, Character, Byte, Short, Integer, Long, Float, Double, String,
      case "UUID":
        resultStringBuilder.append(methodReturnObject.toString());
        break;
      case "Quantity":
        resultStringBuilder.append(
            processQuantity((Quantity<?>) methodReturnObject, fieldName, resultModel)
                .orElseThrow(
                    () ->
                        new EntityProcessorException(
                            "Unable to process value for attribute "
                                + fieldName
                                + " in system participant result model"
                                + getRegisteredClass().getSimpleName()
                                + ".class.")));
        break;
      case "ZonedDateTime":
        resultStringBuilder.append(processZonedDateTime((ZonedDateTime) methodReturnObject));
        break;
        // everything else (incl. unknown stuff)
      default:
        throw new EntityProcessorException(
            "Unable to process value for attribute/field "
                + fieldName
                + " and method return type "
                + method.getName()
                + " in system participant result model "
                + getRegisteredClass().getSimpleName()
                + ".class.");
    }

    return resultStringBuilder.toString();
  }
}
