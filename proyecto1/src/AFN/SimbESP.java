package AFN;

import java.util.List;
import java.util.Set;

public final class SimbESP {
    public static char Epsilon = (char)5;
    public static char Fin = (char)0;
    public static int Error = 20000;
    public static int Omitir = 20001; // Omitir Saltos de linea, Espacios y tabulaciones
    public static List<Character> SimbolosOmitir = List.of(' ', '\n' , '\t');
    public static Set<String> SimbolosEpsilon = Set.of("epsilon","ϵ","ee");
}
