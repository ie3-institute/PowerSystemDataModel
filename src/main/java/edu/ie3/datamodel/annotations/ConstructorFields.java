package edu.ie3.datamodel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MIA mehr Doku
 *
 * Only one Annotation per class, for the constructor with the most parameters
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface ConstructorFields {

    String[] value() default {};

}
