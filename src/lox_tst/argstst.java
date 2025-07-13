package lox_tst;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class argstst {
    
    @Test
    public void testRunSimplePrint() {
        String source = "print \"Hello, test!\";";
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        
        Lox.main(new String[]{});
        Lox.run(source); // Se for tornar run() público para teste

        String result = output.toString();
        assertTrue(result.contains("Hello, test!"));
    }
}
