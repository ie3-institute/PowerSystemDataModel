/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source.neo4J;

import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ie3.dataconnection.dataconnectors.Neo4JConnector;
import edu.ie3.dataconnection.source.GridTestEntityBuilder;
import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import edu.ie3.dataconnection.source.neo4j.Neo4JRawGridSource;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.SwitchInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Neo4jRawGridSourceTest {

  private static AggregatedRawGridInput aggregatedRawGridInput;
  private Neo4JRawGridSource src;

  @BeforeAll
  public static void setUpOnce() {
    CsvTypeSource.fillMaps();
    aggregatedRawGridInput = GridTestEntityBuilder.getAggregatedRawGridInput();
  }

  @BeforeEach
  void setUp() {
    Neo4JConnector connector = new Neo4JConnector();
    src = new Neo4JRawGridSource(connector);
  }

  @Test
  public void getNodes() {
    Collection<NodeInput> neo4JNodes = src.getNodes();
    assertTrue(aggregatedRawGridInput.getNodes().containsAll(neo4JNodes));
    assertTrue(neo4JNodes.containsAll(aggregatedRawGridInput.getNodes()));
  }

  @Test
  public void getLines() {
    Collection<LineInput> neo4JLines = src.getLines();
    assertTrue(aggregatedRawGridInput.getLines().containsAll(neo4JLines));
    assertTrue(neo4JLines.containsAll(aggregatedRawGridInput.getLines()));
  }

  @Test
  public void get2WTransformers() {
    Collection<Transformer2WInput> neo4JTransformer2Ws = src.get2WTransformers();
    assertTrue(aggregatedRawGridInput.getTransformer2Ws().containsAll(neo4JTransformer2Ws));
    assertTrue(neo4JTransformer2Ws.containsAll(aggregatedRawGridInput.getTransformer2Ws()));
  }

  @Test
  public void get3WTransformers() {
    Collection<Transformer3WInput> neo4JTransformer3Ws = src.get3WTransformers();
    assertTrue(aggregatedRawGridInput.getTransformer3Ws().containsAll(neo4JTransformer3Ws));
    assertTrue(neo4JTransformer3Ws.containsAll(aggregatedRawGridInput.getTransformer3Ws()));
  }

  @Test
  public void getSwitches() {
    Collection<SwitchInput> neo4JSwitches = src.getSwitches();
    assertTrue(aggregatedRawGridInput.getSwitches().containsAll(neo4JSwitches));
    assertTrue(neo4JSwitches.containsAll(aggregatedRawGridInput.getSwitches()));
  }
}
