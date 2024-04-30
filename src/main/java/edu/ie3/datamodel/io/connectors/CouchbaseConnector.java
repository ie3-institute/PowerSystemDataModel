/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import com.couchbase.client.core.diagnostics.PingResult;
import com.couchbase.client.java.AsyncCollection;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.query.QueryResult;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Implements a DataConnector for Couchbase. Couchbase is a JSON document based database. <br>
 * Entities will be persisted in documents which are then mapped by a key.
 */
public class CouchbaseConnector implements DataConnector {
  private final Cluster cluster;
  private final String bucketName;

  /**
   * Initializes a new CouchbaseConnector
   *
   * @param url the url to the cluster
   * @param bucketName the name of the bucket to connect to
   * @param username the user name
   * @param password the user password
   */
  public CouchbaseConnector(String url, String bucketName, String username, String password) {
    this.bucketName = bucketName;
    cluster = Cluster.connect(url, username, password);
  }

  /**
   * Initializes a new CouchbaseConnector with given KV timeout
   *
   * @param url the url to the cluster
   * @param bucketName the name of the bucket to connect to
   * @param username the user name
   * @param password the user password
   * @param kvTimeout the key-value access timeout
   */
  public CouchbaseConnector(
      String url, String bucketName, String username, String password, Duration kvTimeout) {
    this.bucketName = bucketName;
    ClusterOptions clusterOptions =
        ClusterOptions.clusterOptions(username, password)
            .environment(env -> env.timeoutConfig(to -> to.kvTimeout(kvTimeout)));
    cluster = Cluster.connect(url, clusterOptions);
  }

  /** Returns the option for a set of found fields. */
  @SuppressWarnings("unchecked")
  public Optional<Set<String>> getSourceFields() {
    String query =
        "SELECT ARRAY_DISTINCT(ARRAY_AGG(v)) AS column FROM "
            + bucketName
            + " b UNNEST OBJECT_NAMES(b) AS v";
    cluster.bucket(bucketName).waitUntilReady(Duration.ofSeconds(30));

    QueryResult queryResult = query(query).join();
    JsonObject jsonObject = queryResult.rowsAsObject().get(0);
    Object columns = jsonObject.toMap().get("column");

    Set<String> set = new HashSet<>();

    if (columns != null) {
      set.addAll((List<String>) columns);
    }

    return Optional.of(set);
  }

  /**
   * Return the couchbase java sdk equivalent of a session - a collection - to the previously set
   * bucket
   *
   * @return a couchbase collection
   */
  public Collection getSession() {
    return getSession(bucketName);
  }

  /**
   * Return the couchbase java sdk equivalent of an asynchronous session - an async collection - to
   * the previously set bucket
   *
   * @return an asynchronous couchbase collection
   */
  public AsyncCollection getAsyncSession() {
    return getAsyncSession(bucketName);
  }

  /**
   * Return the couchbase java sdk equivalent of a session - a collection - to the specified bucket
   *
   * @param bucketName the bucket to connect to
   * @return a couchbase collection
   */
  private Collection getSession(String bucketName) {
    return cluster.bucket(bucketName).defaultCollection();
  }

  /**
   * Return the couchbase java sdk equivalent of a session - a collection - to the specified bucket
   *
   * @param bucketName the bucket to connect to
   * @return a couchbase collection
   */
  private AsyncCollection getAsyncSession(String bucketName) {
    return cluster.bucket(bucketName).defaultCollection().async();
  }

  /**
   * Checks if the database connection is valid
   *
   * @return true, if the cluster responds to an application based ping
   */
  public Boolean isConnectionValid() {
    PingResult pingResult = cluster.ping();
    return pingResult != null;
  }

  @Override
  public void shutdown() {
    cluster.disconnect();
  }

  /** @return the bucket name */
  public String getBucketName() {
    return bucketName;
  }

  /**
   * Returns a future for the result of the given query, will be executed asynchronously
   *
   * @param query the query string
   * @return a future of the query result
   */
  public CompletableFuture<QueryResult> query(String query) {
    return cluster.async().query(query);
  }

  /**
   * Returns futures for every key search result, will be executed asychnchronously
   *
   * @param keys the keys to get the documents for
   * @return list of futures for every key search result
   */
  public List<CompletableFuture<GetResult>> bulkGet(List<String> keys) {
    final Collection session = getSession();
    return keys.stream().map(key -> session.async().get(key)).toList();
  }

  /**
   * Returns a future for search result of the given key
   *
   * @param key the key to get the document for
   * @return future of the search result
   */
  public CompletableFuture<GetResult> get(String key) {
    return getSession().async().get(key);
  }

  /**
   * Persist the document to the database and map it to the key
   *
   * @param key the key for the document
   * @param content the document content
   * @return future of the persisting result
   */
  public CompletableFuture<MutationResult> persist(String key, Object content) {
    return getSession().async().insert(key, content);
  }
}
