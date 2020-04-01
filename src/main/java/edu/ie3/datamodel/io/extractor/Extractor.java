/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.exceptions.ExtractorException;
import edu.ie3.datamodel.models.input.InputEntity;
import java.util.*;

/**
 * A simple utility class that can be used by sinks to extract nested elements (e.g. nodes, types)
 * that should be persisted.
 *
 * @version 0.1
 * @since 31.03.20
 */
public final class Extractor {

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
    if (resultingList.isEmpty() || resultingList.contains(null)) {
      throw new ExtractorException(
          "Unable to extract entity of class '"
              + nestedEntity.getClass().getSimpleName()
              + "' "
              + ". Does this class implements "
              + NestedEntity.class.getSimpleName()
              + " and one of its "
              + "sub-interfaces correctly?");
    }

    return Collections.unmodifiableList(resultingList);
  }
}
