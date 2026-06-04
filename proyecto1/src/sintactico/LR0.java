package sintactico;

import AFN.SimbESP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import lexico.AnalisisLexico;

public class LR0 {
	public AnalisisLexico Lex;
    private Gramatica Gram;
    private String[][] TablaLR;
    private int[][] VT;
    private int[] VNT;
    private Set<Simbolo> V;
    
    public LR0(){
        Lex = null;
        Gram = null;
        TablaLR = null;
        VT = null;
        VNT = null;
    }

    public LR0(Gramatica Gram, AnalisisLexico Lex){
        this.Lex = Lex;
        this.Gram = Gram;
        TablaLR = null;
        VT = null;
        VNT = null;
    }

    public LR0(String SigmaGram){
        this.Gram = new Gramatica(SigmaGram);
    }

    public LR0(String SigmaGram, String RutaAFD){
        this.Gram = new Gramatica(SigmaGram);
        this.Lex = new AnalisisLexico(null, RutaAFD);
    }

    public LR0(String SigmaGram, String RutaAFD, String CadenaAnalizar){
        this.Gram = new Gramatica(SigmaGram);
        this.Lex = new AnalisisLexico(CadenaAnalizar, RutaAFD);
    }

    public int ObtenerIndice(Simbolo Simb){
        return Arrays.binarySearch(VNT, Simb.hashCode());
    }

    public int ObtenerColumna(int Token){
        return Arrays.binarySearch(VT[0], Token);
    }

    public int ObtenerColumna(Simbolo Simb){
        int idx = Arrays.binarySearch(VT[2], Simb.hashCode());
        if( idx >= 0)
            return ObtenerColumna(VT[3][idx]);
        return -1;
    }

    public int ObtenerColumnaAbsoluta(Simbolo simb) {
        if (simb.Terminal) {
            return ObtenerColumna(simb); // Si es terminal, su índice en VT[0] mapea directamente a las primeras columnas
        } else { // Si no es terminal, buscamos su índice relativo en VNT
            int idxNoTerm = ObtenerIndice(simb);
            if (idxNoTerm >= 0) {
                // Le sumamos el largo de los terminales para desplazarlo a su sección correspondiente
                return VT[0].length + idxNoTerm;
            }
        }
        return -1; // Símbolo no encontrado
    }

    public int ObtenerColumnaAbsoluta(int tokenTerminal) {
        return ObtenerColumna(tokenTerminal);
    }

    /*private int ObtenerTokenDeSimbolo(Simbolo Simb) {
        int idx = Arrays.binarySearch(VT[2], Simb.hashCode());
        if (idx >= 0) 
            return VT[3][idx];
        return -1;
    }*/

    public boolean CreateVT(List<Simbolo> ListaSimb , List<Integer> Tokens){ // Relación simbolo token debe ser 1 a 1
        if(Tokens.isEmpty() || ListaSimb.isEmpty())
            return false;
        
        int index;
        
        VT = new int[4][0];
        VT[0] = Tokens.stream().mapToInt(Token->Token).toArray();
        Arrays.sort(VT[0]);

        VT[1] = new int[VT[0].length];

        for(int i=0; i<VT[0].length; i++){
            index = Arrays.binarySearch(VT[0], Tokens.get(i));
            VT[1][index] = ListaSimb.get(i).hashCode();
        }

        VT[2] = ListaSimb.stream().mapToInt(Simb->Simb.hashCode()).toArray();
        Arrays.sort(VT[2]);

        VT[3] = new int[VT[2].length];

        for (int i = 0; i < VT[3].length; i++) {
            index = Arrays.binarySearch(VT[2], ListaSimb.get(i).hashCode());
            VT[3][index] = Tokens.get(i);
        }
        
        return true;
    }

    public void CreateVNT(){
        VNT = Gram.SimbolosNoTerminales.stream().mapToInt(Simb->Simb.hashCode()).toArray();
        Arrays.sort(VNT);
    }

    public void CreateV(){
        V = new HashSet<>();
        V.addAll(Gram.SimbolosNoTerminales);
        V.addAll(Gram.SimbolosTerminales);
    }

