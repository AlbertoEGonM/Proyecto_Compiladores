package sintactico;

import AFN.SimbESP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import lexico.AnalisisLexico;

public class LL1 {
    AnalisisLexico Lex;
    Gramatica Gram;
    int[][] TablaLL;
    int[][] VT;
    int[] VNT;


    public LL1(){
        Gram = null;
        Lex = null;
        TablaLL = null;
        VT = null;
        VNT = null;
    }

    public LL1(Gramatica Gram, AnalisisLexico Lex){
        this.Gram = Gram;
        this.Lex = Lex;
        TablaLL = null;
        VT = null;
        VNT = null;
    }

    public LL1(String sigma, String ruta, Gramatica Gram){
        this.Gram = Gram;
        this.Lex = new AnalisisLexico(sigma,ruta);
        TablaLL = null;
        VT = null;
        VNT = null;
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

    private int ObtenerTokenDeSimbolo(Simbolo Simb) {
        int idx = Arrays.binarySearch(VT[2], Simb.hashCode());
        if (idx >= 0) 
            return VT[3][idx];
        return -1;
    }

    private String mapearPilaAString(Stack<Simbolo> pila) {
        StringBuilder sb = new StringBuilder();
        for (Simbolo s : pila) {
            sb.append(s.Nombre).append(" ");
        }
        return sb.toString().trim();
    }

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

    public void init_Table(){
        TablaLL = new int[VNT.length][VT[0].length];

        for (int i = 0; i < VNT.length; i++) 
            Arrays.fill(TablaLL[i], -1);
        
        Set<Simbolo> Aux;
        int columna, renglon;
        LadoIzq Regla;

        for (int idx = 0; idx < Gram.Reglas.size(); idx++) {
            Regla = Gram.Reglas.get(idx);
            Aux = Gram.First(Regla.Simbolos);
            
            if(Aux.remove(Simbolo.SimbEPS))
                Aux.addAll(Gram.Follow(Regla.SimboloIzq));

            renglon = ObtenerIndice(Regla.SimboloIzq);
            
            for(Simbolo Simb : Aux){
                columna = ObtenerColumna(Simb);
                TablaLL[renglon][columna] = idx;
            }
        }
    }

    public String[][] AnalizarYRegistrar() {
        List<FilaProcesoLL1> historial = new ArrayList<>();
        String[][] tablaHistorial = new String[0][0];

        if (Lex == null || Gram == null || TablaLL == null) {
            System.err.println("Error: El analizador sintáctico no está inicializado.");
            return tablaHistorial;
        }

        // 1. Inicializar Pila Sintáctica
        Stack<Simbolo> pilaSintactica = new Stack<>();
        pilaSintactica.push(Simbolo.SimboloFinal); // $
        
        if (Gram.Reglas.isEmpty()) return tablaHistorial;
        pilaSintactica.push(Gram.Reglas.get(0).SimboloIzq); // Símbolo inicial

        // 2. Control del token actual
        int tokenActual = Lex.yylex(); 

        // Bucle de reconocimiento
        while (!pilaSintactica.isEmpty()) {
            // --- CAPTURA DE ESTADO (Pila y Entrada) ---
            String estadoPila = mapearPilaAString(pilaSintactica);
            String entradaRestante = Lex.CadenaAnalizar(); 
            if (entradaRestante.isEmpty()) {
                entradaRestante = "$";
            }
            
            Simbolo X = pilaSintactica.peek();

            // CASO 1: Cima es Terminal o Fin de archivo ($)
            if (X.Terminal || X.equals(Simbolo.SimboloFinal)) {
                int tokenDeX = (X.equals(Simbolo.SimboloFinal)) ? SimbESP.Fin : ObtenerTokenDeSimbolo(X);

                if (tokenDeX == tokenActual) {
                    // Registrar acción Match
                    historial.add(new FilaProcesoLL1(estadoPila, entradaRestante, "Match '" + X.Nombre + "'"));
                    
                    pilaSintactica.pop();
                    if (tokenActual != SimbESP.Fin) {
                        tokenActual = Lex.yylex();
                    }
                } else {
                    // Registrar error por discrepancia de tokens
                    historial.add(new FilaProcesoLL1(estadoPila, entradaRestante, 
                        "Error: Se esperaba '" + X.Nombre + "' pero se halló token " + tokenActual));
                    break; 
                }
            } 
            // CASO 2: Cima es un No Terminal
            else {
                int renglon = ObtenerIndice(X);
                int columna = ObtenerColumna(tokenActual);

                if (renglon < 0 || columna < 0) {
                    historial.add(new FilaProcesoLL1(estadoPila, entradaRestante, 
                        "Error: Símbolo o Token fuera de rango (" + X.Nombre + ", Token: " + tokenActual + ")"));
                    break;
                }

                int numeroRegla = TablaLL[renglon][columna];

                if (numeroRegla != -1) {
                    LadoIzq reglaElegida = Gram.Reglas.get(numeroRegla);
                    List<Simbolo> produccion = reglaElegida.Simbolos;

                    // Construir el string de la regla aplicada
                    StringBuilder reglaStr = new StringBuilder(reglaElegida.SimboloIzq.Nombre + " -> ");
                    for(Simbolo s : produccion) reglaStr.append(s.Nombre).append(" ");

                    // Registrar acción de Producción
                    historial.add(new FilaProcesoLL1(estadoPila, entradaRestante, "Aplica regla (" + numeroRegla + "): " + reglaStr.toString().trim()));

                    // Desapilar y expandir en orden inverso
                    pilaSintactica.pop();
                    for (int i = produccion.size() - 1; i >= 0; i--) {
                        Simbolo simboloHijo = produccion.get(i);
                        if (!simboloHijo.equals(Simbolo.SimbEPS)) {
                            pilaSintactica.push(simboloHijo);
                        }
                    }
                } else {
                    // Registrar error por celda vacía en la matriz distributiva
                    historial.add(new FilaProcesoLL1(estadoPila, entradaRestante, 
                        "Error: Sin transición en TablaLL[" + X.Nombre + "][" + tokenActual + "]"));
                    break;
                }
            }
        }

        // Agregar registro de cierre si la evaluación terminó con éxito total
        if (pilaSintactica.isEmpty() && tokenActual == SimbESP.Fin) {
            historial.add(new FilaProcesoLL1("$", "$", "Aceptada (Cadena Válida)"));
        }

        tablaHistorial = new String[historial.size()][0];

        for (int idx=0; idx < historial.size(); idx++) 
            tablaHistorial[idx] = historial.get(idx).getArray();
        

        return tablaHistorial;
    }

}


class FilaProcesoLL1 {
    private final String pila;
    private final String entradaRestante;
    private final String accion;

    public FilaProcesoLL1(String pila, String entradaRestante, String accion) {
        this.pila = pila;
        this.entradaRestante = entradaRestante;
        this.accion = accion;
    }

    // Getters (Útiles para conectar directamente con JTable si lo necesitas)
    public String getPila() { return pila; }
    public String getEntradaRestante() { return entradaRestante; }
    public String getAccion() { return accion; }

    public String[] getArray(){
        String[] FilaArray = new String[3];
        FilaArray[1] = pila;
        FilaArray[2] = entradaRestante;
        FilaArray[3] = accion;
        return FilaArray;
    }

    @Override
    public String toString() {
        // Formato tabulado para impresión limpia en consola
        return String.format("%-30s | %-25s | %s", pila, entradaRestante, accion);
    }
}


