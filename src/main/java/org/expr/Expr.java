package org.expr;




public sealed interface Expr permits Constant, Negate, Exponent, Addition, Multiplication {
    double evaluate();
}