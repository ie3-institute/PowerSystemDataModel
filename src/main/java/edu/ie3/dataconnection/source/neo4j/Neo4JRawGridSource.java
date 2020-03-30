/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source.neo4j;

import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.dataconnection.dataconnectors.Neo4JConnector;
import edu.ie3.dataconnection.source.RawGridSource;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.SwitchInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import edu.ie3.models.neo4j.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.ogm.model.Result;

public class Neo4JRawGridSource implements RawGridSource {

  private static Logger mainLogger = LogManager.getLogger("Main");

  private Neo4JConnector connector;
  private AggregatedRawGridInput aggregatedRawGridInput = new AggregatedRawGridInput();
  private boolean fetchedNodes;
  private Map<Integer, NodeInput> tidToNode = new HashMap<>();
  private boolean fetched;

  public Neo4JRawGridSource(Neo4JConnector connector) {
    this.connector = connector;
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
    final String cypherQuery =
        "MATCH(n1) - [l:LINE] - (n2) - [t:TRANSFORMER] - (n3:Node {subnet : $subnet}) RETURN n1";
    Map<String, Integer> params = Collections.singletonMap("subnet", subnet);
    Iterable<Neo4JNodeInput> neo4JNodeInputs =
        connector.execCypherQuery(cypherQuery, params, Neo4JNodeInput.class);
    Collection<NodeInput> nodeInputs =
        StreamSupport.stream(neo4JNodeInputs.spliterator(), false)
            .map(Neo4JMapper::toNodeInput)
            .collect(Collectors.toSet());
    return nodeInputs;
  }

