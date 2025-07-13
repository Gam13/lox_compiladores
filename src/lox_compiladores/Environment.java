package lox_compiladores;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    private final Environment enclosing;

    // Construtor para ambiente global (sem enclosing)
    public Environment() {
        this.enclosing = null;
    }

    // Construtor para ambientes aninhados (com enclosing)
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    // Define uma variável no ambiente atual
    public void define(Token token, Object value) {
        values.put(token, value);
    }

    // Obtém o valor de uma variável, procurando nos ambientes aninhados
    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    // Atribui valor a uma variável, procurando nos ambientes aninhados
    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    // Obtém valor em um ambiente específico da cadeia (para resolução de variáveis)
    public Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    // Atribui valor em um ambiente específico da cadeia
    public void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }

    // Encontra o ambiente ancestral na distância especificada
    private Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }
}