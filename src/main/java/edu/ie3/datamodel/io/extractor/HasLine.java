package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.models.input.connector.LineInput;


/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 01.04.20
 */
public interface HasLine extends NestedEntity {

    LineInput getLine();
}
