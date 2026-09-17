/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/** Class that contains the model definition. */
public final class ModelDefinition implements HelperMethods {
  /** Name of the model. */
  public String name;

  /** Description of the model class. */
  public String description = "";

  /** If {@code true} the class is abstract (default: true). */
  @JsonProperty("abstract")
  public boolean isAbstract = true;

  /** Defines a superior model class that is extended by this class. */
  @JsonProperty("extends")
  public String extendsName;

  /** List of components (fields) of this model */
  public List<ComponentDefinition> components = new ArrayList<>();

  /** List with nested model definitions. */
  public List<ModelDefinition> nested = new ArrayList<>();

  /** Definition of model components. */
  public static final class ComponentDefinition extends Parameter {

    /** List with keys that are used in the source. */
    public List<String> keys = new ArrayList<>();

    /** If {@code true} the field is required to build this model (default: true). */
    public boolean required = true;

    /** If {@code true} the component is a nested model (default: false). */
    public boolean nested = false;

    /** If {@code true} the source can provide null values (default: false). */
    public boolean nullable = false;
  }

  /** Definition for a parameter. */
  public static sealed class Parameter {
    /** Name of the parameter. */
    public String name;

    /** Type of the parameter. */
    public String type;

    /** Description of the parameter. */
    public String description = "";
  }
}
