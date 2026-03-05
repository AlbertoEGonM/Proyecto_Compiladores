package AFD;
import AFN.AFN;
import AFN.Estado;
import java.util.HashSet;

public class Sj {
    protected HashSet<Estado> ConjuntoEdo;
    protected int[] Transiciones ;
    protected int j;
    protected Boolean EsFinal;
    protected int Token;

    public Sj(){
        ConjuntoEdo = new HashSet<>();
        ConjuntoEdo.clear();
        j = 0;
        EsFinal = false;
        Transiciones = new int[257];
        for(int i = 0; i < 257; i++)
            Transiciones[i] = -1;
        
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Sj(int j, HashSet<Estado> ConjuntoEdo,AFN f){
        this.j = j;
        this.ConjuntoEdo = ConjuntoEdo;
        Transiciones = new int[257];
        for(int i = 0; i < 257; i++)
            Transiciones[i] = -1;
        this.EstablecerEsFinal(f);
    }

    public void EstablecerEsFinal(AFN f){
        if(ConjuntoEdo == null || ConjuntoEdo.isEmpty()){
            EsFinal = false;
            return;
        }
        
        HashSet<Estado> ConjuntoEdoTemp = new HashSet<>();
        ConjuntoEdoTemp.addAll(ConjuntoEdo);
        
        ConjuntoEdoTemp.retainAll(f.EstadosAcept);

        if(ConjuntoEdoTemp.isEmpty()){
            EsFinal = false;
            return;
        }
        if(ConjuntoEdoTemp.size() == 1){
            EsFinal = true;
            this.Token = ConjuntoEdoTemp.iterator().next().Token;
            this.Transiciones[256] = this.Token;
            return;
        }
        if(ConjuntoEdoTemp.size() > 1){
            EsFinal = true;
            this.Token = ConjuntoEdoTemp.stream().mapToInt(e -> e.Token).min().orElse(-1);
            this.Transiciones[256] = this.Token;
        }
    }

    public void AgregarTransicion(Sj SjTemp,char a){
        this.Transiciones[a] = SjTemp.j;
    }
}
