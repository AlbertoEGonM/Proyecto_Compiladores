package GUI;

import java.awt.*;
import javax.swing.*;

public class VentanaCerraduraPositiva extends JDialog {
    public VentanaCerraduraPositiva(JFrame parent) {
        super(parent, "Aplicar Cerradura Positiva (+)", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        String[] afns = {"AFN 1", "AFN 2"};
        JComboBox<String> comboAfn = new JComboBox<>(afns);
        JButton btnAplicar = new JButton("Aplicar Cerradura +");

        btnAplicar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Cerradura + aplicada a: " + comboAfn.getSelectedItem());
            this.dispose();
        });

        add(new JLabel("Selecciona un AFN:"));
        add(comboAfn);
        add(btnAplicar);
    }
}