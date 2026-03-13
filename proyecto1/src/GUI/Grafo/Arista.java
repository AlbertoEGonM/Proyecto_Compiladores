package GUI.Grafo;

public class Arista {
    public Nodo origen, destino;
    public String Simbolo;
    public Arista(Nodo origen, Nodo destino, String Simbolo) {
        this.origen = origen;
        this.destino = destino;
        this.Simbolo = Simbolo;
    }
}
