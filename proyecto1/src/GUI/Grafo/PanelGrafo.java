package GUI;

/*import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GrafoShow extends JFrame {

    public GrafoShow(){
        JFrame Ventana = new JFrame();
        Ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Ventana.setSize(800,500);
        Ventana.setTitle("Grafo");
        Ventana.setLocationRelativeTo(null);
        Ventana.setVisible(true);
        JPanel panel = new JPanel();
        Ventana.add(panel);
        panel.setBounds(20, 20, 600, 300);
        panel.setBackground(Color.BLUE);
        
        JFrame frame = new JFrame("Grafo Interactivo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new PanelGrafo(nodos, aristas));
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    }


    /*public static void main(String[] args) {
        new GrafoShow();
    }
}*/

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/*class Nodo {
    int x, y;
    String etiqueta;
    static final int RADIO = 20;

    public Nodo(String etiqueta, int x, int y) {
        this.etiqueta = etiqueta;
        this.x = x;
        this.y = y;
    }

    public boolean contienePunto(int px, int py) {
        return Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2)) <= RADIO;
    }
}*/

/*class Arista {
    Nodo origen, destino;
    String Simbolo;
    public Arista(Nodo origen, Nodo destino, String Simbolo) {
        this.origen = origen;
        this.destino = destino;
        this.Simbolo = Simbolo;
    }
}*/



public class PanelGrafo extends JPanel {
    private final List<Nodo> nodos;
    private final List<Arista> aristas;
    private Nodo nodoSeleccionado = null;
    private Nodo nodoOrigenArista = null; // Nodo para crear aristas

    public PanelGrafo(List<Nodo> nodos, List<Arista> aristas) {
        this.nodos = nodos;
        this.aristas = aristas;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean clickEnNodo = false;
                for (Nodo n : nodos) {
                    if (n.contienePunto(e.getX(), e.getY())) {
                        clickEnNodo = true;
                        // CLIC IZQUIERDO: Seleccionar para arrastrar
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            nodoSeleccionado = n;
                        } 
                        // CLIC DERECHO: Crear Arista
                        /*else if (SwingUtilities.isRightMouseButton(e)) {
                            if (nodoOrigenArista == null) {
                                nodoOrigenArista = n; // Primer nodo seleccionado
                            } else {
                                // Segundo nodo seleccionado: crear arista
                                aristas.add(new Arista(nodoOrigenArista, n));
                                nodoOrigenArista = null; 
                            }
                        }*/
                        repaint();
                        return;
                    }
                }
                
                // Si haces clic en el vacío, cancelamos la selección de arista
                if (!clickEnNodo) {
                    nodoOrigenArista = null;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                nodoSeleccionado = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (nodoSeleccionado != null) {
                    nodoSeleccionado.x = e.getX();
                    nodoSeleccionado.y = e.getY();
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. DIBUJAR ARISTAS (Siempre primero)
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        for (Arista a : aristas) {
            g2.setColor(Color.BLACK);
            dibujarTransicion(g2, a);
        }

        // 2. DIBUJAR NODOS
        for (Nodo n : nodos) {
            // Si es el origen de una nueva arista, lo pintamos diferente
            if (n == nodoOrigenArista) {
                g2.setColor(Color.ORANGE);
            } else {
                g2.setColor(Color.white);
            }
            
            g2.fillOval(n.x - Nodo.RADIO, n.y - Nodo.RADIO, Nodo.RADIO * 2, Nodo.RADIO * 2);
            
            g2.setColor(Color.BLACK);
            g2.drawOval(n.x - Nodo.RADIO, n.y - Nodo.RADIO, Nodo.RADIO * 2, Nodo.RADIO * 2);

            // Etiqueta
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(n.etiqueta);
            g2.drawString(n.etiqueta, n.x - textWidth / 2, n.y + fm.getAscent() / 4);
        }
    }

    private void dibujarTransicion(Graphics2D g2, Arista a) {
        int x1 = a.origen.x;
        int y1 = a.origen.y;
        int x2 = a.destino.x;
        int y2 = a.destino.y;

        // Calcular el ángulo de la línea
        double angulo = Math.atan2(y2 - y1, x2 - x1);

        // Ajustar el punto de destino para que la flecha toque el BORDE del nodo
        // (Restamos el RADIO del nodo en la dirección del ángulo)
        int destinoX = (int) (x2 - Nodo.RADIO * Math.cos(angulo));
        int destinoY = (int) (y2 - Nodo.RADIO * Math.sin(angulo));

        // Dibujar la línea principal
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(x1, y1, destinoX, destinoY);

        // Dibujar la punta de la flecha
        int largoFlecha = 12;
        double anguloFlecha = Math.toRadians(25);
        int xP1 = (int) (destinoX - largoFlecha * Math.cos(angulo - anguloFlecha));
        int yP1 = (int) (destinoY - largoFlecha * Math.sin(angulo - anguloFlecha));
        int xP2 = (int) (destinoX - largoFlecha * Math.cos(angulo + anguloFlecha));
        int yP2 = (int) (destinoY - largoFlecha * Math.sin(angulo + anguloFlecha));
        
        g2.drawLine(destinoX, destinoY, xP1, yP1);
        g2.drawLine(destinoX, destinoY, xP2, yP2);

        // Dibujar el símbolo (alfabeto) en el punto medio
        if (a.Simbolo != null) {
            int midX = (x1 + destinoX) / 2;
            int midY = (y1 + destinoY) / 2;
            g2.setColor(Color.black); // Color distintivo para el alfabeto
            g2.drawString(a.Simbolo, midX, midY - 5);
        }
    }

}
/* 
public class GrafoInteractivo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Nodo> nodos = new ArrayList<>();

            Nodo noda = new Nodo("A", 100, 100);
            Nodo nodb = new Nodo("B", 300, 100);

            nodos.add(noda);
            nodos.add(nodb);
            nodos.add(new Nodo("C", 200, 250));

            List<Arista> aristas = new ArrayList<>();

            aristas.add(new Arista(noda, nodb, "[a-b]"));
            

            JFrame frame = new JFrame("Grafo Interactivo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new PanelGrafo(nodos, aristas));
            frame.setSize(600, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
*/

