package lexico;

import AFD.AFD;
import AFN.SimbESP;
import java.util.Stack;

public class AnalisisLexico {
    public int token;
    private int EdoActual, EdoTransicion;
    private String CadenaSigma;
    public String Lexema;
    private boolean PasoPorEdoAcept;
    private int IniLexema, FinLexema, IndiceCaracterActual;
    private char CaracterActual;
    private Stack<Integer> Pila = new Stack<>();
    private AFD AutomataFD;

    public AnalisisLexico() {
        CadenaSigma = "";
        PasoPorEdoAcept = false;
        IniLexema = 0;
        FinLexema = -1;
        IndiceCaracterActual = 0;
        token = -1;
        Pila.clear();
        AutomataFD = null;
    }


    public AnalisisLexico(String sigma, String rutaAFD) {
        AutomataFD = new AFD(rutaAFD);
        CadenaSigma = sigma;
        PasoPorEdoAcept = false;
        IniLexema = 0;
        FinLexema = -1;
        IndiceCaracterActual = 0;
        token = -1;
        Pila.clear();
    }

    public void SetSigma(String sigma) {
        CadenaSigma = sigma;
        PasoPorEdoAcept = false;
        IniLexema = 0;
        FinLexema = -1;
        IndiceCaracterActual = 0;
        token = -1;
        Pila.clear();
    }

    public int yylex() {
        while (true) {
            Pila.push(IndiceCaracterActual); 

            if (IndiceCaracterActual >= CadenaSigma.length()) {
                Lexema = "";
                return SimbESP.Fin; 
            }

            IniLexema = IndiceCaracterActual;
            EdoActual = 0;
            PasoPorEdoAcept = false;
            FinLexema = -1;
            token = -1;

            while (IndiceCaracterActual < CadenaSigma.length()) {
                CaracterActual = CadenaSigma.charAt(IndiceCaracterActual);
                
                int c = (int) CaracterActual;
                if (c > 255) c = 255; 

                EdoTransicion = AutomataFD.TablaAFD[EdoActual][c];

                if (EdoTransicion != -1) {
    
                    if (AutomataFD.TablaAFD[EdoTransicion][256] != -1) {
                        PasoPorEdoAcept = true;
                        token = AutomataFD.TablaAFD[EdoTransicion][256];
                        FinLexema = IndiceCaracterActual;
                    }
                    IndiceCaracterActual++;
                    EdoActual = EdoTransicion;
                }
                else
                    break;
            }


            if (!PasoPorEdoAcept) {
                IndiceCaracterActual = IniLexema + 1;
                Lexema = CadenaSigma.substring(IniLexema, IniLexema + 1);
                token = SimbESP.Error; 
                return token;
            }

            Lexema = CadenaSigma.substring(IniLexema, FinLexema + 1);
            IndiceCaracterActual = FinLexema + 1;

            if (token != SimbESP.Omitir)
                return token;
        }
    }
}