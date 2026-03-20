package GUI;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.geom.QuadCurve2D;

import AFN.AFN;
import AFN.Estado;
import AFD.AFD;
import AFN.SimbESP;
import AFN.Transicion;
import java.util.HashMap;

public class VentanaGrafo extends JDialog {
    public final List<Nodo> nodos = new ArrayList<>();
    public final List<Arista> aristas = new ArrayList<>();
    private PanelGrafo panelDibujo;

    public VentanaGrafo(JFrame parent, AFN f) {
        super(parent, "Visualización de Grafo AFN", true);
        setSize(1000, 700); // Un poco más de espacio para autómatas grandes
        setLocationRelativeTo(parent);
        
        // Usamos BorderLayout para que el panel de dibujo ocupe todo el centro
        setLayout(new BorderLayout());

        // Obtencion de las listas de nodos y aristas
        getNodosAndAristas(f);
        

        this.setJMenuBar(BarraMenu());

        // Creación de paneles principales.
        PanelGrafo panelDibujo = new PanelGrafo(nodos, aristas);
        JPanel panelInfo = crearPanelInfo(f);
        
        // 
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelInfo , panelDibujo);
        splitPane.setDividerLocation(175); // Posición inicial de la división
        splitPane.setOneTouchExpandable(true); // Flechitas para colapsar

