package HOC6;

import java.io.StringReader;
// Estas dos importaciones corrigen el error de "missing type Scanner" y "Symbol"
import java_cup.runtime.Symbol; 
import java_cup.runtime.Scanner; 

public class HOC6 {

    private Machine maquina;

    public HOC6() {
        this.maquina = new Machine();
    }

    public Machine getMaquina() {
        return this.maquina;
    }

    public void reiniciarMaquina() {
        this.maquina = new Machine();
    }

    public String analizarLexicamente(String source) {
        if (source == null || source.trim().isEmpty()) return "Ingresa una expresión.";

        try {
            AnalizadorLexico lexer = new AnalizadorLexico(new StringReader(source), maquina.getTable());
            StringBuilder out = new StringBuilder();
            Symbol token;
            
            while ((token = lexer.next_token()).sym != AnalizadorSintacticoSym.EOF) {
                out.append("Token ID: ").append(token.sym)
                   .append(" | Lexema: ").append(token.value != null ? token.value.toString() : "null")
                   .append("\n");
            }
            
            return out.toString();
        } catch (Exception ex) {
            return "Error léxico: " + ex.getMessage();
        }
    }

    public String analizarSintacticamente(String source) {
        if (source == null || source.trim().isEmpty()) return "Ingresa una expresión.";

        try {
            reiniciarMaquina();

            StringReader sr = new StringReader(source);
            
            AnalizadorLexico lexer = new AnalizadorLexico(sr, maquina.getTable());
            AnalizadorSintactico parser = new AnalizadorSintactico(lexer, maquina);
            
            parser.parse();
            
            return "FIN DEL ANÁLISIS SINTÁCTICO.\nCódigo compilado exitosamente. Total de instrucciones: " + maquina.getProgP();
        } catch (Exception ex) {
            return "Error Sintáctico/Compilación: " + ex.getMessage();
        }
    }

    public void ejecutarMaquinaVirtual() {
        try {
            if (maquina.getProgP() > 0) {
                maquina.execute(0);
            } else {
                System.out.println("No hay código compilado en la memoria para ejecutar.");
            }
        } catch (Exception ex) {
            System.err.println("Error de ejecución en la Máquina Virtual: " + ex.getMessage());
        }
    }
}