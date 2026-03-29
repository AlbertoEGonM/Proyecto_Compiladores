package GUI;

import AFN.AFN;
import java.awt.*;
import javax.swing.*;

public class VentanaBasico extends JDialog {

    public VentanaBasico(JFrame parent) {
        super(parent, "Crear AFN Básico", true); // El 'true' la hace modal
        setSize(300, 180);
        setLocationRelativeTo(parent);
        // DISPOSE_ON_CLOSE solo cierra esta ventanita, no todo el programa
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel label = new JLabel("Ingresa el carácter para el AFN:");
        JTextField txtCaracter = new JTextField(5);
        JLabel label2 = new JLabel("Ingresa el carácter 2 para el AFN:");
        JTextField txtCaracter2 = new JTextField(5);
        JButton btnCrear = new JButton("Crear AFN");

        btnCrear.addActionListener(e -> {
            String c1 = txtCaracter.getText();
            String c2 = txtCaracter2.getText();

            txtCaracter.setText("");
            txtCaracter2.setText("");

            // Validaciones de longitud
            if (c1.length() > 1 || c2.length() > 1) {
                JOptionPane.showMessageDialog(this, "Solo se permite un símbolo por campo.");
                return;
            }

            AFN miAfn = null;

            try {
                // Caso 1: Rango o dos caracteres (Ej: 'a' y 'b' o rango 'a'-'z')
                if (!c1.isEmpty() && !c2.isEmpty()) {
                    if (c1.equals(c2)) {
                        JOptionPane.showMessageDialog(this, "Los caracteres no pueden ser iguales para un rango.");
                        return;
                    }
                    miAfn = new AFN(c1.charAt(0), c2.charAt(0));
                    JOptionPane.showMessageDialog(this, "AFN (Rango) creado: " + miAfn.E_Regular);
                } 
                // Caso 2: Un solo carácter
                else if (!c1.isEmpty() && c2.isEmpty()) {
                    miAfn = new AFN(c1.charAt(0));
                    JOptionPane.showMessageDialog(this, "AFN Básico creado: " + miAfn.E_Regular);
                } 
                else {
                    JOptionPane.showMessageDialog(this, "Por favor, ingresa al menos el primer carácter.");
                    return;
                }

                // --- AQUÍ SE IMPLEMENTA LA VISUALIZACIÓN ---
                if (miAfn != null) {
                    // Obtenemos el JFrame padre para que la ventana del grafo se posicione bien
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    VentanaGrafo vGrafo = new VentanaGrafo(parentFrame, miAfn);
                    
                    //this.dispose(); // Cerramos la ventanita de entrada
                    vGrafo.setVisible(true); // Mostramos el grafo resultante
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear el AFN: " + ex.getMessage());
            }
        });

        add(label);
        add(txtCaracter);
        add(label2);
        add(txtCaracter2);
        add(btnCrear);
    }
}