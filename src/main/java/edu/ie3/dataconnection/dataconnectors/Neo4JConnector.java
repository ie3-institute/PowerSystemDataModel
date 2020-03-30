/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.dataconnectors;

import java.util.Collection;
import java.util.Map;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
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

  public Session getSession() {
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

  public <E> Collection<E> findAll(Class<E> entityClass) {
    return getSession().loadAll(entityClass, 1);
  }

  public <E> Collection<E> findAll(Class<E> entityClass, Filter filter) {
    return getSession().loadAll(entityClass, filter, 1);
  }

  public <E> Collection<E> findAll(Class<E> entityClass, Filters filters) {
    return getSession().loadAll(entityClass, filters, 1);
  }

  public <E> Iterable<E> execCypherQuery(
      String cypherQuery, Map<String, ?> params, Class<E> entityClass) {
    return getSession().query(entityClass, cypherQuery, params);
  }

  public Result execCypherQuery(String cypherQuery, Map<String, ?> params) {
    return getSession().query(cypherQuery, params);
  }
}
