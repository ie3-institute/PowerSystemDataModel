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
import java.util.Optional;
import java.util.Set;

/**
 * Interface that provides the capability to build entities of type {@link
 * edu.ie3.datamodel.models.input.graphics.GraphicInput} from different data sources e.g. .csv files
 * or databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public interface GraphicSource extends DataSource {

  /**
   * Should return either a consistent instance of {@link GraphicElements} wrapped in {@link
   * Optional} or an empty {@link Optional}. The decision to use {@link Optional} instead of
   * returning the {@link GraphicElements} instance directly is motivated by the fact, that a {@link
   * GraphicElements} is a container instance that depends on several other entities. Without being
   * complete, it is useless for further processing. Hence, whenever at least one entity {@link
   * GraphicElements} depends on cannot be provided, {@link Optional#empty()} should be returned and
   * extensive logging should provide enough information to debug the error and fix the persistent
   * data that has been failed to processed.
   *
   * <p>Furthermore, it is expected, that the specific implementation of this method ensures not
   * only the completeness of the resulting {@link GraphicElements} instance, but also its validity
   * e.g. in the sense that not duplicate UUIDs exist within all entities contained in the returning
   * instance.
   *
   * @return either a valid, complete {@link GraphicElements} optional or {@link Optional#empty()}
   */
  Optional<GraphicElements> getGraphicElements();

  /**
   * Returns a set of {@link NodeGraphicInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link NodeGraphicInput} which has to be checked manually, as {@link
   * NodeGraphicInput#equals(Object)} is NOT restricted on the uuid of {@link NodeGraphicInput}.
   *
   * @return a set of object and uuid unique {@link NodeGraphicInput} entities
   */
  Set<NodeGraphicInput> getNodeGraphicInput();

  /**
   * Returns a set of {@link NodeGraphicInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link NodeGraphicInput} which has to be checked manually, as {@link
   * NodeGraphicInput#equals(Object)} is NOT restricted on the uuid of {@link NodeGraphicInput}.
   *
   * <p>In contrast to {@link this#getNodeGraphicInput()} this interface provides the ability to
   * pass in an already existing set of {@link NodeInput} entities, the {@link NodeGraphicInput}
   * instances depend on. Doing so, already loaded nodes can be recycled to improve performance and
   * prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param nodes a set of object and uuid unique nodes that should be used for the returning
   *     instances
   * @return a set of object and uuid unique {@link NodeGraphicInput} entities
   */
  Set<NodeGraphicInput> getNodeGraphicInput(Set<NodeInput> nodes);

  /**
   * Returns a set of {@link LineGraphicInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link LineGraphicInput} which has to be checked manually, as {@link
   * LineGraphicInput#equals(Object)} is NOT restricted on the uuid of {@link LineGraphicInput}.
   *
   * @return a set of object and uuid unique {@link LineGraphicInput} entities
   */
  Set<LineGraphicInput> getLineGraphicInput();

  /**
   * Returns a set of {@link LineGraphicInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link LineGraphicInput} which has to be checked manually, as {@link
   * LineGraphicInput#equals(Object)} is NOT restricted on the uuid of {@link LineGraphicInput}.
   *
   * <p>In contrast to {@link this#getLineGraphicInput()} this interface provides the ability to
   * pass in an already existing set of {@link LineInput} entities, the {@link LineGraphicInput}
   * instances depend on. Doing so, already loaded nodes can be recycled to improve performance and
   * prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param lines a set of object and uuid unique lines that should be used for the returning
   *     instances
   * @return a set of object and uuid unique {@link LineGraphicInput} entities
   */
  Set<LineGraphicInput> getLineGraphicInput(Set<LineInput> lines);
}