        add(splitPane, BorderLayout.CENTER);
        
    }

    public VentanaGrafo(JFrame parent, AFD f) {
        super(parent, "Visualización de Grafo AFN", true);
        setSize(1000, 700); // Un poco más de espacio para autómatas grandes
        setLocationRelativeTo(parent);
        
        // Usamos BorderLayout para que el panel de dibujo ocupe todo el centro
        setLayout(new BorderLayout());

        // Obtencion de las listas de nodos y aristas
        getNodosAndAristas(f);
        

        this.setJMenuBar(BarraMenu());

        // Creación de paneles principales.
        PanelGrafo panelDibujo = new PanelGrafo(nodos, aristas);
        JPanel panelInfo = crearPanelInfo();
        
        // 
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelInfo , panelDibujo);
        splitPane.setDividerLocation(175); // Posición inicial de la división
        splitPane.setOneTouchExpandable(true); // Flechitas para colapsar

        add(splitPane, BorderLayout.CENTER);
        
    }

    private JPanel crearPanelInfo(AFN f) {

        String[] info = f.getInfoAFN();


        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(" Información del Autómata "));
        p.setBackground(new Color(245, 245, 245));
        p.setPreferredSize(new Dimension(300, 0));


        // Formatear los datos
        String[] etiquetas = {"ID AFN:", "Expresión Regular:", "Alfabeto:", "Conjunto de Estados:", "Estado Inicial:", "Estados Finales", "Token's"};
        
        for (int i = 0; i < info.length; i++) {
            // Etiqueta de título (Negrita)
            JLabel titulo = new JLabel(etiquetas[i]);
            titulo.setFont(new Font("Arial", Font.BOLD, 12));
            titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Etiqueta de valor
            // Usamos JTextArea para que el texto largo (como los estados) haga scroll o salto de línea
            JTextArea valor = new JTextArea(info[i]);
            valor.setEditable(false);
            valor.setLineWrap(true);
            valor.setWrapStyleWord(true);
            valor.setBackground(null);
            valor.setAlignmentX(Component.LEFT_ALIGNMENT);
            valor.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

            p.add(titulo);
            p.add(valor);
        }

        return p;
    }

    private JPanel crearPanelInfo(){
        String[] info = AFD.getInfo();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(" Información del Autómata "));
        p.setBackground(new Color(245, 245, 245));
        p.setPreferredSize(new Dimension(300, 0));


        // Formatear los datos
        String[] etiquetas = {"Expresión Regular:", "Alfabeto:", "Numero de Estados:", "Token's por estado"};
        
        for (int i = 0; i < info.length; i++) {
            // Etiqueta de título (Negrita)
            JLabel titulo = new JLabel(etiquetas[i]);
            titulo.setFont(new Font("Arial", Font.BOLD, 12));
            titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Etiqueta de valor
            // Usamos JTextArea para que el texto largo (como los estados) haga scroll o salto de línea
            JTextArea valor = new JTextArea(info[i]);
            valor.setEditable(false);
            valor.setLineWrap(true);
            valor.setWrapStyleWord(true);
            valor.setBackground(null);
            valor.setAlignmentX(Component.LEFT_ALIGNMENT);
            valor.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

            p.add(titulo);
            p.add(valor);
        }

        return p;
    }

    private JMenuBar BarraMenu(){
        // Ensamblar el menú
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemExportar = new JMenuItem("Exportar como imagen...");

        // Configurar la acción de exportar
        itemExportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Usar un JFileChooser para que el usuario elija dónde guardar
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar imagen del grafo");
                
                // Filtrar para mostrar solo archivos .png
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes PNG (*.png)", "png");
                fileChooser.setFileFilter(filter);
                
                int userSelection = fileChooser.showSaveDialog(VentanaGrafo.this);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String filePath = fileToSave.getAbsolutePath();
                    
                    // Asegurar que el archivo termine en .png
                    if (!filePath.toLowerCase().endsWith(".png")) {
                        filePath += ".png";
                    }
                    
                    // *** LLAMAR AL MÉTODO DE EXPORTADO DEL PANEL ***
                    panelDibujo.exportarImagen(filePath);
                }
            }
        });
        menuArchivo.add(itemExportar);
        menuBar.add(menuArchivo);

        return menuBar;
    }

    private void getNodosAndAristas(AFN F) {
        nodos.clear();
        aristas.clear();
        HashMap<Estado, Nodo> R = new HashMap<>();
        
        // Espaciado inicial
        int xBase = 100;
        int yBase = 200;

        // Primero creamos todos los nodos para que existan al buscar EdoFinal
        for (Estado ev : F.EstadosAFN) {
            Nodo nodEv = new Nodo("" + ev.IdEdo, xBase, yBase);
            R.put(ev, nodEv);
            nodos.add(nodEv);
            
            // Distribuir nodos horizontalmente
            xBase += 150; 
            // Si hay muchos, bajamos a la siguiente fila
            if (xBase > 800) { xBase = 100; yBase += 150; }
        }

        // Luego creamos las aristas
        for (Estado ev : F.EstadosAFN) {
            Nodo nodoOrigen = R.get(ev);
            for (Transicion t : ev.Transiciones) {
                Nodo nodoDestino = R.get(t.EdoFinal);
                
                if (nodoDestino != null) {
                    String simbolo = (t.Simbolo1 == t.Simbolo2) ? 
                    String.valueOf(t.Simbolo1) : 
                    "[" + t.Simbolo1 + "-" + t.Simbolo2 + "]";
                    
                    aristas.add(new Arista(nodoOrigen, nodoDestino, simbolo));
                }
            }
        }
    }

    private void getNodosAndAristas(AFD F){
        nodos.clear();
        aristas.clear();
        
        // Espaciado inicial
        int xBase = 100;
        int yBase = 200;

        for (int i = 0; i < F.numEstadosSj-1; i++) {
            Nodo nodEv = new Nodo("S"+i , xBase, yBase);
            nodos.add(nodEv);
            
            // Distribuir nodos horizontalmente
            xBase += 150; 
            // Si hay muchos, bajamos a la siguiente fila
            if (xBase > 800) { xBase = 100; yBase += 150; }
        }

        Arista aristaAux = null;
        int i = 0, aux = -1;
        for(Nodo nodev : nodos){
            for(char c : F.Alfabeto){
                if(F.TablaAFD[i][c] != -1){
                    aux = F.TablaAFD[i][c];
                    aristaAux = Arista.MismaDirecion(nodev, nodos.get(aux), aristas);
                    if(aristaAux != null){
                        aristaAux.Simbolo += ", "+ c;
                    }else{
                        aristas.add(new Arista(nodev, nodos.get(aux), ""+c));
                    }
                }
            }
            i++;
        }
    }
}

class PanelGrafo extends JPanel {
    //static final int RADIO = 20;
    public List<Nodo> nodos = new ArrayList<>();
    public List<Arista> aristas = new ArrayList<>();
    public Nodo nodoSeleccionado = null;
    public Nodo nodoOrigenArista = null; // Nodo para crear aristas
    private int offsetX = 0; // Desplazamiento horizontal acumulado
    private int offsetY = 0; // Desplazamiento vertical acumulado
    private Point puntoPresionado;

