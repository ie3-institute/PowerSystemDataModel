/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.dataconnectors;

import edu.ie3.models.hibernate.output.HibernateLineResult;
import java.io.Serializable;
import java.time.ZoneId;
import java.util.*;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateConnector implements DataConnector {

  private static Logger mainLogger = LogManager.getLogger("Main");

  private CriteriaBuilder builder;
  private EntityManager manager;
  private EntityManagerFactory factory;
  private String persistenceUnitName;

  public HibernateConnector(String persistenceUnitName) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
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
    deleteAll(HibernateLineResult.class);
    if (manager != null) {
      manager.close();
    }
    if (factory != null) {
      factory.close();
    }
  }

  private void deleteAll(Class clazz) {
    EntityTransaction transaction = null;
    try {
      transaction = manager.getTransaction();
      if (!transaction.isActive()) transaction.begin();
      CriteriaDelete<HibernateLineResult> delete = builder.createCriteriaDelete(clazz);
      Root r = delete.from(HibernateLineResult.class);
      Query query = manager.createQuery(delete);
      query.executeUpdate();
      transaction.commit();
    } catch (Exception ex) {
      // If there are any exceptions, roll back the changes
      if (transaction != null) {
        transaction.rollback();
      }
      mainLogger.error(ex);
      ex.printStackTrace();
    }
    manager.joinTransaction();
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
    try {
      CriteriaQuery<C> criteria = builder.createQuery(entityClass);
      criteria.select(criteria.from(entityClass));
      entities = execCriteriaQuery(criteria).orElse(Collections.emptyList());
    } catch (Throwable ex) {
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
    mainLogger.trace(
        "Execute query '" + queryName + "'" + "with params {" + params.toString() + "}");
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
    List objs = null;
    try {
      Query query = manager.createNamedQuery(queryName);
      for (Map.Entry<String, Object> entry : namedParams.entrySet()) {
        query.setParameter(entry.getKey(), entry.getValue());
      }
      objs = query.getResultList();
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

  public <T> Optional<List<T>> execSqlQuery(String query, Class<T> resultClass) {
    Query nativeQuery = manager.createNativeQuery(query, resultClass);
    List<T> resultList = null;
    try {
      resultList = nativeQuery.getResultList();
    } catch (Exception ex) {
      mainLogger.error(ex);
    }
    return Optional.ofNullable(resultList);
  }

  public <T> Optional<List<T>> execCriteriaQuery(CriteriaQuery<T> query) {
    List<T> resultList = null;
    try {
      resultList = manager.createQuery(query).getResultList();
    } catch (Exception ex) {
      mainLogger.error(ex);
    }
    return Optional.ofNullable(resultList);
  }

  public void flush() {
    EntityTransaction t = manager.getTransaction();
    if (!t.isActive()) t.begin();
    t.commit();
  }

  public CriteriaBuilder getBuilder() {
    return builder;
  }
}
