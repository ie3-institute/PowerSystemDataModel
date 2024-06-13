/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.models.result.ModelResultEntity;
import java.time.format.DateTimeFormatter;

public abstract class ModelResultFactory<T extends ModelResultEntity>
    extends ResultEntityFactory<T> {
  protected static final String INPUT_MODEL = "inputModel";

  protected ModelResultFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }

  protected ModelResultFactory(
      DateTimeFormatter dateTimeFormatter, Class<? extends T>... allowedClasses) {
    super(dateTimeFormatter, allowedClasses);
  }
}
