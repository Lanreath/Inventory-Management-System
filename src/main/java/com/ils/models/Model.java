package com.ils.models;

import java.util.stream.Stream;

public abstract class Model {
    private static final Class<?>[] modelClasses = {
        Transfer.class,
        Product.class,
        Part.class,
        Customer.class
    };

    public static Stream<Class<?>> getModels() {
        return Stream.of(modelClasses);
    }
}
