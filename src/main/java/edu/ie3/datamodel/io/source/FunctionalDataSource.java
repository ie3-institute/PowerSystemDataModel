package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputFactory;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputFactory;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public interface FunctionalDataSource {

    /*
    protected static final Logger log = LoggerFactory.getLogger(FunctionalDataSource.class);

    // field names
    protected static final String OPERATOR = "operator";
    protected static final String NODE_A = "nodeA";
    protected static final String NODE_B = "nodeB";
    protected static final String NODE = "node";
    protected static final String TYPE = "type";
    protected static final String FIELDS_TO_VALUES_MAP = "fieldsToValuesMap";

    protected static final FileNamingStrategy namingStrategy = new FileNamingStrategy();

     */


    public abstract <T extends UniqueEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass);

    public abstract <T extends UniqueEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass, String specialPlace) throws SourceException;
    public abstract <T extends UniqueEntity> Stream<Map<String, String>> getSourceData(String specialPlace) throws SourceException;

    public abstract <T extends UniqueEntity> Stream<Map<String, String>> getSourceData();

    public abstract Stream<Map<String, String>> getSourceData(IdCoordinateFactory factory);
}
