package edu.ie3.datamodel.io.factory.deserializing;

import java.util.Optional;

public interface Parser<T> {

    Optional<T> parse(String valueToParse);

    Class<T> getType();

}
