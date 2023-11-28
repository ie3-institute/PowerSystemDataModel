/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import java.util.*;
import java.util.stream.Collectors;

/** Represents the aggregation of raw grid elements (nodes, lines, transformers, switches) */
public class RawGridElements implements InputContainer<AssetInput> {
  /** Set of nodes in this grid */
  private final Set<NodeInput> nodes;
  /** Set of lines in this grid */
  private final Set<LineInput> lines;
  /** Set of two winding transformers in this grid */
  private final Set<Transformer2WInput> transformer2Ws;
  /** Set of three winding in this grid */
  private final Set<Transformer3WInput> transformer3Ws;
  /** Set of switches in this grid */
  private final Set<SwitchInput> switches;
  /** Measurement units in this grid */
  private final Set<MeasurementUnitInput> measurementUnits;

  public RawGridElements(
      Set<NodeInput> nodes,
      Set<LineInput> lines,
      Set<Transformer2WInput> transformer2Ws,
      Set<Transformer3WInput> transformer3Ws,
      Set<SwitchInput> switches,
      Set<MeasurementUnitInput> measurementUnits) {
    this.nodes = nodes;
    this.lines = lines;
    this.transformer2Ws = transformer2Ws;
    this.transformer3Ws = transformer3Ws;
    this.switches = switches;
    this.measurementUnits = measurementUnits;
  }

  /**
   * Combine different already existing containers
   *
   * @param rawGridElements Already existing containers
   */
  public RawGridElements(Collection<RawGridElements> rawGridElements) {
    this.nodes =
        rawGridElements.stream()
            .flatMap(rawElements -> rawElements.getNodes().stream())
            .collect(Collectors.toSet());
    this.lines =
        rawGridElements.stream()
            .flatMap(rawElements -> rawElements.getLines().stream())
            .collect(Collectors.toSet());
    this.transformer2Ws =
        rawGridElements.stream()
            .flatMap(rawElements -> rawElements.getTransformer2Ws().stream())
            .collect(Collectors.toSet());
    this.transformer3Ws =
        rawGridElements.stream()
            .flatMap(rawElements -> rawElements.getTransformer3Ws().stream())
            .collect(Collectors.toSet());
    this.switches =
        rawGridElements.stream()
            .flatMap(rawElements -> rawElements.getSwitches().stream())
            .collect(Collectors.toSet());
    this.measurementUnits =
        rawGridElements.stream()
            .flatMap(rawElements -> rawElements.getMeasurementUnits().stream())
            .collect(Collectors.toSet());
  }

  /**
   * Create an instance based on a list of {@link AssetInput} entities that are included in {@link
   * RawGridElements}
   *
   * @param rawGridElements list of grid elements this container instance should created from
   */
  public RawGridElements(List<AssetInput> rawGridElements) {

    /* init sets */
    this.nodes =
        rawGridElements.parallelStream()
            .filter(NodeInput.class::isInstance)
            .map(NodeInput.class::cast)
            .collect(Collectors.toSet());
    this.lines =
        rawGridElements.parallelStream()
            .filter(LineInput.class::isInstance)
            .map(LineInput.class::cast)
            .collect(Collectors.toSet());
    this.transformer2Ws =
        rawGridElements.parallelStream()
            .filter(Transformer2WInput.class::isInstance)
            .map(Transformer2WInput.class::cast)
            .collect(Collectors.toSet());
    this.transformer3Ws =
        rawGridElements.parallelStream()
            .filter(Transformer3WInput.class::isInstance)
            .map(Transformer3WInput.class::cast)
            .collect(Collectors.toSet());
    this.switches =
        rawGridElements.parallelStream()
            .filter(SwitchInput.class::isInstance)
            .map(SwitchInput.class::cast)
            .collect(Collectors.toSet());
    this.measurementUnits =
        rawGridElements.parallelStream()
            .filter(MeasurementUnitInput.class::isInstance)
            .map(MeasurementUnitInput.class::cast)
            .collect(Collectors.toSet());
  }

  @Override
  public final List<AssetInput> allEntitiesAsList() {
    List<AssetInput> allEntities = new ArrayList<>();
    allEntities.addAll(nodes);
    allEntities.addAll(lines);
    allEntities.addAll(transformer2Ws);
    allEntities.addAll(transformer3Ws);
    allEntities.addAll(switches);
    allEntities.addAll(measurementUnits);
    return Collections.unmodifiableList(allEntities);
  }

  @Override
  public RawGridElementsCopyBuilder copy() {
    return new RawGridElementsCopyBuilder(this);
  }

  /** @return unmodifiable ; of all three winding transformers in this grid */
  public Set<NodeInput> getNodes() {
    return Collections.unmodifiableSet(nodes);
  }

