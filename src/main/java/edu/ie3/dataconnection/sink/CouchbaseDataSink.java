/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.sink;

import com.couchbase.client.java.json.JsonObject;
import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.models.json.JsonMapper;
import edu.ie3.models.result.ResultEntity;
import java.util.Collection;

public class CouchbaseDataSink implements DataSink {

  CouchbaseConnector connector;

  public CouchbaseDataSink(CouchbaseConnector connector) {
    this.connector = connector;
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  @Override
  public void persist(ResultEntity entity) {
    JsonObject json = JsonMapper.toJsonResult(entity);
    connector.getAsyncSession().upsert(generateKey(entity), json);
  }

  @Override
  public void persistAll(Collection<? extends ResultEntity> entities) {
    com.couchbase.client.java.Collection session = connector.getSession();
    for (ResultEntity entity : entities) {
      JsonObject json = JsonMapper.toJsonResult(entity);
      session.upsert(generateKey(entity), json);
    }
  }

  public static String generateKey(ResultEntity entity) {
    String key = generateResultKeyPrefix(entity.getClass());
    key += entity.getInputModel() + "::";
    key += entity.getTimestamp();
    return key;
  }

  public static String generateResultKeyPrefix(Class clazz) {
    String scenarioName = "vn_simona";
    String keyPrefix = scenarioName + "::";
    keyPrefix += clazz.getSimpleName() + "::";
    return keyPrefix;
  }
}
