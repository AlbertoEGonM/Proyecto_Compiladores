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

import AFN.AFN;
import AFN.Estado;
import AFN.SimbESP;
import AFN.Transicion;
import java.util.HashMap;

public class VentanaGrafo extends JDialog {
    public final List<Nodo> nodos = new ArrayList<>();
    public final List<Arista> aristas = new ArrayList<>();
    private PanelGrafo panelDibujo;

    public VentanaGrafo(JFrame parent, AFN f) {
        super(parent, "Visualización de Grafo AFN", true);
        setSize(900, 700); // Un poco más de espacio para autómatas grandes
        setLocationRelativeTo(parent);
        
        // Usamos BorderLayout para que el panel de dibujo ocupe todo el centro
        setLayout(new BorderLayout());

        // Obtencion de las listas de nodos y aristas
        getNodosAndAristas(f);
        String[] info = f.getInfoAFN();

        this.setJMenuBar(BarraMenu());

        // Creación de paneles principales.
        PanelGrafo panelDibujo = new PanelGrafo(nodos, aristas);
        JPanel panelInfo = crearPanelInfo(info);
        
        // 
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelInfo , panelDibujo);
        splitPane.setDividerLocation(175); // Posición inicial de la división
        splitPane.setOneTouchExpandable(true); // Flechitas para colapsar

        add(splitPane, BorderLayout.CENTER);
        

        // Opcional: Un botón para cerrar o instrucciones
        /*JLabel lblHint = new JLabel(" Arrastra los nodos con clic izquierdo | Crea aristas manuales con clic derecho");
        lblHint.setFont(new Font("Arial", Font.ITALIC, 12));
        add(lblHint, BorderLayout.SOUTH);*/
    }

    private JPanel crearPanelInfo(String[] info) {
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
}

class PanelGrafo extends JPanel {
    //static final int RADIO = 20;
    public List<Nodo> nodos = new ArrayList<>();
    public List<Arista> aristas = new ArrayList<>();
    public Nodo nodoSeleccionado = null;
    public Nodo nodoOrigenArista = null; // Nodo para crear aristas

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

        // Calcular el ángulo de la línea
        double angulo = Math.atan2(y2 - y1, x2 - x1);

        // Ajustar el punto de destino para que la flecha toque el BORDE del nodo
        // (Restamos el RADIO del nodo en la dirección del ángulo)
        int destinoX = (int) (x2 - Nodo.RADIO * Math.cos(angulo));
        int destinoY = (int) (y2 - Nodo.RADIO * Math.sin(angulo));

        // Dibujar la línea principal
        
        if(a.Simbolo.charAt(0) != SimbESP.Epsilon )
            g2.setColor(Color.BLACK);
        else
            g2.setColor(Color.RED);
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
            if(a.Simbolo.charAt(0) != SimbESP.Epsilon ){
                g2.setColor(Color.BLACK);
                g2.drawString(a.Simbolo, midX, midY - 5);
            }
            else{
                g2.setColor(Color.RED);
                g2.drawString("ε", midX, midY - 5);
            }
        }
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

