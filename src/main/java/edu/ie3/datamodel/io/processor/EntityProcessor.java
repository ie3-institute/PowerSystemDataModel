/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.models.UniqueEntity;

import java.beans.IntrospectionException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.ie3.datamodel.utils.FieldNameUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Internal API Interface for EntityProcessors. Main purpose is to 'de-serialize' models into a
 * fieldName to value representation to allow for an easy processing into a database or file sink
 * e.g. .csv
 *
 * @version 0.1
 * @since 31.01.20
 */
public abstract class EntityProcessor<T extends UniqueEntity> extends Processor<T> {

  public final Logger log = LogManager.getLogger(this.getClass());
  protected final String[] headerElements;
  protected final Map<String, Function<Object, Optional<Object>>> fieldNameToFunction;

  /**
   * Create a new EntityProcessor
   *
   * @param registeredClass the class the entity processor should be able to handle
   */
  public EntityProcessor(Class<? extends T> registeredClass) {
    super(registeredClass);
    fieldNameToFunction = putUuidFirst(buildFunctionMap(registeredClass));
    this.headerElements = fieldNameToFunction.keySet().toArray(new String[0]);
  }

  /**
   * Standard call to handle an entity
   *
   * @param entity the entity that should be 'de-serialized' into a map of fieldName to fieldValue
   * @return an optional Map with fieldName to fieldValue or an empty optional if an error occurred
   *     during processing
   */
  public Optional<LinkedHashMap<String, String>> handleEntity(T entity) {
    if (!registeredClass.equals(entity.getClass()))
      throw new EntityProcessorException(
          "Cannot process "
              + entity.getClass().getSimpleName()
              + ".class with this EntityProcessor. Please either provide an element of "
              + registeredClass.getSimpleName()
              + ".class or create a new processor for "
              + entity.getClass().getSimpleName()
              + ".class!");

    try {
      return Optional.of(processObject(entity, fieldNameToFunction));
    } catch (EntityProcessorException e) {
      logger.error("Cannot process the entity{}.", entity, e);
      return Optional.empty();
    }
  }

  protected <U> Map<String, Function<U, Optional<Object>>> buildFunctionMap(Class<? extends U> registeredClass) {
    Map<String, Function<U, Optional<Object>>> tempFieldNameToFunction;
    try {
      tempFieldNameToFunction = FieldNameUtil.mapFieldNameToFunctionWithExclusions(registeredClass).entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, entry -> ((Function<U, Optional<Object>>) entry.getValue())));
    } catch (IntrospectionException e) {
      tempFieldNameToFunction = Collections.emptyMap();
    }
    return tempFieldNameToFunction;
  }

  @Override
  public String[] getHeaderElements() {
    return headerElements;
  }
}
