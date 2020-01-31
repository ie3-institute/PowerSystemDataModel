package edu.ie3.dataconnection.sink;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.util.JSONPObject;
import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.models.result.ResultEntity;

import java.util.Collection;

public class CouchbaseDataSink implements DataSink{

    CouchbaseConnector connector;

    @Override
    public DataConnector getDataConnector() {
        return connector;
    }

    @Override
    public void persist(ResultEntity entity) {
        JSONPObject json = null; //TODO json magic
        connector.getSession().upsert(generateKey(entity), json);
    }

    @Override
    public void persistAll(Collection<? extends ResultEntity> entity) {

    }

    public String generateKey(ResultEntity entity) {
        String scenarioName = "vn_simona";
        String key = scenarioName + "::";
        key += entity.getClass().getSimpleName() + "::";
        key += entity.getInputModel() + "::";
        key += entity.getTimestamp();
        return key;
    }
}
