/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Interface that provides the capability to build entities of type {@link edu.ie3.datamodel.models.input.graphics.GraphicInput}
 * from different data sources e.g. .csv files or databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public interface GraphicSource extends DataSource {

  Optional<GraphicElements> getGraphicElements();

  Collection<NodeGraphicInput> getNodeGraphicInput();

  Collection<NodeGraphicInput> getNodeGraphicInput(Set<NodeInput> nodes);

  Collection<LineGraphicInput> getLineGraphicInput();

  Collection<LineGraphicInput> getLineGraphicInput(Set<LineInput> lines);
}
