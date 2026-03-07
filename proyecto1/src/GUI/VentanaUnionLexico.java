package GUI;

import java.awt.*;
import javax.swing.*;

import AFN.AFN;

public class VentanaUnionLexico extends JDialog {
    public VentanaUnionLexico(JFrame parent) {
        super(parent, "Unión para Analizador Léxico", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        JComboBox<String> comboAfn = new JComboBox<>(AFN.getAllERegular());
        JTextField txtToken = new JTextField(10);
        JButton btnAgregar = new JButton("Agregar al Analizador");

        btnAgregar.addActionListener(e -> {
            String token = txtToken.getText();
            JOptionPane.showMessageDialog(this, "AFN agregado con el Token: " + token);
            txtToken.setText("");
        });

        add(new JLabel("Selecciona AFN:"));
        add(comboAfn);
        add(new JLabel("Token asociado:"));
        add(txtToken);
        add(btnAgregar);
    }
}
