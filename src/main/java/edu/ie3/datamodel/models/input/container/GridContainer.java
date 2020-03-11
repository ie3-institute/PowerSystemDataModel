/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.ContainerUtils;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridContainer implements InputContainer {
  private static Logger logger = LoggerFactory.getLogger(GridContainer.class);

  /** Name of this grid */
  protected final String gridName;
  /** Accumulated raw grid elements (lines, nodes, transformers, switches) */
  protected final RawGridElements rawGrid;
  /** Accumulated system participant elements */
  protected final SystemParticipants systemParticipants;
  /** Accumulated graphic data entities (node graphics, line graphics) */
  protected final GraphicElements graphics;
  /** A graph describing the subnet dependencies */
  private final ImmutableGraph<SubGridContainer> subnetDependencyGraph;

  public GridContainer(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      GraphicElements graphics) {
    this.gridName = gridName;

    this.rawGrid = rawGrid;
    if (!this.rawGrid.validate())
      throw new InvalidGridException(
          "You provided NULL as raw grid data for "
              + gridName
              + ". It has at least have to have nodes.");

    this.systemParticipants = systemParticipants;
    if (!this.systemParticipants.validate())
      logger.warn(
          "You provided NULL as system participants for {}, which doesn't make much sense...",
          gridName);

    this.graphics = graphics;
    if (!this.graphics.validate())
      logger.debug("No graphic information provided for {}.", gridName);

    /* Build sub grid dependency */
    this.subnetDependencyGraph = disassemble();
  }

  @Override
  public List<UniqueEntity> allEntitiesAsList() {
    List<UniqueEntity> allEntities = new LinkedList<>();
    allEntities.addAll(rawGrid.allEntitiesAsList());
    allEntities.addAll(systemParticipants.allEntitiesAsList());
    allEntities.addAll(graphics.allEntitiesAsList());
    return Collections.unmodifiableList(allEntities);
  }

  /**
   * @return true, as we are positive people and believe in what we do. Just kidding. Checks are
   *     made during initialisation.
   */
  @Override
  public boolean validate() {
    return true;
  }

  public String getGridName() {
    return gridName;
  }

  public RawGridElements getRawGrid() {
    return rawGrid;
  }

  public SystemParticipants getSystemParticipants() {
    return systemParticipants;
  }

  public GraphicElements getGraphics() {
    return graphics;
  }

  public ImmutableGraph<SubGridContainer> getSubnetDependencyGraph() {
    return subnetDependencyGraph;
  }

  /**
   * Determining the predominant voltage level in this grid by counting the occurrences of the
   * different voltage levels
   *
   * @param rawGrid Raw grid elements
   * @return The predominant voltage level in this grid
   * @throws InvalidGridException If not a single, predominant voltage level can be determined
   */
  protected static VoltageLevel determinePredominantVoltLvl(RawGridElements rawGrid) {
    return rawGrid.getNodes().stream()
        .map(NodeInput::getVoltLvl)
        .collect(Collectors.groupingBy(voltLvl -> voltLvl, Collectors.counting()))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElseThrow(
            () -> new InvalidGridException("Cannot determine the predominant voltage level."));
  }

  /**
   * Disassembles this grid model into sub grid models and returns a topology of the sub grids as a
   * directed, immutable graph. The direction points from higher to lower voltage level.
   *
   * @return An immutable, directed graph of sub grid topologies.
   */
  private ImmutableGraph<SubGridContainer> disassemble() {
    /* Collect the different sub nets. Through the validation of lines, it is ensured, no calvanically connected grid
     * has more than one subnet number assigned */
    SortedSet<Integer> subnetNumbers =
        rawGrid.getNodes().stream()
            .map(NodeInput::getSubnet)
            .collect(Collectors.toCollection(TreeSet::new));

    /* Build the single sub grid models */
    HashMap<Integer, SubGridContainer> subgrids = new HashMap<>(subnetNumbers.size());
    for (int subnetNumber : subnetNumbers) {
      RawGridElements rawGridElements = ContainerUtils.filterForSubnet(this.rawGrid, subnetNumber);
      SystemParticipants systemParticipantElements =
          ContainerUtils.filterForSubnet(this.systemParticipants, subnetNumber);
      GraphicElements graphicElements = ContainerUtils.filterForSubnet(this.graphics, subnetNumber);

      subgrids.put(
          subnetNumber,
          new SubGridContainer(
              this.gridName,
              subnetNumber,
              rawGridElements,
              systemParticipantElements,
              graphicElements));
    }

    /* Build the graph structure denoting the topology of the grid */
    ImmutableGraph.Builder<SubGridContainer> graphBuilder =
        GraphBuilder.directed().nodeOrder(ElementOrder.insertion()).immutable();
    /* Add connections of two winding transformers */
    for (Transformer2WInput transformer : this.rawGrid.getTransformer2Ws()) {
      SubGridContainer from = subgrids.get(transformer.getNodeA().getSubnet());
      SubGridContainer to = subgrids.get(transformer.getNodeB().getSubnet());
      if (from == null)
        throw new InvalidGridException(
            "Transformer "
                + transformer
                + " connects two sub grids, but the sub grid model "
                + transformer.getNodeA().getSubnet()
                + " cannot be found");
      if (to == null)
        throw new InvalidGridException(
            "Transformer "
                + transformer
                + " connects two sub grids, but the sub grid model "
                + transformer.getNodeB().getSubnet()
                + " cannot be found");
      graphBuilder.putEdge(from, to);
    }

    /* Add connections of three winding transformers */
    for (Transformer3WInput transformer : this.rawGrid.getTransformer3Ws()) {
      SubGridContainer from = subgrids.get(transformer.getNodeA().getSubnet());
      SubGridContainer to0 = subgrids.get(transformer.getNodeB().getSubnet());
      SubGridContainer to1 = subgrids.get(transformer.getNodeC().getSubnet());
      if (from == null)
        throw new InvalidGridException(
            "Transformer "
                + transformer
                + " connects two sub grids, but the sub grid model "
                + transformer.getNodeA().getSubnet()
                + " cannot be found");
      if (to0 == null)
        throw new InvalidGridException(
            "Transformer "
                + transformer
                + " connects two sub grids, but the sub grid model "
                + transformer.getNodeB().getSubnet()
                + " cannot be found");
      if (to1 == null)
        throw new InvalidGridException(
            "Transformer "
                + transformer
                + " connects two sub grids, but the sub grid model "
                + transformer.getNodeC().getSubnet()
                + " cannot be found");
      graphBuilder.putEdge(from, to0);
      graphBuilder.putEdge(from, to1);
    }

    return graphBuilder.build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GridContainer that = (GridContainer) o;
    return gridName.equals(that.gridName)
        && rawGrid.equals(that.rawGrid)
        && systemParticipants.equals(that.systemParticipants)
        && graphics.equals(that.graphics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gridName, rawGrid, systemParticipants, graphics);
  }

  @Override
  public String toString() {
    return "GridContainer{" + "gridName='" + gridName + '\'' + '}';
  }
}
