import javax.swing.*;

public class FormularioAutomatas extends JFrame {

    public FormularioAutomatas() {
        setTitle("Analizador de Autómatas");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuAFN = new JMenu("AFN's");
        JMenu menuSintactico = new JMenu("Analisis Sintáctico");

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

        // --- CONEXIÓN DE LAS VENTANAS ---
        
        itemBasico.addActionListener(e -> {
            VentanaBasico ventana = new VentanaBasico(this);
            ventana.setVisible(true);
        });

        itemUnir.addActionListener(e -> {
            VentanaUnir ventana = new VentanaUnir(this);
            ventana.setVisible(true);
        });

        itemERaAFN.addActionListener(e -> {
            VentanaERaAFN ventana = new VentanaERaAFN(this);
            ventana.setVisible(true);
        });

        // Para las demás, sigue exactamente el mismo patrón:
        itemConcatenar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ventana en construcción"));
        itemCerraduraPositiva.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ventana en construcción"));
        itemCerraduraEstrella.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ventana en construcción"));
        itemOpcional.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ventana en construcción"));
        itemUnionLexico.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ventana en construcción"));
        itemConvertirAFNaAFD.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ventana en construcción"));
        itemAnalizarCadena.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ventana en construcción"));
        itemProbarLexico.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ventana en construcción"));

        // Ensamblar
        menuAFN.add(itemBasico);
        menuAFN.add(itemUnir);
        menuAFN.add(itemConcatenar);
        menuAFN.add(itemCerraduraPositiva);
        menuAFN.add(itemCerraduraEstrella);
        menuAFN.add(itemOpcional);
        menuAFN.addSeparator();
        menuAFN.add(itemERaAFN);
        menuAFN.add(itemUnionLexico);
        menuAFN.add(itemConvertirAFNaAFD);
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