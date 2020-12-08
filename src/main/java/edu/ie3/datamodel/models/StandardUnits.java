/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import static edu.ie3.util.quantities.PowerSystemUnits.*;

import edu.ie3.util.quantities.interfaces.*;
import javax.measure.MetricPrefix;
import javax.measure.Unit;
import javax.measure.quantity.*;

/** A collection of Units that fit to the different input and output models by convention */
public class StandardUnits {

  /* Electrical units */
  /** Rated apparent power (mainly for input purposes) in kVA */
  public static final Unit<Power> S_RATED = KILOVOLTAMPERE;
  /** Active power for input purposes in kW */
  public static final Unit<Power> ACTIVE_POWER_IN = KILOWATT;
  /** Reactive power for input purposes in kVAr */
  public static final Unit<Power> REACTIVE_POWER_IN = KILOVAR;
  /** Active power for result purposes in MW */
  public static final Unit<Power> ACTIVE_POWER_RESULT = MEGAWATT;
  /** Reactive power for result purposes in MVAr */
  public static final Unit<Power> REACTIVE_POWER_RESULT = MEGAVAR;
  /** Heat demand (power) for result purposes in MW */
  public static final Unit<Power> Q_DOT_RESULT = MEGAWATT;
  /** Energy for input purposes in kWh */
  public static final Unit<Energy> ENERGY_IN = KILOWATTHOUR;
  /** Energy for result purposes in MWh */
  public static final Unit<Energy> ENERGY_RESULT = MetricPrefix.MEGA(WATTHOUR);
  /** Rated voltage magnitude (mainly for input purposes) in kV */
  public static final Unit<ElectricPotential> RATED_VOLTAGE_MAGNITUDE = KILOVOLT;
  /** Magnitude of an electric current in A */
  public static final Unit<ElectricCurrent> ELECTRIC_CURRENT_MAGNITUDE = AMPERE;
  /** Angle of an electric current in ° (arc degree) */
  public static final Unit<Angle> ELECTRIC_CURRENT_ANGLE = DEGREE_GEOM;
  /** Impedance in Ω */
  public static final Unit<ElectricResistance> IMPEDANCE = OHM;
  /** Impedance per length (mainly for lines) in Ω/km */
  public static final Unit<SpecificResistance> IMPEDANCE_PER_LENGTH = OHM_PER_KILOMETRE;
  /** Admittance in nS */
  public static final Unit<ElectricConductance> ADMITTANCE = MetricPrefix.NANO(SIEMENS);
  /** Admittance per length (mainly for lines) in µS/km */
  public static final Unit<SpecificConductance> ADMITTANCE_PER_LENGTH = MICRO_SIEMENS_PER_KILOMETRE;
  /** Target voltage magnitude in p.U. */
  public static final Unit<Dimensionless> TARGET_VOLTAGE_MAGNITUDE = PU;
  /** Voltage magnitude (mainly for result purposes) in p.U. */
  public static final Unit<Dimensionless> VOLTAGE_MAGNITUDE = PU;
  /** Voltage angle (mainly for result purposes) in ° (arc degree) */
  public static final Unit<Angle> VOLTAGE_ANGLE = DEGREE_GEOM;
  /** Electric energy per driven distance in kWh/km */
  public static final Unit<SpecificEnergy> ENERGY_PER_DISTANCE = KILOWATTHOUR_PER_KILOMETRE;

