package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.InputEntity;

import java.util.Set;

public interface DataSourceFactory {

    <T extends InputEntity> Set<T> buildEntities();











}
