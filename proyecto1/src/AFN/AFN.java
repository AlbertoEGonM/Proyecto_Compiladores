package AFN;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Stack;

public class AFN {
    
    public static HashSet<AFN> ColeccAFNs = new HashSet<>(); // Lista de AFN's
    public static int ContadorAFNs = 0; // Contador de AFN's creados

    public Estado EdoInicial;
    public HashSet<Character> Alfabeto;
    public HashSet<Estado> EstadosAFN;
    public HashSet<Estado> EstadosAcept;

    public int IdAFN;
    public String E_Regular;

    // Constructor Basico
    public AFN(){
        Alfabeto = new HashSet<>();
        Alfabeto.clear();
        EstadosAFN = new HashSet<>();
        EstadosAFN.clear();
        EstadosAcept = new HashSet<>();
        EstadosAcept.clear();
        IdAFN = ContadorAFNs++;
    }

    // Constructor con un caracter
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public AFN(char c){
        Alfabeto = new HashSet<>();
        Alfabeto.clear();
        EstadosAFN = new HashSet<>();
        EstadosAFN.clear();
        EstadosAcept = new HashSet<>();
        EstadosAcept.clear();
        IdAFN = ContadorAFNs++;

        this.CrearAFNBasico(c);
    }

    // Constructor con un rango de caracteres
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public AFN(char a, char b){
        Alfabeto = new HashSet<>();
        Alfabeto.clear();
        EstadosAFN = new HashSet<>();
        EstadosAFN.clear();
        EstadosAcept = new HashSet<>();
        EstadosAcept.clear();
        IdAFN = ContadorAFNs++;

        this.CrearAFNBasico(a, b);
    }


    // AFN Basico de la forma ->(e1)-c->(e2)->
    public void CrearAFNBasico(char c){
        Estado e1 = new Estado(), e2 = new Estado();

        e1.Transiciones.add(new Transicion(c,e2)); // e1->e2 con el simbolo c
        e2.EdoAcept = true;

        this.EdoInicial = e1; 
        this.Alfabeto.add(c); // Sigma = {c}
        this.EstadosAFN.add(e1);
        this.EstadosAFN.add(e2);
        this.EstadosAcept.add(e2);
        this.E_Regular = ""+c;

        ColeccAFNs.add(this);
    }

    // AFN Basico de la forma ->(e1)-[c1,c2]->(e2)->
    public void CrearAFNBasico(char c1, char c2){
        Estado e1 = new Estado(), e2 = new Estado();

        e1.Transiciones.add(new Transicion(c1,c2,e2)); // e1->e2 con el simbolo [c1,c2]
        e2.EdoAcept = true;

        this.EdoInicial = e1;
        this.EstadosAFN.add(e1);
        this.EstadosAFN.add(e2);
        this.EstadosAcept.add(e2);
        this.E_Regular = "["+c1+"-"+c2+"]";

        for(char c = c1 ; c <= c2; c++) //Sigma = {c1, c1+1, ..., c2}
            this.Alfabeto.add(c);

        ColeccAFNs.add(this);
    }
    
    /*  Union de AFN's   ->(e1)->AFN1\>
        Es de la e.r. (a|b)    \>AFN2->(e2)-> */
    public void UnirAFN(AFN F2){
        Estado e1 = new Estado(), e2 = new Estado();

        e2.EdoAcept = true; // El estado Final de ambos AFN's ->e2-> 

        e1.Transiciones.add(new Transicion(SimbESP.Epsilon,this.EdoInicial)); // e1->AFN1
        e1.Transiciones.add(new Transicion(SimbESP.Epsilon,F2.EdoInicial)); // e1->AFN2

        for(Estado e : this.EstadosAcept){
            e.Transiciones.add(new Transicion(SimbESP.Epsilon,e2)); // AFN1->e2
            e.EdoAcept = false; //Este ya no es el fin del AFN1
        }
        for(Estado e : F2.EstadosAcept){
            e.Transiciones.add(new Transicion(SimbESP.Epsilon,e2)); // AFN2->e2
            e.EdoAcept = false; //Este ya no es el fin del AFN2
        }

        this.E_Regular = "("+this.E_Regular+"|"+F2.E_Regular+")"; // EJ. ([a,z]|[0,9])
        this.EdoInicial = e1;
        this.Alfabeto.addAll(F2.Alfabeto);
        this.EstadosAFN.add(e1);
        this.EstadosAFN.add(e2);
        this.EstadosAFN.addAll(F2.EstadosAFN);
        this.EstadosAcept.clear();
        this.EstadosAcept.add(e2);

        ColeccAFNs.remove(F2);
        /* Al ser que el AFN1 y 2 se fucionan y toman el lugar del AFN1,
        AFN2 ya no tiene lugar en la lista. */
    }


