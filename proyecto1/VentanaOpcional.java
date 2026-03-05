import javax.swing.*;
import java.awt.*;

public class VentanaOpcional extends JDialog {
    public VentanaOpcional(JFrame parent) {
        super(parent, "Aplicar Operación Opcional (?)", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        String[] afns = {"AFN 1", "AFN 2"};
        JComboBox<String> comboAfn = new JComboBox<>(afns);
        JButton btnAplicar = new JButton("Hacer Opcional (?)");

        btnAplicar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Operación Opcional aplicada a: " + comboAfn.getSelectedItem());
            this.dispose();
        });

        add(new JLabel("Selecciona un AFN:"));
        add(comboAfn);
        add(btnAplicar);
    }
}