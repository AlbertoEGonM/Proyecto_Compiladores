package GUI.Grafo;

public class Arista {
    protected Nodo origen, destino;
    protected String Simbolo;
    protected Arista(Nodo origen, Nodo destino, String Simbolo) {
        this.origen = origen;
        this.destino = destino;
        this.Simbolo = Simbolo;
    }
}
