package org.testinfected.petstore;

public final class Check {

    public static <T> NotNull<T> notNull(T value) {
        return new NotNull<T>(value);
    }

    public static NotBlank notBlank(String value) {
        return new NotBlank(value);
    }

    public static <T> Valid<T> valid(T value) {
        return new Valid<T>(value);
    }

    private Check() {}
}