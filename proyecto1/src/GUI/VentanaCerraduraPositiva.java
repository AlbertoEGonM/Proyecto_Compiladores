package GUI;

import AFN.AFN;
import java.awt.*;
import javax.swing.*;

public class VentanaCerraduraPositiva extends JDialog {
    public VentanaCerraduraPositiva(JFrame parent) {
        super(parent, "Aplicar Cerradura Positiva (+)", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JComboBox<String> comboAfn = new JComboBox<>(AFN.getAllERegular());
        JButton btnAplicar = new JButton("Aplicar Cerradura +");

        btnAplicar.addActionListener(e -> {
            AFN afn = AFN.getAFNByER((String) comboAfn.getSelectedItem());
            if(afn != null) {
                afn.CerrPositiva();
                JOptionPane.showMessageDialog(this, "Cerradura + aplicada a: " + comboAfn.getSelectedItem() + "\nNuevo AFN: " + afn.IdAFN + "\nER: " + afn.E_Regular);
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                VentanaGrafo vGrafo = new VentanaGrafo(parentFrame, afn);
                
                //this.dispose(); // Cerramos la ventanita de entrada
                vGrafo.setVisible(true); // Mostramos el grafo resultante
                this.dispose();
            }else{
                JOptionPane.showMessageDialog(this, "Error al obtener el AFN seleccionado");
            }
            
        });

        add(new JLabel("Selecciona un AFN:"));
        add(comboAfn);
        add(btnAplicar);
    }
}