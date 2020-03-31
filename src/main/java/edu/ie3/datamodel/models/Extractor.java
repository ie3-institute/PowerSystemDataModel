/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import edu.ie3.datamodel.models.input.InputEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 31.03.20
 */
public class Extractor {

  private final List<InputEntity> extractedElements;

  public Extractor(Nested nestedEntity) {
    this.extractedElements = extractElements(nestedEntity);
  }

  private List<InputEntity> extractElements(Nested nestedEntity) {
    List<InputEntity> resultingList = new ArrayList<>();
    if (nestedEntity instanceof Nodes) {
      resultingList.addAll(((Nodes) nestedEntity).getNodes());
    }
    if (nestedEntity instanceof Type) {
      resultingList.add(((Type) nestedEntity).getType());
    }

    return Collections.unmodifiableList(resultingList);
  }

  public List<InputEntity> getExtractedElements() {
    return extractedElements;
  }
}
