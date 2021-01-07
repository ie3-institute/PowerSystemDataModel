/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

/**
 * Denotes possible external ports of a connector. For two {@link Transformer2WInput}, {@link
 * ConnectorPort#A} is the high voltage and {@link ConnectorPort#B} the low voltage port. For {@link
 * Transformer3WInput}, {@link ConnectorPort#A} is the high voltage, {@link ConnectorPort#B} medium
 * voltage and {@link ConnectorPort#C} low voltage port
 */
public enum ConnectorPort {
  A,
  B,
  C
}
