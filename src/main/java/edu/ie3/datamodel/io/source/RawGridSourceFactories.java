package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.input.*;

public class RawGridSourceFactories {

    public static NodeInputFactory nodeInputFactory = new NodeInputFactory();
    public static LineInputFactory lineInputFactory = new LineInputFactory();
    public static Transformer2WInputFactory transformer2WInputFactory = new Transformer2WInputFactory();
    public static Transformer3WInputFactory transformer3WInputFactory = new Transformer3WInputFactory();
    public static SwitchInputFactory switchInputFactory = new SwitchInputFactory();
    public static MeasurementUnitInputFactory measurementUnitInputFactory = new MeasurementUnitInputFactory();

    public RawGridSourceFactories() {}

    public static NodeInputFactory getNodeInputFactory() {
        return nodeInputFactory;
    }

    public static LineInputFactory getLineInputFactory() { return lineInputFactory; }

    public static Transformer2WInputFactory getTransformer2WInputFactory() {
        return transformer2WInputFactory;
    }

    public static Transformer3WInputFactory getTransformer3WInputFactory() { return transformer3WInputFactory; }

    public static SwitchInputFactory getSwitchInputFactory() { return switchInputFactory; }

    public static MeasurementUnitInputFactory getMeasurementUnitInputFactory() { return measurementUnitInputFactory; }
}
