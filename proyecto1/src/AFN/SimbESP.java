package AFN;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SimbESP {
    public static char Epsilon = (char)5;
    public static char Fin = (char)0;
    public static int Error = 20000;
    public static int Omitir = 20001; // Omitir Saltos, Espacios y tabulaciones
    public static Set<Character> SimbolosOmitir = new HashSet<>(Arrays.asList(' ', '\n' , '\t'));
    public static Set<String> SimbolosEpsilon = new HashSet<>(Set.of("epsilon","ϵ"));
}
