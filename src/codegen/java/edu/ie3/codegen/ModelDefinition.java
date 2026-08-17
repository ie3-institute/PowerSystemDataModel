/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public final class ModelDefinition implements HelperMethods {
  public String name;

  @JsonProperty("class")
  public boolean isClass = false;

  @JsonProperty("extends")
  public String extendsName;

  public List<String> unsupported = new ArrayList<>();

  public List<ComponentDefinition> components = new ArrayList<>();

  public static final class ComponentDefinition {
    public String name;
    public String type;
    public List<String> keys = new ArrayList<>();
    public boolean required = true;
    public boolean nested = false;
    public boolean nullable = false;
    public String javaDoc = "";
  }
}
