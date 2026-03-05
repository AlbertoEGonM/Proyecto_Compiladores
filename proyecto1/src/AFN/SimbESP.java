package AFN;

public class SimbESP {
    public static char Epsilon = (char)5;
    public static char Fin = (char)0;
    public static int Error = 20000;
    public static int Omitir = 20001; // Omitir Saltos, Espacios y tabulaciones

    public static boolean isSimbOmitir(char c){
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public static boolean isTokenOmitir(int token){
        return token == Omitir;
    }

    public static boolean isSimbEspecial(char c){
        return c == Epsilon || c == Fin || c == (char)Error || c == (char)Omitir;
    }
}