package lexico;

import AFN.AFN;
import java.nio.file.Paths;

public class ERaAFN {

    // Tokens
    private static final int UNION = 10; // |
    private static final int CERRPOS = 20; // +
    private static final int CERRKLN = 30; // '*
    private static final int CERROP = 40; // '?
    private static final int PARIZQ = 50; // (
    private static final int PARDER = 60; // )
    private static final int CORCHIZQ = 70; // [
    private static final int CORCHDER = 80; // ]
    private static final int GUION = 90; // -
    private static final int SIMB = 100; // [a-z]|[A-Z]|[0-9]|\°(...)

    // Analizador
    private final AnalisisLexico Lexic;

    //D:\\ARchivos\\JAVA\\Compilador\\Proyecto_Compiladores\\proyecto1\\src\\lexico
    public ERaAFN(String sigma, AFN F){
        // Path AFDpath = Paths.get("proyecto1\\src\\lexico\\AFD_ER.afnd"); 
        // System.out.println(AFDpath.toAbsolutePath().toString());
        Lexic = new AnalisisLexico(sigma,Paths.get("proyecto1\\src\\lexico\\afdER1.afnd").toAbsolutePath().toString());
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
        if(Lexic.yylex() == UNION){ // or TE'
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
        return switch (Lexic.yylex()) {
            case CERRPOS->{ // +C'
                f.CerrPositiva();
                yield Cp(f);
            }
            case CERRKLN->{ // '*'C?
                f.CerrKleene();
                yield Cp(f);
            }
            case CERROP->{ // '?'C'
                f.CerrOpcional();
                yield  Cp(f);
            }
            default->{  // \epsilon
                Lexic.UndoToken();
                yield true;
            }
        };
    }

    /*
    private boolean Cp(AFN f){
        switch (Lexic.yylex()) {
            case CERRPOS: // +C'
                f.CerrPositiva();
                return Cp(f);
    
            case CERRKLN: // '*'C?
                f.CerrKleene();;
                return Cp(f);

            case CERROP: // '?'C'
                f.CerrOpcional();
                return Cp(f);
        }
        
        
        // \epsilon
        Lexic.UndoToken();
        return true;
    }
    */

    // F->(E)|Simb|[Simb-Simb]
    private boolean F(AFN f){
        return switch (Lexic.yylex()) { // get token
            case PARIZQ-> (E(f) ? Lexic.yylex() == PARDER : false); // (E)
                /*if(E(f)){
                    return Lexic.yylex() == ParDer;
                }*/
            
            case SIMB->{ // Simb
                f.CrearAFNBasico((Lexic.Lexema.contains("\\") ? Lexic.Lexema.charAt(1) : Lexic.Lexema.charAt(0)));
                yield true;
            }
            case CORCHIZQ->{ //[Simb-Simb]
                if(Lexic.yylex() != SIMB) yield false;
                char simb1 = (Lexic.Lexema.contains("\\") ? Lexic.Lexema.charAt(1) : Lexic.Lexema.charAt(0));
                
                if(Lexic.yylex() != GUION) yield false;
                if(Lexic.yylex() != SIMB) yield false;
                char simb2 = (Lexic.Lexema.contains("\\") ? Lexic.Lexema.charAt(1) : Lexic.Lexema.charAt(0));

                if(Lexic.yylex() != CORCHDER) yield false;

                f.CrearAFNBasico(simb1,simb2);
                yield true;
            }
            default-> false;
        };
    }

}
