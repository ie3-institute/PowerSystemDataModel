/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.input.container.SoilLayer;
import edu.ie3.datamodel.models.input.container.SoilType;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Abstract base for soil data sources (csv and sql). It holds the source agnostic reading logic and
 * delegates the transport specific row access to the concrete implementations.
 */
public abstract class SoilDataSource {
  /**
   * Reads all soil types from the underlying source.
   *
   * @return the soil types
   * @throws SourceException if the source cannot be read
   */
  public List<SoilType> getSoilTypes() throws SourceException {
    return SoilDataParser.readSoilTypes(getSourceData(SoilType.class));
  }

  /**
   * Reads all soil layers from the underlying source.
   *
   * @return the soil layers
   * @throws SourceException if the source cannot be read
   */
  public List<SoilLayer> getSoilLayers() throws SourceException {
    return SoilDataParser.readSoilLayers(getSourceData(SoilLayer.class));
  }

  /**
   * Returns a stream of the raw rows for the given entity class from the underlying transport.
   *
   * @param entityClass the entity class whose rows are to be read
   * @return a stream mapping field names to their string values
   * @throws SourceException if the rows cannot be read
   */
  protected abstract Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass)
      throws SourceException;
}
