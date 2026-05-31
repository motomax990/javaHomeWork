package orm.core;

import java.lang.annotation.Annotation;

public record Violation(
        String field,
        Object invalidValue,
        String message,
        Class<? extends Annotation> annotation
) {
}
