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

  public String classJavaDoc = "";

  @JsonProperty("sealed")
  public boolean isSealed = false;

  public List<ConstructorDefinition> constructors = new ArrayList<>();

  public List<String> inherits = new ArrayList<>();

  public boolean getters = true;

  public boolean equals = true;

  public boolean hashCode = true;

  public boolean toString = true;

  public boolean copy = true;

  public List<StaticFieldDefinition> staticFields = new ArrayList<>();

  public List<String> booleanGetter = new ArrayList<>();

  public List<String> nonCapitalizedGetters = new ArrayList<>();

  public List<String> fieldNameGetters = new ArrayList<>();

  public List<String> noGetters = new ArrayList<>();

  public List<MethodOverride> methodOverrides = new ArrayList<>();

  public List<MethodInsert> methodInserts = new ArrayList<>();

  public List<MethodInsert> copyBuilderAdditionalMethods = new ArrayList<>();

  public List<String> nestedClasses = new ArrayList<>();

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper definition

  public static final class ConstructorDefinition {
    public String name;
    public List<String> superArgs;
    public List<String> components = new ArrayList<>();
    public String javaDoc = "";

    @JsonProperty("private")
    public boolean isPrivate = false;

    public List<ModelDefinition.ComponentDefinition> additionalComponents = new ArrayList<>();
    public Map<String, ConstructorModification> constructorModifications = new HashMap<>();
    public List<ConstructorModification> constructorChecks = new ArrayList<>();
  }

  public static sealed class BasicExpression {
    public String expression;
    public String className;

    public boolean usableClassName() {
      return usable(className);
    }

    boolean usable(String name) {
      return name != null && !name.isBlank();
    }
  }

  public static sealed class StandardFields extends BasicExpression {
    public String name;
    public String type;
    public String javaDoc = "";
  }

  public static final class StaticFieldDefinition extends StandardFields {}

  public static final class MethodOverride extends StandardFields {}

  public static final class MethodInsert extends StandardFields {
    public boolean isAbstract = false;
    public boolean annotation = true;
    public String comment = "";
    public List<ModelDefinition.Parameter> parameters = new ArrayList<>();
  }

  public static final class ConstructorModification extends BasicExpression {
    public boolean insert;
    public String unitClass;

    public boolean usableUnitClass() {
      return usable(unitClass);
    }
  }
}
