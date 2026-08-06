package edu.ie3.codegen;

import java.util.ArrayList;
import java.util.List;

public final class GenerationConfig implements HelperMethods {
    public List<ConstructorDefinition> constructors = new ArrayList<>();

    public List<String> resolverOrder = new ArrayList<>();

    public String fromMapConstructor;

    public boolean getters = true;

    public boolean equals = true;

    public boolean hashCode = true;

    public boolean toString = true;

    public boolean fromMap = false;

    public List<StaticFieldDefinition> staticFields = new ArrayList<>();

    public List<String> optionalGetters = new ArrayList<>();

    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // helper definition

    public static final class ConstructorDefinition {
        public String name;

        public List<String> superArgs;

        public List<String> components = new ArrayList<>();
    }

    public static final class StaticFieldDefinition {
        public String name;
        public String type;
        public String expression;
        public String className;
    }
}
