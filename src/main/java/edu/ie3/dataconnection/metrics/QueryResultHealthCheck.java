/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics;

import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class QueryResultHealthCheck {

  private static Logger mainLogger = LogManager.getLogger("Main");

  private static final int numberOfNeighborNodes = 3;
  private static final List<String> neighborIds =
      Arrays.asList(
          "SO-·Horstmar/SA-30.0·sa_ds/SAA-1/AB-1",
          "SO-·Waltrup/KSO-30.0·",
          "SO-·Waltrup/KSO-30.0· (1)");

  private static final int numberOfSubnetNodes = 23;
  private static final int numberOfSubnetLines = 22;
  private static final int numberOfSubnetSwitches = 0;
  private static final int numberOfSubnetTrafo2Ws = 1;
  private static final int numberOfSubnetTrafo3Ws = 0;

  public static boolean check(Collection<NodeInput> nodes) {
    if (nodes.size() != numberOfNeighborNodes) return false;
    return nodes.stream().map(NodeInput::getId).allMatch(neighborIds::contains);
  }

  public static boolean check(Optional<AggregatedRawGridInput> optSubnet, int subnetID) {
    if (optSubnet.isEmpty()) {
      return false;
    }
    AggregatedRawGridInput subnet = optSubnet.get();
    if(subnet.getNodes().size() != numberOfSubnetNodes) return false;
    if(subnet.getLines().size() != numberOfSubnetLines) return false;
    if(subnet.getSwitches().size() != numberOfSubnetSwitches) return false;
    if(subnet.getTransformer2Ws().size() != numberOfSubnetTrafo2Ws) return false;
    if(subnet.getTransformer3Ws().size() != numberOfSubnetTrafo3Ws) return false;
    Object[] subnetIDs = subnet.getNodes().stream().map(NodeInput::getSubnet).distinct().toArray();
    if(subnetIDs.length != 1) return false;
    return subnetIDs[0].equals(subnetID);
  }
}
