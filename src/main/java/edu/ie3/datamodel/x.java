/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.sink.CsvFileSink;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class x {
  public static void main(String[] args)
      throws SourceException, EntityProcessorException, InvalidGridException {
    Path path = new File("./input").toPath();
    FileNamingStrategy namingStrategy = new FileNamingStrategy();

    CsvDataSource dataSource = new CsvDataSource(",", path, namingStrategy);
    TypeSource typeSource = new TypeSource(dataSource);
    RawGridSource gridSource = new RawGridSource(typeSource, dataSource);

    List<UUID> uuids =
        Stream.of(
                "803c298b-61c6-412c-9b60-21cabc5bd945",
                "f66fc57d-f2be-41fd-bc60-d1177b091ac6",
                "f265e497-3a6d-4f96-9329-a7644cd8e785",
                "d3293c00-7bc8-434f-bfc8-b90cc2ff85be",
                "0cf49259-c126-4602-9b8a-764208d67914",
                "9ac19e4c-0379-4aaf-a96a-b2e71462abb3",
                "c940e435-0523-419a-90bc-f3dbf2e463f7",
                "d0f81106-444d-4832-ad0b-a293d719206a")
            .map(UUID::fromString)
            .toList();

    Map<String, LineTypeInput> types =
        typeSource.getLineTypes().values().stream()
            .filter(e -> uuids.contains(e.getUuid()))
            .collect(Collectors.toMap(AssetTypeInput::getId, Function.identity()));

    List<LineInput> lines =
        gridSource.getGridData().getLines().stream()
            .filter(l -> !uuids.contains(l.getType().getUuid()))
            .map(
                l -> {
                  LineTypeInput newType = types.get(l.getType().getId());
                  return l.copy().type(newType).build();
                })
            .toList();

    List<AssetInput> assetInputs = lines.stream().map(l -> (AssetInput) l).toList();

    JointGridContainer container =
        new JointGridContainer(
            "",
            new RawGridElements(assetInputs),
            new SystemParticipants((List<SystemParticipantInput>) Collections.EMPTY_LIST),
            new GraphicElements((List<GraphicInput>) Collections.EMPTY_LIST));

    CsvFileSink sink = new CsvFileSink(path.resolve("out"), namingStrategy, ",");
    sink.persistJointGrid(container);
  }
}
