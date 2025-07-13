package lox_compiladores;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Teste simples do scanner
        Scanner scanner = new Scanner("var x = 42;");
        List<Token> tokens = scanner.scanTokens();
        
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}


// Nota, esse arquivo é LIXO, eu o fiz pois precisava testar se os pedaços que eu fiz estavam minimamente corretos, favor não os levar a sério. Davi Nobrevoid