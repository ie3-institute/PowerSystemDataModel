/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.io.source.SystemParticipantSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 06.04.20
 */
public class CsvSystemParticipantSource implements SystemParticipantSource {

  private static final Logger log = LogManager.getLogger(CsvSystemParticipantSource.class);

  // general fields
  private final CsvFileConnector connector;
  private final TypeSource typeSource;

  public CsvSystemParticipantSource(CsvFileConnector connector, TypeSource typeSource) {
    this.connector = connector;
    this.typeSource = typeSource;
  }

  // factories
  //    private final
  //    private final NodeInputFactory            nodeInputFactory;
  //    private final LineInputFactory            lineInputFactory;
  //    private final Transformer2WInputFactory   transformer2WInputFactory;
  //    private final Transformer3WInputFactory   transformer3WInputFactory;
  //    private final SwitchInputFactory          switchInputFactory;
  //    private final MeasurementUnitInputFactory measurementUnitInputFactory;
  //
  // stuff
  //    //  anyway
  //
  //    // field names
  //    private static final String OPERATOR_FIELD = "operator";
  //    private static final String NODE_A = "nodeA";
  //    private static final String NODE_B = "nodeB";
  //    private static final String TYPE = "type";

  //    public CsvRawGridSource(
  //                    String csvSep,
  //                    String gridFolderPath,
  //                    FileNamingStrategy fileNamingStrategy,
  //                    TypeSource typeSource) {
  //        super(csvSep);
  //        this.connector = new CsvFileConnector(gridFolderPath, fileNamingStrategy);
  //        this.typeSource = typeSource;
  //
  //        // init factories
  //        nodeInputFactory = new NodeInputFactory();
  //        lineInputFactory = new LineInputFactory();
  //        transformer2WInputFactory = new Transformer2WInputFactory();
  //        transformer3WInputFactory = new Transformer3WInputFactory();
  //        switchInputFactory = new SwitchInputFactory();
  //        measurementUnitInputFactory = new MeasurementUnitInputFactory();
  //    }

  @Override
  public SystemParticipants getSystemParticipants() {
    return null;
  }
}
