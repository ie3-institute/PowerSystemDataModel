/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.hibernate.output;

import edu.ie3.models.result.connector.LineResult;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.Column;

public class HibernateLineResult implements HibernateResult {

  @Column(name = "datum")
  ZonedDateTime date;

  @Column(name = "input_line", columnDefinition = "uuid")
  UUID input_uuid;

  @Column(name = "uuid", columnDefinition = "uuid")
  UUID uuid;

  @Column(name = "iAMag")
  Double iAMag;

  @Column(name = "iAAng")
  Double iAAng;

  @Column(name = "iBMag")
  Double iBMag;

  @Column(name = "iBAng")
  Double iBAng;

  public HibernateLineResult() {}

  public HibernateLineResult(LineResult lineResult) {
    date = lineResult.getTimestamp();
    input_uuid = lineResult.getInputModel();
    uuid = lineResult.getUuid();
    iAMag = lineResult.getiAMag().getValue().doubleValue();
    iAAng = lineResult.getiAAng().getValue().doubleValue();
    iBMag = lineResult.getiBMag().getValue().doubleValue();
    iBAng = lineResult.getiBAng().getValue().doubleValue();
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public UUID getInput_uuid() {
    return input_uuid;
  }

  public void setInput_uuid(UUID input_uuid) {
    this.input_uuid = input_uuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public Double getiAMag() {
    return iAMag;
  }

  public void setiAMag(Double iAMag) {
    this.iAMag = iAMag;
  }

  public Double getiAAng() {
    return iAAng;
  }

  public void setiAAng(Double iAAng) {
    this.iAAng = iAAng;
  }

  public Double getiBMag() {
    return iBMag;
  }

  public void setiBMag(Double iBMag) {
    this.iBMag = iBMag;
  }

  public Double getiBAng() {
    return iBAng;
  }

  public void setiBAng(Double iBAng) {
    this.iBAng = iBAng;
  }
}
