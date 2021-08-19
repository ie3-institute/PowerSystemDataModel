/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type.evcslocation;

/**
 * Describes type of location of an {@link edu.ie3.datamodel.models.input.system.EvcsInput}. Parsing
 * strings into one of these types is done in {@link EvcsLocationTypeUtils}.
 */
public enum EvcsLocationType {
  /** Charging at home (private home or apartment building, type: private location) */
  HOME,
  /** Charging at work (type: private location) */
  WORK,
  /** Charging at store parking lots (type: public location) */
  CUSTOMER_PARKING,
  /** Charging at street side (type: public location) */
  STREET,
  /** Charging at hub in town (type: public location) */
  CHARGING_HUB_TOWN,
  /** Charging at hub out of town, highway (type: public location) */
  CHARGING_HUB_HIGHWAY
}
