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
    String schema = extractText(root, MARKOV_SCHEMA);
    ZonedDateTime generatedAt = parseTimestamp(extractText(root, jsonField(MARKOV_GENERATED_AT)));
    Generator generator = parseGenerator(extractNode(root, MARKOV_GENERATOR));
    TimeModel timeModel = extractTimeModel(extractNode(root, jsonField(MARKOV_TIME_MODEL)));
    ValueModel valueModel = parseValueModel(extractNode(root, jsonField(MARKOV_VALUE_MODEL)));
    Parameters parameters = parseParameters(root.path(MARKOV_PARAMETERS));

    JsonNode dataNode = extractNode(root, MARKOV_DATA);
    TransitionData transitionData =
        parseTransitions(dataNode, timeModel.bucketCount(), valueModel.discretization().states());
    GmmBuckets gmmBuckets = parseGmmBuckets(extractNode(dataNode, jsonLeafField(MARKOV_GMMS)));

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
