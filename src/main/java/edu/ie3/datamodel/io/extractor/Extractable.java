/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

/**
 * Private API interface to prevent implementation of {@link NestedEntity} outside of this package.
 * This allows for an exhaustive, pattern matching alike usage of the {@link NestedEntity} interface
 * in {@link Extractor}
 */
interface Extractable {}
