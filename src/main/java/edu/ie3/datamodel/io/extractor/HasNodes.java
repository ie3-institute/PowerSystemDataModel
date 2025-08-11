/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.models.input.NodeInput;
import java.util.List;

/**
 * Interface that should be implemented by all elements holding one or more {@link NodeInput}
 * elements and should be processable by the {@link Extractor}.
 */
public interface HasNodes extends NestedEntity {

  /**
   * Retrieves all nodes associated with this entity.
   *
   * @return a list of {@link NodeInput} representing all nodes.
   */
  List<NodeInput> allNodes();
}
