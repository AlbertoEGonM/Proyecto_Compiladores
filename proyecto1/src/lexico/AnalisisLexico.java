package lexico;

import AFD.AFD;
import AFN.SimbESP;

import java.util.ArrayList;
import java.util.Stack;

public class AnalisisLexico {
    private int IniLexema, FinLexema, IndiceCaracterActual;
    public int token;
    private int EdoActual, EdoTransicion;
    private String CadenaSigma;
    public String Lexema;
    private boolean PasoPorEdoAcept;
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
    public AnalisisLexico(String sigma, AFD F) {
        AutomataFD = F;
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

    public void SetAFD(AFD F){
        AutomataFD = F;
    }

    public String CadenaAnalizar(){
        return CadenaSigma.substring(IndiceCaracterActual,CadenaSigma.length());
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
                }else
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

            if (token != SimbESP.Omitir) {
                return token;
            }
        }
    }

    

    public void UndoToken(){
        if(Pila.empty()){
            return;
        }
        IndiceCaracterActual = Pila.pop();
    }

    public String[][] AnalisisSimple(){
        ArrayList<ArrayList<String>> Table = new ArrayList<>();
        ArrayList<String> Auxiliar;
        int Token = -1;
        //int z = 0;
        do{
            Auxiliar = new ArrayList<>();
            Token = yylex();
            Auxiliar.add(Lexema);
            Auxiliar.add(""+Token);
            Table.add(Auxiliar);
            /*System.out.println("Aquí vamos en el " + z + " Tok "+ Token + " Lexema " + Lexema);
            if(Lexema.contains("\\"))
                System.out.println(Lexema.charAt(0) + " :: " + Lexema.charAt(1));
            
            z++;*/
        }while(Token != SimbESP.Fin);

        String[][] TablaTokenLex = new String[Table.size()][2];
        for(int i=0; i<Table.size();i++){
            Auxiliar = Table.get(i);
            TablaTokenLex[i] = Auxiliar.toArray(new String[0]);
        }
        return TablaTokenLex;
    } 

    //  Set and Get
    public int getIniLexema(){return IniLexema;}

    public int getFinlexema(){return FinLexema;}

    public int getInidiceCA(){return IndiceCaracterActual;}

    public int getToken(){return token;}

    public int getEdoActual(){return EdoActual;}

    public int getEdoTransicion(){return EdoTransicion;}

    public String getCadenaSigma(){return CadenaSigma;}

    public String getLexema(){return Lexema;}

    public boolean getPasoPorEstadoAcept(){return PasoPorEdoAcept;}

    public char getCaracterActual(){return CaracterActual;}

    public Stack<Integer> getPila(){return Pila;}
    
    public AFD getAFD(){return AutomataFD;}

    public void SetStatus(StatusLexico e){
        IniLexema = e.getIniLexema();
        FinLexema = e.getFinLexema();
        IndiceCaracterActual = e.getIndiceCaracterActual();
        token = e.getToken();
        EdoActual = e.getEdoActual();
        EdoTransicion = e.getEdoTransicion();
        CadenaSigma = e.getCadenaSigma();
        Lexema = e.getLexema();
        PasoPorEdoAcept = e.getPasoPorEdoAcept();
        CaracterActual = e.getCaracterActual();
        Pila = e.getPila();
        AutomataFD = e.getAutomataFD();
    }
}