    public void init_table(){
        int totalColumnas = VT[0].length + VNT.length;
        int j = 0;

        Map<Set<itemLR0>, Conj_Sj> estadosDescubiertos = new HashMap<>();
        Queue<Conj_Sj> porAnalizar = new LinkedList<>();
        List<String[]> tablaTemporal = new ArrayList<>();

        Set<itemLR0> inicialCerradura = Cerradura(new itemLR0(0, 0));
        Conj_Sj s0 = new Conj_Sj(j++, inicialCerradura);
        estadosDescubiertos.put(inicialCerradura, s0);
        porAnalizar.add(s0);
        
        String[] filaInicial = new String[totalColumnas];
        Arrays.fill(filaInicial, "-1");
        tablaTemporal.add(filaInicial);

        while (!porAnalizar.isEmpty()) {
            Conj_Sj estadoActual = porAnalizar.poll();
            
            int filaActual = estadoActual.j;

            for (itemLR0 item : estadoActual.ConjuntoSJ) {
                int tamRegla = Gram.Reglas.get(item.NumRegla).Simbolos.size();
                
                if (item.PosPunto == tamRegla) {
                    if (item.NumRegla == 0) {
                        int colAceptar = ObtenerColumna(SimbESP.Fin); 
                        if (colAceptar >= 0) {
                            tablaTemporal.get(filaActual)[colAceptar] = "acc";
                        }
                    } else {
                        for (Simbolo simbTerminal : Gram.Follow(Gram.Reglas.get(item.NumRegla).SimboloIzq)) {
                            int colTerminal = ObtenerColumna(simbTerminal);
                            if (colTerminal >= 0)
                                tablaTemporal.get(filaActual)[colTerminal] = "r" + item.NumRegla;
                        }
                    }
                }
            }

            for (Simbolo simb : V) {
                Set<itemLR0> deIrA = IrA(estadoActual.ConjuntoSJ, simb);

                if (deIrA != null && !deIrA.isEmpty()) {
                    Conj_Sj estadoDestino = estadosDescubiertos.get(deIrA);

                    if (estadoDestino == null) {
                        estadoDestino = new Conj_Sj(j++, deIrA);
                        
                        estadosDescubiertos.put(deIrA, estadoDestino);
                        porAnalizar.add(estadoDestino);

                        String[] nuevaFila = new String[totalColumnas];
                        Arrays.fill(nuevaFila, "-1");
                        tablaTemporal.add(nuevaFila);
                    }

                    estadoActual.agregarTransicion(simb, estadoDestino);

                    int colMatriz = ObtenerColumnaAbsoluta(simb);
                    if(colMatriz >=0){
                        tablaTemporal.get(filaActual)[colMatriz] = (simb.Terminal ? "d" +estadoDestino.j : String.valueOf(estadoDestino.j));

                    }
                    
                }
            }
        }
        TablaLR = tablaTemporal.toArray(String[][]::new);
    }


    public String[][] AnalisisLR(String cadenaInicial) {
        if (this.TablaLR == null) {
            System.err.println("Error: La tabla LR no ha sido inicializada.");
            return null;
        }
        this.Lex.SetSigma(cadenaInicial);

        List<String[]> bitacora = new ArrayList<>();

        Stack<String> pilaEstados = new Stack<>();
        pilaEstados.push("$");
        pilaEstados.push("0"); // Estado inicial I0

        // Obtener el primer token
        int tokenActual = this.Lex.yylex();
    
        while (true) {
            int estadoActual = Integer.parseInt(pilaEstados.peek());
            int col = ObtenerColumna(tokenActual);
            
            String formatoPila = pilaEstados.stream()
                    .collect(Collectors.joining(" ", "", ""));

            String cadenaRestante = this.Lex.CadenaAnalizar();
            String lexemaActual = this.Lex.Lexema;
            
            if (tokenActual == SimbESP.Fin) {
                cadenaRestante = "$";
            } else {
                cadenaRestante = this.Lex.Lexema + cadenaRestante;
            }

            String accion = (col != -1) ? TablaLR[estadoActual][col] : "-1";

            String accionFormato = accion;
            if (accion == null || accion.equals("-1")) {
                accionFormato = "Error Sintáctico";
            } 
            else if (accion.startsWith("d")) {
                accionFormato = accion; 
            } 
            else if (accion.startsWith("r")) {
                int numRegla = Integer.parseInt(accion.substring(1));
                LadoIzq regla = Gram.Reglas.get(numRegla);
                accionFormato = "r" + numRegla + " :: " + regla.SimboloIzq.Nombre + "->" 
                                + regla.Simbolos.stream().map(s -> s.Nombre).collect(Collectors.joining(""));
            }

            // Registrar la línea en la bitácora
            bitacora.add(new String[]{ formatoPila, cadenaRestante, accionFormato });

            // --- 3. PROCESAR LOGICA TRANSICIONAL ---
            if (accion == null || accion.equals("-1")) {
                System.err.println("Error sintáctico detectado.");
                break; 
            }

            if (accion.equals("acc")) {
                break; // Análisis exitoso
            }

            if (accion.startsWith("d")) {
                String nuevoEstado = accion.substring(1);
                pilaEstados.push(lexemaActual);
                pilaEstados.push(nuevoEstado);
                
                tokenActual = this.Lex.yylex(); 
            } 
            else if (accion.startsWith("r")) { 
                int numRegla = Integer.parseInt(accion.substring(1));
                LadoIzq regla = Gram.Reglas.get(numRegla);

                int elementosAPop = regla.Simbolos.size() * 2;
                for (int i = 0; i < elementosAPop; i++) {
                    if (!pilaEstados.isEmpty()) pilaEstados.pop();
                }
                int estadoExpuesto = Integer.parseInt(pilaEstados.peek());
                int colNoTerminal = ObtenerColumnaAbsoluta(regla.SimboloIzq);

                if (colNoTerminal >= 0) {
                    String irAStr = TablaLR[estadoExpuesto][colNoTerminal];
                    if (irAStr != null && !irAStr.equals("-1")) {
                        pilaEstados.push(regla.SimboloIzq.Nombre);
                        pilaEstados.push(irAStr);
                    } else {
                        bitacora.add(new String[]{formatoPila, cadenaRestante, "Error Ir_A"});
                        break;
                    }
                } else {
                    bitacora.add(new String[]{formatoPila, cadenaRestante, "Error NoTerminal Faltante"});
                    break;
                }
            }
        }

        return bitacora.toArray(String[][]::new);
    }

