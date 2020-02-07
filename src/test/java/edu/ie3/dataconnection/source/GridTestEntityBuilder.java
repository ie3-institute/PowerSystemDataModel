/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source;

import com.opencsv.bean.CsvToBeanBuilder;
import edu.ie3.models.csv.*;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GridTestEntityBuilder {

  private static AggregatedRawGridInput aggregatedRawGridInput = new AggregatedRawGridInput();
  private static boolean fetchedGrid;

  public static void fillAggregatedRawGrid() {
    List<CsvNodeInput> csvNodeInputs = new LinkedList<>();
    try {
      String file = GridTestEntityBuilder.class.getClassLoader().getResource("nodes.csv").getFile();
      csvNodeInputs =
          new CsvToBeanBuilder(new FileReader(file))
              .withType(CsvNodeInput.class)
              .withSeparator(';')
              .build()
              .parse();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    Map<Integer, NodeInput> tidToNode =
        csvNodeInputs.stream()
            .collect(Collectors.toMap(CsvNodeInput::getTid, CsvNodeInput::toNodeInput));
    tidToNode.values().forEach(aggregatedRawGridInput::add);

    List<CsvLineInput> csvLineInputs = new LinkedList<>();
    try {
      String file = GridTestEntityBuilder.class.getClassLoader().getResource("lines.csv").getFile();
      csvLineInputs =
          new CsvToBeanBuilder(new FileReader(file))
              .withType(CsvLineInput.class)
              .withSeparator(';')
              .build()
              .parse();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    csvLineInputs.stream()
        .map(
            csvLine -> {
              List<Integer> requiredNodes = csvLine.getRequiredNodes();
              Map<Integer, NodeInput> requiredTidToNodes =
                  requiredNodes.stream().collect(Collectors.toMap(tid -> tid, tidToNode::get));
              return csvLine.toLineInput(requiredTidToNodes);
            })
        .forEach(aggregatedRawGridInput::add);

    List<CsvTrafo2WInput> csvTrafo2WInputs = new LinkedList<>();
    try {
      String file =
          GridTestEntityBuilder.class.getClassLoader().getResource("transformers.csv").getFile();
      csvTrafo2WInputs =
          new CsvToBeanBuilder(new FileReader(file))
              .withType(CsvTrafo2WInput.class)
              .withSeparator(';')
              .build()
              .parse();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    csvTrafo2WInputs.stream()
        .map(
            csvTrafo -> {
              List<Integer> requiredNodes = csvTrafo.getRequiredNodes();
              Map<Integer, NodeInput> requiredTidToNodes =
                  requiredNodes.stream().collect(Collectors.toMap(tid -> tid, tidToNode::get));
              return csvTrafo.toTransformer2WInput(requiredTidToNodes);
            })
        .forEach(aggregatedRawGridInput::add);

    List<CsvTrafo3WInput> csvTrafo3WInputs = new LinkedList<>();
    try {
      String file =
          GridTestEntityBuilder.class
              .getClassLoader()
              .getResource("transformers_three_windings.csv")
              .getFile();
      csvTrafo3WInputs =
          new CsvToBeanBuilder(new FileReader(file))
              .withType(CsvTrafo3WInput.class)
              .withSeparator(';')
              .build()
              .parse();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    csvTrafo3WInputs.stream()
        .map(
            csvTrafo -> {
              List<Integer> requiredNodes = csvTrafo.getRequiredNodes();
              Map<Integer, NodeInput> requiredTidToNodes =
                  requiredNodes.stream().collect(Collectors.toMap(tid -> tid, tidToNode::get));
              return csvTrafo.toTransformer3WInput(requiredTidToNodes);
            })
        .forEach(aggregatedRawGridInput::add);

    List<CsvSwitchInput> csvSwitchInputs = new LinkedList<>();
    try {
      String file =
          GridTestEntityBuilder.class.getClassLoader().getResource("switches.csv").getFile();
      csvSwitchInputs =
          new CsvToBeanBuilder(new FileReader(file))
              .withType(CsvSwitchInput.class)
              .withSeparator(';')
              .build()
              .parse();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    csvSwitchInputs.stream()
        .map(
            csvSwitch -> {
              List<Integer> requiredNodes = csvSwitch.getRequiredNodes();
              Map<Integer, NodeInput> requiredTidToNodes =
                  requiredNodes.stream().collect(Collectors.toMap(tid -> tid, tidToNode::get));
              return csvSwitch.toSwitchInput(requiredTidToNodes);
            })
        .forEach(aggregatedRawGridInput::add);

    fetchedGrid = true;
  }

  public static AggregatedRawGridInput getAggregatedRawGridInput() {
    if (!fetchedGrid) fillAggregatedRawGrid();
    return aggregatedRawGridInput;
  }
}