    public PanelGrafo(List<Nodo> nodos, List<Arista> aristas) {
        this.nodos = nodos;
        this.aristas = aristas;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                puntoPresionado = e.getPoint(); // Guardar posición inicial del click
                
                boolean clickEnNodo = false;
                for (Nodo n : nodos) {
                    // Ajustamos las coordenadas de detección restando el offset
                    if (n.contienePunto(e.getX() - offsetX, e.getY() - offsetY)) {
                        clickEnNodo = true;
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            nodoSeleccionado = n;
                        }
                        repaint();
                        return;
                    }
                }
                if (!clickEnNodo) {
                    nodoSeleccionado = null;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (nodoSeleccionado != null) {
                    // Mover nodo: el ratón se mueve, restamos el offset para que el nodo 
                    // siga al puntero correctamente en un lienzo desplazado
                    nodoSeleccionado.x = e.getX() - offsetX;
                    nodoSeleccionado.y = e.getY() - offsetY;
                } else {
                    // MOVER EL LIENZO (Panning)
                    // Calculamos cuánto se movió el ratón desde el punto inicial
                    int deltaX = e.getX() - puntoPresionado.x;
                    int deltaY = e.getY() - puntoPresionado.y;

                    offsetX += deltaX;
                    offsetY += deltaY;

                    puntoPresionado = e.getPoint(); // Actualizar para el siguiente frame
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(offsetX, offsetY);

        // DIBUJAR ARISTAS
        for (Arista a : aristas) {
            dibujarTransicion(g2, a);
        }

        // DIBUJAR NODOS
        for (Nodo n : nodos) {
            // Círculo
            g2.setColor(Color.WHITE);
            g2.fillOval(n.x - Nodo.RADIO, n.y - Nodo.RADIO, Nodo.RADIO * 2, Nodo.RADIO * 2);
            g2.setColor(Color.BLACK);
            g2.drawOval(n.x - Nodo.RADIO, n.y - Nodo.RADIO, Nodo.RADIO * 2, Nodo.RADIO * 2);
            
            // ID del Estado
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(n.etiqueta, n.x - fm.stringWidth(n.etiqueta)/2, n.y + 5);
        }
        // Al final, es buena práctica resetear la traslación si vas a dibujar algo estático (como un HUD)
        g2.translate(-offsetX, -offsetY);
    }

    private void dibujarTransicion(Graphics2D g2, Arista a) {
        if (a.origen == a.destino) {
            int r = Nodo.RADIO;
            g2.setColor(Color.BLACK);
            // Dibujamos un arco encima del nodo
            g2.drawArc(a.origen.x - r, a.origen.y - r * 2, r * 2, r * 2, 0, 180);
            
            // Punta de flecha para el bucle
            g2.drawLine(a.origen.x + r, a.origen.y - r, a.origen.x + r + 5, a.origen.y - r + 5);
            
            // Etiqueta del bucle
            g2.setColor(Color.RED);
            g2.drawString(a.Simbolo, a.origen.x, a.origen.y - r * 2 - 2);
            return;
        }

        int x1 = a.origen.x;
        int y1 = a.origen.y;
        int x2 = a.destino.x;
        int y2 = a.destino.y;

        double angulo = Math.atan2(y2 - y1, x2 - x1);
        g2.setStroke(new BasicStroke(2f));
        
        // Configurar color según Símbolo (Epsilon o normal)
        if (a.Simbolo.charAt(0) != SimbESP.Epsilon) g2.setColor(Color.BLACK);
        else g2.setColor(Color.RED);

        // 2. DETECTAR SOBRELAPE
        if (Arista.Sobrelapa(a.origen, a.destino, aristas)) {
            // Calculamos un punto de control desplazado perpendicularmente a la línea
            // Esto crea el efecto de "arco"
            /*double dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));*/
            int curvaOffset = 30; // Qué tan pronunciada es la curva

            // Punto medio
            double mx = (x1 + x2) / 2.0;
            double my = (y1 + y2) / 2.0;

            // Punto de control (perpendicular)
            double ctrlX = mx + curvaOffset * Math.sin(angulo);
            double ctrlY = my - curvaOffset * Math.cos(angulo);

            /*// Dibujar curva
            QuadCurve2D curva = new QuadCurve2D.Float(x1, y1, (float)ctrlX, (float)ctrlY, x2, y2);*/
            
            // Calcular punto de contacto en el borde del nodo destino para la flecha
            // En una curva, el ángulo de entrada cambia, usamos el punto de control
            double anguloFinal = Math.atan2(y2 - ctrlY, x2 - ctrlX);
            int destX = (int) (x2 - Nodo.RADIO * Math.cos(anguloFinal));
            int destY = (int) (y2 - Nodo.RADIO * Math.sin(anguloFinal));

            g2.draw(new QuadCurve2D.Float(x1, y1, (float)ctrlX, (float)ctrlY, destX, destY));
            
            // Dibujar punta de flecha con el nuevo ángulo
            dibujarPuntaFlecha(g2, destX, destY, anguloFinal);

            // Dibujar símbolo sobre el punto de control
            String texto = a.Simbolo.charAt(0) == SimbESP.Epsilon ? "ε" : a.Simbolo;
            g2.drawString(texto, (int)ctrlX, (int)ctrlY);

        } else {
            // 3. Caso normal (Línea recta - tu código actual)
            int destX = (int) (x2 - Nodo.RADIO * Math.cos(angulo));
            int destY = (int) (y2 - Nodo.RADIO * Math.sin(angulo));
            
            g2.drawLine(x1, y1, destX, destY);
            dibujarPuntaFlecha(g2, destX, destY, angulo);

            // Símbolo en punto medio
            int midX = (x1 + destX) / 2;
            int midY = (y1 + destY) / 2;
            String texto = a.Simbolo.charAt(0) == SimbESP.Epsilon ? "ε" : a.Simbolo;
            g2.drawString(texto, midX, midY - 5);
        }
    }

    // Método auxiliar para no repetir código de flechas
    private void dibujarPuntaFlecha(Graphics2D g2, int x, int y, double angulo) {
        int largo = 12;
        double apertura = Math.toRadians(25);
        int xP1 = (int) (x - largo * Math.cos(angulo - apertura));
        int yP1 = (int) (y - largo * Math.sin(angulo - apertura));
        int xP2 = (int) (x - largo * Math.cos(angulo + apertura));
        int yP2 = (int) (y - largo * Math.sin(angulo + apertura));
        g2.drawLine(x, y, xP1, yP1);
        g2.drawLine(x, y, xP2, yP2);
    }

    public void exportarImagen(String nombreArchivo) {
        // 1. Crear la imagen en memoria con el tamaño del panel
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        // 2. Obtener el Graphics2D de esa imagen para dibujar en ella
        Graphics2D g2 = image.createGraphics();
        
        // 3. (Opcional) Dibujar un fondo blanco para que no sea transparente
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // 4. Llamar al método paintAll o paint del panel para dibujar todo en la imagen
        //    (Incluye nodos, aristas, fondo y componentes Swing si los hubiera)
        this.paintAll(g2); 
        
        // 5. Liberar los recursos del Graphics2D
        g2.dispose();
        
        // 6. Guardar la imagen en disco como PNG
        try {
            ImageIO.write(image, "PNG", new File(nombreArchivo));
            JOptionPane.showMessageDialog(this, "Imagen exportada exitosamente como " + nombreArchivo);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al exportar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

class Arista {
    protected Nodo origen, destino;
    protected String Simbolo;
    protected Arista(Nodo origen, Nodo destino, String Simbolo) {
        this.origen = origen;
        this.destino = destino;
        this.Simbolo = Simbolo;
    }

    protected static Arista MismaDirecion(Nodo Origen, Nodo Destino, List<Arista> aristas){
        for (Arista A : aristas) {
            if(A.origen.equals(Origen) && A.destino.equals(Destino))
                return A;
        }
        return null;
    }

    protected static Boolean Sobrelapa(Nodo Origen, Nodo Destino, List<Arista> aristas){
        for (Arista A : aristas){
            if(A.origen.equals(Destino) && A.destino.equals(Origen))
                return true;
        }
        return false;
    }
}

class Nodo {
    protected int x, y;
    protected String etiqueta;
    static final int RADIO = 20;

    protected Nodo(String etiqueta, int x, int y) {
        this.etiqueta = etiqueta;
        this.x = x;
        this.y = y;
    }

    protected boolean contienePunto(int px, int py) {
        return Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2)) <= RADIO;
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

