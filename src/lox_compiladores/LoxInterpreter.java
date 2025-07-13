package lox_compiladores;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import lox_compiladores.Expr.Assign;
import lox_compiladores.Expr.BinaryOp;
import lox_compiladores.Expr.CallExpr;
import lox_compiladores.Expr.GetProp;
import lox_compiladores.Expr.Group;
import lox_compiladores.Expr.LogicalOp;
import lox_compiladores.Expr.SuperCall;
import lox_compiladores.Expr.ThisRef;
import lox_compiladores.Expr.UnaryOp;
import lox_compiladores.Expr.Value;
import lox_compiladores.Expr.VarRef;
import lox_compiladores.Stmt.Block;
import lox_compiladores.Stmt.Class;
import lox_compiladores.Stmt.Expression;
import lox_compiladores.Stmt.Function;
import lox_compiladores.Stmt.If;
import lox_compiladores.Stmt.Print;
import lox_compiladores.Stmt.Return;
import lox_compiladores.Stmt.Var;
import lox_compiladores.Stmt.While;

public class LoxInterpreter implements Expr.ExpressionEvaluator<Object>, Stmt.Visitor<Void> {
    
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
            Lox.runtimeError(error);
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

	@Override
	public Void visitBlockStmt(Block stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitExpressionStmt(Expression stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitFunctionStmt(Function stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitIfStmt(If stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitPrintStmt(Print stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitReturnStmt(Return stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitVarStmt(Var stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitWhileStmt(While stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitClassStmt(Class stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateAssignment(Assign expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateBinary(BinaryOp expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateCall(CallExpr expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateGet(GetProp expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateLiteral(Value expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateGrouping(Group expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateLogical(LogicalOp expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateUnary(UnaryOp expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateVariable(VarRef expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateThis(ThisRef expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluateSuper(SuperCall expr) {
		// TODO Auto-generated method stub
		return null;
	}

}