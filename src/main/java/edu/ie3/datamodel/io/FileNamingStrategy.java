package edu.ie3.datamodel.io;

import edu.ie3.datamodel.io.csv.DefaultDirectoryHierarchy;
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;

public abstract class FileNamingStrategy {

  private final EntityPersistenceNamingStrategy entityPersistenceNamingStrategy;

  private final DefaultDirectoryHierarchy defaultDirectoryHierarchy;

  private final String fileExtension;

  public FileNamingStrategy(EntityPersistenceNamingStrategy entityPersistenceNamingStrategy, DefaultDirectoryHierarchy defaultDirectoryHierarchy, String fileExtension) {
    this.entityPersistenceNamingStrategy = entityPersistenceNamingStrategy;
    this.defaultDirectoryHierarchy = defaultDirectoryHierarchy;
    this.fileExtension = fileExtension;
  }

}
