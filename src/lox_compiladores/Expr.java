package lox_compiladores;

import java.util.List;

public abstract class Expr {


	public interface ExpressionEvaluator<T> {
        T evaluateAssignment(Assign expr);
        T evaluateBinary(BinaryOp expr);
        T evaluateCall(CallExpr expr);
        T evaluateGet(GetProp expr);
        T evaluateLiteral(Value expr);
        T evaluateGrouping(Group expr);
        T evaluateLogical(LogicalOp expr);
        T evaluateUnary(UnaryOp expr);
        T evaluateVariable(VarRef expr);
        T evaluateThis(ThisRef expr);
        T evaluateSuper(SuperCall expr);
    }

    public abstract <T> T accept(ExpressionEvaluator<T> evaluator);

    // 1. Atribuição (x = 10)
    public static class Assign extends Expr {
        public final Token target;
        public final Expr value;
        public Assign(Token target, Expr value) {
            this.target = target;
            this.value = value;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateAssignment(this);
        }
    }

    // 2. Operação binária (1 + 2)
    public static class BinaryOp extends Expr {
        public final Expr left;
        public final Token operator;
        public final Expr right;
        public BinaryOp(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateBinary(this);
        }
    }

    // 3. Chamada de função (funcao())
    public static class CallExpr extends Expr {
        public final Expr callee;
        public final Token paren;
        public final List<Expr> arguments;
        public CallExpr(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateCall(this);
        }
    }

    // 4. Acesso a propriedade (objeto.propriedade)
    public static class GetProp extends Expr {
        public final Expr object;
        public final Token property;
        public GetProp(Expr object, Token property) {
            this.object = object;
            this.property = property;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateGet(this);
        }
    }

    // 5. Literal (42, "texto", true, nil)
    public static class Value extends Expr {
        public final Object value;
        public Value(Object value) {
            this.value = value;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateLiteral(this);
        }
    }

    // 6. Agrupamento (1 + (2 * 3))
    public static class Group extends Expr {
        public final Expr expression;
        public Group(Expr expression) {
            this.expression = expression;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateGrouping(this);
        }
    }

    // 7. Operadores lógicos (and/or)
    public static class LogicalOp extends Expr {
        public final Expr left;
        public final Token operator;
        public final Expr right;
        public LogicalOp(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateLogical(this);
        }
    }

    // 8. Operador unário (-, !)
    public static class UnaryOp extends Expr {
        public final Token operator;
        public final Expr right;
        public UnaryOp(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateUnary(this);
        }
    }

    // 9. Referência a variável (x)
    public static class VarRef extends Expr {
        public final Token name;
        public VarRef(Token name) {
            this.name = name;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateVariable(this);
        }
    }

    // 10. Referência a 'this' (this.propriedade)
    public static class ThisRef extends Expr {
        public final Token keyword;
        public ThisRef(Token keyword) {
            this.keyword = keyword;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateThis(this);
        }
    }

    // 11. Chamada super (super.metodo())
    public static class SuperCall extends Expr {
        public final Token keyword;
        public final Token method;
        public SuperCall(Token keyword, Token method) {
            this.keyword = keyword;
            this.method = method;
        }
        @Override public <T> T accept(ExpressionEvaluator<T> evaluator) {
            return evaluator.evaluateSuper(this);
        }
    }

    public static class Variable extends Expr {
        public final Token name;
        
        public Variable(Token name) {
            this.name = name;
        }
        
        @Override
        public <T> T accept(ExpressionEvaluator<T> evaluator) {
            // Como esta é uma classe especial para uso em herança,
            // podemos tratá-la como uma VarRef normal
            return evaluator.evaluateVariable(new VarRef(name));
        }
    }
}