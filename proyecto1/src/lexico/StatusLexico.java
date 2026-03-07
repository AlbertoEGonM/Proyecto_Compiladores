package lexico;

import java.util.Stack;

public class StatusLexico {    
    public int token;
    public int edoActual;
    public int edoTransicion;
    public String cadenaSigma;
    public String lexema;
    public boolean PasoPorEdoAcept;
    public int IniLexema;
    public int FinLexema;
    public int IndiceCaracterActual;
    public char caracterActual;
    public Stack<Integer> Pila;

    public StatusLexico(){
        this.token = -1;
        this.edoActual = 0;
        this.edoTransicion = 0;
        this.cadenaSigma = "";
        this.lexema = "";
        this.PasoPorEdoAcept = false;
        this.IniLexema = 0;
        this.FinLexema = -1;
        this.IndiceCaracterActual = 0;
        this.caracterActual = '\0';
        this.Pila = new Stack<>();
    }
}
