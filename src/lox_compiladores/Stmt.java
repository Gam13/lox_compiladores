package lox_compiladores;

import java.util.List;

public abstract class Stmt {
    // Interface do visitor com nomes diferentes
    public interface StatementExecutor {
        void executeBlock(CodeBlock stmt);
        void executeExpression(ExprStmt stmt);
        void executeFunction(FunctionDecl stmt);
        void executeConditional(IfStmt stmt);
        void executePrint(PrintStmt stmt);
        void executeReturn(ReturnStmt stmt);
        void executeVarDecl(VarDecl stmt);
        void executeWhileLoop(WhileLoop stmt);
        void executeClass(ClassDecl stmt);
    }

    public abstract void accept(StatementExecutor executor);

    // 1. Bloco de código ({ ... })
    public static class CodeBlock extends Stmt {
        public final List<Stmt> statements;
        public CodeBlock(List<Stmt> statements) {
            this.statements = statements;
        }
        @Override public void accept(StatementExecutor executor) {
            executor.executeBlock(this);
        }
    }

    // 2. Expressão como statement (x + 1;)
    public static class ExprStmt extends Stmt {
        public final Expr expression;
        public ExprStmt(Expr expression) {
            this.expression = expression;
        }
        @Override public void accept(StatementExecutor executor) {
            executor.executeExpression(this);
        }
    }

    // 3. Declaração de função (fun soma() { ... })
    public static class FunctionDecl extends Stmt {
        public final Token name;
        public final List<Token> params;
        public final List<Stmt> body;
        public FunctionDecl(Token name, List<Token> params, List<Stmt> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }
        @Override public void accept(StatementExecutor executor) {
            executor.executeFunction(this);
        }
    }

    // 4. Condicional (if/else)
    public static class IfStmt extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch;
        public IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }
        @Override public void accept(StatementExecutor executor) {
            executor.executeConditional(this);
        }
    }

    // 5. Statement de print (print "oi";)
    public static class PrintStmt extends Stmt {
        public final Expr expression;
        public PrintStmt(Expr expression) {
            this.expression = expression;
        }
        @Override public void accept(StatementExecutor executor) {
            executor.executePrint(this);
        }
    }

    // 6. Return (return x;)
    public static class ReturnStmt extends Stmt {
        public final Token keyword;
        public final Expr value;
        public ReturnStmt(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }
        @Override public void accept(StatementExecutor executor) {
            executor.executeReturn(this);
        }
    }

    // 7. Declaração de variável (var x = 10;)
    public static class VarDecl extends Stmt {
        public final Token name;
        public final Expr initializer;
        public VarDecl(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }
        @Override public void accept(StatementExecutor executor) {
            executor.executeVarDecl(this);
        }
    }

    // 8. Loop while (while (x) { ... })
    public static class WhileLoop extends Stmt {
        public final Expr condition;
        public final Stmt body;
        public WhileLoop(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }
        @Override public void accept(StatementExecutor executor) {
            executor.executeWhileLoop(this);
        }
    }

    // 9. Declaração de classe (class Nome { ... })
    public static class ClassDecl extends Stmt {
        public final Token name;
        public final Expr.VarRef superclass;
        public final List<Stmt.FunctionDecl> methods;
        public ClassDecl(Token name, Expr.VarRef superclass, 
                       List<Stmt.FunctionDecl> methods) {
            this.name = name;
            this.superclass = superclass;
            this.methods = methods;
        }
        @Override public void accept(StatementExecutor executor) {
            executor.executeClass(this);
        }
    }
}