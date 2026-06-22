package HOC5;

import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;

/** Compatibilidad mínima para código que aún instancia el parser CUP generado. */
public class AnalizadorSintactico {
    private final Scanner scanner;
    public AnalizadorSintactico(Scanner scanner) { this.scanner = scanner; }
    public Symbol parse() throws Exception {
        while (scanner.next_token().sym != AnalizadorSintacticoSym.EOF) {
            // Consume tokens para validar que el lexer puede recorrer la entrada.
        }
        return new Symbol(AnalizadorSintacticoSym.EOF);
    }
}
