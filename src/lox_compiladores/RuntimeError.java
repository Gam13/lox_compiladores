package lox_compiladores;

class RuntimeError extends RuntimeException {
	private static final long serialVersionUID = 3045665823372545478L;
  final Token token;

  RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
  }
}
