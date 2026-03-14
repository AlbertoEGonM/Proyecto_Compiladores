package GUI;

import AFN.AFN;

import java.awt.*;
import javax.swing.*;

public class VentanaConcatenar extends JDialog {
    public VentanaConcatenar(JFrame parent) {
        super(parent, "Concatenar AFNs", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));
        
    
        JLabel label1 = new JLabel("Selecciona AFN A:");
        JComboBox<String> comboAfn1 = new JComboBox<>(AFN.getAllERegular());
        JLabel label2 = new JLabel("Selecciona AFN B:");
        JComboBox<String> comboAfn2 = new JComboBox<>(AFN.getAllERegular());

        JButton btnConcatenar = new JButton("Concatenar");
        JButton btnCancelar = new JButton("Cancelar");

        btnConcatenar.addActionListener(e -> {
            String afnA = (String) comboAfn1.getSelectedItem();
            String afnB = (String) comboAfn2.getSelectedItem();
            if(afnA.equals(afnB)) {
                JOptionPane.showMessageDialog(this, "No puedes concatenar el mismo AFN consigo mismo");
            } else {
                AFN afn1 = AFN.getAFNByER(afnA);
                AFN afn2 = AFN.getAFNByER(afnB);
                if(afn1 == null || afn2 == null) {
                    JOptionPane.showMessageDialog(this, "Error al obtener los AFNs seleccionados");
                }else{
                    afn1.ConcatenarAFN(afn2); // Aquí llamas a tu método de concatenación en la clase AFN
                    JOptionPane.showMessageDialog(this, "Se concatenaron: " + afnA + " y " + afnB + "\nNuevo AFN con ID: " + afn1.IdAFN + " : E.R.: " + afn1.E_Regular);
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    VentanaGrafo vGrafo = new VentanaGrafo(parentFrame, afn1);
                    
                    //this.dispose(); // Cerramos la ventanita de entrada
                    vGrafo.setVisible(true); // Mostramos el grafo resultante
                    this.dispose();
                }
            }
        });
        btnCancelar.addActionListener(e -> this.dispose());

        add(label1); add(comboAfn1);
        add(label2); add(comboAfn2);
        add(btnConcatenar); add(btnCancelar);
    }
}