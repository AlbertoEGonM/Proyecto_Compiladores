package GUI;

import java.awt.*;
import javax.swing.*;

public class VentanaConvertirAFNaAFD extends JDialog {
    public VentanaConvertirAFNaAFD(JFrame parent) {
        super(parent, "Convertir AFN a AFD", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        String[] afns = {"AFN 1", "AFN Especial Léxico"};
        JComboBox<String> comboAfn = new JComboBox<>(afns);
        JButton btnConvertir = new JButton("Convertir a AFD");

        btnConvertir.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Convirtiendo " + comboAfn.getSelectedItem() + " a AFD mediante subconjuntos.");
            this.dispose();
        });

        add(new JLabel("Selecciona el AFN a convertir:"));
        add(comboAfn);
        add(btnConvertir);
    }
}