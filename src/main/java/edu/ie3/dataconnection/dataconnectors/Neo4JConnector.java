package edu.ie3.dataconnection.dataconnectors;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;

public class Neo4JConnector implements DataConnector {

    private static final int DEPTH_LIST = 0;
    private static final int DEPTH_ENTITY = 1;

    String uri = "bolt://localhost";
    String user = "neo4j";
    String password = "root";
    private final Configuration configuration;
    private final SessionFactory sessionFactory;

    public Neo4JConnector() {
        configuration = new Configuration.Builder().uri(uri).credentials(user, password).build();
        sessionFactory = new SessionFactory(configuration, "edu.ie3.models.neo4j");
    }

    public org.neo4j.ogm.session.Session getSession() {
        return sessionFactory.openSession();
    }


    @Override
    public Boolean isConnectionValid() {
        return true;
    }

    @Override
    public void shutdown() {
        sessionFactory.close();
    }

    public <E> Iterable<E> findAll(Class<E> entityClass) {
        return getSession().loadAll(entityClass, 1);
    }
}
