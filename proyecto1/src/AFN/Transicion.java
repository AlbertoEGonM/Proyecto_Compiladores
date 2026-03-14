package AFN;

public class Transicion{
    public char Simbolo1;
    public char Simbolo2;
    public Estado EdoFinal;

    public Transicion(){
        Simbolo1 = (char)0;
        Simbolo2 = (char)0;
        EdoFinal = null;
    }

    public Transicion(char asign, Estado EdoFinal){
        this.Simbolo1 = asign;
        this.Simbolo2 = asign;
        this.EdoFinal = EdoFinal;
    }
    
    public Transicion(char asignSup, char asignInf, Estado EdoFinal){
        this.Simbolo1 = asignSup;
        this.Simbolo2 = asignInf;
        this.EdoFinal = EdoFinal;
    }

    

}