    /*  Union de varios AFN's en un SuperUnion */
    public static void UnirAFN(Stack<AFN> F){
        if(F == null || F.isEmpty())
            return;

        AFN f1 = F.pop();

        Estado e1 = new Estado();

        e1.Transiciones.add(new Transicion(SimbESP.Epsilon,f1.EdoInicial));
		f1.E_Regular = '('+f1.E_Regular+'|';
		
		for(AFN f: F){
			e1.Transiciones.add(new Transicion(SimbESP.Epsilon,f.EdoInicial));
			f1.Alfabeto.addAll(f.Alfabeto);
			f1.EstadosAFN.addAll(f.EstadosAFN);
			f1.EstadosAcept.addAll(f.EstadosAcept);
			f1.E_Regular = f1.E_Regular + f.E_Regular +"|";
			ColeccAFNs.remove(f);
		}
		/* Remove last carácter in E_Regular and add a ) to close the expresion*/
		f1.E_Regular = f1.E_Regular.substring(0, f1.E_Regular.length() - 1) + ')'+"";
        f1.EdoInicial = e1;
        f1.EstadosAFN.add(e1);
        ColeccAFNs.add(f1);
    }


    /*  Concatenación de AFN's ->AFN1->AFN2->
        Es de la e.r. (a°b)    */
    public void ConcatenarAFN(AFN F2){

        // AFN1->AFN2, Une todos los estados de aceptación (finales) de AFN1 con el inicio de AFN2 mediante sus transiciones
        for(Estado e : this.EstadosAcept){
            e.Transiciones.addAll(F2.EdoInicial.Transiciones); 
            e.EdoAcept = false;
        }

        F2.EstadosAFN.remove(F2.EdoInicial); //Eliminamos el estado inicial de F2 (pierde importancia en el AFN final)
        // Confio en el Recolector de Java

        this.E_Regular = "("+this.E_Regular+"°"+F2.E_Regular+")"; // EJ. ([a,z] ° 5)
        this.Alfabeto.addAll(F2.Alfabeto); // Unimos todo el Alfabeto de AFN1 y 2
        this.EstadosAFN.addAll(F2.EstadosAFN); // Contamos todos los Estados del AFN1 y 2

        this.EstadosAcept.clear(); // AFN1 No tiene estados de Aceptación
        this.EstadosAcept.addAll(F2.EstadosAcept); // La concatenación del AFN1 y 2 pasan a ser los del 2

        ColeccAFNs.remove(F2);
        /* Al ser que el AFN1 y 2 se fucionan y toman el lugar del AFN1,
        AFN2 ya no tiene lugar en la lista. */
    }

    /*  Cerradura Positiva: ->(e1)->AFN->(e2)->
        Es de la e.r (a)^+          \</         */
    public void CerrPositiva(){
        Estado e1 = new Estado(), e2 = new Estado();
        
        e1.Transiciones.add(new Transicion(SimbESP.Epsilon,this.EdoInicial)); // Se une (e1)->AFN
        e2.EdoAcept = true; // Se define el nuevo estado de aceptación

        for(Estado e : this.EstadosAcept){
            e.Transiciones.add(new Transicion(SimbESP.Epsilon, this.EdoInicial)); // AFN->AFN (La recursión)
            e.Transiciones.add(new Transicion(SimbESP.Epsilon, e2)); // AFN->e2 (La salida)
            e.EdoAcept = false; // Los estados de aceptación del AFN1 ya no son validos, ahora solo e2 lo es.
        }

        this.E_Regular = this.E_Regular + "+"; // Ej. a^+ o [a,z]^+
        this.EdoInicial = e1;
        this.EstadosAFN.add(e1);
        this.EstadosAFN.add(e2);
        this.EstadosAcept.clear();
        this.EstadosAcept.add(e2);
    }

