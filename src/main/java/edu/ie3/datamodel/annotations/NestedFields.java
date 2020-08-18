package edu.ie3.datamodel.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.FIELD)
public @interface NestedFields {

    String referenceName();
    String prefix() default "";

}
