package Lexico;

import AFD.AFD;
import AFN.SimbESP;
import java.util.Stack;

public class AnalizLexico {
    protected int token, edoActual, edoTransicion;
    protected int IniLexema, FinLexema, IndiceCaracterActual;
    public String lexema;
    protected String sigma;
    protected char caracterActual;
    protected boolean PasoPorEdoAcept;
    protected Stack<Integer> Pila = new Stack<>();
    protected AFD AutomataAFD;

    public AnalizLexico() {
        sigma = "";
        token = -1;
        IniLexema = FinLexema = -1;
        IndiceCaracterActual = -1;
        Pila.clear();
        PasoPorEdoAcept = false;
    }

    public AnalizLexico(String CadenaSigma, AFD Automata){
        this.sigma = CadenaSigma;
        this.AutomataAFD = Automata;
        token = -1;
        IniLexema = IndiceCaracterActual = 0;
        FinLexema = -1;
        Pila.clear();
        PasoPorEdoAcept = false;
    }

    public AnalizLexico(String sigma, String RutaAFD){
        this.sigma = sigma;
        this.AutomataAFD = new AFD(RutaAFD);
        token = -1;
        IniLexema = IndiceCaracterActual = 0;
        FinLexema = -1;
        Pila.clear();
        PasoPorEdoAcept = false;
    }


    public void CargarCadena(String CadenaSigma){
        this.sigma = CadenaSigma;
        token = -1;
        IniLexema = FinLexema = -1;
        IndiceCaracterActual = 0;
        lexema = "";
        PasoPorEdoAcept = false;
    }

    public void CargarAFD(String RutaAFD){
        this.AutomataAFD = new AFD(RutaAFD);
    }

    public void CargarAFD(AFD Automata){
        this.AutomataAFD = Automata;
    }

    public String CadenaXAnalizar(){
        return sigma.substring(IndiceCaracterActual, sigma.length());
    }

    public int yylex(){
        Pila.push(IndiceCaracterActual);

        if (AutomataAFD == null) {
            System.out.println("Error: El AFD no ha sido cargado.");
            lexema = "";
            return SimbESP.Error;
        }

        if(IndiceCaracterActual >= sigma.length()){
            lexema = "";
            return SimbESP.Fin;
        }

        // Inicio:
        IniLexema = IndiceCaracterActual;
        Pila.push(IniLexema);
        edoActual = 0;
        PasoPorEdoAcept = false;
        FinLexema = -1;
        token = -1;

        while(IndiceCaracterActual < sigma.length()){
            caracterActual = sigma.charAt(IndiceCaracterActual);
            edoTransicion = AutomataAFD.TablaAFD[edoActual][(int)caracterActual];
            if(edoTransicion == -1){
                
                if(PasoPorEdoAcept){
                    
                    lexema = sigma.substring(IniLexema, FinLexema+1);
                    IndiceCaracterActual = FinLexema+1;
                    if(!SimbESP.isSimbOmitir(caracterActual)){
                        return token;
                    }
                    token = SimbESP.Omitir;
                    return yylex();
                }
                else{ // Si no se ha pasado por un estado de aceptación, se regresa al inicio del lexema y se marca el error
                    IndiceCaracterActual = IniLexema +1;
                    lexema = sigma.substring(IniLexema, IndiceCaracterActual);
                    token = SimbESP.Error;
                    return token;
                }
            }
            else{
                if(AutomataAFD.TablaAFD[edoActual][256] != -1){ // Si el estado al que se transiciona es un estado de aceptación
                    PasoPorEdoAcept = true;
                    FinLexema = IndiceCaracterActual;
                    token = AutomataAFD.TablaAFD[edoActual][256];
                }
                edoActual = edoTransicion;
                IndiceCaracterActual++;
            }
        }
        

        return token;
    }

    class StatusLexico {
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


}
