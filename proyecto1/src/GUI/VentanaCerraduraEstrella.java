import javax.swing.*;
import java.awt.*;

public class VentanaCerraduraEstrella extends JDialog {
    public VentanaCerraduraEstrella(JFrame parent) {
        super(parent, "Aplicar Cerradura de Kleene (*)", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        String[] afns = {"AFN 1", "AFN 2"};
        JComboBox<String> comboAfn = new JComboBox<>(afns);
        JButton btnAplicar = new JButton("Aplicar Cerradura *");

        btnAplicar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Cerradura * aplicada a: " + comboAfn.getSelectedItem());
            this.dispose();
        });

        add(new JLabel("Selecciona un AFN:"));
        add(comboAfn);
        add(btnAplicar);
    }
}