    Set<itemLR0> IrA(Set<itemLR0> Sj, Simbolo Simbolo_a ){
        return Cerradura(Mover(Sj, Simbolo_a));
    }
    
    Set<itemLR0> Cerradura(Set<itemLR0> A){
        Set<itemLR0> ConjuntoCerr = new HashSet<>();
        
        if(A == null || A.isEmpty())
            return ConjuntoCerr;
        
        ConjuntoCerr.addAll(A);
        
        Queue<Simbolo> PorAnalizarC = new LinkedList<>();
        Set<Simbolo> Analizados = new HashSet<>();
        Simbolo SimboloAct;
        LadoIzq NextRegla;

        for (itemLR0 item : A) {
            Simbolo simbAx = item.GetSimbolobyGram(Gram);
            if(!(simbAx == null || simbAx.Terminal))
                PorAnalizarC.add(simbAx); 
        }

        while(!PorAnalizarC.isEmpty()){
            SimboloAct = PorAnalizarC.poll();
            Analizados.add(SimboloAct);
            
            for(int i=0; i<Gram.Reglas.size(); i++){
                NextRegla = Gram.Reglas.get(i);
                
                if(NextRegla.SimboloIzq.equals(SimboloAct)){
                    ConjuntoCerr.add(new itemLR0(i,0));
                    
                    if(!(Analizados.contains(NextRegla.Simbolos.get(0)) || PorAnalizarC.contains(NextRegla.Simbolos.get(0)) || NextRegla.Simbolos.get(0).Terminal))
                        PorAnalizarC.add(NextRegla.Simbolos.get(0));
                    
                }
            }
        }
        
        return ConjuntoCerr;
    }

    Set<itemLR0> Cerradura(itemLR0 a){
        Set<itemLR0> ConjuntoCerr = new HashSet<>();
        if(a == null)
            return ConjuntoCerr;

        ConjuntoCerr.add(new itemLR0(a.NumRegla, a.PosPunto));

        Simbolo Simb = a.GetSimbolobyGram(Gram);
        if(Simb == null || Simb.Terminal)
            return ConjuntoCerr;
        
        Queue<Simbolo> PorAnalizarC = new LinkedList<>();
        Set<Simbolo> Analizados = new HashSet<>();
        Simbolo SimboloAct;
        LadoIzq NextRegla;

        PorAnalizarC.add(Simb);

        while(!PorAnalizarC.isEmpty()){
            SimboloAct = PorAnalizarC.poll();
            Analizados.add(SimboloAct);
            
            for(int i=0; i<Gram.Reglas.size(); i++){
                NextRegla = Gram.Reglas.get(i);
                
                if(NextRegla.SimboloIzq.equals(SimboloAct)){
                    ConjuntoCerr.add(new itemLR0(i,0));
                    
                    if(!NextRegla.Simbolos.isEmpty()){
                        Simbolo primerSimb = NextRegla.Simbolos.get(0);
                        if(!(Analizados.contains(primerSimb) || PorAnalizarC.contains(primerSimb) || primerSimb.Terminal))
                            PorAnalizarC.add(primerSimb);
                    }
                }
            }
        }

        return ConjuntoCerr;
    }
    
    Set<itemLR0> Mover(Set<itemLR0> A, Simbolo Simbolo_a){
        Set<itemLR0> ConjuntoMov = new HashSet<>();
        
        if(A == null || A.isEmpty())
            return ConjuntoMov;
        
        ConjuntoMov = A.stream()
            .filter(item -> item.GetSimbolobyGram(Gram) != null && item.GetSimbolobyGram(Gram).equals(Simbolo_a))
            .map(item -> new itemLR0(item.NumRegla, item.PosPunto + 1))
            .collect(Collectors.toSet());

        return ConjuntoMov;
    }

