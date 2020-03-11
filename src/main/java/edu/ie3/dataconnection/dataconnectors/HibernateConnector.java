/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.dataconnectors;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateConnector implements DataConnector {

  private static Logger mainLogger = LogManager.getLogger("Main");

  private final CriteriaBuilder builder;
  private final EntityManager manager;
  private EntityManagerFactory factory;
  private String persistenceUnitName;

  public HibernateConnector(String persistenceUnitName) {
    this.persistenceUnitName = persistenceUnitName;
    manager = getEntityManagerFactory().createEntityManager();
    builder = getEntityManagerFactory().getCriteriaBuilder();
  }

  @Override
  public Boolean isConnectionValid() {
    return factory.isOpen();
  }

  @Override
  public void shutdown() {
    if (manager != null) {
      manager.close();
    }
    if (factory != null) {
      factory.close();
    }
  }

  public final EntityManagerFactory getEntityManagerFactory() {
    if (factory == null) {
      try {
        factory = Persistence.createEntityManagerFactory(persistenceUnitName);
      } catch (Exception e) {
        mainLogger.error("Error at Entity Manager Factory creation: ", e);
      }
    }
    return factory;
  }

  public void persist(Serializable entity) {
    EntityTransaction transaction = null;
    try {
      transaction = manager.getTransaction();
      if (!transaction.isActive()) transaction.begin();

      manager.persist(entity);

      transaction.commit();
    } catch (Exception ex) {
      // If there are any exceptions, roll back the changes
      if (transaction != null) {
        transaction.rollback();
      }
      mainLogger.error(ex);
    }
    manager.joinTransaction();
  }

  public void persist(Collection<? extends Serializable> entities) {
    EntityTransaction transaction = null;
    try {
      transaction = manager.getTransaction();
      if (!transaction.isActive()) transaction.begin();

      for (Serializable entity : entities) manager.persist(entity);

      transaction.commit();
    } catch (Exception ex) {
      // If there are any exceptions, roll back the changes
      if (transaction != null) {
        transaction.rollback();
      }
      mainLogger.error(ex);
    }
  }

  public <C extends Serializable> List<C> readAll(Class<C> entityClass) {
    List<C> entities = null;
    //        EntityManager manager = factory.createEntityManager();
    try {
      CriteriaQuery<C> criteria = builder.createQuery(entityClass);
      criteria.select(criteria.from(entityClass));
      entities = manager.createQuery(criteria).getResultList();
    } catch (Exception ex) {
      ex.printStackTrace();
      mainLogger.error(ex.getMessage());
    }
    return entities;
  }

  public <C extends Serializable> C find(Class<C> clazz, Object id) {
    C entity = null;
    try {
      entity = manager.find(clazz, id);
    } catch (Exception ex) {
      mainLogger.error(ex);
      manager.flush();
    }
    return entity;
  }

  public List execNamedQuery(String queryName, List params) {
    mainLogger.trace("Execute query '" + queryName + "'" + "with params {" + params.toString() + "}");
    List objs = null;
    try {
      Query query = manager.createNamedQuery(queryName);
      int i = 1;
      for (Object obj : params) {
        query.setParameter(i++, obj);
      }
      objs = query.getResultList();
    } catch (Exception ex) {
      mainLogger.error(ex);
    }
    return objs;
  }

  public List execNamedQuery(String queryName, Map<String, Object> namedParams) {
    mainLogger.trace("Execute query '" + queryName + "'" + "with params {" + namedParams.values().toString() + "}");
    List objs = null;
    try {
      Query query = manager.createNamedQuery(queryName);
      for (Map.Entry<String, Object> entry : namedParams.entrySet()) {
        query.setParameter(entry.getKey(), entry.getValue());
      }
      mainLogger.debug("GetResultList");
      objs = query.getResultList();
      mainLogger.debug(" ReceivedResultList");
    } catch (Throwable ex) {
      mainLogger.error("Error at Query Execution: " + ex);
    }
    return objs;
  }

  public Object execSingleResultNamedQuery(String queryName, List params) {
    Object res = null;
    try {
      Query query = manager.createNamedQuery(queryName);
      int i = 1;
      for (Object obj : params) {
        query.setParameter(i++, obj);
      }
      res = query.getSingleResult();
    } catch (Exception ex) {
      mainLogger.error(ex);
    }
    return res;
  }

  public void flush() {
    EntityTransaction t = manager.getTransaction();
    if (!t.isActive()) t.begin();
    t.commit();
  }
}
