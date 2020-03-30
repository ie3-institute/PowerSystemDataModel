/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source.couchbase;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;
import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.dataconnection.source.RawGridSource;
import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.SwitchInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import edu.ie3.models.json.JsonMapper;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CouchbaseRawGridSource implements RawGridSource {

  private static Logger mainLogger = LogManager.getLogger("Main");

  private CouchbaseConnector connector;
  private String scenarioName;

  private AggregatedRawGridInput aggregatedRawGridInput = new AggregatedRawGridInput();
  private boolean fetchedNodes;
  private boolean fetched;
  private Map<Integer, NodeInput> idToNode = new HashMap<>();

  public CouchbaseRawGridSource(CouchbaseConnector connector, String scenarioName) {
    this.connector = connector;
    this.scenarioName = scenarioName;
    CsvTypeSource.fillMaps();
  }

  @Override
  public AggregatedRawGridInput getGridData() {
    if (!fetched) fetch();
    return aggregatedRawGridInput;
  }

  @Override
  public Collection<NodeInput> getNodes() {
    if (!fetched) fetch();
    return aggregatedRawGridInput.getNodes();
  }

  @Override
  public Collection<LineInput> getLines() {
    if (!fetched) fetch();
    return aggregatedRawGridInput.getLines();
  }

  @Override
  public Collection<Transformer2WInput> get2WTransformers() {
    if (!fetched) fetch();
    return aggregatedRawGridInput.getTransformer2Ws();
  }

  @Override
  public Collection<Transformer3WInput> get3WTransformers() {
    if (!fetched) fetch();
    return aggregatedRawGridInput.getTransformer3Ws();
  }

  @Override
  public Collection<SwitchInput> getSwitches() {

    if (!fetched) fetch();
    return aggregatedRawGridInput.getSwitches();
  }

  @Override
  public Collection<NodeInput> getNeighborNodesOfSubnet(Integer subnet) {
    // Abfrage nicht wirklich möglich
    return null;
  }

  @Override
  public Optional<AggregatedRawGridInput> getSubnet(Integer subnet) {
    AggregatedRawGridInput subnetInput = new AggregatedRawGridInput();
    String subnetKey = generateSubnetKey(subnet, "vn_simona");
    CompletableFuture<GetResult> resultFuture = connector.get(subnetKey);
    GetResult result = resultFuture.join();
    JsonObject jsonObject = result.contentAsObject();
    JsonArray nodesArr = jsonObject.getArray("nodes");
    if (nodesArr == null) {
      mainLogger.error("Could not find any nodes, can not build grid");
      return Optional.empty();
    }
    for (int i = 0;
         i < nodesArr.size();
         i++) { // for is used to avoid the cast in forEach(..) or iterator
      JsonObject object = nodesArr.getObject(i);
      NodeInput node = JsonMapper.toNodeInput(object);
      idToNode.put(JsonMapper.getTid(object), node);
      subnetInput.add(node);
    }
    extractLines(jsonObject.getArray("lines")).forEach(subnetInput::add);
    extractSwitches(jsonObject.getArray("switches")).forEach(subnetInput::add);
    extractTransformer2Ws(jsonObject.getArray("trafo2ws")).forEach(subnetInput::add);
    extractTransformer3Ws(jsonObject.getArray("trafo3ws")).forEach(subnetInput::add);
    return Optional.of(subnetInput);
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  public void fetchNodes() {
    String query = createQueryStringForScenarioNodes();
    CompletableFuture<QueryResult> futureResult = connector.query(query);
    QueryResult queryResult = futureResult.join();
    JsonArray nodesArr = queryResult.rowsAsObject().get(0).getArray("nodes");
    if (nodesArr == null)
      throw new IllegalArgumentException("Could not find any nodes, can not build grid");
    for (int i = 0;
        i < nodesArr.size();
        i++) { // for is used to avoid the cast in forEach(..) or iterator
      JsonObject object = nodesArr.getObject(i);
      NodeInput node = JsonMapper.toNodeInput(object);
      idToNode.put(JsonMapper.getTid(object), node);
      aggregatedRawGridInput.add(node);
    }
    fetchedNodes = true;
  }

  public void fetch() {
    fetchNodes();
    String query = createQueryStringForScenarioSubnets();
    CompletableFuture<QueryResult> futureResult = connector.query(query);
    QueryResult queryResult = futureResult.join();

    for (JsonObject subnetJson : queryResult.rowsAsObject()) {
      fetchLines(subnetJson.getArray("lines"));
      fetchSwitches(subnetJson.getArray("switches"));
      fetch2WTrafos(subnetJson.getArray("trafo2ws"));
      fetch3WTrafos(subnetJson.getArray("trafo3ws"));
    }
    fetched = true;
  }

  private void fetchLines(JsonArray linesArr) {
    if (!fetchedNodes) fetchNodes();
    HashSet<LineInput> lines = extractLines(linesArr);
    lines.forEach(aggregatedRawGridInput::add);
  }

  private void fetchSwitches(JsonArray switchesArr) {
    if (!fetchedNodes) fetchNodes();
    HashSet<SwitchInput> switches = extractSwitches(switchesArr);
    switches.forEach(aggregatedRawGridInput::add);
  }

  private void fetch2WTrafos(JsonArray trafo2WArr) {
    if (!fetchedNodes) fetchNodes();
    HashSet<Transformer2WInput> trafos = extractTransformer2Ws(trafo2WArr);
    trafos.forEach(aggregatedRawGridInput::add);
  }

  private void fetch3WTrafos(JsonArray trafo3WArr) {
    if (!fetchedNodes) fetchNodes();
    HashSet<Transformer3WInput> trafos = extractTransformer3Ws(trafo3WArr);
    trafos.forEach(aggregatedRawGridInput::add);
  }
  

  @NotNull
  private HashSet<LineInput> extractLines(JsonArray linesArr) {
    HashSet<LineInput> lines = new HashSet<>();
    if (linesArr != null) {
      for (int i = 0;
           i < linesArr.size();
           i++) { // for is used to avoid the cast in forEach(..) or iterator
        JsonObject object = linesArr.getObject(i);
        NodeInput nodeA = idToNode.get(JsonMapper.identifyNodeA(object));
        NodeInput nodeB = idToNode.get(JsonMapper.identifyNodeB(object));
        lines.add(JsonMapper.toLineInput(object, nodeA, nodeB));
      }
    }
    return lines;
  }
  
  @NotNull
  private HashSet<SwitchInput> extractSwitches(JsonArray switchesArr) {
    HashSet<SwitchInput> switches = new HashSet<>();
    if (switchesArr != null) {
      for (int i = 0;
           i < switchesArr.size();
           i++) { // for is used to avoid the cast in forEach(..) or iterator
        JsonObject object = switchesArr.getObject(i);
        NodeInput nodeA = idToNode.get(JsonMapper.identifyNodeA(object));
        NodeInput nodeB = idToNode.get(JsonMapper.identifyNodeB(object));
        switches.add(JsonMapper.toSwitchInput(object, nodeA, nodeB));
      }
    }
    return switches;
  }
  
  @NotNull
  private HashSet<Transformer2WInput> extractTransformer2Ws(JsonArray transformer2WsArr) {
    HashSet<Transformer2WInput> transformer2Ws = new HashSet<>();
    if (transformer2WsArr != null) {
      for (int i = 0;
           i < transformer2WsArr.size();
           i++) { // for is used to avoid the cast in forEach(..) or iterator
        JsonObject object = transformer2WsArr.getObject(i);
        NodeInput nodeA = idToNode.get(JsonMapper.identifyNodeA(object));
        NodeInput nodeB = idToNode.get(JsonMapper.identifyNodeB(object));
        transformer2Ws.add(JsonMapper.toTransformer2W(object, nodeA, nodeB));
      }
    }
    return transformer2Ws;
  }
  
  @NotNull
  private HashSet<Transformer3WInput> extractTransformer3Ws(JsonArray transformer3WsArr) {
    HashSet<Transformer3WInput> transformer3Ws = new HashSet<>();
    if (transformer3WsArr != null) {
      for (int i = 0;
           i < transformer3WsArr.size();
           i++) { // for is used to avoid the cast in forEach(..) or iterator
        JsonObject object = transformer3WsArr.getObject(i);
        NodeInput nodeA = idToNode.get(JsonMapper.identifyNodeA(object));
        NodeInput nodeB = idToNode.get(JsonMapper.identifyNodeB(object));
        NodeInput nodeC = idToNode.get(JsonMapper.identifyNodeC(object));
        transformer3Ws.add(JsonMapper.toTransformer3W(object, nodeA, nodeB, nodeC));
      }
    }
    return transformer3Ws;
  }

  public String createQueryStringForScenarioSubnets() {
    String query = "SELECT " + connector.getBucketName() + ".* FROM " + connector.getBucketName();
    query += " WHERE META().id LIKE '" + "subnet::" + scenarioName + "::%'";
    return query;
  }

  public String createQueryStringForScenarioNodes() {
    String query = "SELECT ARRAY_AGG(nds) AS nodes FROM " + connector.getBucketName() + " AS b";
    query += " UNNEST nodes nds";
    query += " WHERE META(b ).id LIKE '" + "subnet::" + scenarioName + "::%'";
    return query;
  }

  public String generateSubnetKey(Integer subnetId, String scenarioName) {
    return "subnet::" + scenarioName + "::" + subnetId;
  }
}
