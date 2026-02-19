package org.expr;

public record Constant(double val) implements Expr {
    @Override
    public double evaluate() {
        return val;
    }
}
