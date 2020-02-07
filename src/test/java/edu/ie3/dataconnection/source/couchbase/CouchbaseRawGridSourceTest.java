/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source.couchbase;

import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
import edu.ie3.dataconnection.source.GridTestEntityBuilder;
import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.SwitchInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CouchbaseRawGridSourceTest {

  private static AggregatedRawGridInput aggregatedRawGridInput;
  private CouchbaseRawGridSource src;

  @BeforeAll
  public static void setUpOnce() {
    CsvTypeSource.fillMaps();
    aggregatedRawGridInput = GridTestEntityBuilder.getAggregatedRawGridInput();
  }

  @BeforeEach
  void setUp() {
    CouchbaseConnector connector_in = new CouchbaseConnector();
    src = new CouchbaseRawGridSource(connector_in, "vn_simona");
  }

  @Test
  public void getNodes() {
    Collection<NodeInput> couchbaseNodes = src.getNodes();
    assertTrue(aggregatedRawGridInput.getNodes().containsAll(couchbaseNodes));
    assertTrue(couchbaseNodes.containsAll(aggregatedRawGridInput.getNodes()));
  }

  @Test
  public void getLines() {
    Collection<LineInput> couchbaseLines = src.getLines();
    assertTrue(aggregatedRawGridInput.getLines().containsAll(couchbaseLines));
    assertTrue(couchbaseLines.containsAll(aggregatedRawGridInput.getLines()));
  }

  @Test
  public void get2WTransformers() {
    Collection<Transformer2WInput> couchbaseTransformer2Ws = src.get2WTransformers();
    assertTrue(aggregatedRawGridInput.getTransformer2Ws().containsAll(couchbaseTransformer2Ws));
    assertTrue(couchbaseTransformer2Ws.containsAll(aggregatedRawGridInput.getTransformer2Ws()));
  }

  @Test
  public void get3WTransformers() {
    Collection<Transformer3WInput> couchbaseTransformer3Ws = src.get3WTransformers();
    assertTrue(aggregatedRawGridInput.getTransformer3Ws().containsAll(couchbaseTransformer3Ws));
    assertTrue(couchbaseTransformer3Ws.containsAll(aggregatedRawGridInput.getTransformer3Ws()));
  }

  @Test
  public void getSwitches() {
    Collection<SwitchInput> couchbaseSwitches = src.getSwitches();
    assertTrue(aggregatedRawGridInput.getSwitches().containsAll(couchbaseSwitches));
    assertTrue(couchbaseSwitches.containsAll(aggregatedRawGridInput.getSwitches()));
  }
}
