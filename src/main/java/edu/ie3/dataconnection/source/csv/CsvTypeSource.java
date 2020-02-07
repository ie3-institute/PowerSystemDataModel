/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source.csv;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.connector.type.LineTypeInput;
import edu.ie3.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.util.quantities.interfaces.SpecificConductance;
import edu.ie3.util.quantities.interfaces.SpecificResistance;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import javax.measure.Quantity;
import javax.measure.quantity.*;
import tec.uom.se.quantity.Quantities;

public class CsvTypeSource {
  private static HashMap<Integer, LineTypeInput> tidToLineType = new HashMap<>();
  private static HashMap<Integer, Transformer2WTypeInput> tidToTrafo2WType = new HashMap<>();
  private static HashMap<Integer, Transformer3WTypeInput> tidToTrafo3WType = new HashMap<>();

  private static boolean filledLineTypes;
  private static boolean filledTrafo2WTypes;
  private static boolean filledTrafo3WTypes;

  public static void fillMaps() {
    if (!filledLineTypes) fillLineTypes();
    if (!filledTrafo2WTypes) fillTrafo2WTypes();
    if (!filledTrafo3WTypes) fillTrafo3WTypes();
  }

  public static void fillLineTypes() {
    CSVReader reader = null;
    try {
      CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
      String file =
          CsvCoordinateSource.class.getClassLoader().getResource("line_types.csv").getFile();
      reader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(parser).build();
      String[] nextLine = reader.readNext();

      int tidIndex = -1,
          bIndex = -1,
          capexIndex = -1,
          gIndex = -1,
          iMaxIndex = -1,
          idIndex = -1,
          opexIndex = -1,
          rIndex = -1,
          vRatedIndex = -1,
          xIndex = -1;
      if (nextLine == null) return;
      for (int i = 0; i < nextLine.length; i++) {
        if (nextLine[i].equals("tid")) tidIndex = i;
        if (nextLine[i].equals("b")) bIndex = i;
        if (nextLine[i].equals("capex")) capexIndex = i;
        if (nextLine[i].equals("g")) gIndex = i;
        if (nextLine[i].equals("i_max")) iMaxIndex = i;
        if (nextLine[i].equals("id")) idIndex = i;
        if (nextLine[i].equals("opex")) opexIndex = i;
        if (nextLine[i].equals("r")) rIndex = i;
        if (nextLine[i].equals("v_rated")) vRatedIndex = i;
        if (nextLine[i].equals("x")) xIndex = i;
      }
      while ((nextLine = reader.readNext()) != null) {
        Quantity<SpecificConductance> b =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[bIndex]), StandardUnits.SPECIFIC_ADMITTANCE);
        Quantity<SpecificConductance> g =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[gIndex]), StandardUnits.SPECIFIC_ADMITTANCE);
        Quantity<SpecificResistance> r =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[rIndex]), StandardUnits.SPECIFIC_IMPEDANCE);
        Quantity<SpecificResistance> x =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[xIndex]), StandardUnits.SPECIFIC_IMPEDANCE);
        Quantity<ElectricCurrent> iMax =
            Quantities.getQuantity(Double.parseDouble(nextLine[iMaxIndex]), StandardUnits.CURRENT);
        Quantity<ElectricPotential> vRated =
            Quantities.getQuantity(Double.parseDouble(nextLine[rIndex]), StandardUnits.V_RATED);
        LineTypeInput lineTypeInput =
            new LineTypeInput(null, nextLine[idIndex], b, g, r, x, iMax, vRated);
        tidToLineType.put(Integer.parseInt(nextLine[tidIndex]), lineTypeInput);
      }
    } catch (IOException | CsvValidationException e) {
      e.printStackTrace();
    }
    filledLineTypes = true;
  }

  public static void fillTrafo2WTypes() {
    CSVReader reader = null;
    try {
      CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
      String file =
          CsvCoordinateSource.class.getClassLoader().getResource("trafo2w_types.csv").getFile();
      reader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(parser).build();
      String[] nextLine = reader.readNext();

      int tidIndex = -1,
          bMIndex = -1,
          capexIndex = -1,
          dPhiIndex = -1,
          dVIndex = -1,
          gMIndex = -1,
          idIndex = -1,
          opexIndex = -1,
          rScIndex = -1,
          sRatedIndex = -1,
          tapMinIndex = -1,
          tapMaxIndex = -1,
          tapNeutrIndex = -1,
          tapSideIndex = -1,
          vHvIndex = -1,
          vLvIndex = -1,
          xScIndex = -1;
      if (nextLine == null) return;
      for (int i = 0; i < nextLine.length; i++) {
        if (nextLine[i].equals("tid")) tidIndex = i;
        if (nextLine[i].equals("b_m")) bMIndex = i;
        if (nextLine[i].equals("capex")) capexIndex = i;
        if (nextLine[i].equals("d_phi")) dPhiIndex = i;
        if (nextLine[i].equals("d_v")) dVIndex = i;
        if (nextLine[i].equals("g_m")) gMIndex = i;
        if (nextLine[i].equals("id")) idIndex = i;
        if (nextLine[i].equals("opex")) opexIndex = i;
        if (nextLine[i].equals("r_sc")) rScIndex = i;
        if (nextLine[i].equals("s_rated")) sRatedIndex = i;
        if (nextLine[i].equals("tap_min")) tapMinIndex = i;
        if (nextLine[i].equals("tap_max")) tapMaxIndex = i;
        if (nextLine[i].equals("tap_neutr")) tapNeutrIndex = i;
        if (nextLine[i].equals("tap_side")) tapSideIndex = i;
        if (nextLine[i].equals("v_hv")) vHvIndex = i;
        if (nextLine[i].equals("v_lv")) vLvIndex = i;
        if (nextLine[i].equals("x_sc")) xScIndex = i;
      }
      while ((nextLine = reader.readNext()) != null) {
        String id = nextLine[idIndex];
        Quantity<ElectricResistance> rSc =
            Quantities.getQuantity(Double.parseDouble(nextLine[rScIndex]), StandardUnits.IMPEDANCE);
        Quantity<ElectricResistance> xSc =
            Quantities.getQuantity(Double.parseDouble(nextLine[xScIndex]), StandardUnits.IMPEDANCE);
        Quantity<Power> sRated =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[sRatedIndex]), StandardUnits.S_RATED);
        Quantity<ElectricPotential> vRatedA =
            Quantities.getQuantity(Double.parseDouble(nextLine[vHvIndex]), StandardUnits.V_RATED);
        Quantity<ElectricPotential> vRatedB =
            Quantities.getQuantity(Double.parseDouble(nextLine[vLvIndex]), StandardUnits.V_RATED);
        Quantity<ElectricConductance> gM =
            Quantities.getQuantity(Double.parseDouble(nextLine[gMIndex]), StandardUnits.ADMITTANCE);
        Quantity<ElectricConductance> bM =
            Quantities.getQuantity(Double.parseDouble(nextLine[bMIndex]), StandardUnits.ADMITTANCE);
        Quantity<Dimensionless> dV =
            Quantities.getQuantity(Double.parseDouble(nextLine[dVIndex]), StandardUnits.DV_TAP);
        Quantity<Angle> dPhi =
            Quantities.getQuantity(Double.parseDouble(nextLine[dPhiIndex]), StandardUnits.DPHI_TAP);
        boolean tapSide = Boolean.parseBoolean(nextLine[tapSideIndex]);
        int tapNeutr = Integer.parseInt(nextLine[tapNeutrIndex]);
        int tapMin = Integer.parseInt(nextLine[tapMinIndex]);
        int tapMax = Integer.parseInt(nextLine[tapMaxIndex]);
        Transformer2WTypeInput transformer2WTypeInput =
            new Transformer2WTypeInput(
                null, id, rSc, xSc, sRated, vRatedA, vRatedB, gM, bM, dV, dPhi, tapSide, tapNeutr,
                tapMin, tapMax);
        tidToTrafo2WType.put(Integer.parseInt(nextLine[tidIndex]), transformer2WTypeInput);
      }
    } catch (IOException | CsvValidationException e) {
      e.printStackTrace();
    }
    filledTrafo2WTypes = true;
  }

  public static void fillTrafo3WTypes() {
    CSVReader reader = null;
    try {
      CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
      String file =
          CsvCoordinateSource.class.getClassLoader().getResource("trafo3w_types.csv").getFile();
      reader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(parser).build();
      String[] nextLine = reader.readNext();

      int tidIndex = -1,
          bMIndex = -1,
          capexIndex = -1,
          dPhiIndex = -1,
          dVIndex = -1,
          gMIndex = -1,
          idIndex = -1,
          opexIndex = -1,
          rScAIndex = -1,
          rScBIndex = -1,
          rScCIndex = -1,
          sRatedAIndex = -1,
          sRatedBIndex = -1,
          sRatedCIndex = -1,
          tapMinIndex = -1,
          tapMaxIndex = -1,
          tapNeutrIndex = -1,
          tapSideIndex = -1,
          vAIndex = -1,
          vBIndex = -1,
          vCIndex = -1,
          xScAIndex = -1,
          xScBIndex = -1,
          xScCIndex = -1;

      if (nextLine == null) return;
      for (int i = 0; i < nextLine.length; i++) {
        if (nextLine[i].equals("tid")) tidIndex = i;
        if (nextLine[i].equals("b_m")) bMIndex = i;
        if (nextLine[i].equals("capex")) capexIndex = i;
        if (nextLine[i].equals("d_phi")) dPhiIndex = i;
        if (nextLine[i].equals("d_v")) dVIndex = i;
        if (nextLine[i].equals("g_m")) gMIndex = i;
        if (nextLine[i].equals("id")) idIndex = i;
        if (nextLine[i].equals("opex")) opexIndex = i;
        if (nextLine[i].equals("r_sc_a")) rScAIndex = i;
        if (nextLine[i].equals("r_sc_b")) rScBIndex = i;
        if (nextLine[i].equals("r_sc_c")) rScCIndex = i;
        if (nextLine[i].equals("s_rated_a")) sRatedAIndex = i;
        if (nextLine[i].equals("s_rated_b")) sRatedBIndex = i;
        if (nextLine[i].equals("s_rated_c")) sRatedCIndex = i;
        if (nextLine[i].equals("tap_min")) tapMinIndex = i;
        if (nextLine[i].equals("tap_max")) tapMaxIndex = i;
        if (nextLine[i].equals("tap_neutr")) tapNeutrIndex = i;
        if (nextLine[i].equals("tap_side")) tapSideIndex = i;
        if (nextLine[i].equals("v_a")) vAIndex = i;
        if (nextLine[i].equals("v_b")) vBIndex = i;
        if (nextLine[i].equals("v_c")) vCIndex = i;
        if (nextLine[i].equals("x_sc_a")) xScAIndex = i;
        if (nextLine[i].equals("x_sc_b")) xScBIndex = i;
        if (nextLine[i].equals("x_sc_c")) xScCIndex = i;
      }
      while ((nextLine = reader.readNext()) != null) {
        String id = nextLine[idIndex];
        Quantity<Power> sRatedA =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[sRatedAIndex]), StandardUnits.S_RATED);
        ; // Hv
        Quantity<Power> sRatedB =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[sRatedBIndex]), StandardUnits.S_RATED);
        ; // Mv
        Quantity<Power> sRatedC =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[sRatedCIndex]), StandardUnits.S_RATED);
        ; // Lv
        Quantity<ElectricPotential> vRatedA =
            Quantities.getQuantity(Double.parseDouble(nextLine[vAIndex]), StandardUnits.V_RATED);
        ; // Hv
        Quantity<ElectricPotential> vRatedB =
            Quantities.getQuantity(Double.parseDouble(nextLine[vBIndex]), StandardUnits.V_RATED);
        ; // Mv
        Quantity<ElectricPotential> vRatedC =
            Quantities.getQuantity(Double.parseDouble(nextLine[vCIndex]), StandardUnits.V_RATED);
        ; // Lv
        Quantity<ElectricResistance> rScA =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[rScAIndex]), StandardUnits.IMPEDANCE); // Hv
        Quantity<ElectricResistance> rScB =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[rScBIndex]), StandardUnits.IMPEDANCE); // Mv
        Quantity<ElectricResistance> rScC =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[rScCIndex]), StandardUnits.IMPEDANCE); // Lv
        Quantity<ElectricResistance> xScA =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[xScAIndex]), StandardUnits.IMPEDANCE); // Hv
        Quantity<ElectricResistance> xScB =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[xScBIndex]), StandardUnits.IMPEDANCE); // Mv
        Quantity<ElectricResistance> xScC =
            Quantities.getQuantity(
                Double.parseDouble(nextLine[xScCIndex]), StandardUnits.IMPEDANCE); // Lv
        Quantity<ElectricConductance> gM =
            Quantities.getQuantity(Double.parseDouble(nextLine[gMIndex]), StandardUnits.ADMITTANCE);
        Quantity<ElectricConductance> bM =
            Quantities.getQuantity(Double.parseDouble(nextLine[bMIndex]), StandardUnits.ADMITTANCE);
        Quantity<Dimensionless> dV =
            Quantities.getQuantity(Double.parseDouble(nextLine[dVIndex]), StandardUnits.DV_TAP);
        Quantity<Angle> dPhi =
            Quantities.getQuantity(Double.parseDouble(nextLine[dPhiIndex]), StandardUnits.DPHI_TAP);
        int tapNeutr = Integer.parseInt(nextLine[tapNeutrIndex]);
        int tapMin = Integer.parseInt(nextLine[tapMinIndex]);
        int tapMax = Integer.parseInt(nextLine[tapMaxIndex]);
        Transformer3WTypeInput transformer3WTypeInput =
            new Transformer3WTypeInput(
                null, id, sRatedA, sRatedB, sRatedC, vRatedA, vRatedB, vRatedC, rScA, rScB, rScC,
                xScA, xScB, xScC, gM, bM, dV, dPhi, tapNeutr, tapMin, tapMax);
        tidToTrafo3WType.put(Integer.parseInt(nextLine[tidIndex]), transformer3WTypeInput);
      }
    } catch (IOException | CsvValidationException e) {
      e.printStackTrace();
    }
    filledTrafo3WTypes = true;
  }

  public static LineTypeInput getLineType(int tid) {
    return tidToLineType.get(tid);
  }

  public static Transformer2WTypeInput getTrafo2WType(int tid) {
    return tidToTrafo2WType.get(tid);
  }

  public static Transformer3WTypeInput getTrafo3WType(int tid) {
    return tidToTrafo3WType.get(tid);
  }
}
