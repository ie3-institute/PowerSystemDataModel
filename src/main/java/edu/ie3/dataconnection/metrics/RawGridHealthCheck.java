/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics;

import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import edu.ie3.models.GermanVoltageLevel;
import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import java.util.UUID;
import tec.uom.se.quantity.Quantities;

public class RawGridHealthCheck {

  private static final int nodeCount = 1; // MIA
  private static final int lineCount = 1; // MIA
  private static final int switchCount = 1; // MIA
  private static final int trafo2wCount = 1; // MIA
  private static final int trafo3wCount = 1; // MIA
  private static final NodeInput exampleNodeA =
      new NodeInput(
          UUID.fromString("uuid"),
          OperationTime.builder().build(),
          null,
          "id",
          Quantities.getQuantity(0, StandardUnits.TARGET_VOLTAGE),
          Quantities.getQuantity(0, StandardUnits.V_RATED),
          false,
          CsvCoordinateSource.getCoordinate(11111),
          GermanVoltageLevel.LV,
          116);
  private static final NodeInput exampleNodeB =
      new NodeInput(
          UUID.fromString("uuid"),
          OperationTime.builder().build(),
          null,
          "id",
          Quantities.getQuantity(0, StandardUnits.TARGET_VOLTAGE),
          Quantities.getQuantity(0, StandardUnits.V_RATED),
          false,
          CsvCoordinateSource.getCoordinate(11111),
          GermanVoltageLevel.LV,
          116);
  private static final NodeInput exampleNodeC =
      new NodeInput(
          UUID.fromString("uuid"),
          OperationTime.builder().build(),
          null,
          "id",
          Quantities.getQuantity(0, StandardUnits.TARGET_VOLTAGE),
          Quantities.getQuantity(0, StandardUnits.V_RATED),
          false,
          CsvCoordinateSource.getCoordinate(11111),
          GermanVoltageLevel.LV,
          116);
  private static final Transformer3WInput exampleTransformer3W =
      new Transformer3WInput(
          UUID.fromString("uuid"),
          OperationTime.builder().build(),
          null,
          "id",
          exampleNodeA,
          exampleNodeB,
          exampleNodeC,
          1,
          CsvTypeSource.getTrafo3WType(111111),
          1,
          true);

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
