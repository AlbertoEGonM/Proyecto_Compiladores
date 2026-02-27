package AFN;

public class Transicion{
    protected char Simbolo1;
    protected char Simbolo2;
    protected Estado EdoFinal;

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