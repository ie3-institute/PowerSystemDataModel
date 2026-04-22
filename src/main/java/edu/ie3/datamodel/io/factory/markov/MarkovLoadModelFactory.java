/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.markov;

import static edu.ie3.datamodel.utils.CollectionUtils.newSet;

import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import tools.jackson.databind.JsonNode;

/**
 * Factory turning Markov JSON data into {@link MarkovLoadModel}s.
 *
 * <p>The JSON fields follow the simonaMarkovLoad schema (snake_case), which is mapped to the model
 * records used within PSDM.
 */
public class MarkovLoadModelFactory
    extends Factory<MarkovLoadModel, MarkovModelData, MarkovLoadModel>
    implements MarkovModelParsingSupport {

  public MarkovLoadModelFactory() {
    super(MarkovLoadModel.class);
  }

  /**
   * Build a {@link MarkovLoadModel} from a parsed JSON tree.
   *
   * <p>This method validates the transition shape and requires GMM buckets to be present.
   */
  @Override
  protected MarkovLoadModel buildModel(MarkovModelData data) {
    JsonNode root = data.getRoot();
    String schema = extractText(root, "schema");
    ZonedDateTime generatedAt = parseTimestamp(extractText(root, "generated_at"));
    Generator generator = parseGenerator(extractNode(root, "generator"));
    TimeModel timeModel = extractTimeModel(extractNode(root, "time_model"));
    ValueModel valueModel = parseValueModel(extractNode(root, "value_model"));
    Parameters parameters = parseParameters(root.path("parameters"));

    JsonNode dataNode = extractNode(root, "data");
    TransitionData transitionData =
        parseTransitions(dataNode, timeModel.bucketCount(), valueModel.discretization().states());
    GmmBuckets gmmBuckets = parseGmmBuckets(extractNode(dataNode, "gmms"));

    return new MarkovLoadModel(
        schema,
        generatedAt,
        generator,
        timeModel,
        valueModel,
        parameters,
        transitionData,
        Optional.of(gmmBuckets));
  }

  @Override
  protected List<Set<String>> getFields(Class<? extends MarkovLoadModel> entityClass) {
    Set<String> requiredFields =
        newSet(
            "schema",
            "generatedAt",
            "generator.name",
            "generator.version",
            "timeModel.bucketCount",
            "timeModel.bucketEncoding.formula",
            "timeModel.samplingIntervalMinutes",
            "timeModel.timezone",
            "valueModel.valueUnit",
            "valueModel.normalization.method",
            "valueModel.discretization.states",
            "valueModel.discretization.thresholdsRight",
            "data.transitions.shape",
            "data.transitions.values",
            "data.gmms.buckets");
    return List.of(requiredFields);
  }
}