  //  MATCH(n:Node {subnet: 116}) - [c*0..1] - () RETURN n, c;
  @Override
  public Optional<AggregatedRawGridInput> getSubnet(Integer subnet) {
    AggregatedRawGridInput subnetInput = new AggregatedRawGridInput();
    final String cypherQuery = "MATCH(n:Node {subnet: $subnet}) - [c*0..1] - (m) RETURN n, c, m;";
    Map<String, Integer> params = Collections.singletonMap("subnet", subnet);
   try { Result queryResult = connector.execCypherQuery(cypherQuery, params);
    Iterator<Map<String, Object>> resultIterator = queryResult.iterator();
    HashSet<Object> relationships = new HashSet<>(); //relationships can only be interpreted after nodes are done
    while (resultIterator.hasNext()) {
      Map<String, Object> row = resultIterator.next();
      Neo4JNodeInput neo4jNode = (Neo4JNodeInput) row.get("n");
      NodeInput node = Neo4JMapper.toNodeInput(neo4jNode);
      tidToNode.put(neo4jNode.getTid(), node);
      subnetInput.add(node);
      Object cObj = row.get("c");
      if (cObj instanceof ArrayList) {
        relationships.add(((ArrayList) cObj).get(0));
      }
    }
    HashSet<Neo4JTransformer3WInput> transformer3Ws = new HashSet<>();
    for (Object relationship : relationships) {
      if (relationship instanceof Neo4JLineInput) {
        Neo4JLineInput neo4JLine = (Neo4JLineInput) relationship;
          LineInput lineInput = Neo4JMapper.toLineInput(neo4JLine, tidToNode.get(neo4JLine.getNodeA().getTid()), tidToNode.get(neo4JLine.getNodeB().getTid()));
          subnetInput.add(lineInput);
      }
      if (relationship instanceof Neo4JSwitchInput) {
        Neo4JSwitchInput neo4JSwitch = (Neo4JSwitchInput) relationship;
          SwitchInput switchInput = Neo4JMapper.toSwitchInput(neo4JSwitch, tidToNode.get(neo4JSwitch.getNodeA().getTid()), tidToNode.get(neo4JSwitch.getNodeB().getTid()));
        subnetInput.add(switchInput);
      }
      if (relationship instanceof Neo4JTransformer2WInput) {
        Neo4JTransformer2WInput neo4JTransformer2W = (Neo4JTransformer2WInput) relationship;
          Transformer2WInput transformer2WInput = Neo4JMapper.toTransformer2W(neo4JTransformer2W, tidToNode.get(neo4JTransformer2W.getNodeA().getTid()), tidToNode.get(neo4JTransformer2W.getNodeB().getTid()));
            subnetInput.add(transformer2WInput);
      }
      if (relationship instanceof Neo4JTransformer3WInput) {
        Neo4JTransformer3WInput neo4JTransformer3W = (Neo4JTransformer3WInput) relationship;
        transformer3Ws.add(neo4JTransformer3W);
      }
    }
    if(!transformer3Ws.isEmpty()) {
      Map<String, List<Neo4JTransformer3WInput>> transformerCollections =
              transformer3Ws.stream().collect(Collectors.groupingBy(Neo4JTransformer3WInput::getUuid));
      transformerCollections
              .values()
              .forEach(
                      transformerCollection -> {
                        Integer[] nodeTids =
                                Neo4JMapper.getNodeTids(
                                        transformerCollection.get(0), transformerCollection.get(1));
                        Transformer3WInput Transformer =
                                Neo4JMapper.toTransformer3W(
                                        transformerCollection.get(0),
                                        tidToNode.get(nodeTids[0]),
                                        tidToNode.get(nodeTids[1]),
                                        tidToNode.get(nodeTids[2]));
                        subnetInput.add(Transformer);
                      });
    }
   }  catch (Exception ex) {
     mainLogger.error("Error at query result interpretation: ", ex);
     return Optional.empty();
   }
    return Optional.of(subnetInput);
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  public void fetch() {
    try {
      fetchNodes();
      fetchLines();
      fetchSwitches();
      fetchTrafos2W();
      fetchTrafos3W();
      fetched = true;
    } catch (Exception e) {
      mainLogger.error("Error at neo4j fetch: ", e);
    }
  }

  public void fetchNodes() {
    Iterable<Neo4JNodeInput> neo4JNodes = connector.findAll(Neo4JNodeInput.class);
    Neo4JNodeInput next = neo4JNodes.iterator().next();
    Neo4JMapper.toNodeInput(next);
    neo4JNodes.forEach(
        neo4JNode -> tidToNode.put(neo4JNode.getTid(), Neo4JMapper.toNodeInput(neo4JNode)));
    tidToNode.values().forEach(aggregatedRawGridInput::add);
    fetchedNodes = true;
  }

  private void fetchLines() {
    if (!fetchedNodes) fetchNodes();
    Iterable<Neo4JLineInput> neo4JLines = connector.findAll(Neo4JLineInput.class);
    neo4JLines.forEach(
        neo4JLine -> {
          NodeInput nodeA = tidToNode.get(neo4JLine.getNodeA().getTid());
          NodeInput nodeB = tidToNode.get(neo4JLine.getNodeB().getTid());
          LineInput line = Neo4JMapper.toLineInput(neo4JLine, nodeA, nodeB);
          aggregatedRawGridInput.add(line);
        });
  }

  private void fetchSwitches() {
    if (!fetchedNodes) fetchNodes();
    Iterable<Neo4JSwitchInput> neo4JSwitches = connector.findAll(Neo4JSwitchInput.class);
    neo4JSwitches.forEach(
        neo4JSwitch -> {
          NodeInput nodeA = tidToNode.get(neo4JSwitch.getNodeA().getTid());
          NodeInput nodeB = tidToNode.get(neo4JSwitch.getNodeB().getTid());
          SwitchInput switchInput = Neo4JMapper.toSwitchInput(neo4JSwitch, nodeA, nodeB);
          aggregatedRawGridInput.add(switchInput);
        });
  }

  private void fetchTrafos2W() {
    if (!fetchedNodes) fetchNodes();
    Collection<Neo4JTransformer2WInput> neo4JTrafos =
        connector.findAll(Neo4JTransformer2WInput.class);
    neo4JTrafos.forEach(
        neo4JTrafo -> {
          NodeInput nodeA = tidToNode.get(neo4JTrafo.getNodeA().getTid());
          NodeInput nodeB = tidToNode.get(neo4JTrafo.getNodeB().getTid());
          Transformer2WInput trafoInput = Neo4JMapper.toTransformer2W(neo4JTrafo, nodeA, nodeB);
          aggregatedRawGridInput.add(trafoInput);
        });

    // Add data entry that does not fit neo4j format
    aggregatedRawGridInput.add(Neo4JMapper.getBoundaryInjectionTransformer());
  }

  private void fetchTrafos3W() {
    if (!fetchedNodes) fetchNodes();
    Collection<Neo4JTransformer3WInput> neo4JTrafos =
        connector.findAll(Neo4JTransformer3WInput.class);
    Map<String, List<Neo4JTransformer3WInput>> transformerCollections =
        neo4JTrafos.stream().collect(Collectors.groupingBy(Neo4JTransformer3WInput::getUuid));
    transformerCollections
        .values()
        .forEach(
            transformerCollection -> {
              Integer[] nodeTids =
                  Neo4JMapper.getNodeTids(
                      transformerCollection.get(0), transformerCollection.get(1));
              Transformer3WInput Transformer =
                  Neo4JMapper.toTransformer3W(
                      transformerCollection.get(0),
                      tidToNode.get(nodeTids[0]),
                      tidToNode.get(nodeTids[1]),
                      tidToNode.get(nodeTids[2]));
              aggregatedRawGridInput.add(Transformer);
            });
  }
}
