package edu.ie3.dataconnection.source.couchbase;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;
import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.dataconnection.source.RawGridSource;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.SwitchInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import edu.ie3.models.json.JsonMapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CouchbaseRawGridSource implements RawGridSource {

    private CouchbaseConnector connector;
    private String scenarioName;

    private AggregatedRawGridInput aggregatedRawGridInput;
    private boolean fetchedNodes;
    private Map<Integer, NodeInput> idToNode = new HashMap<>();

    private List<Object> nodeJsons = Collections.emptyList();

    public CouchbaseRawGridSource(CouchbaseConnector connector, String scenarioName){
        this.connector = connector;
        this.scenarioName = scenarioName;
        fetch();
    }

    @Override
    public AggregatedRawGridInput getGridData() {
        return null;
    }

    @Override
    public Collection<NodeInput> getNodes() {
        return null;
    }

    @Override
    public Collection<LineInput> getLines() {
        return null;
    }

    @Override
    public Collection<Transformer2WInput> get2WTransformers() {
        return null;
    }

    @Override
    public Collection<Transformer3WInput> get3WTransformers() {
        return null;
    }

    @Override
    public Collection<SwitchInput> getSwitches() {
        return null;
    }

    @Override
    public DataConnector getDataConnector() {
        return connector;
    }


    public void fetchNodes(){
        String query = createQueryStringForScenarioNodes();
        CompletableFuture<QueryResult> futureResult = connector.query(query);
        QueryResult queryResult = futureResult.join();
        JsonArray nodesArr = queryResult.rowsAsObject().get(0).getArray("nodes");
        if(nodesArr==null) throw new IllegalArgumentException("Could not find any nodes, can not build grid");
        for(int i = 0; i< nodesArr.size(); i++){
            JsonObject object = nodesArr.getObject(i);
            idToNode.put(JsonMapper.getTid(object), JsonMapper.toNodeInput(object));
        }
        fetchedNodes = true;
    }

    public void fetch() {
        fetchNodes();
        String query = createQueryStringForScenarioSubnets();
        CompletableFuture<QueryResult> futureResult = connector.query(query);
        QueryResult queryResult = futureResult.join();
        final Iterator<JsonObject> jsonIterator = queryResult.rowsAsObject().iterator();
        while (jsonIterator.hasNext()){
            JsonObject subnetJson = jsonIterator.next();
        }


    }

    public String createQueryStringForScenarioSubnets(){
        String query = "SELECT " + connector.getBucketName() + ".* FROM " + connector.getBucketName();
        query += " WHERE META().id LIKE '" + "subnet::" + scenarioName + "::%'";
        return query;
    }

    public String createQueryStringForScenarioNodes(){
        String query = "SELECT ARRAY_AGG(nds) AS nodes FROM " + connector.getBucketName() + " AS b";
        query += " UNNEST nodes nds";
        query += " WHERE META(b ).id LIKE '" + "subnet::" + scenarioName + "::%'";
        return query;
    }

    public  String generateSubnetKey(Integer subnetId, String scenarioName){
        return "subnet::" + scenarioName + "::" + subnetId;
    }

//    public AggregatedRawGridInput toAggregatedRawGridInput(JsonObject subnetJson){
//    AggregatedRawGridInput aggregatedRawGridInput = new AggregatedRawGridInput();
//        Object obj = subnetJson.get("nodes");
//        if(obj instanceof JsonArray){
//            ((JsonArray) obj).getObject().
//                    List<Object> objList = ((JsonArray) obj).toList();
//            nodeJsons.addAll(objList);
//        }
//        int i = 0;
//    }




}
