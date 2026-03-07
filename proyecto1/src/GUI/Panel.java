package GUI;

import javax.swing.*;

public class Panel extends JFrame {

    public Panel() {
        setTitle("Analizador de Autómatas - ESCOM");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuAFN = new JMenu("AFN's");
        JMenu menuSintactico = new JMenu("Analisis Sintáctico");
        /*JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());*/

        JMenuItem itemBasico = new JMenuItem("Básico");
        JMenuItem itemUnir = new JMenuItem("Unir");
        JMenuItem itemConcatenar = new JMenuItem("Concatenar");
        JMenuItem itemCerraduraPositiva = new JMenuItem("Cerradura +");
        JMenuItem itemCerraduraEstrella = new JMenuItem("Cerradura *");
        JMenuItem itemOpcional = new JMenuItem("Opcional");
        JMenuItem itemERaAFN = new JMenuItem("ER->AFN");
        JMenuItem itemUnionLexico = new JMenuItem("Unión para Analizador Léxico");
        JMenuItem itemConvertirAFNaAFD = new JMenuItem("Convertir AFN a AFD");
        JMenuItem itemAnalizarCadena = new JMenuItem("Analizar una Cadena");
        JMenuItem itemProbarLexico = new JMenuItem("Probar analizador Léxico");

        // --- CONEXIÓN DE TODAS LAS VENTANAS ---
        // Nota: Para que este archivo compile sin errores, deberás crear 
        // un archivo .java para cada una de estas clases.
        
        itemBasico.addActionListener(e -> new VentanaBasico(this).setVisible(true));
        itemUnir.addActionListener(e -> new VentanaUnir(this).setVisible(true));
        itemConcatenar.addActionListener(e -> new VentanaConcatenar(this).setVisible(true));
        
        itemCerraduraPositiva.addActionListener(e -> new VentanaCerraduraPositiva(this).setVisible(true));
        itemCerraduraEstrella.addActionListener(e -> new VentanaCerraduraEstrella(this).setVisible(true));
        itemOpcional.addActionListener(e -> new VentanaOpcional(this).setVisible(true));
        
        itemERaAFN.addActionListener(e -> new VentanaERaAFN(this).setVisible(true));
        itemUnionLexico.addActionListener(e -> new VentanaUnionLexico(this).setVisible(true));
        itemConvertirAFNaAFD.addActionListener(e -> new VentanaConvertirAFNaAFD(this).setVisible(true));
        
        itemAnalizarCadena.addActionListener(e -> new VentanaAnalizarCadena(this).setVisible(true));
        itemProbarLexico.addActionListener(e -> new VentanaProbarLexico(this).setVisible(true));

        // Ensamblar el menú desplegable
        menuAFN.add(itemBasico);
        menuAFN.add(itemUnir);
        menuAFN.add(itemConcatenar);
        menuAFN.add(itemCerraduraPositiva);
        menuAFN.add(itemCerraduraEstrella);
        menuAFN.add(itemOpcional);
        menuAFN.addSeparator(); // Divisor visual
        menuAFN.add(itemERaAFN);
        menuAFN.add(itemUnionLexico);
        menuAFN.add(itemConvertirAFNaAFD);
        menuAFN.addSeparator(); // Divisor visual
        menuAFN.add(itemAnalizarCadena);
        menuAFN.add(itemProbarLexico);

        menuBar.add(menuAFN);
        menuBar.add(menuSintactico);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormularioAutomatas ventana = new FormularioAutomatas();
            ventana.setVisible(true);
        });
    }
}