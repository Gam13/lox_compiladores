package lox_compiladores;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    final Environment enclosing;

    // Construtores - básico mesmo, um sem pai e outro com pai
    public Environment() {
        this.enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    // Define uma variável no escopo atual
    public void define(String name, Object value) {
        values.put(name, value);
    }

    public void define(Token token, Object value) {
        values.put(token.lexeme, value);
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) {
            return enclosing.get(name); // não achou? pergunta pro pai
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

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

    // Métodos otimizados - o resolver já calculou a distância
    public Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    public void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }

    private Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }
}