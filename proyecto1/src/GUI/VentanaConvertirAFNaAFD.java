package GUI;

import AFD.AFD;
import AFN.AFN;
import java.awt.*;
import javax.swing.*;

public class VentanaConvertirAFNaAFD extends JDialog {
    public VentanaConvertirAFNaAFD(JFrame parent) {
        super(parent, "Convertir AFN a AFD", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        JTable tabla = new JTable();

        JComboBox<String> comboAfn = new JComboBox<>(AFN.getAllERegular());
        JButton btnConvertir = new JButton("Convertir a AFD");

        btnConvertir.addActionListener(e -> {
            AFN afnSeleccionado = AFN.getAFNByER((String) comboAfn.getSelectedItem());
            if(afnSeleccionado != null) {
                AFD.afdResultante = new AFD(afnSeleccionado);
                // Aquí podrías mostrar el AFD resultante en una nueva ventana o guardarlo
                JOptionPane.showMessageDialog(this, "AFN '" + comboAfn.getSelectedItem() + "' convertido a AFD con éxito." + "\nNúmero de estados en el AFD: " + afdResultante.numEstadosSj);
                
            } else {
                JOptionPane.showMessageDialog(this, "Error al convertir el AFN seleccionado.");
            }
        });

        add(new JLabel("Selecciona el AFN a convertir:"));
        add(comboAfn);
        add(btnConvertir);
        add(tabla);
    }
}