package AFN;

import java.util.HashSet;

public class Estado {
    
    protected static int ContadorEdo = 0;
    
    public int IdEdo;
    public HashSet<Transicion> Transiciones;
    public Boolean EdoAcept;
    public int Token;

    public Estado(){
        IdEdo = ContadorEdo++;
        Transiciones = new HashSet<>();

        Transiciones.clear();
        EdoAcept = false;
        Token = -1;
    }   

    public Estado(Transicion t){
        IdEdo = ContadorEdo++;
        Transiciones = new HashSet<>();

        Transiciones.clear();
        Transiciones.add(t);
        EdoAcept = false;
        Token = -1;
    }

    /*  Considerando al estado a analizar, Busca entre el conjunto de transiciones aquellas que tengan un simbolo c,
        y retorna el conjunto C, que contiene los estados finales de dichas transiciones */
    public HashSet<Estado> TieneTransicionesA(char c){
        HashSet <Estado> C = new HashSet<>();
        C.clear();

        if(this.Transiciones == null || this.Transiciones.isEmpty()) // En caso de ser un estado final, no deberia tener transiciones, por lo que se omite el proceso.
            return C;

        for(Transicion t : this.Transiciones){
            if(t == null)
                continue;
            if(t.Simbolo1 <= c && c <= t.Simbolo2)
                C.add(t.EdoFinal);
        }

        return C;
    }
/* s
    public int getId(){
        return this.IdEdo;
    }

    public void setId(int id){
        this.IdEdo = id;
    }

    public boolean getAcept(){
        return this.EdoAcept;
    }

    public void setAcept(boolean Acept){
        this.EdoAcept = Acept;
    }

    public int getToken(){
        return this.Token;
    }

    public void setToken(int Token){
        this.Token = Token;
    }

    public void AgregarTransicion(Transicion t){
        this.Transiciones.add(t);
    }

    public void AgregarTransicion(char Simbolo1, char Simbolo2, Estado EdoFinal){
        Transicion t = new Transicion(Simbolo1, Simbolo2, EdoFinal);
        this.Transiciones.add(t);
    }

    public void AgregarTransicion(char Simbolo, Estado EdoFinal){
        Transicion t = new Transicion(Simbolo, EdoFinal);
        this.Transiciones.add(t);
    }

    public void RemoverTransicion(Transicion t){
        this.Transiciones.remove(t);
    }

    public Boolean EsEdoFinal(){
        return this.EdoAcept;
    }
*/
}
