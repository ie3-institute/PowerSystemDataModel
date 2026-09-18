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

/** Class that contains the generation configuration. */
public final class GenerationConfig implements HelperMethods {

  /** List of all class modifiers (default: only public). */
  public List<String> modifiers = List.of("public");

  /** List of inherited interfaces. */
  public List<String> inherits = new ArrayList<>();

  /** If getters should be generated. */
  public boolean getters = true;

  /** If setters should be generated. */
  public boolean setters = false;

  /** If an equals method should be generated. */
  public boolean equals = true;

  /** If a hashCode method should be generated. */
  public boolean hashCode = true;

  /** If a toString method should be generated. */
  public boolean toString = true;

  /** If a toMap method should be generated. */
  public boolean toMap = true;

  /** If a copy method and a copy builder should be generated. */
  public boolean copy = true;

  // modifications
  /** List with definitions for additional constructors. */
  public List<ConstructorDefinition> constructors = new ArrayList<>();

  /** List with definitions for additional fields. */
  public List<FieldDefinition> fields = new ArrayList<>();

  /** List with definitions for additional methods. */
  public List<MethodDefinition> methods = new ArrayList<>();

  /** List with definitions for additional methods in a copy builder. */
  public List<MethodDefinition> copyBuilderMethods = new ArrayList<>();

  /** Map: key to expression. This is used when providing keys for a field. */
  public Map<String, String> keyMapper = new HashMap<>();

  /**
   * List with fields that should be excluded from all methods. This applies to setters, toMap,
   * equals, hashCode and toString.
   */
  public List<String> excludeFromMethods = new ArrayList<>();

  /** List of method that should not start capitalized after `get`. */
  public List<String> nonCapitalized = new ArrayList<>();

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper definition

  /** Class that contains a definition for additional constructors. */
  public static final class ConstructorDefinition {
    /** If {@code true} the constructor is private. */
    @JsonProperty("private")
    public boolean isPrivate = false;

    /** List of constructor parameters. */
    public List<ModelDefinition.Parameter> parameters = new ArrayList<>();

    /** String that contains a block of code that is inserted in the constructor. */
    public String codeBlock = "";
  }

  /** Class that contains the base fields for definitions. */
  public static sealed class BasicOptions {
    /** Name of the field or method. */
    public String name;

    /** Type of the field or return type of the method. */
    public String type;

    /**
     * Description (Javadoc) of the field or method. For multi line statements `\n` needs to be
     * present in the string.
     */
    public String description = "";

    /** The expression. It can be multiple lines, if `\n` is used. */
    public String expression;

    /** List of all modifiers. */
    public List<String> modifiers = new ArrayList<>();
  }

  /** Class that contains additional fields for methods. */
  public static final class MethodDefinition extends BasicOptions {

    /** If {@code true} a return statement is inserted before the {@link #expression}. */
    public boolean addReturn = true;

    /** If {@code true} a {@link Override} annotation is added. */
    public boolean annotation = true;

    /** List of method parameters. */
    public List<ModelDefinition.Parameter> parameters = new ArrayList<>();
  }

  /** Class that contains the definition for additional fields. */
  public static final class FieldDefinition extends BasicOptions {}
}
