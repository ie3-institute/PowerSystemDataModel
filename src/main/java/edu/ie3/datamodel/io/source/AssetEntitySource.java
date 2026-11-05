/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

/** Class that provides all functionalities to build asset entities */
public abstract class AssetEntitySource extends EntitySource {

  protected final DataSource dataSource;

  protected AssetEntitySource(DataSource dataSource) {
    this.dataSource = dataSource;
  }
}
