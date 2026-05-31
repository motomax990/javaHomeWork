package orm.core;

import orm.annotation.validation.Constraint;
import orm.annotation.validation.Max;
import orm.annotation.validation.Min;
import orm.annotation.validation.NotBlank;
import orm.annotation.validation.NotNull;
import orm.annotation.validation.Pattern;
import orm.annotation.validation.Size;
import orm.core.validation.ConstraintValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Validator {

    private static final Set<Class<?>> NOT_NULL_SUPPORTED = Set.of(
            String.class, Long.class, Integer.class, Double.class, Boolean.class, LocalDate.class
    );

    private static final ConcurrentHashMap<Class<? extends ConstraintValidator<?, ?>>, ConstraintValidator<?, ?>> VALIDATORS =
            new ConcurrentHashMap<>();

    private Validator() {
    }

    public static List<Violation> validate(Object entity) {
        if (entity == null) {
            throw new OrmException("Нельзя валидировать null");
        }
        List<Violation> violations = new ArrayList<>();
        Class<?> clazz = entity.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            for (Annotation annotation : field.getAnnotations()) {
                Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);
                if (constraint == null) continue;
                checkFieldType(annotation, field);
                Object value = readField(field, entity);
                if (!invokeValidator(constraint, annotation, value)) {
                    String message = readMessage(annotation);
                    violations.add(new Violation(
                            field.getName(), value, message, annotation.annotationType()
                    ));
                }
            }
        }
        return violations;
    }

    private static Object readField(Field field, Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new OrmException("Не удалось прочитать поле " + field.getName(), e);
        }
    }

    private static String readMessage(Annotation annotation) {
        try {
            Method method = annotation.annotationType().getMethod("message");
            return (String) method.invoke(annotation);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return "constraint violated";
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean invokeValidator(Constraint constraint, Annotation annotation, Object value) {
        Class<? extends ConstraintValidator<?, ?>> validatorClass = constraint.validatedBy();
        ConstraintValidator validator = VALIDATORS.computeIfAbsent(validatorClass, Validator::instantiate);
        try {
            return validator.isValid(value, annotation);
        } catch (ClassCastException e) {
            throw new ValidationException(List.of(new Violation(
                    "?", value,
                    "тип значения не подходит для аннотации " + annotation.annotationType().getSimpleName(),
                    annotation.annotationType()
            )));
        }
    }

    private static ConstraintValidator<?, ?> instantiate(Class<? extends ConstraintValidator<?, ?>> cls) {
        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new OrmException("Не удалось создать валидатор " + cls.getName(), e);
        }
    }

    private static void checkFieldType(Annotation annotation, Field field) {
        Class<?> annType = annotation.annotationType();
        Class<?> fieldType = field.getType();
        if (annType == NotNull.class) {
            if (fieldType.isPrimitive()) {
                throw new OrmException("@NotNull нельзя ставить на примитив: " + field.getName());
            }
            if (!NOT_NULL_SUPPORTED.contains(fieldType)) {
                throw new OrmException(
                        "@NotNull не поддерживает тип " + fieldType.getSimpleName() + " (поле " + field.getName() + ")"
                );
            }
        } else if (annType == NotBlank.class || annType == Size.class || annType == Pattern.class) {
            if (fieldType != String.class) {
                throw new OrmException(
                        "@" + annType.getSimpleName() + " можно ставить только на String (поле " + field.getName() + ")"
                );
            }
        } else if (annType == Min.class || annType == Max.class) {
            if (!isIntegerType(fieldType)) {
                throw new OrmException(
                        "@" + annType.getSimpleName() + " можно ставить только на int/long/Integer/Long (поле " + field.getName() + ")"
                );
            }
        }
    }

    private static boolean isIntegerType(Class<?> type) {
        return type == int.class || type == Integer.class
                || type == long.class || type == Long.class;
    }
}
