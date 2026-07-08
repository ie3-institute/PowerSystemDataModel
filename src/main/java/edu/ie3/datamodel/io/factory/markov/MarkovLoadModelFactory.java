/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.markov;

import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.*;
import java.time.ZonedDateTime;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/** Factory turning Markov JSON data into {@link MarkovLoadModel}s. */
public class MarkovLoadModelFactory
    extends Factory<MarkovLoadModel, MarkovModelData, MarkovLoadModel>
    implements MarkovModelParsingSupport {

  public MarkovLoadModelFactory() {
    super(MarkovLoadModel.class);
  }

  /** Builds a {@link MarkovLoadModel} from a parsed JSON tree. */
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
}
