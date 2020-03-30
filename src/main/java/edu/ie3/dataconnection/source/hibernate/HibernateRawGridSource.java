/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source.hibernate;

import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.dataconnection.dataconnectors.HibernateConnector;
import edu.ie3.dataconnection.source.RawGridSource;
import edu.ie3.models.hibernate.HibernateMapper;
import edu.ie3.models.hibernate.input.*;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import edu.ie3.models.input.connector.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateRawGridSource implements RawGridSource {

  private static Logger mainLogger = LogManager.getLogger("Main");

  private HibernateConnector connector;
  private AggregatedRawGridInput aggregatedRawGridInput = new AggregatedRawGridInput();
  private boolean fetched;

  public HibernateRawGridSource(HibernateConnector connector) {
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
    final String queryString =
        "SELECT  distinct(node1.*) FROM public.nodes node1\n"
            + "JOIN public.lines line ON (line.node_a = node1.tid OR line.node_b = node1.tid)\n"
            + "JOIN public.nodes node2 ON (line.node_a = node2.tid OR line.node_b = node2.tid)\n"
            + "JOIN public.transformers trafo ON (trafo.node_b = node2.tid)\n"
            + "JOIN public.nodes node3 ON (trafo.node_a = node3.tid)\n"
            + "WHERE node3.subnet = %d AND node1.tid != node2.tid;";
    String sqlQuery = String.format(queryString, subnet);
    Optional<List<HibernateNodeInput>> hibernateNodeInputs =
        connector.execSqlQuery(sqlQuery, HibernateNodeInput.class);
    return hibernateNodeInputs.orElse(Collections.emptyList()).stream()
        .map(HibernateMapper::toNodeInput)
        .collect(Collectors.toSet());
  }

  @Override
  public Optional<AggregatedRawGridInput> getSubnet(Integer subnet) {
    AggregatedRawGridInput subnetInput = new AggregatedRawGridInput();
    Optional<List<HibernateNodeInput>> nodes = fetchNodesInSubnet(subnet);
    if (nodes.isEmpty()) return Optional.empty();
    nodes.get().stream().map(HibernateMapper::toNodeInput).forEach(subnetInput::add);

    Optional<List<HibernateLineInput>> lines = fetchLinesInSubnet(subnet);
    lines.ifPresent(
            hibernateLineInputs ->
                    hibernateLineInputs.stream()
                            .map(HibernateMapper::toLineInput)
                            .forEach(subnetInput::add));

    Optional<List<HibernateSwitchInput>> switches = fetchSwitchesInSubnet(subnet);
    switches.ifPresent(
            hibernateLineInputs ->
                    hibernateLineInputs.stream()
                            .map(HibernateMapper::toSwitchInput)
                            .forEach(subnetInput::add));

    Optional<List<HibernateTransformer2WInput>> transformers2w =
            fetchTransformer2WsInSubnet(subnet);
    transformers2w.ifPresent(
            hibernateTransformerInputs ->
                    hibernateTransformerInputs.stream()
                            .map(HibernateMapper::toTransformer2W)
                            .forEach(subnetInput::add));


    Optional<List<HibernateTransformer3WInput>> transformers3w =
            fetchTransformer3WsInSubnet(subnet);
    transformers3w.ifPresent(
            hibernateTransformerInputs ->
                    hibernateTransformerInputs.stream()
                            .map(HibernateMapper::toTransformer3W)
                            .forEach(subnetInput::add));
    return Optional.ofNullable(subnetInput);
  }

  private Optional<List<HibernateNodeInput>> fetchNodesInSubnet(Integer subnet) {
    CriteriaBuilder cb = connector.getBuilder();
    CriteriaQuery<HibernateNodeInput> nodeQuery = cb.createQuery(HibernateNodeInput.class);
    Root<HibernateNodeInput> nodeRoot = nodeQuery.from(HibernateNodeInput.class);
    nodeQuery.where(cb.equal(nodeRoot.get(HibernateNodeInput_.subnet), subnet));
    Optional<List<HibernateNodeInput>> nodes = connector.execCriteriaQuery(nodeQuery);
    return nodes;
  }

  private Optional<List<HibernateLineInput>> fetchLinesInSubnet(Integer subnet) {
    CriteriaBuilder cb = connector.getBuilder();
    CriteriaQuery<HibernateLineInput> lineQuery = cb.createQuery(HibernateLineInput.class);
    Root<HibernateLineInput> lineRoot = lineQuery.from(HibernateLineInput.class);
    Join<HibernateLineInput, HibernateNodeInput> nodeA = lineRoot.join(HibernateLineInput_.nodeA);
    Join<HibernateLineInput, HibernateNodeInput> nodeB = lineRoot.join(HibernateLineInput_.nodeB);
    lineQuery.where(
        cb.or(
            cb.equal(nodeA.get(HibernateNodeInput_.subnet), subnet),
            cb.equal(nodeB.get(HibernateNodeInput_.subnet), subnet)));
    lineQuery.select(lineRoot);
    return connector.execCriteriaQuery(lineQuery);
  }

  private Optional<List<HibernateSwitchInput>> fetchSwitchesInSubnet(Integer subnet) {
    CriteriaBuilder cb = connector.getBuilder();
    CriteriaQuery<HibernateSwitchInput> switchQuery = cb.createQuery(HibernateSwitchInput.class);
    Root<HibernateSwitchInput> switchRoot = switchQuery.from(HibernateSwitchInput.class);
    Join<HibernateSwitchInput, HibernateNodeInput> nodeA =
        switchRoot.join(HibernateSwitchInput_.nodeA);
    Join<HibernateSwitchInput, HibernateNodeInput> nodeB =
        switchRoot.join(HibernateSwitchInput_.nodeB);
    switchQuery.where(
        cb.or(
            cb.equal(nodeA.get(HibernateNodeInput_.subnet), subnet),
            cb.equal(nodeB.get(HibernateNodeInput_.subnet), subnet)));
    switchQuery.select(switchRoot);
    return connector.execCriteriaQuery(switchQuery);
  }

  private Optional<List<HibernateTransformer2WInput>> fetchTransformer2WsInSubnet(Integer subnet) {
    CriteriaBuilder cb = connector.getBuilder();
    CriteriaQuery<HibernateTransformer2WInput> transformerQuery =
        cb.createQuery(HibernateTransformer2WInput.class);
    Root<HibernateTransformer2WInput> transformerRoot =
        transformerQuery.from(HibernateTransformer2WInput.class);
    Join<HibernateTransformer2WInput, HibernateNodeInput> nodeA =
        transformerRoot.join(HibernateTransformer2WInput_.nodeA);
    Join<HibernateTransformer2WInput, HibernateNodeInput> nodeB =
        transformerRoot.join(HibernateTransformer2WInput_.nodeB);
    transformerQuery.where(
        cb.or(
            cb.equal(nodeA.get(HibernateNodeInput_.subnet), subnet),
            cb.equal(nodeB.get(HibernateNodeInput_.subnet), subnet)));
    transformerQuery.select(transformerRoot);
    return connector.execCriteriaQuery(transformerQuery);
  }

  private Optional<List<HibernateTransformer3WInput>> fetchTransformer3WsInSubnet(Integer subnet) {
    CriteriaBuilder cb = connector.getBuilder();
    CriteriaQuery<HibernateTransformer3WInput> transformerQuery =
        cb.createQuery(HibernateTransformer3WInput.class);
    Root<HibernateTransformer3WInput> transformerRoot =
        transformerQuery.from(HibernateTransformer3WInput.class);
    Join<HibernateTransformer3WInput, HibernateNodeInput> nodeA =
        transformerRoot.join(HibernateTransformer3WInput_.nodeA);
    Join<HibernateTransformer3WInput, HibernateNodeInput> nodeB =
        transformerRoot.join(HibernateTransformer3WInput_.nodeB);
    Join<HibernateTransformer3WInput, HibernateNodeInput> nodeC =
        transformerRoot.join(HibernateTransformer3WInput_.nodeC);
    transformerQuery.where(
        cb.or(
            cb.equal(nodeA.get(HibernateNodeInput_.subnet), subnet),
            cb.equal(nodeB.get(HibernateNodeInput_.subnet), subnet),
            cb.equal(nodeC.get(HibernateNodeInput_.subnet), subnet)));
    transformerQuery.select(transformerRoot);
    return connector.execCriteriaQuery(transformerQuery);
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  public void fetch() {
    fetchNodes();
    fetchLines();
    fetchSwitches();
    fetchTrafos2W();
    fetchTrafos3W();
    fetched = true;
  }

  public void fetchNodes() {
    try {
      List<HibernateNodeInput> hibernateNodes = connector.readAll(HibernateNodeInput.class);
      hibernateNodes.forEach(
          hNode -> aggregatedRawGridInput.add(HibernateMapper.toNodeInput(hNode)));
    } catch (Exception e) {
      mainLogger.error(e);
    }
  }

  private void fetchLines() {
    try {
      List<HibernateLineInput> hibernateLines = connector.readAll(HibernateLineInput.class);
      hibernateLines.forEach(
          hLine -> aggregatedRawGridInput.add(HibernateMapper.toLineInput(hLine)));
    } catch (Exception e) {
      mainLogger.error(e);
    }
  }

  private void fetchSwitches() {
    try {
      List<HibernateSwitchInput> hibernateSwitches = connector.readAll(HibernateSwitchInput.class);
      hibernateSwitches.forEach(
          hSwitch -> aggregatedRawGridInput.add(HibernateMapper.toSwitchInput(hSwitch)));
    } catch (Exception e) {
      mainLogger.error(e);
    }
  }

  private void fetchTrafos2W() {
    try {
      Iterable<HibernateTransformer2WInput> hibernateTransformer2Ws =
          connector.readAll(HibernateTransformer2WInput.class);
      hibernateTransformer2Ws.forEach(
          hTransformer2W ->
              aggregatedRawGridInput.add(HibernateMapper.toTransformer2W(hTransformer2W)));
    } catch (Exception e) {
      mainLogger.error(e);
    }
  }

  private void fetchTrafos3W() {
    try {
      Iterable<HibernateTransformer3WInput> hibernateTransformer3Ws =
          connector.readAll(HibernateTransformer3WInput.class);
      hibernateTransformer3Ws.forEach(
          hTransformer3W ->
              aggregatedRawGridInput.add(HibernateMapper.toTransformer3W(hTransformer3W)));
    } catch (Exception e) {
      mainLogger.error(e);
    }
  }
}
