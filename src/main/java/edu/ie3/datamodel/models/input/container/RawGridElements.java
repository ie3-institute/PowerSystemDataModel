/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.utils.ValidationUtils;
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

    // sanity check to ensure distinct UUIDs
    Optional<String> exceptionString =
        ValidationUtils.checkForDuplicateUuids(new HashSet<>(this.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      throw new InvalidGridException(
          "The provided entities in '"
              + this.getClass().getSimpleName()
              + "' contains duplicate UUIDs. "
              + "This is not allowed!\nDuplicated uuids:\n\n"
              + exceptionString);
    }
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

  @Override
  public List<AssetInput> allEntitiesAsList() {
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
  public void validate() {
    ValidationUtils.checkRawGridElements(this);
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
    if (o == null || getClass() != o.getClass()) return false;
    RawGridElements that = (RawGridElements) o;
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
}
