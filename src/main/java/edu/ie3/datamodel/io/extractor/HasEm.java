/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.models.input.EmInput;
import java.util.Optional;

/**
 * Interface that should be implemented by all elements that can be controlled by {@link
 * edu.ie3.datamodel.models.input.EmInput} elements and should be processable by the {@link
 * Extractor}.
 */
public interface HasEm extends NestedEntity {
  Optional<EmInput> getControllingEm();
}
