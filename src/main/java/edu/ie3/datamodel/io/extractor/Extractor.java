/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.extractor;

import edu.ie3.datamodel.exceptions.ExtractorException;
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

    private final List<InputEntity> extractedEntities;

    public Extractor(Nested nestedEntity) throws ExtractorException {
        this.extractedEntities = extractElements(nestedEntity);
    }

    private List<InputEntity> extractElements(Nested nestedEntity) throws ExtractorException {
        List<InputEntity> resultingList = new ArrayList<>();
        if(nestedEntity instanceof Nodes) {
            resultingList.addAll(((Nodes) nestedEntity).getNodes());
        }
        if(nestedEntity instanceof Type) {
            resultingList.add(((Type) nestedEntity).getType());
        } else {
            throw new ExtractorException(
                            "The interface 'Nested' is not meant to be extended and cannot be processed by " +
                            "the extractor! Currently only the interfaces ‘Nodes‘ and ‘Type' are supported!");
        }

        return Collections.unmodifiableList(resultingList);
    }

    public List<InputEntity> getExtractedEntities() {
        return extractedEntities;
    }
}
