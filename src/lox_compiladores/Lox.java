package lox_compiladores;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
	private static final LoxInterpreter interpreter = new LoxInterpreter();

	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
		      System.out.println("Usage: jlox [script]");
		      System.exit(64); // [64]
		    } else if (args.length == 1) {
		      runFile(args[0]);
		    } else {
		      runPrompt();
		    }
	}
	private static void runFile(String path) throws IOException {
	//Execução direta do arquivo
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
	}
	
	private static void runPrompt() throws IOException { 
	//Metodo ensinado pelo professor de READ-EVAL-PRINT-LOOP
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		
		for (;;) {
			System.out.print("> ");
			String line = reader.readLine();
			if(line == null) break;
			run(line);
		}
	}
	
	private static void run(String source) {
		//Aqui é onde é feito a anaálise léxica
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanToken();
		
		//Aqui é onde é feito a análise sintática
		Parser parser = new Parser(tokens);
		List<Stmt> statements = parser.parse();
		
		//Resolução de variáveis
		Resolver resolver = new Resolver(interpreter);
		resolver.resolve(statements);
		
		interpreter.interpret(statements);
	}
    public static void RuntimeError(RuntimeError error) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'RuntimeError'");
    }

}
