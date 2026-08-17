/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GenerationConfig implements HelperMethods {

  @JsonProperty("package")
  public String packageName;

  public String classJavaDoc = "";

  public List<ConstructorDefinition> constructors = new ArrayList<>();

  public List<String> inherits = new ArrayList<>();

  public boolean getters = true;

  public boolean equals = true;

  public boolean hashCode = true;

  public boolean toString = true;

  public List<StaticFieldDefinition> staticFields = new ArrayList<>();

  public List<String> nonCapitalizedGetters = new ArrayList<>();

  public List<String> optionalGetters = new ArrayList<>();

  public List<String> noGetters = new ArrayList<>();

  public List<MethodOverride> methodOverrides = new ArrayList<>();

  public List<MethodInsert> methodInserts = new ArrayList<>();

  public List<CopyBuilderMethods> copyBuilderAdditionalMethods = new ArrayList<>();

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper definition

  public static final class ConstructorDefinition {
    public String name;

    public List<String> superArgs;

    public List<String> components = new ArrayList<>();

    public String javaDoc = "";

    public Map<String, ConstructorModification> constructorModifications = new HashMap<>();

    public List<ConstructorCheck> constructorChecks = new ArrayList<>();
  }

  public static final class StaticFieldDefinition {
    public String name;
    public String type;
    public String expression;
    public String className;
    public String javaDoc = "";
  }

  public static final class MethodOverride {
    public String name;
    public String type;
    public String expression;
    public String className;
  }

  public static final class MethodInsert {
    public String name;
    public String type;
    public boolean isAbstract = false;
    public String expression;
    public String className;
    public String javaDoc = "";
  }

  public static final class CopyBuilderMethods {
    public String name;
    public boolean isAbstract;
    public String expression;
    public String className;
    public String javaDoc = "";
    public String comment = "";
    public List<Parameter> parameters = new ArrayList<>();

    public static final class Parameter {
      public String name;
      public String type;
    }
  }

  public static final class ConstructorModification {
    public String expression;
    public String className;
    public boolean insert;
    public String unitClass;
  }

  public static final class ConstructorCheck {
    public String expression;
    public String className;
  }
}
