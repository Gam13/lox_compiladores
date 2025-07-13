package lox_compiladores;

import java.util.List;

interface LoxCallable {
	int ParamNumbs();
	Object call(LoxInterpreter interpreter, List<Object> arguments);
}