    /*  Cerradura Kleene ->(e1)->AFN->(e2)->
        Es de la e.r.         \  \</   /
        (a)^x                  \-->>--/ */
    public void CerrKleene(){
        Estado e1 = new Estado(), e2 = new Estado();
        
        // Se une el nuevo estado de inicio e1 al AFN y al estado final e2 para formar la cerradura.
        e1.Transiciones.add(new Transicion(SimbESP.Epsilon, this.EdoInicial)); // (e1)->AFN
        e1.Transiciones.add(new Transicion(SimbESP.Epsilon, e2)); // (e1)->(e2)

        // El Nuevo estado de aceptación
        e2.EdoAcept = true;

        // Se realiza el proceso de union del AFN->(e2) y AFN->AFN (Recursión)
        for( Estado e : this.EstadosAcept ){
            e.Transiciones.add(new Transicion(SimbESP.Epsilon, e2)); // AFN->(e2)
			e.Transiciones.add(new Transicion(SimbESP.Epsilon,this.EdoInicial)); // AFN->AFN (Recursión)
            e.EdoAcept = false;
        }
        
        this.E_Regular = this.E_Regular + "*"; // Ej. a^x o ([a,z] | 0)^x
        this.EdoInicial = e1;
        this.EstadosAFN.add(e1);
        this.EstadosAFN.add(e2);

        this.EstadosAcept.clear();
        this.EstadosAcept.add(e2);
    }

    /*  Cerradura Opcional ->(e1)->AFN->(e2)->
        De la e.r. a^?          \--->---/       */
    public void CerrOpcional(){
        Estado e1 = new Estado(), e2 = new Estado();
        
        // Se une el nuevo estado de inicio e1 al AFN y al estado final e2 para formar la cerradura.
        e1.Transiciones.add(new Transicion(SimbESP.Epsilon, this.EdoInicial)); // (e1)->AFN
        e1.Transiciones.add(new Transicion(SimbESP.Epsilon, e2)); // (e1)->(e2)

        // El Nuevo estado de aceptación
        e2.EdoAcept = true;

        // Se realiza el proceso de union del AFN->(e2)
        for( Estado e : this.EstadosAcept ){
            e.Transiciones.add(new Transicion(SimbESP.Epsilon, e2)); // AFN->(e2)
            e.EdoAcept = false;
        }
        
        this.E_Regular = this.E_Regular + "?"; // Ej. a^x o ([a,z] | 0)^?
        this.EdoInicial = e1;
        this.EstadosAFN.add(e1);
        this.EstadosAFN.add(e2);
        this.EstadosAcept.clear();
        this.EstadosAcept.add(e2);
    }

    /*  ---- Metodos para analisis y Conversión del AFN a AFD ----   */

    /*  Cerradura Epsilon: Dado un estado e, se obtiene el conjunto de estados alcanzables mediante transiciones epsilon */
    public HashSet<Estado> CerraduraEpsilon(Estado e){
        HashSet<Estado> CE = new HashSet<>();
        Queue<Estado> Q = new ArrayDeque<>();
        CE.clear(); 
        Q.clear();

        if(e == null || e.Transiciones == null || e.Transiciones.isEmpty()) // En caso de ser un estado final o nulo
            return CE;

        Q.add(e);
        CE.add(e);
        while(!Q.isEmpty()){
            Estado e1 = Q.poll();
            if(e1.Transiciones == null || e1.Transiciones.isEmpty()) // En caso de ser un estado final, no deberia tener transiciones, por lo que se omite el proceso.
                continue;
            for(Transicion t : e1.Transiciones){
                if(t.Simbolo1 == SimbESP.Epsilon && !CE.contains(t.EdoFinal)){
                    CE.add(t.EdoFinal);
                    Q.add(t.EdoFinal);
                }
            }
        }
        return CE;
    }

