package com.goals.course.authenticator.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class ReactiveHelper {
    public static Function<Throwable, Throwable> mapNullPointerExceptionTo(final Supplier<Throwable> throwableSupplier) {
        return expectedThrowable -> {
            if (expectedThrowable instanceof NullPointerException) {
                return throwableSupplier.get();
            }
            return expectedThrowable;
        };
    }
}
