package lox_compiladores;

import java.util.List;
import java.util.Map;

class LoxClasses implements LoxCallable{
	
	final String name;
	final LoxClasses superclass;
	
	private final Map<String, LoxFunc> methods;

	
	public LoxClasses(String name, LoxClasses superclass, Map<String, LoxFunc> methods) {
		this.name = name;
		this.superclass = superclass;
		this.methods = methods;
	}
	
	LoxFunc findMethod(String name) {
		if(methods.containsKey(name)) {
			return methods.get(name);
		}
		if(superclass != null) {
			return superclass.findMethod(name);
		}
		return null;
	}
	
	@Override
	public int ParamNumbs() {
		LoxFunc init = findMethod("init");
		if(init == null) return 0;
		return init.ParamNumbs();
	}

	@Override
	public Object call(LoxInterpreter interpreter, List<Object> arguments) {
		LoxInstance instance = new LoxInstance(this);
		LoxFunc init = findMethod("init");
		if(init != null) {
			init.bind(instance).call(interpreter, arguments);
		}
		return instance;
	}
	
	public String toString() {
		return name;
	}
	
}