    /*  Cerradura Epsilon: Dado un conjunto de estados R, se obtiene el conjunto de estados alcanzables mediante transiciones epsilon */
    public HashSet<Estado> CerraduraEpsilon(HashSet<Estado> R){
        HashSet<Estado> CE = new HashSet<>();
        Queue<Estado> Q = new ArrayDeque<>();
        CE.clear(); 
        Q.clear();

        if(R == null || R.isEmpty())
            return CE;
        
        for(Estado e : R){
            if(e == null)
                continue;
            Q.add(e);
            CE.add(e);
        }

        while(!Q.isEmpty()){
            Estado e1 = Q.poll();
            if(e1.Transiciones == null|| e1.Transiciones.isEmpty()) // En caso de ser un estado final, no deberia tener transiciones, por lo que se omite el proceso.
                continue;
            for(Transicion t : e1.Transiciones){
                if(t.Simbolo1 == SimbESP.Epsilon && !CE.contains(t.EdoFinal)){
                    CE.add(t.EdoFinal);
                    Q.add(t.EdoFinal);
                }
            }
        }
        return CE;
    }

    /*  Mover: Dado un estado e y un caracter c, se obtiene el conjunto de estados alcanzables mediante transiciones con el caracter c */
    public HashSet<Estado> Mover( Estado e , char c){
        HashSet<Estado> R = new HashSet<>();
        R.clear();

        if(e == null || e.Transiciones == null) // En caso de ser un estado final o nulo
            return R;

        R.addAll(e.TieneTransicionesA(c));

        return R;
    }

    /*  Mover: Dado un conjunto de estados A y un caracter c, se obtiene el conjunto de estados alcanzables mediante
        transiciones con el caracter c */
    public HashSet<Estado> Mover(HashSet<Estado> A, char c){
        HashSet<Estado> R = new HashSet<>();
        R.clear();

        if(A == null || A.isEmpty() )
            return R;

        for(Estado e : A){
            if(e == null || e.Transiciones == null)
                continue;
            R.addAll(e.TieneTransicionesA(c));
        }
        return R;
    }

    /*  IrA: Dado un conjunto de estados C y un caracter c, se obtiene el conjunto de estados alcanzables mediante
        transiciones con el caracter c y cerradura epsilon */
    public HashSet<Estado> IrA(HashSet<Estado> C, char c){
		return CerraduraEpsilon(Mover(C,c));
    }

    /*public void ImprimirAFN(List<Arista> aristas,List<Nodo> nodos){
        Stack<Estado> Visitados = new Stack<>(); // Stack
        System.out.println("AFN " + this.IdAFN + ": " + this.E_Regular);
        System.out.println("Alfabeto: " + this.Alfabeto);
        /*System.out.println("Estados de Aceptacion: ");
        this.EstadosAcept.forEach(e -> System.out.println("Estado: " + e.IdEdo + " Aceptacion: " + e.EdoAcept + " Token: " + e.Token));
*//*
        System.out.print("Estados: { ");
            this.EstadosAFN.forEach(e -> System.out.print((e.IdEdo)+","));
        System.out.println(" }");
        System.out.println("Numero de Estados: " + this.EstadosAFN.size());

        System.out.println("Estado Inicial: " + this.EdoInicial.IdEdo);
        Despliege(this.EdoInicial, Visitados);

        System.out.print("Conjunto de visitados: {");

        Visitados.forEach(e -> System.out.print(e.IdEdo+","));
        System.out.println("}");
        System.out.println("Numero de visitados: " + Visitados.size());
        System.out.println("Numero de Estados del AFN: " + this.EstadosAFN.size());
        // System.out.println("Conjunto de visitados == Conjunto de Estados del AFN: " + this.EstadosAFN.equals(Visitados));

        System.out.println("--------------------------------------------------");
        
    }

    public void Despliege(Estado e, Stack<Estado> Visitados, List<Arista> aristas,List<Nodo> nodos){
        if(Visitados.contains(e))
            return;

        Visitados.push(e);
        Nodo nod = new Nodo(""+ e.IdEdo , 10*e.IdEdo , 10*e.IdEdo);
        nodos.add(nod);

        if(e.Transiciones == null || e.Transiciones.isEmpty()){ // En caso de ser un estado final
            
            return;
        }

        for(Transicion t : e.Transiciones){
            System.out.println("Estado: " + e.IdEdo + " -> " + t.EdoFinal.IdEdo + " con simbolo: " + (t.Simbolo1 == SimbESP.Epsilon ? "Epsilon" : t.Simbolo1) );
            aristas.add(new Arista(nod, nod, E_Regular));

            Despliege(t.EdoFinal, Visitados , aristas, nodos);
        }
    }*/

