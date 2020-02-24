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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Neo4JRawGridSource implements RawGridSource {

    private Neo4JConnector connector;
    private AggregatedRawGridInput aggregatedRawGridInput = new AggregatedRawGridInput();
    private boolean fetchedNodes;
    private Map<Integer, NodeInput> tidToNode = new HashMap<>();

    public Neo4JRawGridSource(Neo4JConnector connector) {
        this.connector = connector;
        fetch();
    }

    @Override
    public AggregatedRawGridInput getGridData() {
        return aggregatedRawGridInput;
    }

    @Override
    public Collection<NodeInput> getNodes() {
        return aggregatedRawGridInput.getNodes();
    }

    @Override
    public Collection<LineInput> getLines() {
        return aggregatedRawGridInput.getLines();
    }

    @Override
    public Collection<Transformer2WInput> get2WTransformers() {
        return aggregatedRawGridInput.getTransformer2Ws();
    }

    @Override
    public Collection<Transformer3WInput> get3WTransformers() {
        return aggregatedRawGridInput.getTransformer3Ws();
    }

    @Override
    public Collection<SwitchInput> getSwitches() {
        return aggregatedRawGridInput.getSwitches();
    }

    @Override
    public DataConnector getDataConnector() {
        return connector;
    }


    public void fetch() {
        fetchNodes();
            fetchLines();
            fetchSwitches();
            fetchTrafos();
    }

    public void fetchNodes() {
        Iterable<Neo4JNodeInput> neo4JNodes = connector.findAll(Neo4JNodeInput.class);
        Neo4JNodeInput next = neo4JNodes.iterator().next();
        Neo4JMapper.toNodeInput(next);
        neo4JNodes.forEach(neo4JNode -> tidToNode.put(neo4JNode.getTid(), Neo4JMapper.toNodeInput(neo4JNode)));
        tidToNode.values().forEach(aggregatedRawGridInput::add);
        fetchedNodes = true;
    }

    private void fetchLines() {
        if(!fetchedNodes) fetchNodes();
        Iterable<Neo4JLineInput> neo4JLines = connector.findAll(Neo4JLineInput.class);
        neo4JLines.forEach(neo4JLine -> {
            NodeInput nodeA = tidToNode.get(neo4JLine.getNodeA().getTid());
            NodeInput nodeB = tidToNode.get(neo4JLine.getNodeB().getTid());
            LineInput line = Neo4JMapper.toLineInput(neo4JLine, nodeA, nodeB);
            aggregatedRawGridInput.add(line);
        });
    }

    private void fetchSwitches() {
        if(!fetchedNodes) fetchNodes();
        Iterable<Neo4JSwitchInput> neo4JSwitches = connector.findAll(Neo4JSwitchInput.class);
        neo4JSwitches.forEach(neo4JSwitch -> {
            NodeInput nodeA = tidToNode.get(neo4JSwitch.getNodeA().getTid());
            NodeInput nodeB = tidToNode.get(neo4JSwitch.getNodeB().getTid());
            SwitchInput switchInput = Neo4JMapper.toSwitchInput(neo4JSwitch, nodeA, nodeB);
            aggregatedRawGridInput.add(switchInput);
        });
    }

    private void fetchTrafos() {
        if(!fetchedNodes) fetchNodes();
        Iterable<Neo4JTransformerInput> neo4JTransformers = connector.findAll(Neo4JTransformerInput.class);
        Stream<Neo4JTransformerInput> neo4JTransformerInputStream = StreamSupport.stream(neo4JTransformers.spliterator(), false);


        Map<Boolean, List<Neo4JTransformerInput>> groups = StreamSupport.stream(neo4JTransformers.spliterator(), false)
                .collect(Collectors.partitioningBy(Neo4JTransformerInput::getThreewindings));

        //2wTrafos
        groups.get(Boolean.FALSE).forEach(neo4JTransformer -> {
            NodeInput nodeA = tidToNode.get(neo4JTransformer.getNodeA().getTid());
            NodeInput nodeB = tidToNode.get(neo4JTransformer.getNodeB().getTid());
            Transformer2WInput Transformer = Neo4JMapper.toTransformer2W(neo4JTransformer, nodeA, nodeB);
            aggregatedRawGridInput.add(Transformer);
        });

        //3wTrafos
        Map<String, List<Neo4JTransformerInput>> transformerCollections = groups.get(Boolean.TRUE).stream().collect(Collectors.groupingBy(Neo4JTransformerInput::getUuid));
        transformerCollections.values().forEach(transformerCollection -> {
            Integer[] nodeTids = Neo4JMapper.getNodeTids(transformerCollection.get(0), transformerCollection.get(1));
            Transformer3WInput Transformer = Neo4JMapper.toTransformer3W(transformerCollection.get(0), tidToNode.get(nodeTids[0]), tidToNode.get(nodeTids[1]), tidToNode.get(nodeTids[2]));
            aggregatedRawGridInput.add(Transformer);
        });
    }

}
