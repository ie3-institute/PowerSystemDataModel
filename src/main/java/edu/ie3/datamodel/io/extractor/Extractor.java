/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.exceptions.ExtractorException;
import edu.ie3.datamodel.models.Operable;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.UniqueInputEntity;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple utility class that can be used by sinks to extract nested elements (e.g. nodes, types)
 * that should be persisted.
 *
 * @version 0.1
 * @since 31.03.20
 */
public final class Extractor {

  private static final Logger log = LoggerFactory.getLogger(Extractor.class);

  private Extractor() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Extract elements set.
   *
   * @param nestedEntity the nested entity
   * @return the set
   * @throws ExtractorException the extractor exception
   */
  public static Set<UniqueInputEntity> extractElements(NestedEntity nestedEntity)
      throws ExtractorException {
    CopyOnWriteArrayList<UniqueInputEntity> resultingList = new CopyOnWriteArrayList<>();
    if (nestedEntity instanceof HasNodes nestedHasNode) {
      resultingList.addAll(nestedHasNode.allNodes());
    }
    if (nestedEntity instanceof Operable nestedOperable) {
      extractOperator(nestedOperable).ifPresent(resultingList::add);
    }
    if (nestedEntity instanceof HasType nestedHasType) {
      resultingList.add(extractType(nestedHasType));
    }
    if (nestedEntity instanceof HasThermalBus nestedHasThermalBus) {
      resultingList.add(nestedHasThermalBus.getThermalBus());
    }
    if (nestedEntity instanceof HasThermalStorage nestedHasThermalStorage) {
      resultingList.add(nestedHasThermalStorage.getThermalStorage());
    }
    if (nestedEntity instanceof HasLine nestedHasLine) {
      resultingList.add(nestedHasLine.getLine());
    }
    if (nestedEntity instanceof HasEm nestedHasEms) {
      nestedHasEms.getControllingEm().ifPresent(resultingList::add);
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
              if (element instanceof NestedEntity nestedElement) {
                try {
                  resultingList.addAll(extractElements(nestedElement));
                } catch (ExtractorException e) {
                  log.error(
                      "An error occurred during extraction of nested entity '{}':{}",
                      element.getClass().getSimpleName(),
                      e);
                }
              }
            });

    return Set.copyOf(resultingList);
  }

  /**
   * Extract type asset type input.
   *
   * @param entityWithType the entity with type
   * @return the asset type input
   */
  public static AssetTypeInput extractType(HasType entityWithType) {
    return entityWithType.getType();
  }

  /**
   * Extract operator optional.
   *
   * @param entityWithOperator the entity with operator
   * @return the optional
   */
  public static Optional<OperatorInput> extractOperator(Operable entityWithOperator) {
    return entityWithOperator.getOperator().getId().equalsIgnoreCase("NO_OPERATOR_ASSIGNED")
        ? Optional.empty()
        : Optional.of(entityWithOperator.getOperator());
  }
}
