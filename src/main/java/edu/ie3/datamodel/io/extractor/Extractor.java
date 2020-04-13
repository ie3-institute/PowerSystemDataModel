/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.exceptions.ExtractorException;
import edu.ie3.datamodel.models.Operable;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple utility class that can be used by sinks to extract nested elements (e.g. nodes, types)
 * that should be persisted.
 *
 * @version 0.1
 * @since 31.03.20
 */
public final class Extractor {

  private static final Logger log = LogManager.getLogger(Extractor.class);

  private Extractor() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  public static List<InputEntity> extractElements(NestedEntity nestedEntity)
      throws ExtractorException {
    CopyOnWriteArrayList<InputEntity> resultingList = new CopyOnWriteArrayList<>();
    if (nestedEntity instanceof HasNodes) {
      resultingList.addAll(((HasNodes) nestedEntity).allNodes());
    }
    if (nestedEntity instanceof Operable) {
      extractOperator((Operable) nestedEntity).ifPresent(resultingList::add);
    }
    if (nestedEntity instanceof HasType) {
      resultingList.add(extractType((HasType) nestedEntity));
    }
    if (nestedEntity instanceof HasThermalBus) {
      resultingList.add(((HasThermalBus) nestedEntity).getThermalBus());
    }
    if (nestedEntity instanceof HasThermalStorage) {
      resultingList.add(((HasThermalStorage) nestedEntity).getThermalStorage());
    }
    if (nestedEntity instanceof HasLine) {
      resultingList.add(((HasLine) nestedEntity).getLine());
    }

    if (resultingList.contains(null)) {
      log.warn(
          "Entity of class '{}' contains null values in fields!",
          nestedEntity.getClass().getSimpleName());
    }

    if (resultingList.isEmpty() && !(nestedEntity instanceof Operable)) {
      throw new ExtractorException(
          "Unable to extract entity of class '"
              + nestedEntity.getClass().getSimpleName()
              + "'. Does this class implements "
              + NestedEntity.class.getSimpleName()
              + " and one of its "
              + "sub-interfaces correctly?");
    }

    resultingList.stream()
        .parallel()
        .forEach(
            element -> {
              if (element instanceof NestedEntity) {
                try {
                  resultingList.addAll(extractElements((NestedEntity) element));
                } catch (ExtractorException e) {
                  log.error(
                      "An error occurred during extraction of nested entity'"
                          + element.getClass().getSimpleName()
                          + "': ",
                      e);
                }
              }
            });

    return Collections.unmodifiableList(resultingList);
  }

  public static AssetTypeInput extractType(HasType entityWithType) {
    return entityWithType.getType();
  }

  public static Optional<OperatorInput> extractOperator(Operable entityWithOperator) {
    return entityWithOperator.getOperator().getId().equalsIgnoreCase("NO_OPERATOR_ASSIGNED")
        ? Optional.empty()
        : Optional.of(entityWithOperator.getOperator());
  }
}
