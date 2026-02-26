import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Panel extends JFrame {

    public Panel() {
        // 1. Configuración de la ventana principal
        setTitle("Form1");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 2. Crear la barra y los menús principales
        JMenuBar menuBar = new JMenuBar();
        JMenu menuAFN = new JMenu("AFN's");
        JMenu menuSintactico = new JMenu("Analisis Sintáctico");

        // 3. Declarar cada opción del menú individualmente
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

        // 4. Asignar un ActionListener específico a cada opción
        
        itemBasico.addActionListener(e -> {
            // TODO: Aquí va código para crear un AFN Básico (Ej. crear estados inicial y final)
            JOptionPane.showMessageDialog(this, "Ejecutando función: Crear AFN Básico");
        });

        itemUnir.addActionListener(e -> {
            // TODO: Aquí va código para aplicar el algoritmo de Unión (Thompson)
            JOptionPane.showMessageDialog(this, "Ejecutando función: Unir AFNs");
        });

        itemConcatenar.addActionListener(e -> {
            // TODO: Aquí va código para Concatenar
            JOptionPane.showMessageDialog(this, "Ejecutando función: Concatenar AFNs");
        });

        itemCerraduraPositiva.addActionListener(e -> {
            // TODO: Aquí va código para Cerradura Positiva
            JOptionPane.showMessageDialog(this, "Ejecutando función: Cerradura +");
        });

        itemCerraduraEstrella.addActionListener(e -> {
            // TODO: Aquí va código para Cerradura de Kleene
            JOptionPane.showMessageDialog(this, "Ejecutando función: Cerradura *");
        });

        itemOpcional.addActionListener(e -> {
            // TODO: Aquí va código para la operación Opcional (?)
            JOptionPane.showMessageDialog(this, "Ejecutando función: Operación Opcional");
        });

        itemERaAFN.addActionListener(e -> {
            // TODO: Aquí va código para convertir Expresión Regular a AFN
            JOptionPane.showMessageDialog(this, "Ejecutando función: ER -> AFN");
        });

        itemUnionLexico.addActionListener(e -> {
            // TODO: Aquí va código para unir todo en un analizador léxico
            JOptionPane.showMessageDialog(this, "Ejecutando función: Unión Analizador Léxico");
        });

        itemConvertirAFNaAFD.addActionListener(e -> {
            // TODO: Aquí va código para el algoritmo de Construcción de Subconjuntos
            JOptionPane.showMessageDialog(this, "Ejecutando función: AFN a AFD");
        });

        itemAnalizarCadena.addActionListener(e -> {
            // TODO: Aquí va código para simular el AFD con una cadena de entrada
            JOptionPane.showMessageDialog(this, "Ejecutando función: Analizar Cadena");
        });

        itemProbarLexico.addActionListener(e -> {
            // TODO: Aquí va código para probar el analizador completo
            JOptionPane.showMessageDialog(this, "Ejecutando función: Probar Analizador Léxico");
        });

        // 5. Agregar todas las opciones al menú "AFN's" en orden
        menuAFN.add(itemBasico);
        menuAFN.add(itemUnir);
        menuAFN.add(itemConcatenar);
        menuAFN.add(itemCerraduraPositiva);
        menuAFN.add(itemCerraduraEstrella);
        menuAFN.add(itemOpcional);
        menuAFN.add(itemERaAFN);
        menuAFN.add(itemUnionLexico);
        menuAFN.add(itemConvertirAFNaAFD);
        menuAFN.add(itemAnalizarCadena);
        menuAFN.add(itemProbarLexico);

        // 6. Ensamblar la barra
        menuBar.add(menuAFN);
        menuBar.add(menuSintactico);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Panel ventana = new Panel();
            ventana.setVisible(true);
        });
    }
}