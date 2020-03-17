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
import java.util.List;
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
