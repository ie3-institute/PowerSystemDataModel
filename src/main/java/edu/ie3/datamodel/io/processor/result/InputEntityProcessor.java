package edu.ie3.datamodel.io.processor.result;

import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.models.input.InputEntity;

import javax.measure.Quantity;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;


/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 23.03.20
 */
public class InputEntityProcessor extends EntityProcessor<InputEntity> {
    /**
     * Create a new EntityProcessor
     *
     * @param registeredClass the class the entity processor should be able to handle
     */
    public InputEntityProcessor(Class<? extends InputEntity> registeredClass) {
        super(registeredClass);
    }

    @Override
    protected Optional<LinkedHashMap<String, String>> processEntity(InputEntity entity) {
        return Optional.empty();
    }

    @Override
    protected Optional<String> handleProcessorSpecificQuantity(Quantity<?> quantity, String fieldName) {
        return Optional.empty();
    }

    @Override
    protected List<Class<? extends InputEntity>> getAllEligibleClasses() {
        return null;
    }
}
