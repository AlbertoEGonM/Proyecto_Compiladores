package sintactico;

import AFN.SimbESP;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lexico.AnalisisLexico;

public class Gramatica {
    public List<LadoIzq> Reglas = new LinkedList<>();;
    public Set<Simbolo> SimbolosTerminales = new HashSet<>();;
    public Set<Simbolo> SimbolosNoTerminales = new HashSet<>();;
    public Map<Simbolo,Set<Simbolo>> FirmMap = new HashMap<>();
    public Map<Simbolo,Set<Simbolo>> FollMap = new HashMap<>();

    public Gramatica(){}

    public Gramatica(String input) {
        DescensoRecursivo DS = new DescensoRecursivo(this , input);
        DS.getHello();
        this.SimbolosTerminales.add(Simbolo.SimboloFinal);
        //this.SimbolosTerminales.remove(Simbolo.SimbEPS);
    }

    public void AumentarGramatica(){
        this.Reglas.addFirst(new LadoIzq(){{
            this.SimboloIzq = new Simbolo(){{
                this.Nombre = "_Amt_";
                this.Terminal = false;
            }};
            this.Simbolos = new LinkedList<>();
        }});
        this.Reglas.get(0).Simbolos.add(this.Reglas.get(1).SimboloIzq);
        this.SimbolosNoTerminales.add(this.Reglas.get(0).SimboloIzq);
    }

    public Set<Simbolo> First(Simbolo L){
        if(L == null) 
            return java.util.Collections.emptySet();
        
        if(FirmMap.containsKey(L)) 
            return FirmMap.get(L);

        if(L.Terminal)
            return Set.of(L);
        
        FirmMap.put(L, java.util.Collections.emptySet());
        
        Set<Simbolo> C = new HashSet<>();
        for (LadoIzq Regla : this.Reglas)
            if(L.Nombre.equals(Regla.SimboloIzq.Nombre)) C.addAll(First(Regla.Simbolos));
        
        FirmMap.put(L, C);
        return  C;
    }

    public Set<Simbolo> First(List<Simbolo> L){
        if(L.isEmpty()) 
            return java.util.Collections.emptySet();

        Set<Simbolo> C = new HashSet<>();
        
        C.addAll(First(L.get(0)));

        if(C.contains(Simbolo.SimbEPS) && L.size()>1){
            List<Simbolo> Aux = new LinkedList<>(L);
            Aux.removeFirst();
            C.remove(Simbolo.SimbEPS);
            C.addAll(First(Aux));
        }
        return C;
    }

    public Set<Simbolo> Follow(Simbolo A){
        if(FollMap.containsKey(A)) 
            return FollMap.get(A);

        Set<Simbolo> C = new HashSet<>();
        
        if(A.Terminal) 
            return C;

        FollMap.put(A, java.util.Collections.emptySet());

        if(Reglas.get(0).SimboloIzq.equals(A)) 
            C.add(Simbolo.SimboloFinal); // Simbolo $
        
        int index;
        Simbolo B;
        for (LadoIzq Regla : this.Reglas){
            index = Regla.Simbolos.indexOf(A);
            if(index <= -1) 
                continue;

            if(Regla.Simbolos.size()-1 > index){ // Dentro de los simbolos derechos B->aAB
                index++;
                B = Regla.Simbolos.get(index);

                if(B.equals(A)) continue;
            
                Set<Simbolo> Aux = new HashSet<>();
                
                Aux.addAll(First(B));
                if(Aux.remove(Simbolo.SimbEPS))
                    Aux.addAll(Follow(B));
                
                C.addAll(Aux);
            }
            else{ // Al final de los simbolos derechos B->aA
                B = Regla.SimboloIzq;
                if(B.equals(A)) continue;
                C.addAll(Follow(B));
            }
        }

        FollMap.put(A, C);

        return C;
    }

    public void setGramatica(String input){
        this.SimbolosNoTerminales.clear();
        this.SimbolosTerminales.clear();
        this.FirmMap.clear();
        this.FollMap.clear();
        DescensoRecursivo DS = new DescensoRecursivo(this , input);
        DS.getHello();
        this.SimbolosTerminales.add(Simbolo.SimboloFinal);
    }

    public List<Simbolo> getSimbolosTerminales() {
        return new LinkedList<>(List.copyOf(SimbolosTerminales));
    }

}

class DescensoRecursivo{
    // Tokens
    private static final int PC = 10; // ';'
    private static final int OR = 20; // '|' || \|
    private static final int FLECHA = 30; // '->' || \->
    private static final int SIMBOLO = 40; // // ([a-z]|[A-Z]|[0-9])+ || (([!-,])+|(\\\*)+|(\\\-)+|([.-:])+|(\\;)+|([<-=])+|(\\>)+|([\?-{])+|(\\\|)+|([}-~])+)
    // ((([A-Z][a-z]|[a-z]|_))+(')*|([A-Z]|_)+(')*|([0-9])+|(\()|(\))|(\[)|(\])|(\-)|(\+)|(\\)|(\*)|(\?)|(<)|(>)|(=)|(&)|(^)|({)|(})|(.)|(%)|(#)|(@)|(~)|(/))
    // // ((([A-Z]*|[a-z]|_))+(')*|([0-9])+|(\()|(\))|(\[)|(\])|(\-)|(\+)|(\\)|(\*)|(\?)|(<)|(>)|(=)|(&)|(^)|({)|(})|(.)|(%)|(#)|(@)|(~)|(/))

