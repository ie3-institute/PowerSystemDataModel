/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record GetterMethod(String name, Getter getter, String returnType) {

  public GetterMethod(Method method) {
    this(method.getName(), method::invoke, method.getReturnType().getSimpleName());
  }

  public Object invoke(Object object)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    return getter.get(object);
  }

  @FunctionalInterface
  public interface Getter {
    Object get(Object object)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
  }
}
