package edu.ie3.datamodel.io.factory.deserializing;

import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.Map;
import java.util.Optional;

import static edu.ie3.util.quantities.PowerSystemUnits.PU;

//TODO
public class QuantityDeserializingStrategy implements DeserializingStrategy<Quantity> {
    @Override
    public Optional<Quantity> deserialize(Map<String, String> fieldMap) {
        return Optional.of(Quantities.getQuantity(1d, PU));
    }

    @Override
    public Optional<Quantity> deserialize(Map<String, String> fieldMap, Map<String, Object> deserializedFields) {
        return Optional.of(Quantities.getQuantity(1d, PU));
    }

    @Override
    public Class<Quantity> getType() {
        return Quantity.class;
    }
}
