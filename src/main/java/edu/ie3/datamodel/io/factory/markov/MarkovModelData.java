/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.markov;

import com.fasterxml.jackson.databind.JsonNode;
import edu.ie3.datamodel.io.factory.FactoryData;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel;
import java.util.Collections;
import java.util.Objects;

/** Factory data wrapper around a parsed Markov-load JSON tree. */
public class MarkovModelData extends FactoryData {
  private final JsonNode root;

  public MarkovModelData(JsonNode root) {
    super(Collections.emptyMap(), MarkovLoadModel.class);
    this.root = Objects.requireNonNull(root, "root");
  }

  public JsonNode getRoot() {
    return root;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MarkovModelData that)) return false;
    return Objects.equals(getTargetClass(), that.getTargetClass())
        && Objects.equals(getFieldsToValues(), that.getFieldsToValues())
        && Objects.equals(root, that.root);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTargetClass(), getFieldsToValues(), root);
  }
}
