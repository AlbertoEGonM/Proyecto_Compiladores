package GUI.Grafo;

public class Nodo {
    protected int x, y;
    protected String etiqueta;
    //static final int RADIO = 20;

    protected Nodo(String etiqueta, int x, int y) {
        this.etiqueta = etiqueta;
        this.x = x;
        this.y = y;
    }

    protected boolean contienePunto(int px, int py) {
        return Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2)) <= RADIO;
    }
}

