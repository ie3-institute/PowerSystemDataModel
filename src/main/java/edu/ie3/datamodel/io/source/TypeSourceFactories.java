package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.input.OperatorInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.LineTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.SystemParticipantTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.Transformer2WTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.Transformer3WTypeInputFactory;

public class TypeSourceFactories {

    public static OperatorInputFactory operatorInputFactory = new OperatorInputFactory();
    public static Transformer2WTypeInputFactory transformer2WTypeInputFactory = new Transformer2WTypeInputFactory();
    public static LineTypeInputFactory lineTypeInputFactory = new LineTypeInputFactory();
    public static Transformer3WTypeInputFactory transformer3WTypeInputFactory = new Transformer3WTypeInputFactory();
    public static SystemParticipantTypeInputFactory systemParticipantTypeInputFactory = new SystemParticipantTypeInputFactory();

    public TypeSourceFactories() {}

    public static OperatorInputFactory getOperatorInputFactory() {
        return operatorInputFactory;
    }

    public static Transformer2WTypeInputFactory getTransformer2WTypeInputFactory() {
        return transformer2WTypeInputFactory;
    }

    public static LineTypeInputFactory getLineTypeInputFactory() {
        return lineTypeInputFactory;
    }

    public static Transformer3WTypeInputFactory getTransformer3WTypeInputFactory() {
        return transformer3WTypeInputFactory;
    }

    public static SystemParticipantTypeInputFactory getSystemParticipantTypeInputFactory() {
        return systemParticipantTypeInputFactory;
    }



}