    // Variables
    Gramatica gramatica;
    AnalisisLexico analisisLexico;
    Map<String,Simbolo> noTerminales = new HashMap<>();
    Map<String,Simbolo> Terminales = new HashMap<>();

    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DescensoRecursivo(Gramatica gramatica , String input) {
        this.gramatica = gramatica;
        analisisLexico = new AnalisisLexico(input , Paths.get("proyecto1\\src\\sintactico\\AFDGram.afnd").toAbsolutePath().toString());
        
        ////SimbESP.SimbolosEpsilon.forEach(Eps->Terminales.put(Eps,Simbolo.SimbEPS));
        G();
        gramatica.SimbolosTerminales.addAll(Terminales.values());
        gramatica.SimbolosNoTerminales.addAll(noTerminales.values());

    }

    // G → Reglas
    boolean G(){
        return Reglas();
    }

    // Reglas → Regla PC ReglasP
    boolean Reglas(){
        return 
            (Regla() ? 
                ((analisisLexico.yylex() == PC) ? ReglasP() : false) 
            : false); 
    }

    // ReglasP → Regla PC ReglasP | ϵ
    boolean ReglasP(){
        if(Regla())
            return (analisisLexico.yylex() == PC ? ReglasP() : false);
        // ϵ
        analisisLexico.UndoToken();
        return true;
    }

    // Regla → LadoIzq FLECHA LadosDerechos
    boolean Regla(){
        Simbolo s = LadoIzquierdo();
        return 
            (s != null ? 
                (analisisLexico.yylex() == FLECHA ? LadosDerechos(s) : false) 
            : false);
    }

    // LadoIzq → SIMB
    Simbolo LadoIzquierdo(){
        if(analisisLexico.yylex() != SIMBOLO) return null; // false
        if(noTerminales.containsKey(analisisLexico.Lexema)) return noTerminales.get(analisisLexico.Lexema);
        
        Simbolo s;
        
        if(Terminales.containsKey(analisisLexico.Lexema)){
            s = Terminales.remove(analisisLexico.Lexema);
            s.Terminal = false;    
        }
        else{        
            s = new Simbolo();
            s.Nombre = analisisLexico.Lexema;
            s.Terminal = false;
        }
        noTerminales.put(analisisLexico.Lexema,s);
        return s;
    }

    // LadosDerechos → LadoDerecho LadosDerechosP
    boolean LadosDerechos(Simbolo s){
        return (LadoDerecho(s) ? LadosDerechosP(s) : false);
    }

    // LadosDerechosP → OR LadoDerecho LadoDerechosP | ϵ
    boolean LadosDerechosP(Simbolo s){
        if(analisisLexico.yylex() == OR)
            return (LadoDerecho(s) ? LadosDerechosP(s) : false);
        // ϵ
        analisisLexico.UndoToken();
        return true;
    }

    // LadoDerecho → ListaSimbolos 
    boolean LadoDerecho(Simbolo s){
        List<Simbolo> listaSimbolos = new LinkedList<>();
        listaSimbolos.clear();
        if(ListaSimbolos(listaSimbolos)){
            gramatica.Reglas.add(new LadoIzq(){
                {
                SimboloIzq = s;
                Simbolos = listaSimbolos;
                }
            });
            return true;
        }
        return false;
    }

    // ListaSimbolos → SIMB ListaSimbolosP
    boolean ListaSimbolos(List<Simbolo> listaSimbolos){
        return SIMB(listaSimbolos);
    }

    // ListaSimbolosP→SIMB ListaSimbolosP | ϵ
    boolean ListaSimbolosP(List<Simbolo> listaSimbolos){
        if(SIMB(listaSimbolos))
            return true;
        
        analisisLexico.UndoToken();
        return true;
    }

    // SIMB 
    boolean SIMB(List<Simbolo> listaSimbolos){
        if(analisisLexico.yylex() != SIMBOLO) return false;
        Simbolo s;

        if(Terminales.containsKey(analisisLexico.Lexema)) s = Terminales.get(analisisLexico.Lexema);
        else if(noTerminales.containsKey(analisisLexico.Lexema)) s = noTerminales.get(analisisLexico.Lexema);
        else if(SimbESP.SimbolosEpsilon.contains(analisisLexico.Lexema)) s = Simbolo.SimbEPS;
        
        else{
            s = new Simbolo();
            s.Nombre = analisisLexico.Lexema;
            s.Terminal = true;
        }
        

        if(ListaSimbolosP(listaSimbolos)){
            listaSimbolos.addFirst(s);
            if(!(noTerminales.containsKey(s.Nombre) || Terminales.containsKey(s.Nombre))) Terminales.put(s.Nombre, s);
            return true;
        }

        /*if(analisisLexico.yylex() == Simbolo){
            Simbolo s = new Simbolo();
            s.Nombre = analisisLexico.Lexema;
            s.Terminal = true;
            gramatica.SimbolosTerminales.add(s);
            if(ListaSimbolosP(listaSimbolos)){
                listaSimbolos.addFirst(s);
                gramatica.SimbolosTerminales.add(s);
                return true;
            }
        }
        return false;    
        */
        return false;
    }

    void getHello(){
        System.out.println("Hello");
    }

}

