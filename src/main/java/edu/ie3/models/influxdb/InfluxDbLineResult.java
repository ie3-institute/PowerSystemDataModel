/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.influxdb;

import edu.ie3.models.result.connector.LineResult;
import java.time.Instant;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "line_result")
public class InfluxDbLineResult extends InfluxDbEntity<LineResult> {
  @Column(name = "input_uuid", tag = true)
  String input_uuid;

  @Column(name = "uuid")
  String uuid;

  @Column(name = "iAMag")
  Double iAMag;

  @Column(name = "iAAng")
  Double iAAng;

  @Column(name = "iBMag")
  Double iBMag;

  @Column(name = "iBAng")
  Double iBAng;

  public InfluxDbLineResult(
      Instant time,
      String input_uuid,
      String uuid,
      Double iAMag,
      Double iAAng,
      Double iBMag,
      Double iBAng) {
    super(time);
    this.input_uuid = input_uuid;
    this.uuid = uuid;
    this.iAMag = iAMag;
    this.iAAng = iAAng;
    this.iBMag = iBMag;
    this.iBAng = iBAng;
  }

  public InfluxDbLineResult() {}

  public InfluxDbLineResult(LineResult lineResult) {
    this.time = lineResult.getTimestamp().toInstant();
    this.input_uuid = lineResult.getInputModel().toString();
    this.uuid = lineResult.getUuid().toString();
    this.iAMag = lineResult.getiAMag().getValue().doubleValue();
    this.iAAng = lineResult.getiAAng().getValue().doubleValue();
    this.iBMag = lineResult.getiBMag().getValue().doubleValue();
    this.iBAng = lineResult.getiBAng().getValue().doubleValue();
  }
}
