package lexico;

import AFD.AFD;
import java.util.Stack;

public class StatusLexico {    
    public int token;
    private int EdoActual, EdoTransicion;
    private String CadenaSigma;
    public String Lexema;
    private boolean PasoPorEdoAcept;
    private int IniLexema, FinLexema, IndiceCaracterActual;
    private char CaracterActual;
    private Stack<Integer> Pila = new Stack<>();
    private AFD AutomataFD;

    public StatusLexico(){
        this.token = -1;
        this.EdoActual = 0;
        this.EdoTransicion = 0;
        this.CadenaSigma = "";
        this.Lexema = "";
        this.PasoPorEdoAcept = false;
        this.IniLexema = 0;
        this.FinLexema = -1;
        this.IndiceCaracterActual = 0;
        this.CaracterActual = '\0';
        this.Pila = new Stack<>();
    }

    // setters y getters

    public void setToken(int token){
        this.token = token;
    }

    public void setEdoActual(int EdoActual){
        this.EdoActual = EdoActual;
    }

    public void setEdoTransicion(int EdoTransicion){
        this.EdoTransicion = EdoTransicion;
    }

    public void setCadenaSigma(String CadenaSigma){
        this.CadenaSigma = CadenaSigma;
    }

    public void setLexema(String Lexema){
        this.Lexema = Lexema;
    }

    public void setPasoPorEdoAcept(boolean PasoPorEdoAcept){
        this.PasoPorEdoAcept = PasoPorEdoAcept;
    }

    public void setIniLexema(int IniLexema){
        this.IniLexema = IniLexema;
    }

    public void setFinLexema(int FinLexema){
        this.FinLexema = FinLexema;
    }

    public void setIndiceCaracterActual(int IndiceCaracterActual){
        this.IndiceCaracterActual = IndiceCaracterActual;
    }

    public void setCaracterActual(char CaracterActual){
        this.CaracterActual = CaracterActual;
    }

    public void setPila(Stack<Integer> Pila){
        this.Pila = Pila;
    }

    public void setAutomataFD(AFD AutomataFD){
        this.AutomataFD = AutomataFD;
    }

    public int getToken(){
        return this.token;
    }

    public int getEdoActual(){
        return this.EdoActual;
    }

    public int getEdoTransicion(){
        return this.EdoTransicion;
    }

    public String getCadenaSigma(){
        return this.CadenaSigma;
    }

    public String getLexema(){
        return this.Lexema;
    }

    public boolean getPasoPorEdoAcept(){
        return this.PasoPorEdoAcept;
    }

    public int getIniLexema(){
        return this.IniLexema;
    }

    public int getFinLexema(){
        return this.FinLexema;
    }

    public int getIndiceCaracterActual(){
        return this.IndiceCaracterActual;
    }

    public char getCaracterActual(){
        return this.CaracterActual;
    }

    public Stack<Integer> getPila(){
        return this.Pila;
    }

    public AFD getAutomataFD(){
        return this.AutomataFD;
    }
}
