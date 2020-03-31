/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.models.input.NodeInput;
import java.util.List;

/**
 * //ToDo: Class Description // todo: UML
 *
 * @version 0.1
 * @since 31.03.20
 */
public interface Nodes extends Nested {

  List<NodeInput> getNodes();
}
