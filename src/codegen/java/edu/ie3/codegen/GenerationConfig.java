/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GenerationConfig implements HelperMethods {
  public String classJavaDoc = "";

  public List<ConstructorDefinition> constructors = new ArrayList<>();

  public List<String> resolverOrder = new ArrayList<>();

  public String fromMapConstructor;

  public List<String> inherits = new ArrayList<>();

  public boolean getters = true;

  public boolean equals = true;

  public boolean hashCode = true;

  public boolean toString = true;

  public boolean fromMap = false;

  public List<StaticFieldDefinition> staticFields = new ArrayList<>();

  public Map<String, GetterOptions> getterOptions = new HashMap<>();

  public List<MethodOverride> methodOverrides = new ArrayList<>();

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper definition

  public static final class ConstructorDefinition {
    public String name;

    public List<String> superArgs;

    public List<String> components = new ArrayList<>();

    public String javaDoc = "";
  }

  public static final class StaticFieldDefinition {
    public String name;
    public String type;
    public String expression;
    public String className;
    public String javaDoc = "";
  }

  public static final class GetterOptions {
    public boolean optional = false;
    public boolean capitalize = true;
    public String javaDoc = "";
  }

  public static final class MethodOverride {
    public String name;
    public String expression;
    public String className;
    public String javaDoc = "";
  }
}