    public String[][] getVt(){
        String[][] tablaVT = new String[2][VT[0].length];
        for(Simbolo Simb : Gram.SimbolosTerminales){
            int idx = ObtenerColumna(Simb);
            tablaVT[0][idx] = Simb.Nombre;
            tablaVT[1][idx] = String.valueOf(VT[0][idx]);
        }

        return tablaVT;
    }

    public String[] getVNT(){
        String[] tablaVnT = new String[VNT.length];
        for(Simbolo Simb : Gram.SimbolosNoTerminales){
            int idx = ObtenerIndice(Simb);
            tablaVnT[idx] = Simb.Nombre;
        }
        return tablaVnT;
    }

    public String[] getCabeceraTabla(){
        String[] Cabecera = new String[V.size()+1];
        String[] tablaVt = getVt()[0];
        String[] tablaVnt = getVNT();
        
        Cabecera[0] = "";

        System.arraycopy(tablaVt, 0, Cabecera, 1, tablaVt.length);
        System.arraycopy(tablaVnt, 0, Cabecera, tablaVt.length+1, tablaVnt.length);

        return Cabecera;
    }

    public String[][] getTablaLR(){
        String[][] TablaLRsalida = new String[TablaLR.length][TablaLR[0].length+1];

        for (int i = 0; i < TablaLR.length; i++) {
            TablaLRsalida[i][0] = "S" + String.valueOf(i);

            for (int j = 0; j < TablaLR[0].length; j++) {
                String valorRegla = TablaLR[i][j];
            
                if (valorRegla.equals("-1")) {
                    TablaLRsalida[i][j + 1] = "";
                } else {
                    TablaLRsalida[i][j + 1] = valorRegla;
                }   
            }
        }

        return TablaLRsalida;
    }

    public void setSigma(String Sigma){this.Lex.SetSigma(Sigma);}

    public void setGramatica(Gramatica Grama){this.Gram = Grama;}

    public void setLexico(AnalisisLexico LEx){this.Lex = LEx;}

    public void setLexico(String ruta, String sig){this.Lex = new AnalisisLexico(sig, ruta);}
    
}

class Conj_Sj {
    int j;
    Set<itemLR0> ConjuntoSJ;
    Set<Transicion> transiciones; 

    public Conj_Sj() {
        this.j = -1;
        this.ConjuntoSJ = null;
        this.transiciones = new HashSet<>();
    }
    
    public Conj_Sj(int j, Set<itemLR0> Conj_Sj) {
        this.j = j;
        this.ConjuntoSJ = Conj_Sj;
        this.transiciones = new HashSet<>();
    }

    public void agregarTransicion(Simbolo simb, Conj_Sj destino) {
        this.transiciones.add(new Transicion(simb, destino));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Conj_Sj Conj = (Conj_Sj) o;
        return Objects.equals(this.ConjuntoSJ, Conj.ConjuntoSJ);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.ConjuntoSJ);
    }

    @Override
    public String toString(){
        return "[S." + j + "\n" + ConjuntoSJ + "\n" + transiciones + "\n" + ConjuntoSJ.hashCode() +"]" ;
    }
}

// Estructura para representar las aristas del grafo
class Transicion {
    Simbolo simbolo;
    Conj_Sj estadoDestino;

    public Transicion(Simbolo simbolo, Conj_Sj estadoDestino) {
        this.simbolo = simbolo;
        this.estadoDestino = estadoDestino;
    }
}

class itemLR0{
    int NumRegla;
    int PosPunto;
    
    public itemLR0(){
        NumRegla = -1;
        PosPunto = -1;
    }
    
    public itemLR0(int NumRegla, int PosPunto){
        this.NumRegla = NumRegla;
        this.PosPunto = PosPunto;
    }
    
    Simbolo GetSimbolobyGram(Gramatica Gramatica){
    if (this.PosPunto >= 0 && this.PosPunto < Gramatica.Reglas.get(this.NumRegla).Simbolos.size()) {
        return Gramatica.Reglas.get(this.NumRegla).Simbolos.get(this.PosPunto);
    }
    return null; 
}
    
    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;
        if (this == o) return true;
        itemLR0 item = (itemLR0) o;
        return (item.NumRegla == this.NumRegla && item.PosPunto == this.PosPunto);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(NumRegla,PosPunto);
    }
    
    @Override
    public String toString(){
        return "Item: " + this.NumRegla +","+this.PosPunto+" Hash: "+this.hashCode();
        //return String.format("Item: %s ,%s :: Hash:: %s + :: Simbolo: ", this.NumRegla , this.PosPunto, this.hashCode());
    }
    
    
}

