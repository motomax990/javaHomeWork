package org.expr;

public record Exponent(Expr expr1, int exp) implements Expr {
    @Override
    public double evaluate() {
        return Math.pow(expr1.evaluate(), exp);
    }
}
