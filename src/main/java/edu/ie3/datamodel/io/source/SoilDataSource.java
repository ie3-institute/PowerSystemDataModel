/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.input.SoilLayerInputFactory;
import edu.ie3.datamodel.io.factory.input.SoilTypeInputFactory;
import edu.ie3.datamodel.models.input.SoilLayer;
import edu.ie3.datamodel.models.input.SoilType;
import java.util.Map;
import java.util.UUID;

/**
 * Source for soil layers and soil types. It holds the source agnostic reading logic and delegates
 * the transport specific row access to the provided {@link DataSource}.
 */
public class SoilDataSource extends EntitySource {
  private final DataSource dataSource;
  private final SoilTypeInputFactory soilTypeFactory;
  private final SoilLayerInputFactory soilLayerFactory;

  /**
   * Constructor for a soil data source.
   *
   * @param dataSource the data source to read soil types and layers from
   */
  public SoilDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    this.soilTypeFactory = new SoilTypeInputFactory();
    this.soilLayerFactory = new SoilLayerInputFactory();
  }

  /**
   * Reads all soil types from the underlying source.
   *
   * @return a map from uuid to soil type
   * @throws SourceException if the source cannot be read
   */
  public Map<UUID, SoilType> getSoilTypes() throws SourceException {
    return getEntities(SoilType.class, dataSource, soilTypeFactory);
  }

  /**
   * Reads all soil layers from the underlying source.
   *
   * @return a map from uuid to soil layer
   * @throws SourceException if the source cannot be read
   */
  public Map<UUID, SoilLayer> getSoilLayers() throws SourceException {
    return getEntities(SoilLayer.class, dataSource, soilLayerFactory);
  }

  @Override
  public void validate() throws ValidationException {
    validate(dataSource, SoilType.class, SoilLayer.class);
  }
}
