/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.markov;

import com.fasterxml.jackson.databind.JsonNode;
import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Factory turning Markov JSON data into {@link MarkovLoadModel}s. */
public class MarkovLoadModelFactory
    extends Factory<MarkovLoadModel, MarkovModelData, MarkovLoadModel>
    implements MarkovModelParsingSupport {

  public MarkovLoadModelFactory() {
    super(MarkovLoadModel.class);
  }

  @Override
  protected MarkovLoadModel buildModel(MarkovModelData data) {
    JsonNode root = data.getRoot();
    String schema = requireText(root, "schema");
    ZonedDateTime generatedAt = parseTimestamp(requireText(root, "generated_at"));
    Generator generator = parseGenerator(requireNode(root, "generator"));
    TimeModel timeModel = parseTimeModel(requireNode(root, "time_model"));
    ValueModel valueModel = parseValueModel(requireNode(root, "value_model"));
    Parameters parameters = parseParameters(root.path("parameters"));

    JsonNode dataNode = requireNode(root, "data");
    TransitionData transitionData =
        parseTransitions(dataNode, timeModel.bucketCount(), valueModel.discretization().states());
    GmmBuckets gmmBuckets = parseGmmBuckets(requireNode(dataNode, "gmms"));

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
  protected List<Set<String>> getFields(Class<?> entityClass) {
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
