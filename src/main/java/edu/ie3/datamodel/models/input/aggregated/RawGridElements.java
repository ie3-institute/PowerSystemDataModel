/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.aggregated;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.utils.ValidationUtils;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Represents the aggregation of raw grid elements (nodes, lines, transformers, switches) */
public class RawGridElements implements AggregatedEntities {
  /** List of nodes in this grid */
  private final LinkedList<NodeInput> nodes = new LinkedList<>();
  /** List of lines in this grid */
  private final LinkedList<LineInput> lines = new LinkedList<>();
  /** List of two winding transformers in this grid */
  private final LinkedList<Transformer2WInput> transformer2Ws = new LinkedList<>();
  /** List of three winding in this grid */
  private final LinkedList<Transformer3WInput> transformer3Ws = new LinkedList<>();
  /** List of switches in this grid */
  private final LinkedList<SwitchInput> switches = new LinkedList<>();
  /** Measurement units in this grid */
  private final List<MeasurementUnitInput> measurementUnits = new LinkedList<>();

  @Override
  public void add(UniqueEntity entity) {
    if (entity instanceof MeasurementUnitInput) {
      add((MeasurementUnitInput) entity);
      return;
    }
    if (entity instanceof NodeInput) {
      add((NodeInput) entity);
      return;
    }
    if (entity instanceof LineInput) {
      add((LineInput) entity);
      return;
    }
    if (entity instanceof Transformer2WInput) {
      add((Transformer2WInput) entity);
      return;
    }
    if (entity instanceof Transformer3WInput) {
      add((Transformer3WInput) entity);
      return;
    }
    if (entity instanceof SwitchInput) {
      add((SwitchInput) entity);
      return;
    }
    throw new IllegalArgumentException(
        "Entity type is unknown, cannot add entity [" + entity + "]");
  }

  @Override
  public List<UniqueEntity> allEntitiesAsList() {
    List<UniqueEntity> allEntities = new LinkedList<>();
    allEntities.addAll(nodes);
    allEntities.addAll(lines);
    allEntities.addAll(transformer2Ws);
    allEntities.addAll(transformer3Ws);
    allEntities.addAll(switches);
    allEntities.addAll(measurementUnits);
    return Collections.unmodifiableList(allEntities);
  }

  @Override
  public boolean areValuesValid() {
    for (MeasurementUnitInput measurementUnit : measurementUnits) {
      if (!ValidationUtils.checkMeasurementUnit(measurementUnit)) return false;
    }
    for (NodeInput node : nodes) {
      if (!ValidationUtils.checkNode(node)) return false;
    }
    for (LineInput line : lines) {
      if (!ValidationUtils.checkLine(line)) return false;
    }
    for (Transformer2WInput transformer2W : transformer2Ws) {
      if (!ValidationUtils.checkTransformer2W(transformer2W)) return false;
    }
    for (Transformer3WInput transformer3W : transformer3Ws) {
      if (!ValidationUtils.checkTransformer3W(transformer3W)) return false;
    }
    for (SwitchInput switchInput : switches) {
      if (!ValidationUtils.checkConnector(switchInput)) return false;
    }
    return true;
  }

  public void add(NodeInput entity) {
    nodes.add(entity);
  }

  public void add(LineInput entity) {
    lines.add(entity);
  }

  public void add(Transformer2WInput entity) {
    transformer2Ws.add(entity);
  }

  public void add(Transformer3WInput entity) {
    transformer3Ws.add(entity);
  }

  public void add(SwitchInput entity) {
    switches.add(entity);
  }

  public void add(MeasurementUnitInput entity) {
    measurementUnits.add(entity);
  }

  /** @return unmodifiable List of all three winding transformers in this grid */
  public List<NodeInput> getNodes() {
    return Collections.unmodifiableList(nodes);
  }

  /** @return unmodifiable List of all lines in this grid */
  public List<LineInput> getLines() {
    return Collections.unmodifiableList(lines);
  }

  /** @return unmodifiable List of all two winding transformers in this grid */
  public List<Transformer2WInput> getTransformer2Ws() {
    return Collections.unmodifiableList(transformer2Ws);
  }

  /** @return unmodifiable List of all three winding transformers in this grid */
  public List<Transformer3WInput> getTransformer3Ws() {
    return Collections.unmodifiableList(transformer3Ws);
  }

  /** @return unmodifiable List of all switches in this grid */
  public List<SwitchInput> getSwitches() {
    return Collections.unmodifiableList(switches);
  }

  public List<MeasurementUnitInput> getMeasurementUnits() {
    return Collections.unmodifiableList(measurementUnits);
  }
}
