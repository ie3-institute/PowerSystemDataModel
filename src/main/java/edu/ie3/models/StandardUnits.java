/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models;

import static edu.ie3.util.quantities.PowerSystemUnits.*;

import edu.ie3.util.quantities.interfaces.*;
import javax.measure.Unit;
import javax.measure.quantity.*;
import tec.uom.se.unit.MetricPrefix;

/** A collection of Units that fit to the different input and output models by convention */
public class StandardUnits {

  /* Electrical units */
  /** Rated apparent power (mainly for input purposes) */
  public static final Unit<Power> S_RATED = KILOVOLTAMPERE;
  /** Active power for input purposes */
  public static final Unit<Power> ACTIVE_POWER_IN = KILOWATT;
  /** Reactive power for input purposes */
  public static final Unit<Power> REACTIVE_POWER_IN = KILOVAR;
  /** Active power for result purposes */
  public static final Unit<Power> ACTIVE_POWER_RESULT = MEGAWATT;
  /** Reactive power for result purposes */
  public static final Unit<Power> REACTIVE_POWER_RESULT = MEGAVAR;
  /** Energy for input purposes */
  public static final Unit<Energy> ENERGY_IN = KILOWATTHOUR;
  /** Rated voltage magnitude (mainly for input purposes) */
  public static final Unit<ElectricPotential> RATED_VOLTAGE_MAGNITUDE = KILOVOLT;
  /** Magnitude of an electric current */
  public static final Unit<ElectricCurrent> ELECTRIC_CURRENT_MAGNITUDE = AMPERE;
  /** Angle of an electric current */
  public static final Unit<Angle> ELECTRIC_CURRENT_ANGLE = DEGREE_GEOM;
  /** Impedance */
  public static final Unit<ElectricResistance> IMPEDANCE = OHM;
  /** Impedance per length (mainly for lines) */
  public static final Unit<SpecificResistance> IMPEDANCE_PER_LENGTH = OHM_PER_KILOMETRE;
  /** Admittance */
  public static final Unit<ElectricConductance> ADMITTANCE = MetricPrefix.NANO(SIEMENS);
  /** Admittance per length (mainly for lines) */
  public static final Unit<SpecificConductance> ADMITTANCE_PER_LENGTH = MetricPrefix.NANO(SIEMENS_PER_KILOMETRE);
  /** Target voltage magnitude */
  public static final Unit<Dimensionless> TARGET_VOLTAGE_MAGNITUDE = PU;
  /** Voltage magnitude (mainly for result purposes) */
  public static final Unit<Dimensionless> VOLTAGE_MAGNITUDE = PU;
  /** Voltage angle (mainly for result purposes) */
  public static final Unit<Angle> VOLTAGE_ANGLE = DEGREE_GEOM;

  /* Other Units */
  /** Voltage magnitude variation per tap (for transformers) */
  public static final Unit<Dimensionless> DV_TAP = PERCENT;
  /** Voltage angle variation per tap (for transformers) */
  public static final Unit<Angle> DPHI_TAP = DEGREE_GEOM;
  /** Gradient, with which a system participant can change it's set point (with regard to rated power) */
  public static final Unit<DimensionlessRate> LOAD_GRADIENT = PERCENT_PER_HOUR;
  /** Efficiency of a process */
  public static final Unit<Dimensionless> EFFICIENCY = PERCENT;
  /** Volume */
  public static final Unit<Volume> VOLUME = CUBIC_METRE;
  /** Temperature */
  public static final Unit<Temperature> TEMPERATURE = CELSIUS;
  /** Heat demand of a thermal sink */
  public static final Unit<Energy> HEAT_DEMAND = MetricPrefix.MEGA(WATTHOUR);
  /** Thermal capacity of a given mass */
  public static final Unit<HeatCapacity> HEAT_CAPACITY = KILOWATTHOUR_PER_KELVIN;
  /** Thermal capacity per mass */
  public static final Unit<SpecificHeatCapacity> SPECIFIC_HEAT_CAPACITY = KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE;
  /** Thermal transmission through an insulation */
  public static final Unit<ThermalConductance> THERMAL_TRANSMISSION = KILOWATT_PER_KELVIN;
  /** Depth of discharge */
  public static final Unit<Dimensionless> DOD = PERCENT;
  /** Life time of a system */
  public static final Unit<Time> LIFE_TIME = MILLISECOND;
  /** Area covered by the rotor of a wind energy converter */
  public static final Unit<Area> ROTOR_AREA = SQUARE_METRE;
  /** Height of the hub of an wind energy converter */
  public static final Unit<Length> HUB_HEIGHT = METRE;
  /** Price per energy */
  public static final Unit<EnergyPrice> ENERGY_PRICE = EURO_PER_MEGAWATTHOUR;
  /** Orientation of a pv panel with regard to the north-south line */
  public static final Unit<Angle> AZIMUTH = DEGREE_GEOM;
  /** Elevation of a pv panel with regard to the plane */
  public static final Unit<Angle> SOLAR_HEIGHT = DEGREE_GEOM;
  /** Direction of the wind */
  public static final Unit<Angle> WIND_DIRECTION = DEGREE_GEOM;
  /** Velocity of the wind */
  public static final Unit<Speed> WIND_VELOCITY = METRE_PER_SECOND;
  /** Solar irradiation on a flat surface */
  public static final Unit<Irradiation> IRRADIATION = KILOWATTHOUR_PER_SQUAREMETRE;

  private StandardUnits() {
    throw new IllegalStateException("This is an Utility Class and not meant to be instantiated");
  }
}
