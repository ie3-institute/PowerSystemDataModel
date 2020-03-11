/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/** Offers functionality useful for grouping different models together */
public class ContainerUtils {
  private ContainerUtils() {
    throw new IllegalStateException("Don't try and instantiate a utility class");
  }

  /**
   * Filters all raw grid elements for the provided subnet. The equivalent nodes of transformers are
   * added as well. Two winding transformers are counted, if the low voltage node is in the queried
   * subnet. Three winding transformers are counted, as long as any of the three nodes is in the
   * queried subnet.
   *
   * <p>TODO: As objects now are immutable, no copies of the transformer nodes seem to be necessary.
   * If there is any cruel behaviour ongoing, check for this.
   *
   * @param input The model to filter
   * @param subnet The filter criterion
   * @return A {@link RawGridElements} filtered for the subnet
   */
  public static RawGridElements filterForSubnet(RawGridElements input, int subnet) {
    Set<NodeInput> nodes =
        input.getNodes().stream()
            .filter(node -> node.getSubnet() == subnet)
            .collect(Collectors.toSet());

    Set<LineInput> lines =
        input.getLines().stream()
            .filter(line -> line.getNodeB().getSubnet() == subnet)
            .collect(Collectors.toSet());

    Set<Transformer2WInput> transformer2w =
        input.getTransformer2Ws().stream()
            .filter(transformer -> transformer.getNodeB().getSubnet() == subnet)
            .collect(Collectors.toSet());
    /* Add the higher voltage node to the set of nodes */
    nodes.addAll(
        transformer2w.stream().map(Transformer2WInput::getNodeA).collect(Collectors.toSet()));

    Set<Transformer3WInput> transformer3w =
        input.getTransformer3Ws().stream()
            .filter(
                transformer ->
                    transformer.getNodeA().getSubnet() == subnet
                        || transformer.getNodeB().getSubnet() == subnet
                        || transformer.getNodeC().getSubnet() == subnet)
            .collect(Collectors.toSet());
    /* Add the higher voltage node to the set of nodes */
    nodes.addAll(
        transformer3w.stream()
            .map(Transformer3WInput::getNodeInternal)
            .collect(Collectors.toSet()));

    Set<SwitchInput> switches =
        input.getSwitches().stream()
            .filter(switcher -> switcher.getNodeB().getSubnet() == subnet)
            .collect(Collectors.toSet());

    Set<MeasurementUnitInput> measurements =
        input.getMeasurementUnits().stream()
            .filter(measurement -> measurement.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());

    return new RawGridElements(nodes, lines, transformer2w, transformer3w, switches, measurements);
  }

  /**
   * Filters all system participants for the provided subnet.
   *
   * <p>TODO: Currently electric vehicle charging systems have no nodal reference and therefore
   * cannot be filtered TODO: As objects now are immutable, no copies of the transformer nodes seem
   * to be necessary. If there is any cruel behaviour ongoing, check for this.
   *
   * @param input The model to filter
   * @param subnet The filter criterion
   * @return A {@link SystemParticipants} filtered for the subnet
   */
  public static SystemParticipants filterForSubnet(SystemParticipants input, int subnet) {
    Set<BmInput> bmPlants =
        input.getBmPlants().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<ChpInput> chpPlants =
        input.getChpPlants().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    /* Electric vehicle charging systems are currently dummy implementations without nodal reverence */
    Set<FixedFeedInInput> fixedFeedIns =
        input.getFixedFeedIns().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<HpInput> heatpumps =
        input.getHeatPumps().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<LoadInput> loads =
        input.getLoads().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<PvInput> pvs =
        input.getPvPlants().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<StorageInput> storages =
        input.getStorages().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<WecInput> wecPlants =
        input.getWecPlants().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());

    return new SystemParticipants(
        bmPlants,
        chpPlants,
        new HashSet<>(),
        fixedFeedIns,
        heatpumps,
        loads,
        pvs,
        storages,
        wecPlants);
  }
  /**
   * Filters all graphic elements for the provided subnet.
   *
   * <p>TODO: As objects now are immutable, no copies of the transformer nodes seem to be necessary.
   * If there is any cruel behaviour ongoing, check for this.
   *
   * @param input The model to filter
   * @param subnet The filter criterion
   * @return A {@link GraphicElements} filtered for the subnet
   */
  public static GraphicElements filterForSubnet(GraphicElements input, int subnet) {
    Set<NodeGraphicInput> nodeGraphics =
        input.getNodeGraphics().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<LineGraphicInput> lineGraphics =
        input.getLineGraphics().stream()
            .filter(entity -> entity.getLine().getNodeB().getSubnet() == subnet)
            .collect(Collectors.toSet());

    return new GraphicElements(nodeGraphics, lineGraphics);
  }
}
