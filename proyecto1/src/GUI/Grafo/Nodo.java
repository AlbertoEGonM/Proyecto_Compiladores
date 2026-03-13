package GUI.Grafo;

public class Nodo {
    public int x, y;
    public String etiqueta;
    static final int RADIO = 20;

    public Nodo(String etiqueta, int x, int y) {
        this.etiqueta = etiqueta;
        this.x = x;
        this.y = y;
    }

    public boolean contienePunto(int px, int py) {
        return Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2)) <= RADIO;
    }
}

