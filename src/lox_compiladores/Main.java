package lox_compiladores;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        testParser("var x = 42;");
        testParser("print \"Hello, world!\";");
        testParser("fun soma(a, b) { return a + b; }");
        testParser("class Animal {}");
        testParser("if (x > 5) { print \"Maior\"; }");
    }

    private static void testParser(String code) {
        System.out.println("\n=== Testando: '" + code + "' ===");
        
        try {
            // 1. Análise léxica
            Scanner scanner = new Scanner(code);
            List<Token> tokens = scanner.scanTokens();
            System.out.println("[TOKENS]");
            tokens.forEach(t -> System.out.println("  " + t.type + " '" + t.lexeme + "'" + 
                              (t.literal != null ? " (" + t.literal + ")" : "")));

            // 2. Análise sintática
            Parser parser = new Parser(tokens);
            List<Stmt> statements = parser.parse();
            
            // 3. Exibição simplificada da AST
            System.out.println("[AST Simplificada]");
            for (Stmt stmt : statements) {
                System.out.println("  " + stmtToString(stmt));
            }
            
        } catch (RuntimeError e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static String stmtToString(Stmt stmt) {
        if (stmt instanceof Stmt.Var) {
            Stmt.Var var = (Stmt.Var) stmt;
            return "Declaração de var: " + var.name.lexeme + 
                   (var.initializer != null ? " = " + exprToString(var.initializer) : "");
        }
        if (stmt instanceof Stmt.Print) {
            return "Print: " + exprToString(((Stmt.Print) stmt).expression);
        }
        if (stmt instanceof Stmt.Function) {
            Stmt.Function fun = (Stmt.Function) stmt;
            return "Função: " + fun.name.lexeme + "()";
        }
        if (stmt instanceof Stmt.Class) {
            return "Classe: " + ((Stmt.Class) stmt).name.lexeme;
        }
        if (stmt instanceof Stmt.If) {
            return "Condicional If";
        }
        return stmt.getClass().getSimpleName();
    }

    private static String exprToString(Expr expr) {
        if (expr instanceof Expr.Value) {
            return ((Expr.Value) expr).value.toString();
        }
        if (expr instanceof Expr.VarRef) {
            return ((Expr.VarRef) expr).name.lexeme;
        }
        if (expr instanceof Expr.BinaryOp) {
            Expr.BinaryOp bin = (Expr.BinaryOp) expr;
            return exprToString(bin.left) + " " + bin.operator.lexeme + " " + exprToString(bin.right);
        }
        return expr.getClass().getSimpleName();
    }
}

// Atenção, mesmo eu tendo mexido esse arquivo segue sendo lixo de testagem, nossa verdadeira main é o lox.java. Ass:Davi