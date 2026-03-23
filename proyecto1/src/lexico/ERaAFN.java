package lexico;

import java.nio.file.Paths;

import AFN.AFN;


public class ERaAFN {

    // Tokens
    private static final int Union = 10; // |
    private static final int CerrPos = 20; // +
    private static final int CerrKln = 30; // '*
    private static final int CerrOp = 40; // '?
    private static final int ParIzq = 50; // (
    private static final int ParDer = 60; // )
    private static final int CorchIzq = 70; // [
    private static final int CorchDer = 80; // ]
    private static final int Guion = 90; // -
    private static final int Simb = 100; // [a-z]|[A-Z]|[0-9]|\°(...)

    // Analizador
    private AnalisisLexico Lexic;

//D:\\ARchivos\\JAVA\\Compilador\\Proyecto_Compiladores\\proyecto1\\src\\lexico
    public ERaAFN(String sigma, AFN F){
        //Path AFDpath = Paths.get("proyecto1\\src\\lexico\\AFD_ER.afnd"); 
        // System.out.println(AFDpath.toAbsolutePath().toString());
        Lexic = new AnalisisLexico(sigma,Paths.get("proyecto1\\src\\lexico\\AFD_ER.afnd").toAbsolutePath().toString());
        E(F);
    }

    // * Descenso recursivo * 

    // E->TE'
    private boolean E(AFN f){
        return (T(f) ? Ep(f) : false);
        /*if(T(f))
            return Ep(f);
        return false;*/
    }

    // E'-> or TE' | \epsilon :: (or = '|')
    private boolean Ep(AFN f){
        if(Lexic.yylex() == Union){ // or TE'
            AFN f1 = new AFN();
            if(T(f1)){ 
                f.UnirAFN(f1);
                return Ep(f);
            }
            return false;
        }
        // \epsilon
        Lexic.UndoToken();
        return true;
    }

    // T->CT'
    private boolean T(AFN f){
        return (C(f) ? Tp(f) : false);
        /*if(C(f))
            return Tp(f);
        return false;*/
    }

    // T'->CT' | \epsilon (Concatenación)
    private boolean Tp(AFN f){
        StatusLexico e = new StatusLexico(Lexic);
        AFN f1 = new AFN();
        if(C(f1)){
            // Concatenación
            f.ConcatenarAFN(f1);
            return Tp(f);
        }
        Lexic.SetStatus(e);
        return true;
    }

    // C->FC'
    private boolean C(AFN f){
        return (F(f) ? Cp(f) : false);

        /*
        if(F(f))
            return Cp(f);
        return false;*/
    }

    // C'-> +C'|*C'|?C'|\epsilon (Cerraduras)
    private boolean Cp(AFN f){
        switch (Lexic.yylex()) {
            case CerrPos: // +C'
                f.CerrPositiva();
                return Cp(f);
    
            case CerrKln: // '*'C?
                f.CerrKleene();;
                return Cp(f);

            case CerrOp: // '?'C'
                f.CerrOpcional();
                return Cp(f);
        }
        
        // \epsilon
        Lexic.UndoToken();
        return true;
    }

    // F->(E)|Simb|[Simb-Simb]
    private boolean F(AFN f){
        switch (Lexic.yylex()) { // get token
            case ParIzq: // (E)
                if(E(f)){
                    return Lexic.yylex() == ParDer;
                }
                break;
            
            case Simb: // Simb
                f.CrearAFNBasico((Lexic.Lexema.contains("\\") ? Lexic.Lexema.charAt(1) : Lexic.Lexema.charAt(0)));
                return true;

            case CorchIzq: //[Simb-Simb]
                if(Lexic.yylex() != Simb) return false;
                char simb1 = (Lexic.Lexema.contains("\\") ? Lexic.Lexema.charAt(1) : Lexic.Lexema.charAt(0));
                
                if(Lexic.yylex() != Guion) return false;
                if(Lexic.yylex() != Simb) return false;
                char simb2 = (Lexic.Lexema.contains("\\") ? Lexic.Lexema.charAt(1) : Lexic.Lexema.charAt(0));

                if(Lexic.yylex() != CorchDer) return false;

                f.CrearAFNBasico(simb1,simb2);
                return true;
        }
        return false;
    }

}
