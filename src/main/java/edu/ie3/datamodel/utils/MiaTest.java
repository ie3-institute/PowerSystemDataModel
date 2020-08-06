package edu.ie3.datamodel.utils;


import javax.measure.Quantity;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

//MIA
public class MiaTest {

    private static final HashMap<Class<?>, Function<Object, String>> classToStringFunction = new HashMap<>();
    static {
        classToStringFunction.put(Quantity.class, c -> "Quant");
        classToStringFunction.put(Number.class, Object::toString);
        classToStringFunction.put(UUID.class, c -> "uuID");
    }

    public static String processMethodResult(Object methodReturnObject) {
        Function<Object, String> stringMapper = classToStringFunction.get(methodReturnObject.getClass());
        if(stringMapper == null) return "not found";
        return stringMapper.apply(methodReturnObject);


    }
    }