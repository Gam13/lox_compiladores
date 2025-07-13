package lox_tst;

import static org.junit.jupiter.api.Assertions.*;
import lox_compiladores.Lox;
import lox_compiladores.Token;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class argsTest {

	   @Test
	    void testMainWithTooManyArguments() throws IOException {
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        PrintStream originalOut = System.out;
	        System.setOut(new PrintStream(outputStream));

	        try {
	            Lox.main(new String[]{"file1.lox", "file2.lox"});
	        } finally {
	            System.setOut(originalOut);
	        }

	        String output = outputStream.toString().trim();
	        assertTrue(output.contains("Usage: jlox [script]"));
	   }

}