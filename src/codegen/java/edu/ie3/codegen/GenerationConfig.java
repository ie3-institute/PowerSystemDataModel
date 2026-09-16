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

  public List<String> modifiers = List.of("public");

  public List<String> inherits = new ArrayList<>();

  public boolean getters = true;

  public boolean setters = false;

  public boolean equals = true;

  public boolean hashCode = true;

  public boolean toString = true;

  public boolean toMap = true;

  public boolean copy = true;

  // modifications
  public List<ConstructorDefinition> constructors = new ArrayList<>();

  public List<FieldDefinition> fields = new ArrayList<>();

  public List<MethodDefinition> methods = new ArrayList<>();

  public List<MethodDefinition> copyBuilderMethods = new ArrayList<>();

  public Map<String, String> keyMapper = new HashMap<>();

  public List<String> excludeFromMethods = new ArrayList<>();

  public List<String> booleanGetter = new ArrayList<>();

  public List<String> nonCapitalized = new ArrayList<>();

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper definition

  public static final class ConstructorDefinition {
    @JsonProperty("private")
    public boolean isPrivate = false;

    public List<ModelDefinition.Parameter> parameters = new ArrayList<>();
    public String codeBlock = "";
  }

  public static sealed class BasicExpression {
    public String expression;
    public List<String> modifiers = new ArrayList<>();
  }

  public static sealed class StandardOptions extends BasicExpression {
    public String name;
    public String type;
    public String description = "";
  }

  public static final class MethodDefinition extends StandardOptions {
    public boolean addReturn = true;
    public boolean annotation = true;
    public String comment = "";
    public List<ModelDefinition.Parameter> parameters = new ArrayList<>();
  }

  public static final class FieldDefinition extends StandardOptions {}
}
