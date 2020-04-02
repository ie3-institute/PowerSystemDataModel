/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.exceptions.ExtractorException;
import edu.ie3.datamodel.models.Operable;
import edu.ie3.datamodel.models.input.InputEntity;
import java.util.*;
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
    throw new IllegalStateException("Don't instantiate a utility class");
  }

  public static List<InputEntity> extractElements(NestedEntity nestedEntity)
      throws ExtractorException {
    List<InputEntity> resultingList = new ArrayList<>();
    if (nestedEntity instanceof HasNodes) {
      resultingList.addAll(((HasNodes) nestedEntity).allNodes());
    }
    if (nestedEntity instanceof HasType) {
      resultingList.add(((HasType) nestedEntity).getType());
    }
    if (nestedEntity instanceof Operable) {
      resultingList.add(((Operable) nestedEntity).getOperator());
    }

    if (nestedEntity instanceof HasBus) {
      resultingList.add(((HasBus) nestedEntity).getBus());
    }

    if (nestedEntity instanceof HasLine) {
      resultingList.add(((HasLine) nestedEntity).getLine());
    }

    if (resultingList.contains(null)) {
      log.warn(
          "Entity of class '{}' contains null values in fields!",
          nestedEntity.getClass().getSimpleName());
    }

    if (resultingList.isEmpty()) {
      throw new ExtractorException(
          "Unable to extract entity of class '"
              + nestedEntity.getClass().getSimpleName()
              + "'. Does this class implements "
              + NestedEntity.class.getSimpleName()
              + " and one of its "
              + "sub-interfaces correctly?");
    }

    return Collections.unmodifiableList(resultingList);
  }
}
