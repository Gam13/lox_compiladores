package lox_compiladores;

import java.util.List;

public class LoxFunc implements LoxCallable {
	
	private final Stmt.Function declaration;
	private final Environment closure;
	private final boolean isInitializer;
	
	public LoxFunc(Stmt.Function declaration, Environment closure, boolean isinitializer) {
		this.declaration = declaration;
		this.closure = closure;
		this.isInitializer = isinitializer;
	}

	@Override
	public int ParamNumbs() {
		return declaration.params.size();
	}

	@Override
	public Object call(LoxInterpreter interpreter, List<Object> arguments) {
		Environment environment = new Environment(closure);
		for (int i = 0 ;i < declaration.params.size();i++) {
			environment.define(declaration.params.get(i), arguments.get(i));
			
			try {
				interpreter.executeBlock(declaration.body, environment);
			}catch(Return returnValue) {
				if (isInitializer) return closure.getAt(0, "this");
				return returnValue.value;
			}
		}
		if(isInitializer) return closure.getAt(0, "this");
		return null;
	}

	public LoxFunc bind(LoxInstance instance) {
		Environment environment = new Environment(closure);
		environment.define("this", instance);
		return new LoxFunc(declaration,environment,isInitializer);
	}
	public String toString() {
		return "<fn " + declaration.name.lexeme+ ">";
	}
}
