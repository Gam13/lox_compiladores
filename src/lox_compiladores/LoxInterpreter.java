package lox_compiladores;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class LoxInterpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    
    private final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

	

    LoxInterpreter() {
		
    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.RuntimeError(error);
        }
    }

    // --- Métodos auxiliares básicos ---
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment newEnvironment) {
        Environment previous = this.environment;
        try {
            this.environment = newEnvironment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    // --- Resolução de variáveis ---
    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    // --- Métodos de visita para expressões ---


    // --- Métodos de visita para statements ---

}