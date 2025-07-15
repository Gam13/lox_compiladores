package lox_compiladores;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TESTES BÁSICOS DO PARSER ===");
        testParser("var x = 42;");
        testParser("print \"Hello, world!\";");
        testParser("fun soma(a, b) { return a + b; }");
        testParser("class Animal {}");
        testParser("if (x > 5) { print \"Maior\"; }");
        
        System.out.println("\n=== TESTES DE ANÁLISE SEMÂNTICA ===");
        testSemanticAnalysis();
        
        System.out.println("\n=== TESTES DE FUNÇÕES NATIVAS ===");
        testNativeFunctions();
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

    private static void testSemanticAnalysis() {
        System.out.println("\n--- Teste 1: Resolução de Escopos ---");
        testFullCompilation("var global = \"global\"; { var local = \"local\"; print local; print global; }");
        
        System.out.println("\n--- Teste 2: Shadowing de Variáveis ---");
        testFullCompilation("var x = \"outer\"; { var x = \"inner\"; print x; } print x;");
        
        System.out.println("\n--- Teste 3: Função com Parâmetros ---");
        testFullCompilation("fun greet(name) { print \"Hello, \" + name; } greet(\"World\");");
        
        System.out.println("\n--- Teste 4: Erro Semântico - Variável Não Declarada ---");
        testSemanticError("print undeclared;");
        
        System.out.println("\n--- Teste 5: Erro Semântico - Redeclaração ---");
        testSemanticError("{ var x = 1; var x = 2; }");
        
        System.out.println("\n--- Teste 6: Verificação de Tipos ---");
        testFullCompilation("var x = 10; var y = 20; print x + y;");
        
        System.out.println("\n--- Teste 7: Warning de Tipos ---");
        testFullCompilation("var x = \"hello\"; var y = 42; print x + y;");
    }

    private static void testNativeFunctions() {
        System.out.println("\n--- Teste 1: Função clock() ---");
        testFullCompilation("print clock();");
        
        System.out.println("\n--- Teste 2: Função str() ---");
        testFullCompilation("print str(42);");
        
        System.out.println("\n--- Teste 3: Função type() ---");
        testFullCompilation("print type(42); print type(\"hello\"); print type(true);");
        
        System.out.println("\n--- Teste 4: Função num() ---");
        testFullCompilation("print num(\"123\");");
        
        System.out.println("\n--- Teste 5: Combinação de Funções Nativas ---");
        testFullCompilation("var x = 42; print \"Valor: \" + str(x) + \", Tipo: \" + type(x);");
    }

    private static void testFullCompilation(String code) {
        System.out.println("Código: " + code);
        try {
            // 1. Scanner
            Scanner scanner = new Scanner(code);
            List<Token> tokens = scanner.scanTokens();
            
            // 2. Parser
            Parser parser = new Parser(tokens);
            List<Stmt> statements = parser.parse();
            
            if (Lox.hadError) {
                System.out.println("Erro de parsing");
                Lox.hadError = false;
                return;
            }
            
            // 3. Resolver (Análise Semântica)
            LoxInterpreter interpreter = new LoxInterpreter();
            Resolver resolver = new Resolver(interpreter);
            resolver.resolve(statements);
            
            if (Lox.hadError) {
                System.out.println("Erro semântico detectado");
                Lox.hadError = false;
                return;
            }
            
            // 4. Execução
            System.out.print("Resultado: ");
            interpreter.interpret(statements);
            
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void testSemanticError(String code) {
        System.out.println("Código: " + code);
        try {
            // 1. Scanner
            Scanner scanner = new Scanner(code);
            List<Token> tokens = scanner.scanTokens();
            
            // 2. Parser
            Parser parser = new Parser(tokens);
            List<Stmt> statements = parser.parse();
            
            if (Lox.hadError) {
                System.out.println("Erro de parsing (esperado)");
                Lox.hadError = false;
                return;
            }
            
            // 3. Resolver (deve detectar erro semântico)
            LoxInterpreter interpreter = new LoxInterpreter();
            Resolver resolver = new Resolver(interpreter);
            resolver.resolve(statements);
            
            if (Lox.hadError) {
                System.out.println("Erro semântico detectado corretamente");
                Lox.hadError = false;
                return;
            }
            
            System.out.println("Erro semântico NÃO foi detectado (problema!)");
            
        } catch (Exception e) {
            System.out.println("Erro capturado: " + e.getMessage());
        }
    }
}

// Atenção, mesmo eu tendo mexido esse arquivo segue sendo lixo de testagem, nossa verdadeira main é o lox.java. Ass:Davi