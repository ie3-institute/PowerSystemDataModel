/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * Enum listing different pre-defined storage operation strategies
 */
public enum StorageStrategy {
    /**
     * Storage behaves market oriented
     */
    MARKET_ORIENTED("market"),
    /**
     * Storage behaves grid oriented
     */
    GRID_ORIENTED("grid"),
    /**
     * Storage tries to maximize self consumption at the grid node
     */
    SELF_CONSUMPTION("self");

    /**
     * Token to recognize strategy from text based input
     */
    public final String token;

    StorageStrategy(String token) {
        this.token = token;
    }

    /**
     * Get the predefined storage strategy based on the given token
     *
     * @param token Token to check for
     * @return The corresponding storage strategy or throw {@link IllegalArgumentException}, if no
     * matching strategy is found
     */
    public static StorageStrategy get(String token) {
        return Arrays.stream(StorageStrategy.values())
                        .filter(storageStrategy -> storageStrategy.token.equalsIgnoreCase(token)).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                        "No predefined storage strategy with token '" + token +
                                        "' found. Please provide one of the followign tokens: " +
                                        Arrays.stream(StorageStrategy.values()).map(StorageStrategy::getToken)
                                                        .collect(Collectors.joining(", "))));
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "StorageStrategy{" + "token='" + token + '\'' + '}';
    }
}
