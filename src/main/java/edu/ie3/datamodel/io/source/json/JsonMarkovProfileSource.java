/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.json;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.markov.MarkovLoadModelFactory;
import edu.ie3.datamodel.io.factory.markov.MarkovModelData;
import edu.ie3.datamodel.io.file.FileType;
import edu.ie3.datamodel.io.naming.timeseries.FileLoadProfileMetaInformation;
import edu.ie3.datamodel.io.source.EntitySource;
import edu.ie3.datamodel.io.source.PowerValueSource;
import edu.ie3.datamodel.models.profile.PowerProfileKey;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tools.jackson.databind.JsonNode;

/**
 * Source that reads Markov-based load models from JSON files.
 *
 * <p>The JSON file is parsed lazily and cached. All power value requests are delegated to the
 * parsed {@link MarkovLoadModel}.
 */
public class JsonMarkovProfileSource extends EntitySource implements PowerValueSource.MarkovBased {

  private final JsonDataSource dataSource;
  private final FileLoadProfileMetaInformation metaInformation;
  private final MarkovLoadModelFactory factory;
  private MarkovLoadModel cachedModel;

  public JsonMarkovProfileSource(
      JsonDataSource dataSource, FileLoadProfileMetaInformation metaInformation) {
    this(dataSource, metaInformation, new MarkovLoadModelFactory());
  }

  public JsonMarkovProfileSource(
      JsonDataSource dataSource,
      FileLoadProfileMetaInformation metaInformation,
      MarkovLoadModelFactory factory) {
    this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    this.metaInformation = Objects.requireNonNull(metaInformation, "metaInformation");
    this.factory = Objects.requireNonNull(factory, "factory");
    if (metaInformation.getFileType() != FileType.JSON) {
      throw new IllegalArgumentException("Markov profile source requires JSON meta information.");
    }
  }

  /**
   * Returns the parsed Markov model, parsing the underlying file if needed.
   *
   * @throws SourceException if reading or parsing fails
   */
  public synchronized MarkovLoadModel getModel() throws SourceException {
    if (cachedModel == null) {
      JsonNode root = dataSource.readTree(metaInformation.getFullFilePath());
      try {
        cachedModel = factory.get(new MarkovModelData(root)).getOrThrow();
      } catch (FactoryException e) {
        throw new SourceException(
            "Unable to build Markov load model from '"
                + metaInformation.getProfileKey().getValue()
                + "'.",
            e);
      }
    }
    return cachedModel;
  }

  @Override
  public void validate() throws ValidationException {
    try {
      Set<String> fields =
          dataSource.getSourceFields(metaInformation.getFullFilePath()).orElse(Set.of());
      factory.validate(fields, MarkovLoadModel.class).getOrThrow();
    } catch (SourceException e) {
      throw new FailedValidationException(
          "Unable to read Markov model '"
              + metaInformation.getProfileKey().getValue()
              + "' for validation.",
          e);
    }
  }

  @Override
  public PowerProfileKey getProfileKey() {
    return metaInformation.getProfileKey();
  }

  /** Delegates to the cached {@link MarkovLoadModel} for a single simulation step. */
  @Override
  public Supplier<MarkovOutputValue> getValueSupplier(MarkovIdentifier data) {
    return getModelUnchecked().getValueSupplier(data);
  }

  @Override
  public Optional<ZonedDateTime> getNextTimeKey(ZonedDateTime time) {
    return getModelUnchecked().getNextTimeKey(time);
  }

  @Override
  public Optional<ComparableQuantity<Power>> getMaxPower() {
    return getModelUnchecked().getMaxPower();
  }

  @Override
  public Optional<ComparableQuantity<Energy>> getProfileEnergyScaling() {
    return getModelUnchecked().getProfileEnergyScaling();
  }

  private MarkovLoadModel getModelUnchecked() {
    try {
      return getModel();
    } catch (SourceException e) {
      throw new IllegalStateException(
          "Unable to load Markov model '" + metaInformation.getProfileKey().getValue() + "'.", e);
    }
  }
}
