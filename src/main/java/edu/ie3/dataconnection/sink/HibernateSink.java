/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.sink;

import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.dataconnection.dataconnectors.HibernateConnector;
import edu.ie3.models.hibernate.output.HibernateLineResult;
import edu.ie3.models.hibernate.output.HibernateResult;
import edu.ie3.models.result.ResultEntity;
import edu.ie3.models.result.connector.LineResult;
import java.util.Collection;
import java.util.HashSet;

public class HibernateSink implements DataSink {

  private HibernateConnector connector;

  public HibernateSink(HibernateConnector connector) {
    this.connector = connector;
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  @Override
  public void persist(ResultEntity entity) {
    if (entity instanceof LineResult)
      connector.persist(new HibernateLineResult((LineResult) entity));
  }

  @Override
  public void persistAll(Collection<? extends ResultEntity> entity) {
    Collection<HibernateResult> hibernateResults = new HashSet<>();
    for (ResultEntity resultEntity : entity) {
      if (resultEntity instanceof LineResult)
        hibernateResults.add(new HibernateLineResult((LineResult) resultEntity));
    }
    connector.persist(hibernateResults);
  }
}
