/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The type Getter method.
 *
 * @param name the name
 * @param getter the getter
 * @param returnType the returnType
 */
public record GetterMethod(String name, Getter getter, String returnType) {

  /**
   * Instantiates a new Getter method.
   *
   * @param method the method
   */
  public GetterMethod(Method method) {
    this(method.getName(), method::invoke, method.getReturnType().getSimpleName());
  }

  /**
   * Invoke object.
   *
   * @param object the object
   * @return the object
   * @throws IllegalAccessException the illegal access exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws InvocationTargetException the invocation target exception
   */
  public Object invoke(Object object)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    return getter.get(object);
  }

  /** The interface Getter. */
  @FunctionalInterface
  public interface Getter {
    /**
     * Get object.
     *
     * @param object the object
     * @return the object
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     */
    Object get(Object object)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
  }
}
