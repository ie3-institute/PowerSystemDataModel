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
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.SwitchInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import java.util.Collection;

public class HibernateRawGridSource implements RawGridSource {

  private HibernateConnector connector;
  private AggregatedRawGridInput aggregatedRawGridInput = new AggregatedRawGridInput();

  public HibernateRawGridSource(HibernateConnector connector) {
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
    fetchTrafos2W();
    fetchTrafos3W();
  }

  public void fetchNodes() {
    Iterable<HibernateNodeInput> hibernateNodes = connector.readAll(HibernateNodeInput.class);
    hibernateNodes.forEach(hNode -> aggregatedRawGridInput.add(HibernateMapper.toNodeInput(hNode)));
  }

  private void fetchLines() {
    Iterable<HibernateLineInput> hibernateLines = connector.readAll(HibernateLineInput.class);
    hibernateLines.forEach(hLine -> aggregatedRawGridInput.add(HibernateMapper.toLineInput(hLine)));
  }

  private void fetchSwitches() {
    Iterable<HibernateSwitchInput> hibernateSwitches =
        connector.readAll(HibernateSwitchInput.class);
    hibernateSwitches.forEach(
        hSwitch -> aggregatedRawGridInput.add(HibernateMapper.toSwitchInput(hSwitch)));
  }

  private void fetchTrafos2W() {
    Iterable<HibernateTransformer2WInput> hibernateTransformer2Ws =
        connector.readAll(HibernateTransformer2WInput.class);
    hibernateTransformer2Ws.forEach(
        hTransformer2W ->
            aggregatedRawGridInput.add(HibernateMapper.toTransformer2W(hTransformer2W)));
  }

  private void fetchTrafos3W() {
    Iterable<HibernateTransformer3WInput> hibernateTransformer3Ws =
        connector.readAll(HibernateTransformer3WInput.class);
    hibernateTransformer3Ws.forEach(
        hTransformer3W ->
            aggregatedRawGridInput.add(HibernateMapper.toTransformer3W(hTransformer3W)));
  }
}
