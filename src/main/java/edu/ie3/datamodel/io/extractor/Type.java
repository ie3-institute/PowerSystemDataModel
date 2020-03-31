/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.models.input.AssetTypeInput;

/**
 * //ToDo: Class Description // todo uml
 *
 * @version 0.1
 * @since 31.03.20
 */
public interface Type extends Nested {

  AssetTypeInput getType();
}
