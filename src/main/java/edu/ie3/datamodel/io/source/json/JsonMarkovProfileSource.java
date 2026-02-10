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
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel;
import edu.ie3.datamodel.models.profile.markov.MarkovPowerProfile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final MarkovPowerProfile profile;
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
    this.profile = new MarkovPowerProfile(metaInformation.getProfile());
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
      JsonNode root = readModelTree();
      try {
        cachedModel = factory.get(new MarkovModelData(root)).getOrThrow();
      } catch (FactoryException e) {
        throw new SourceException(
            "Unable to build Markov load model from '" + metaInformation.getProfile() + "'.", e);
      }
    }
    return cachedModel;
  }

  @Override
  public void validate() throws ValidationException {
    JsonNode root;
    try {
      root = readModelTree();
    } catch (SourceException e) {
      throw new FailedValidationException(
          "Unable to read Markov model '" + metaInformation.getProfile() + "' for validation.", e);
    }
    Set<String> fields = collectFieldNames(root);
    factory.validate(fields, MarkovLoadModel.class).getOrThrow();
  }

  @Override
  public MarkovPowerProfile getProfile() {
    return profile;
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

  private JsonNode readModelTree() throws SourceException {
    Path filePath = metaInformation.getFullFilePath();
    try (InputStream inputStream = dataSource.initInputStream(filePath)) {
      return objectMapper.readTree(inputStream);
    } catch (IOException e) {
      throw new SourceException("Unable to read Markov model JSON from '" + filePath + "'.", e);
    }
  }

  private static Set<String> collectFieldNames(JsonNode node) {
    Set<String> fields = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    collectFields("", node, fields);
    return fields;
  }

  private static void collectFields(String prefix, JsonNode node, Set<String> collector) {
    if (node.isArray()) {
      if (!prefix.isEmpty()) {
        collector.add(prefix);
      }
      return;
    }
    if (node.isObject()) {
      node.propertyNames()
          .forEach(name -> collectFields(join(prefix, name), node.get(name), collector));
    } else if (!prefix.isEmpty()) {
      collector.add(prefix);
    }
  }

  private static String join(String prefix, String name) {
    return prefix.isEmpty() ? name : prefix + "." + name;
  }

  private MarkovLoadModel getModelUnchecked() {
    try {
      return getModel();
    } catch (SourceException e) {
      throw new IllegalStateException(
          "Unable to load Markov model '" + metaInformation.getProfile() + "'.", e);
    }
  }
}
