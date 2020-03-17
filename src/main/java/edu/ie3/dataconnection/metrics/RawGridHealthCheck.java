/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics;

import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import edu.ie3.models.GermanVoltageLevel;
import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import edu.ie3.utils.CoordinateUtils;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tec.uom.se.quantity.Quantities;

public class RawGridHealthCheck {

  private static Logger mainLogger = LogManager.getLogger("Main");

  private static final int nodeCount = 47813;
  private static final int lineCount = 47256;
  private static final int switchCount = 145;
  private static final int trafo2wCount = 564;
  private static final int trafo3wCount = 3;

  private static final NodeInput exampleNodeA =
      new NodeInput(
          UUID.fromString("3f8d2ce7-b0aa-3f43-bc00-4cd66269cd66"),
          OperationTime.builder().build(),
          null,
          "SO-·Metelen/SA-380.0·sa_es/SAA-1/SF-1/KS-2",
          Quantities.getQuantity(1, StandardUnits.TARGET_VOLTAGE),
          Quantities.getQuantity(380.0, StandardUnits.V_RATED),
          false,
          CoordinateUtils.xyCoordToPoint(7.23862445937951, 52.1535088229935),
          GermanVoltageLevel.EHV,
          1);
  private static final NodeInput exampleNodeB =
      new NodeInput(
          UUID.fromString("cd329a6f-0df6-3506-9011-072a7f50b97f"),
          OperationTime.builder().build(),
          null,
          "SO-·Metelen/SA-110.0·1/SAA-1/AB-SSB",
          Quantities.getQuantity(1, StandardUnits.TARGET_VOLTAGE),
          Quantities.getQuantity(110.0, StandardUnits.V_RATED),
          false,
          CoordinateUtils.xyCoordToPoint(7.23862445937951, 52.1535088229935),
          GermanVoltageLevel.HV,
          1001);
  private static final NodeInput exampleNodeC =
      new NodeInput(
          UUID.fromString("4acfcb2f-ce24-36d8-877e-9fc85df8efac"),
          OperationTime.builder().build(),
          null,
          "SO-·Metelen/SA-10.0·sa_es/SAA-1/SF-1/KS-2",
          Quantities.getQuantity(1, StandardUnits.TARGET_VOLTAGE),
          Quantities.getQuantity(10, StandardUnits.V_RATED),
          false,
          CoordinateUtils.xyCoordToPoint(7.23862445937951, 52.1535088229935),
          GermanVoltageLevel.MV,
          1013);
  private static final Transformer3WInput exampleTransformer3W =
      new Transformer3WInput(
          UUID.fromString("fbcba2f9-f8be-3cc9-bec4-1513affd9129"),
          OperationTime.builder().build(),
          null,
          "SO-·Metelen/T3-411",
          exampleNodeA,
          exampleNodeB,
          exampleNodeC,
          1,
          CsvTypeSource.getTrafo3WType(12217),
          0,
          false);

  public static boolean check(AggregatedRawGridInput gridData) {
    if (gridData == null) return false;
    if (gridData.getNodes().size() != nodeCount) return false;
    if (gridData.getLines().size() != lineCount) return false;
    if (gridData.getSwitches().size() != switchCount) return false;
    if (gridData.getTransformer2Ws().size() != trafo2wCount) return false;
    if (gridData.getTransformer3Ws().size() != trafo3wCount) return false;
    if (!gridData.getNodes().contains(exampleNodeA)) return false;
    if (!gridData.getNodes().contains(exampleNodeB)) return false;
    if (!gridData.getNodes().contains(exampleNodeC)) return false;
    return gridData.getTransformer3Ws().contains(exampleTransformer3W);
  }
}
