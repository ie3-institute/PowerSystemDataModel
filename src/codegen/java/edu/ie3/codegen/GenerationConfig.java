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

  @JsonProperty("final")
  public boolean isFinal = false;

  @JsonProperty("private")
  public boolean isPrivate = false;

  @JsonProperty("static")
  public boolean isStatic = false;

  public List<ConstructorDefinition> constructors = new ArrayList<>();

  public List<String> inherits = new ArrayList<>();

  public boolean getters = true;

  public boolean equals = true;

  public boolean hashCode = true;

  public boolean toString = true;

  public boolean copy = true;

  public List<String> publicFields = new ArrayList<>();

  public List<String> excludeFromMethods = new ArrayList<>();

  public List<FieldDefinition> staticFields = new ArrayList<>();

  public List<FieldDefinition> additionalFields = new ArrayList<>();

  public List<String> booleanGetter = new ArrayList<>();

  public List<String> nonCapitalized = new ArrayList<>();

  public List<String> fieldNameGetters = new ArrayList<>();

  public List<String> setters = new ArrayList<>();

  public List<String> noGetters = new ArrayList<>();

  public List<MethodOverride> methodOverrides = new ArrayList<>();

  public List<MethodInsert> methodInserts = new ArrayList<>();

  public List<MethodInsert> copyBuilderAdditionalMethods = new ArrayList<>();

  public List<String> nested = new ArrayList<>();

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

    public String valuesMap = "";
  }

  public static sealed class BasicExpression {
    public String expression;
    public String className;

    @JsonProperty("transient")
    public boolean isTransient = false;

    public boolean insert;
    public String unitClass;

    public boolean usableClassName() {
      return usable(className);
    }

    public boolean usableUnitClass() {
      return usable(unitClass);
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

  public static sealed class MethodFields extends StandardFields {
    @JsonProperty("abstract")
    public boolean isAbstract = false;

    public boolean addReturn = true;
    public boolean annotation = true;
    public String comment = "";
    public List<ModelDefinition.Parameter> parameters = new ArrayList<>();
  }

  public static final class FieldDefinition extends StandardFields {
    @JsonProperty("protected")
    public boolean isProtected = false;
  }

  public static final class MethodOverride extends MethodFields {
    @JsonProperty("protected")
    public boolean isProtected = false;
  }

  public static final class MethodInsert extends MethodFields {
    @JsonProperty("private")
    public boolean isPrivate = false;

    @JsonProperty("protected")
    public boolean isProtected = false;

    @JsonProperty("static")
    public boolean isStatic = false;
  }

  public static final class ConstructorModification extends BasicExpression {}
}
