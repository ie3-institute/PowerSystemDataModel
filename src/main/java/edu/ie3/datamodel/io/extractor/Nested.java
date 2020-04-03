/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.extractor;

/**
 * This interface should be implemented only by other interfaces that should be used by the {@link
 * Extractor} It provides the entry point for the extraction method in the {@link Extractor}. If
 * this interface is implemented by other interfaces one has to take care about, that the
 * corresponding method {@link Extractor.extractElements()} is extended accordingly.
 *
 * @version 0.1
 * @since 31.03.20
 */
public interface Nested {}
