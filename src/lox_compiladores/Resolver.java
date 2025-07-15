package lox_compiladores;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.ExpressionEvaluator<Void>, Stmt.Visitor<Void> {
    private final LoxInterpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;
    private ClassType currentClass = ClassType.NONE;

    private enum FunctionType {
        NONE, FUNCTION, INITIALIZER, METHOD
    }

    private enum ClassType {
        NONE, CLASS, SUBCLASS
    }

    // Mini sistema de tipos
    private enum LoxType {
        NIL, BOOLEAN, NUMBER, STRING, FUNCTION, CLASS, INSTANCE
    }

    private final Map<String, LoxType> variableTypes = new HashMap<>();

    public Resolver(LoxInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    // Método principal
    public void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    // Resolve função
    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    // Gerenciamento de escopos
    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    // Declaração de variável
    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Already a variable with this name in this scope.");
        }

        scope.put(name.lexeme, false);
    }

    // Definição de variável
    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }

    // Resolução de variável local
    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    // Implementação do Visitor para Statements
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;

        declare(stmt.name);
        define(stmt.name);

        if (stmt.superclass != null &&
            stmt.name.lexeme.equals(stmt.superclass.name.lexeme)) {
            Lox.error(stmt.superclass.name, "A class can't inherit from itself.");
        }

        if (stmt.superclass != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.superclass);
        }

        if (stmt.superclass != null) {
            beginScope();
            scopes.peek().put("super", true);
        }

        beginScope();
        scopes.peek().put("this", true);

        for (Stmt.Function method : stmt.methods) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.name.lexeme.equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }

            resolveFunction(method, declaration);
        }

        endScope();

        if (stmt.superclass != null) endScope();

        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Can't return from top-level code.");
        }

        if (stmt.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Lox.error(stmt.keyword, "Can't return a value from an initializer.");
            }

            resolve(stmt.value);
        }

        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
            // Armazenar tipo da variável
            LoxType type = inferType(stmt.initializer);
            variableTypes.put(stmt.name.lexeme, type);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    // Implementação do ExpressionEvaluator para Expressions
    @Override
    public Void evaluateAssignment(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.target);
        return null;
    }

    @Override
    public Void evaluateBinary(Expr.BinaryOp expr) {
        resolve(expr.left);
        resolve(expr.right);
        
        // Verificação de tipos básica
        LoxType leftType = inferType(expr.left);
        LoxType rightType = inferType(expr.right);
        checkTypeCompatibility(expr.operator, leftType, rightType);
        
        return null;
    }

    @Override
    public Void evaluateCall(Expr.CallExpr expr) {
        resolve(expr.callee);

        for (Expr argument : expr.arguments) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void evaluateGet(Expr.GetProp expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void evaluateSet(Expr.SetProp expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
    public Void evaluateGrouping(Expr.Group expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void evaluateLiteral(Expr.Value expr) {
        return null;
    }

    @Override
    public Void evaluateLogical(Expr.LogicalOp expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void evaluateSuper(Expr.SuperCall expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.keyword, "Can't use 'super' outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            Lox.error(expr.keyword, "Can't use 'super' in a class with no superclass.");
        }

        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void evaluateThis(Expr.ThisRef expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.keyword, "Can't use 'this' outside of a class.");
            return null;
        }

        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void evaluateUnary(Expr.UnaryOp expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void evaluateVariable(Expr.VarRef expr) {
        if (!scopes.isEmpty() &&
            scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Lox.error(expr.name, "Can't read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    // Método para verificar se uma variável está declarada
    private boolean isVariableDeclared(String name) {
        // Verifica nos escopos locais
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return true;
            }
        }
        // Se não encontrou nos escopos locais, verifica se existe globalmente
        // (isso deveria ser feito pelo interpreter, mas podemos dar uma dica)
        return false;
    }

    // Método para validar uso de variáveis não declaradas
    private void validateVariableUsage(Token name) {
        boolean found = false;
        
        // Verifica nos escopos locais
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                found = true;
                break;
            }
        }
        
        // Se não encontrou nos escopos locais, pode ser global
        // O interpreter vai validar isso em tempo de execução
        if (!found && !scopes.isEmpty()) {
            // Adiciona warning para possível variável não declarada
            System.out.println("Warning: Variable '" + name.lexeme + "' may not be declared.");
        }
    }

    // Método para inferir tipo de uma expressão
    private LoxType inferType(Expr expr) {
        if (expr instanceof Expr.Value) {
            Object value = ((Expr.Value) expr).value;
            if (value == null) return LoxType.NIL;
            if (value instanceof Boolean) return LoxType.BOOLEAN;
            if (value instanceof Double) return LoxType.NUMBER;
            if (value instanceof String) return LoxType.STRING;
        }
        if (expr instanceof Expr.VarRef) {
            String name = ((Expr.VarRef) expr).name.lexeme;
            return variableTypes.getOrDefault(name, LoxType.NIL);
        }
        if (expr instanceof Expr.BinaryOp) {
            Expr.BinaryOp bin = (Expr.BinaryOp) expr;
            switch (bin.operator.type) {
                case PLUS:
                    LoxType leftType = inferType(bin.left);
                    LoxType rightType = inferType(bin.right);
                    if (leftType == LoxType.NUMBER && rightType == LoxType.NUMBER) {
                        return LoxType.NUMBER;
                    }
                    if (leftType == LoxType.STRING || rightType == LoxType.STRING) {
                        return LoxType.STRING;
                    }
                    break;
                case MINUS:
                case STAR:
                case SLASH:
                    return LoxType.NUMBER;
                case EQUAL_EQUAL:
                case BANG_EQUAL:
                case GREATER:
                case GREATER_EQUAL:
                case LESS:
                case LESS_EQUAL:
                    return LoxType.BOOLEAN;
                default:
                    break;
            }
        }
        return LoxType.NIL;
    }

    // Método para verificar compatibilidade de tipos
    private void checkTypeCompatibility(Token operator, LoxType left, LoxType right) {
        switch (operator.type) {
            case PLUS:
                if (!((left == LoxType.NUMBER && right == LoxType.NUMBER) ||
                      (left == LoxType.STRING && right == LoxType.STRING))) {
                    System.out.println("Warning: Type mismatch in addition at line " + operator.line);
                }
                break;
            case MINUS:
            case STAR:
            case SLASH:
                if (left != LoxType.NUMBER || right != LoxType.NUMBER) {
                    System.out.println("Warning: Arithmetic operations require numbers at line " + operator.line);
                }
                break;
            default:
                break;
        }
    }
}