    // Metodos para obtener informacion del AFN

    public String[] getInfoAFN(){
        String[] info = new String[7];
        info[0] = this.IdAFN+ ""; // ID del AFN 
        info[1] = "" + this.E_Regular; // E. Regular del AFN
        info[2] = "" + this.Alfabeto; // Alfabeto del AFN
        info[3] = "{ "; // Estados del AFN
            for(Estado e : this.EstadosAFN){
                info[3] += (e.IdEdo)+",";
            }
        info[3] = info[3].substring(0, info[3].length() - 1) + " }"; // Elimina la ultima coma
        info[4] = "" + this.EdoInicial.IdEdo; // Estado Inicial del AFN
        info[5] = "{ "; // Estados de aceptación
        info[6] = "{ "; // Token's
        for(Estado e : this.EstadosAcept){
            info[5] += e.IdEdo + ",";
            info[6] += e.Token + ",";
        }
        info[5] = info[5].substring(0, info[5].length() - 1) + " }";
        info[6] = info[6].substring(0, info[6].length() - 1) + " }";
        return info;
    }

    public String getE_Regular(){
        return this.E_Regular;
    }

    public static String[] getAllERegular(){
        String [] ERs = new String[ColeccAFNs.size()];
        int i = 0;
        for(AFN afn : ColeccAFNs){
            ERs[i] = afn.E_Regular;
            i++;
        }
        return ERs;
    }

    public static AFN getAFNByER(String ER){
        for(AFN afn : ColeccAFNs){
            if(afn.E_Regular.equals(ER))
                return afn;
        }
        return null;
    }

    public static AFN getAFNById(int Id){
        for(AFN afn : ColeccAFNs){
            if(afn.IdAFN == Id)
                return afn;
        }
        return null;
    }


    public static String[][] getAllInfoAFN(){
        String[][] info = new String[ColeccAFNs.size()][7];
        int i = 0;
        for(AFN afn : ColeccAFNs){
            System.arraycopy(afn.getInfoAFN(), 0, info[i], 0, 7);
            i++;
        }
        return info;
    }


    public void SetToken(int Token){
        for (Estado e : this.EstadosAcept) {
            e.Token = Token;
        }
    }





