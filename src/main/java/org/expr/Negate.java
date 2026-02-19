package org.expr;

public record Negate(Expr expr) implements Expr {
    @Override
    public double evaluate() {
        return -expr.evaluate();
    }
}
