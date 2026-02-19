package org.expr;

public record Multiplication(Expr expr1, Expr expr2) implements Expr {
    @Override
    public double evaluate() {
        return expr1.evaluate() * expr2.evaluate();
    }
}
