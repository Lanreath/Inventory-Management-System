package com.ils.models;

import java.util.stream.Stream;

public abstract class Model {
    // Model classes
    private static final Class<?>[] modelClasses = {
        Transfer.class,
        Product.class,
        Part.class,
        Customer.class
    };

    // Get the model classes
    public static Stream<Class<?>> getModels() {
        return Stream.of(modelClasses);
    }
}
