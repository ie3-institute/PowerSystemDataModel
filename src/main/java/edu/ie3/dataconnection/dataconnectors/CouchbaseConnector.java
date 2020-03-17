/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.dataconnectors;

import com.couchbase.client.core.diagnostics.PingResult;
import com.couchbase.client.java.AsyncCollection;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;
import edu.ie3.dataconnection.sink.CouchbaseDataSink;
import edu.ie3.models.result.connector.LineResult;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CouchbaseConnector implements DataConnector {

  private static Logger mainLogger = LogManager.getLogger("Main");

  private static final String COUCHBASE_URL = "127.0.0.1";
  //    private static final String COUCHBASE_URL = "http://localhost:8091/";
  private String username = "Administrator";
  private String password = "administrator";
  private String bucketName = "ie3_in";
  private Cluster cluster;

  public CouchbaseConnector(String username, String password, String bucketName) {
    this.username = username;
    this.password = password;
    this.bucketName = bucketName;
    cluster = Cluster.connect(COUCHBASE_URL, username, password);
  }

  public CouchbaseConnector(String username, String password) {
    this.username = username;
    this.password = password;
    cluster = Cluster.connect(COUCHBASE_URL, username, password);
  }

  public CouchbaseConnector(String bucketName) {
    this.bucketName = bucketName;
    cluster = Cluster.connect(COUCHBASE_URL, username, password);
  }

  public CouchbaseConnector() {
    cluster = Cluster.connect(COUCHBASE_URL, username, password);
  }

  public Collection getSession() {
    return getSession(bucketName);
  }

  public AsyncCollection getAsyncSession() {
    return getAsyncSession(bucketName);
  }

  private Collection getSession(String bucketName) {
    return cluster.bucket(bucketName).defaultCollection();
  }

  private AsyncCollection getAsyncSession(String bucketName) {
    return cluster.bucket(bucketName).defaultCollection().async();
  }

  @Override
  public Boolean isConnectionValid() {
    PingResult pingResult = cluster.ping();
    return pingResult != null;
  }

  @Override
  public void shutdown() {
    if (bucketName.endsWith("out")) deleteOutputKeysLike(LineResult.class);
    cluster.disconnect();
  }

  public String getBucketName() {
    return bucketName;
  }

  public CompletableFuture<QueryResult> query(String query) {
    return cluster.async().query(query);
  }

  public List<CompletableFuture<GetResult>> bulkGet(List<String> keys) {
    final Collection session = getSession();
    return keys.stream().map(key -> session.async().get(key)).collect(Collectors.toList());
  }

  public CompletableFuture<GetResult> get(String key) {
    return getSession().async().get(key);
  }

  public void persist(String key, Object content) {
    getSession().async().insert(key, content);
  }

  public void deleteOutputKeysLike(Class clazz) {
    String pattern = CouchbaseDataSink.generateResultKeyPrefix(clazz);
    String query = "DELETE FROM ie3_out WHERE Meta().id LIKE '" + pattern + "%';";
    query(query).join();
  }
}