  /** @return unmodifiable Set of all lines in this grid */
  public Set<LineInput> getLines() {
    return Collections.unmodifiableSet(lines);
  }

  /** @return unmodifiable Set of all two winding transformers in this grid */
  public Set<Transformer2WInput> getTransformer2Ws() {
    return Collections.unmodifiableSet(transformer2Ws);
  }

  /** @return unmodifiable Set of all three winding transformers in this grid */
  public Set<Transformer3WInput> getTransformer3Ws() {
    return Collections.unmodifiableSet(transformer3Ws);
  }

  /** @return unmodifiable Set of all switches in this grid */
  public Set<SwitchInput> getSwitches() {
    return Collections.unmodifiableSet(switches);
  }

  /** @return unmodifiable Set of all measurement units in this grid */
  public Set<MeasurementUnitInput> getMeasurementUnits() {
    return Collections.unmodifiableSet(measurementUnits);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RawGridElements that)) return false;
    return nodes.equals(that.nodes)
        && lines.equals(that.lines)
        && transformer2Ws.equals(that.transformer2Ws)
        && transformer3Ws.equals(that.transformer3Ws)
        && switches.equals(that.switches)
        && measurementUnits.equals(that.measurementUnits);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nodes, lines, transformer2Ws, transformer3Ws, switches, measurementUnits);
  }

  /**
   * A builder pattern based approach to create copies of {@link RawGridElements} containers with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * RawGridElements}
   *
   * @version 3.1
   * @since 14.02.23
   */
  public static class RawGridElementsCopyBuilder implements InputContainerCopyBuilder<AssetInput> {
    private Set<NodeInput> nodes;
    private Set<LineInput> lines;
    private Set<Transformer2WInput> transformer2Ws;
    private Set<Transformer3WInput> transformer3Ws;
    private Set<SwitchInput> switches;
    private Set<MeasurementUnitInput> measurementUnits;

    /**
     * Constructor for {@link RawGridElementsCopyBuilder}
     *
     * @param rawGridElements instance of {@link RawGridElementsCopyBuilder}
     */
    protected RawGridElementsCopyBuilder(RawGridElements rawGridElements) {
      this.nodes = rawGridElements.getNodes();
      this.lines = rawGridElements.getLines();
      this.transformer2Ws = rawGridElements.getTransformer2Ws();
      this.transformer3Ws = rawGridElements.getTransformer3Ws();
      this.switches = rawGridElements.getSwitches();
      this.measurementUnits = rawGridElements.getMeasurementUnits();
    }

    /**
     * Method to alter {@link NodeInput}
     *
     * @param nodes set of altered nodes
     * @return this instance of {@link RawGridElementsCopyBuilder}
     */
    public RawGridElementsCopyBuilder nodes(Set<NodeInput> nodes) {
      this.nodes = nodes;
      return this;
    }

    /**
     * Method to alter {@link LineInput}
     *
     * @param lines set of altered lines
     * @return this instance of {@link RawGridElementsCopyBuilder}
     */
    public RawGridElementsCopyBuilder lines(Set<LineInput> lines) {
      this.lines = lines;
      return this;
    }

    /**
     * Method to alter {@link Transformer2WInput}
     *
     * @param transformer2Ws set of altered two winding transformers
     * @return this instance of {@link RawGridElementsCopyBuilder}
     */
    public RawGridElementsCopyBuilder transformers2Ws(Set<Transformer2WInput> transformer2Ws) {
      this.transformer2Ws = transformer2Ws;
      return this;
    }

    /**
     * Method to alter {@link Transformer3WInput}
     *
     * @param transformer3Ws set of altered three winding trnasformers
     * @return this instance of {@link RawGridElementsCopyBuilder}
     */
    public RawGridElementsCopyBuilder transformer3Ws(Set<Transformer3WInput> transformer3Ws) {
      this.transformer3Ws = transformer3Ws;
      return this;
    }

    /**
     * Method to alter {@link SwitchInput}
     *
     * @param switches set of altered switches
     * @return this instance of {@link RawGridElementsCopyBuilder}
     */
    public RawGridElementsCopyBuilder switches(Set<SwitchInput> switches) {
      this.switches = switches;
      return this;
    }

    /**
     * Method to alter {@link MeasurementUnitInput}
     *
     * @param measurementUnits set of altered measurement units
     * @return this instance of {@link RawGridElementsCopyBuilder}
     */
    public RawGridElementsCopyBuilder measurementUnits(Set<MeasurementUnitInput> measurementUnits) {
      this.measurementUnits = measurementUnits;
      return this;
    }

    @Override
    public RawGridElements build() {
      return new RawGridElements(
          nodes, lines, transformer2Ws, transformer3Ws, switches, measurementUnits);
    }
  }
}