    // Test
    public static void main(String[] args) {
        /*
        System.out.println("AFN's creados: " + AFN.ContadorAFNs);
        AFN AFN1 = new AFN('0');
        AFN AFN2 = new AFN('b', 'd');
        AFN AFN3 = new AFN();
        AFN3.CrearAFNBasico('0', '9');
        System.out.println("AFN's creados: " + AFN.ContadorAFNs);
        System.out.println("AFN's en listados: " + AFN.ColeccAFNs.size());
        for(AFN afn : AFN.ColeccAFNs){
            System.out.println("AFN " + afn.IdAFN + ": " + afn.E_Regular);
        }
        AFN1.UnirAFN(AFN2);
        System.err.println("||AFN1| = " + AFN1.EstadosAFN.size());
        System.out.println("AFN's creados: " + AFN.ContadorAFNs);
        System.out.println("AFN's en listados: " + AFN.ColeccAFNs.size());
        for(AFN afn : AFN.ColeccAFNs){
            System.out.println("AFN " + afn.IdAFN + ": " + afn.E_Regular);
        }
        AFN1.ConcatenarAFN(AFN3);
        System.err.println("||AFN1| = " + AFN1.EstadosAFN.size());
        System.out.println("AFN's creados: " + AFN.ContadorAFNs);
        System.out.println("AFN's en listados: " + AFN.ColeccAFNs.size());
        for(AFN afn : AFN.ColeccAFNs){
            System.out.println("AFN " + afn.IdAFN + ": " + afn.E_Regular);
        }
        System.err.println("Alfabeto de AFN1: " + AFN1.Alfabeto );
        AFN1.CerrKleene();
        System.err.println("||AFN1| = " + AFN1.EstadosAFN.size());
        System.out.println("AFN's creados: " + AFN.ContadorAFNs);
        System.out.println("AFN's en listados: " + AFN.ColeccAFNs.size());
        for(AFN afn : AFN.ColeccAFNs){
            System.out.println("AFN " + afn.IdAFN + ": " + afn.E_Regular);
        }

        AFN AFN4 = new AFN('b', 'd');
        AFN AFN5 = new AFN('0', '9');
        AFN4.UnirAFN(AFN5);
            System.err.println("||AFN4| = " + AFN4.EstadosAFN.size());
            System.out.println("AFN's creados: " + AFN.ContadorAFNs);
            System.out.println("AFN's en listados: " + AFN.ColeccAFNs.size());
            for(AFN afn : AFN.ColeccAFNs){
                System.out.println("AFN " + afn.IdAFN + ": " + afn.E_Regular);
            }

        System.out.println("Is AFN1 equal to AFN4? " + AFN1.equals(AFN4));
        System.out.println("Is AFN1 Conjunto Est equal to AFN4 Conj Estad? " + AFN1.EstadosAFN.equals(AFN4.EstadosAFN));
        System.out.println("AFN1: " + AFN1.Alfabeto.toString());
        System.out.println("AFN4: " + AFN4.Alfabeto.toString());
        
        String s = new String();
        String s2 = new String();

        AFN1.EstadosAFN.forEach(e -> System.out.println((e.IdEdo)+""));
        System.out.println("AFN1 Estados: " + AFN1.EstadosAFN.size());
        AFN4.EstadosAFN.forEach(e -> System.out.println((e.IdEdo)+""));

        for(Estado A : AFN1.EstadosAFN){
            s += (A.IdEdo)+",";
        }
        for(Estado A : AFN4.EstadosAFN){
            s2 += (A.IdEdo)+",";
        }
        System.out.println("AFN1 Estados en string: " + s);
        System.out.println("AFN2 Estados en string: " + s2);
        System.out.println("Is AFN1 Estados equal to AFN2 Estados? " + s.equals(s2));

        System.out.println("AFN1 Estado Inicial: " + AFN1.EdoInicial.IdEdo);
        AFN1.EdoInicial.Transiciones.forEach(e -> System.out.println("AFN1 Estado Inicial: " + e.EdoFinal.IdEdo + " con simbolo: " + (e.Simbolo1 == SimbESP.Epsilon ? "Epsilon" : "__")) );

        System.out.println(".()");

        System.out.println("");
            */
        //AFN AFN6 = new AFN('a');
        //AFN AFN7 = new AFN('b');

        //AFN6.ImprimirAFN();
        //AFN7.ImprimirAFN();

        //AFN6.UnirAFN(AFN7);

        //AFN6.ImprimirAFN();

        //AFN6.CerrKleene();

        //AFN6.ImprimirAFN();

        //System.out.println("AFN8: ");

        //AFN AFN8 = new AFN('c');
        //AFN8.ImprimirAFN();
        //AFN8.CerrPositiva();
        //AFN8.ImprimirAFN();

        //AFN6.ConcatenarAFN(AFN8);

        //System.out.println("AFN6 Concatenado con AFN8: \n");

        //AFN6.ImprimirAFN();

        //AFN6.EstadosAcept.forEach(e -> { e.Token = 20; System.out.println("Estado de Aceptacion: " + e.IdEdo + " Token: " + e.Token); });
/*
        for(AFN afn : AFN.ColeccAFNs){
            System.out.println("AFN " + afn.IdAFN + ": " + afn.E_Regular);
        }*/

        //System.out.println("----------------------------------------------");
        //System.out.println("----------------------------------------------");
        //System.out.println("----------------------------------------------");

        //String[][] info = AFN6.getAllInfoAFN();
        //for(String[] i : info){
            //System.out.println("AFN Info:");
          //  for(String s : i){
            //    System.out.println(s);
            //}
            //System.out.println("-------------");
        //}

        //        AFD AFD1 = new AFD();

  //      AFD1.ConvertirAFD(AFN6);

        
    }

}

