package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.models.UniqueEntity;

import java.util.Optional;

public interface DirectoryNamingStrategy extends FileHierarchy {

  Optional<String> getSubDirectory(Class<? extends UniqueEntity> cls, String fileSeparator);

}
