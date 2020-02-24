package edu.ie3.models.neo4j;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import java.util.Date;

@RelationshipEntity(type = "SWITCH")
public class Neo4JSwitchInput {

    private String uuid;
    private Integer tid;
    private Boolean closed;
    private String id;
    private Boolean in_operation;
    @DateString
    private Date operates_from;
    @DateString
    private Date operates_until;
    private String scenario;
    @StartNode
    private Neo4JNodeInput nodeA;
    @EndNode
    private Neo4JNodeInput nodeB;

    public Neo4JSwitchInput() {
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIn_operation() {
        return in_operation;
    }

    public void setIn_operation(Boolean in_operation) {
        this.in_operation = in_operation;
    }

    public Date getOperates_from() {
        return operates_from;
    }

    public void setOperates_from(Date operates_from) {
        this.operates_from = operates_from;
    }

    public Date getOperates_until() {
        return operates_until;
    }

    public void setOperates_until(Date operates_until) {
        this.operates_until = operates_until;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public Neo4JNodeInput getNodeA() {
        return nodeA;
    }

    public void setNodeA(Neo4JNodeInput nodeA) {
        this.nodeA = nodeA;
    }

    public Neo4JNodeInput getNodeB() {
        return nodeB;
    }

    public void setNodeB(Neo4JNodeInput nodeB) {
        this.nodeB = nodeB;
    }
}
