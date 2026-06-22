package GUI;

import HOC5.HOC5;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class VentanaHOC5 extends JDialog {
  
    private JTextArea txtEntrada;
    private JTextArea txtOutSintactico;
    private JTextArea txtOutLexico;
    private JTextArea txtResultados;
    private JTable tablaCodigo;
    private JTable tablaPila;
    
    private final HOC5 hoc5;

    public VentanaHOC5(JFrame parent) {
        super(parent, "HOC 5", false);
        this.hoc5 = new HOC5();
        
        setSize(1150, 650); 
        setLocationRelativeTo(parent);
   
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.X_AXIS));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(new Color(214, 217, 223));

      
        JPanel colIzq = new JPanel();
        colIzq.setLayout(new BoxLayout(colIzq, BoxLayout.Y_AXIS));
        colIzq.setOpaque(false);
        colIzq.setPreferredSize(new Dimension(300, 600));
        colIzq.setMaximumSize(new Dimension(300, 2000));
        
        JLabel lblCadena = new JLabel("Cadena a Analizar Lexicamente");
        lblCadena.setFont(new Font("Tahoma", Font.PLAIN, 11));
        
        txtEntrada = new JTextArea("n=0;\nr=1;\n\nif(n==0)\n  r=1;\nelse {\n  r=1;\n  while(i<=n){\n    r = r*i;\n    i = i + 1;\n  }\n}");
        JScrollPane scrollEntrada = new JScrollPane(txtEntrada);
        scrollEntrada.setPreferredSize(new Dimension(300, 200));
        
        JButton btnSintactico = new JButton("Analizar Sintacticamente");
        
        txtOutSintactico = new JTextArea("FIN DEL ANÁLISIS SINTÁCTICO\n");
        txtOutSintactico.setEditable(false);
        JScrollPane scrollSintactico = new JScrollPane(txtOutSintactico);
        scrollSintactico.setPreferredSize(new Dimension(300, 250));

        colIzq.add(lblCadena);
        colIzq.add(Box.createVerticalStrut(5));
        colIzq.add(scrollEntrada);
        colIzq.add(Box.createVerticalStrut(15));
        colIzq.add(btnSintactico);
        colIzq.add(Box.createVerticalStrut(5));
        colIzq.add(scrollSintactico);

      
        JPanel colCentro = new JPanel();
        colCentro.setLayout(new BoxLayout(colCentro, BoxLayout.Y_AXIS));
        colCentro.setOpaque(false);
        colCentro.setPreferredSize(new Dimension(400, 600));
        colCentro.setBorder(new EmptyBorder(0, 15, 0, 15)); // Márgenes a los lados

        JPanel panelBotonesLex = new JPanel(new BorderLayout());
        panelBotonesLex.setOpaque(false);
        JButton btnLexico = new JButton("Analizar Lexicamente");
        JButton btnBotonExtra = new JButton("jButton1"); 
        panelBotonesLex.add(btnLexico, BorderLayout.WEST);
        panelBotonesLex.add(btnBotonExtra, BorderLayout.EAST);
        panelBotonesLex.setMaximumSize(new Dimension(400, 30));

        txtOutLexico = new JTextArea();
        txtOutLexico.setEditable(false);
        JScrollPane scrollLexico = new JScrollPane(txtOutLexico);
        scrollLexico.setPreferredSize(new Dimension(400, 150));

        JButton btnEjecutar = new JButton("Ejecutar código");

        // Tabla de Máquina Virtual (Vector de código)
        String[] columnasCodigo = {"NumInstrucc...", "INST-SYMB-...", "NAME", "VAL", "FUNCIÓN"};
        DefaultTableModel modCodigo = new DefaultTableModel(columnasCodigo, 0);
        tablaCodigo = new JTable(modCodigo);
        JScrollPane scrollTablaCodigo = new JScrollPane(tablaCodigo);
        scrollTablaCodigo.setPreferredSize(new Dimension(400, 250));

        JLabel lblResultados = new JLabel("RESULTADOS");
        lblResultados.setFont(new Font("Tahoma", Font.PLAIN, 11));
        
        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        JScrollPane scrollResultados = new JScrollPane(txtResultados);
        scrollResultados.setPreferredSize(new Dimension(150, 100));
        scrollResultados.setMaximumSize(new Dimension(150, 100)); 
        
        JPanel panelRes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelRes.setOpaque(false);
        panelRes.add(scrollResultados);

        colCentro.add(panelBotonesLex);
        colCentro.add(Box.createVerticalStrut(5));
        colCentro.add(scrollLexico);
        colCentro.add(Box.createVerticalStrut(10));
        colCentro.add(btnEjecutar);
        colCentro.add(Box.createVerticalStrut(5));
        colCentro.add(scrollTablaCodigo);
        colCentro.add(Box.createVerticalStrut(10));
        colCentro.add(lblResultados);
        colCentro.add(Box.createVerticalStrut(5));
        colCentro.add(panelRes);

    
        JPanel colDer = new JPanel(new BorderLayout(0, 5));
        colDer.setOpaque(false);
        colDer.setPreferredSize(new Dimension(350, 600));
        
        JLabel lblPila = new JLabel("PILA");
        lblPila.setFont(new Font("Tahoma", Font.PLAIN, 11));
        
        // Tabla de la Pila
        String[] columnasPila = {"Datum", "Valor", "Symbol", "Type Symbol", "Val Symbol"};
        DefaultTableModel modPila = new DefaultTableModel(columnasPila, 0);
        tablaPila = new JTable(modPila);
        JScrollPane scrollTablaPila = new JScrollPane(tablaPila);

        colDer.add(lblPila, BorderLayout.NORTH);
        colDer.add(scrollTablaPila, BorderLayout.CENTER);

        // Armar el panel principal
        panelPrincipal.add(colIzq);
        panelPrincipal.add(colCentro);
        panelPrincipal.add(colDer);

        add(panelPrincipal);

     
        btnLexico.addActionListener(e -> {
            try {
                txtOutLexico.setText(hoc5.analizarLexicamente(txtEntrada.getText()));
            } catch (Exception ex) {
                txtOutLexico.setText("Error: " + ex.getMessage());
            }
        });

        btnSintactico.addActionListener(e -> {
            try {
                // Limpia la salida sintáctica anterior
                txtOutSintactico.setText(""); 
                String resultado = hoc5.analizarSintacticamente(txtEntrada.getText());
                txtOutSintactico.setText("FIN DEL ANÁLISIS SINTÁCTICO\n" + resultado);
            } catch (Exception ex) {
                txtOutSintactico.setText("Error: " + ex.getMessage());
            }
        });

        btnEjecutar.addActionListener(e -> {
            txtResultados.setText(hoc5.ejecutar(txtEntrada.getText()));
     
        });
    }
}