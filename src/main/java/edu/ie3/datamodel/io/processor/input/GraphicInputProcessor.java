/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.input;

import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.measure.Quantity;

/**
 * Processor to deserialize all children of {@link GraphicInput} to a mapping from field name to
 * value (as string representation). All necessary processes are already apparent in the {@link
 * EntityProcessor}, therefore, no additional implementations have to be made.
 */
public class GraphicInputProcessor extends EntityProcessor<GraphicInput> {
  public static final List<Class<? extends GraphicInput>> eligibleEntityClasses =
      Collections.unmodifiableList(Arrays.asList(NodeGraphicInput.class, LineGraphicInput.class));

  public GraphicInputProcessor(Class<? extends GraphicInput> registeredClass) {
    super(registeredClass);
  }

  @Override
  protected Optional<String> handleProcessorSpecificQuantity(
      Quantity<?> quantity, String fieldName) {
    throw new UnsupportedOperationException(
        "No specific quantity handling required for graphic input models.");
  }

  @Override
  protected List<Class<? extends GraphicInput>> getAllEligibleClasses() {
    return eligibleEntityClasses;
  }
}
