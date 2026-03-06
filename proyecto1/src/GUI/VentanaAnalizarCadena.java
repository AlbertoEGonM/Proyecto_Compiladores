package GUI;

import java.awt.*;
import javax.swing.*;

public class VentanaAnalizarCadena extends JDialog {
    public VentanaAnalizarCadena(JFrame parent) {
        super(parent, "Analizar una Cadena", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        String[] afds = {"AFD 1", "AFD 2"}; // Usualmente se evalúa en un AFD
        JComboBox<String> comboAfd = new JComboBox<>(afds);
        JTextField txtCadena = new JTextField(20);
        JButton btnAnalizar = new JButton("Analizar");

        btnAnalizar.addActionListener(e -> {
            String cadena = txtCadena.getText();
            JOptionPane.showMessageDialog(this, "Analizando la cadena: '" + cadena + "' en " + comboAfd.getSelectedItem());
        });

        add(new JLabel("Selecciona el AFD:"));
        add(comboAfd);
        add(new JLabel("Ingresa la cadena a evaluar:"));
        add(txtCadena);
        add(btnAnalizar);
    }
}