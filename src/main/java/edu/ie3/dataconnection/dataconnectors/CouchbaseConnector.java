package edu.ie3.dataconnection.dataconnectors;

import com.couchbase.client.core.diagnostics.PingResult;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CouchbaseConnector implements DataConnector {

    private static final String COUCHBASE_URL = "http://localhost:8091/";
    private String username = "Administrator";
    private String password = "administrator";
    private String bucketName ="ie3_in";
    private Cluster cluster;

    public CouchbaseConnector(String username, String password, String bucketName){
        this.username = username;
        this.password = password;
        this.bucketName = bucketName;
        cluster = Cluster.connect(COUCHBASE_URL, username, password);
    }

    public CouchbaseConnector(String username, String password){
        this.username = username;
        this.password = password;
        cluster = Cluster.connect(COUCHBASE_URL, username, password);
    }

    public CouchbaseConnector(){
        cluster = Cluster.connect(COUCHBASE_URL, username, password);
    }

    public Collection getSession(){
        return getSession(bucketName);
    }

    private Collection getSession(String bucketName) {
        return cluster.bucket(bucketName).defaultCollection();
    }

    @Override

    public Boolean isConnectionValid() {
        PingResult pingResult = cluster.ping();
        return pingResult != null;
    }

    @Override
    public void shutdown() {
        cluster.disconnect();
    }

    public String getBucketName(){
        return bucketName;
    }

    public CompletableFuture<QueryResult> query(String query){
        return cluster.async().query(query);
    }

    public List<CompletableFuture<GetResult>> bulkGet(List<String> keys) {
        final Collection session = getSession();
        return keys.stream().map(key -> session.async().get(key)).collect(Collectors.toList());
    }

    public CompletableFuture<GetResult> get(String key){
        return getSession().async().get(key);
    }

    public void persist(String key, Object content) {
        getSession().async().insert(key, content);
    }
}
