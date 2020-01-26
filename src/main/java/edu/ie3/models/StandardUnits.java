/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models;

import static edu.ie3.util.quantities.PowerSystemUnits.*;

import javax.measure.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Area;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricConductance;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;
import javax.measure.quantity.Volume;

import edu.ie3.util.quantities.interfaces.*;
import tec.uom.se.unit.MetricPrefix;

/** A collection of Units that fit to the different input and output models by convention */
public class StandardUnits {

  /* Electrical units */
  public static final Unit<Power> S_RATED = KILOVOLTAMPERE;
  public static final Unit<Power> ACTIVE_POWER_IN = KILOWATT;
  public static final Unit<Power> REACTIVE_POWER_IN = KILOVAR;
  public static final Unit<Power> ACTIVE_POWER_OUT = MEGAWATT;
  public static final Unit<Power> REACTIVE_POWER_OUT = MEGAVAR;
  public static final Unit<Energy> ENERGY = KILOWATTHOUR;
  public static final Unit<ElectricPotential> V_RATED = KILOVOLT;
  public static final Unit<ElectricCurrent> CURRENT = AMPERE;
  public static final Unit<ElectricResistance> IMPEDANCE = OHM;
  public static final Unit<SpecificResistance> SPECIFIC_IMPEDANCE = OHM_PER_KILOMETRE;
  public static final Unit<ElectricConductance> ADMITTANCE = MetricPrefix.NANO(SIEMENS);
  public static final Unit<SpecificConductance> SPECIFIC_ADMITTANCE =
      MetricPrefix.NANO(SIEMENS_PER_KILOMETRE);

  /* Other Units */
  public static final Unit<Dimensionless> TARGET_VOLTAGE = PU;
  public static final Unit<Dimensionless> DV_TAP = PERCENT;
  public static final Unit<Angle>             DPHI_TAP      = DEGREE_GEOM;
  public static final Unit<DimensionlessRate> LOAD_GRADIENT = PERCENT_PER_HOUR;
  public static final Unit<Dimensionless>     EFFICIENCY    = PERCENT;
  public static final Unit<Volume> VOLUME = CUBIC_METRE;
  public static final Unit<Temperature>          TEMPERATURE            = CELSIUS;
  public static final Unit<HeatCapacity>         HEAT_CAPACITY          = KILOWATTHOUR_PER_KELVIN;
  public static final Unit<SpecificHeatCapacity> SPECIFIC_HEAT_CAPACITY =
      KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE;
  public static final Unit<ThermalConductance>   THERMAL_TRANSMISSION   = KILOWATT_PER_KELVIN;
  public static final Unit<Dimensionless>        DOD                    = PERCENT;
  public static final Unit<Time> LIFE_TIME = MILLISECOND;
  public static final Unit<Area> ROTOR_AREA = SQUARE_METRE;
  public static final Unit<Length>      HUB_HEIGHT   = METRE;
  public static final Unit<EnergyPrice> ENERGY_PRICE = EURO_PER_MEGAWATTHOUR;
  public static final Unit<Angle>       AZIMUTH      = DEGREE_GEOM;
  public static final Unit<Angle> SOLAR_HEIGHT = DEGREE_GEOM;
  public static final Unit<Angle> WIND_DIRECTION = DEGREE_GEOM;
  public static final Unit<Speed> WIND_VELOCITY = METRE_PER_SECOND;
  public static final Unit<Irradiation> IRRADIATION = KILOWATTHOUR_PER_SQUAREMETRE;

  private StandardUnits() {
    throw new IllegalStateException("This is an Utility Class and not meant to be instantiated");
  }
}