  /* Other Units */
  /** Voltage magnitude variation per tap (for transformers) in % */
  public static final Unit<Dimensionless> DV_TAP = PERCENT;
  /** Voltage angle variation per tap (for transformers) in ° (arc degree) */
  public static final Unit<Angle> DPHI_TAP = DEGREE_GEOM;
  /**
   * Gradient, with which a system participant can change it's set point (with regard to rated
   * active power = rated apparent power * rated power factor) in %/h
   */
  public static final Unit<DimensionlessRate> ACTIVE_POWER_GRADIENT = PERCENT_PER_HOUR;
  /** Efficiency of a process in % */
  public static final Unit<Dimensionless> EFFICIENCY = PERCENT;
  /** Volume in m³ */
  public static final Unit<Volume> VOLUME = CUBIC_METRE;
  /** Fill level of a storage in % */
  public static final Unit<Dimensionless> FILL_LEVEL = PERCENT;
  /** Temperature in °C */
  public static final Unit<Temperature> TEMPERATURE = CELSIUS;
  /** Heat demand of a thermal sink in MW */
  public static final Unit<Power> HEAT_DEMAND = MEGAWATT;
  /** Heat demand, that is delivered via a profile in kW */
  public static final Unit<Power> HEAT_DEMAND_PROFILE = KILOWATT;
  /** Thermal capacity of a given mass in kWh/K */
  public static final Unit<HeatCapacity> HEAT_CAPACITY = KILOWATTHOUR_PER_KELVIN;
  /** Thermal capacity per mass kWh*m³/K */
  public static final Unit<SpecificHeatCapacity> SPECIFIC_HEAT_CAPACITY =
      KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE;
  /** Thermal transmission through an insulation in kW/K */
  public static final Unit<ThermalConductance> THERMAL_TRANSMISSION = KILOWATT_PER_KELVIN;
  /** Depth of discharge in % */
  public static final Unit<Dimensionless> DOD = PERCENT;
  /** State of charge in % */
  public static final Unit<Dimensionless> SOC = PERCENT;
  /** Life time of a system in h */
  public static final Unit<Time> LIFE_TIME = HOUR;
  /** Area covered by the rotor of a wind energy converter in m² */
  public static final Unit<Area> ROTOR_AREA = SQUARE_METRE;
  /** Height of the hub of an wind energy converter in metre */
  public static final Unit<Length> HUB_HEIGHT = METRE;
  /** Price per energy in euro_per_MWhour */
  public static final Unit<EnergyPrice> ENERGY_PRICE = EURO_PER_MEGAWATTHOUR;
  /** Orientation of a pv panel with regard to the north-south line in degree_geom */
  public static final Unit<Angle> AZIMUTH = DEGREE_GEOM;
  /** Elevation of a pv panel with regard to the plane in degree_geom */
  public static final Unit<Angle> SOLAR_HEIGHT = DEGREE_GEOM;
  /** Direction of the wind in degree_geom */
  public static final Unit<Angle> WIND_DIRECTION = DEGREE_GEOM;
  /** Velocity of the wind in metre_per_second */
  public static final Unit<Speed> WIND_VELOCITY = METRE_PER_SECOND;
  /** Standard unit for the Betz' coefficient curve of wind energy converters in p.U. */
  public static final Unit<Dimensionless> CP_CHARACTERISTIC = PU;
  /** Standard unit for the ev charging curve in p.U. */
  public static final Unit<Dimensionless> EV_CHARACTERISTIC = PU;
  /** Standard unit for the overhead line monitoring characteristic in p.U. */
  public static final Unit<Dimensionless> OLM_CHARACTERISTIC = PU;
  /** Standard unit for reactive power characteristics in p.U. */
  public static final Unit<Dimensionless> Q_CHARACTERISTIC = PU;
  /** Solar irradiation on a flat surface as Power per Area in kW/m² */
  public static final Unit<Irradiation> IRRADIATION = KILOWATT_PER_SQUAREMETRE;
  /**
   * Energy per Area, used as an alternative dimension for {@link StandardUnits#IRRADIATION} in
   * kWh/m²
   */
  public static final Unit<EnergyDensity> ENERGY_DENSITY = KILOWATTHOUR_PER_SQUAREMETRE;
  /** Capex (capital expenditure) in € */
  public static final Unit<Currency> CAPEX = EURO;
  /** Length of a line in km */
  public static final Unit<Length> LINE_LENGTH = KILOMETRE;

  private StandardUnits() {
    throw new IllegalStateException("This is an Utility Class and not meant to be instantiated");
  }
}
