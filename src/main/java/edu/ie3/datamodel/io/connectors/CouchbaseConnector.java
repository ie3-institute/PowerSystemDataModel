/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import com.couchbase.client.core.diagnostics.PingResult;
import com.couchbase.client.java.AsyncCollection;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implements a DataConnector for Couchbase. Couchbase is a JSON document based database. <br>
 * Entities will be persisted in documents which are then mapped by a key.
 */
public class CouchbaseConnector implements DataConnector {

  private static final String DEFAULT_URL = "127.0.0.1";
  private static final String DEFAULT_BUCKET_NAME = "ie3_in";
  private static final String DEFAULT_USERNAME = "Administrator";
  private static final String DEFAULT_PASSWORD = "password";
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
   * Initializes a new CouchbaseConnector, uses the default URL ({@value #DEFAULT_URL} and bucket
   * name ({@value #DEFAULT_BUCKET_NAME})
   *
   * @param username the user name
   * @param password the user password
   */
  public CouchbaseConnector(String username, String password) {
    this(DEFAULT_URL, DEFAULT_BUCKET_NAME, username, password);
  }

  /**
   * Initializes a new CouchbaseConnector, uses the default URL ({@value #DEFAULT_URL}), bucket name
   * ({@value #DEFAULT_BUCKET_NAME}), user name ({@value #DEFAULT_USERNAME}) and password ({@value
   * #DEFAULT_PASSWORD})
   */
  public CouchbaseConnector() {
    this(DEFAULT_URL, DEFAULT_BUCKET_NAME, DEFAULT_USERNAME, DEFAULT_PASSWORD);
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
    return keys.stream().map(key -> session.async().get(key)).collect(Collectors.toList());
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
   */
  public void persist(String key, Object content) {
    getSession().async().insert(key, content);
  }
}
