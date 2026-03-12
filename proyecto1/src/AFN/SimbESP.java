package AFN;

import java.util.Arrays;
import java.util.HashSet;

public class SimbESP {
    public static char Epsilon = (char)5;
    public static char Fin = (char)0;
    public static int Error = 20000;
    public static int Omitir = 20001; // Omitir Saltos, Espacios y tabulaciones
    public static HashSet<Character> SimbolosOmitir = new HashSet<>(Arrays.asList(' ', '\n' , '\t'));
}
