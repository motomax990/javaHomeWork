package orm.core;

import java.util.List;

public class ValidationException extends OrmException {
    private final List<Violation> violations;

    public ValidationException(List<Violation> violations) {
        super(buildMessage(violations));
        this.violations = List.copyOf(violations);
    }

    public List<Violation> getViolations() {
        return violations;
    }

    private static String buildMessage(List<Violation> violations) {
        StringBuilder sb = new StringBuilder("Объект не прошёл валидацию: ");
        for (int i = 0; i < violations.size(); i++) {
            Violation v = violations.get(i);
            if (i > 0) sb.append("; ");
            sb.append(v.field()).append(" — ").append(v.message())
                    .append(" (значение: ").append(v.invalidValue()).append(")");
        }
        return sb.toString();
    }
}
