package GUI;

import AFN.AFN;
import AFN.GeneradorAFN; 
import java.awt.*;
import javax.swing.*;

public class VentanaGenerarAFN extends JDialog {

    public VentanaGenerarAFN(JFrame parent) {
        super(parent, "Generar AFN desde Expresión Regular", true);
        setSize(350, 150);
        setLocationRelativeTo(parent);
        

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        JLabel etiqueta = new JLabel("Introduce la Expresión Regular:");
        JTextField txtExpresion = new JTextField(25); 
        JButton botonGenerar = new JButton("Generar AFN");

        botonGenerar.addActionListener(e -> {
            String regex = txtExpresion.getText().trim();
            
            if (regex.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor, ingresa una expresión regular válida.", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                AFN nuevoAfn = GeneradorAFN.generar(regex);
                
                JOptionPane.showMessageDialog(this, 
                    "¡AFN generado con éxito!\nID del AFN: " + nuevoAfn.IdAFN, 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                e
                dispose(); 
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al generar el AFN. Revisa la sintaxis de tu E.R.\nError: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        panelPrincipal.add(etiqueta);
        panelPrincipal.add(txtExpresion);
        panelPrincipal.add(botonGenerar);

        add(panelPrincipal);
    }
}