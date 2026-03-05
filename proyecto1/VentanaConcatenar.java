import javax.swing.*;
import java.awt.*;

public class VentanaConcatenar extends JDialog {
    public VentanaConcatenar(JFrame parent) {
        super(parent, "Concatenar AFNs", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        String[] afnsDisponibles = {"AFN 1", "AFN 2", "AFN 3"}; 

        JLabel label1 = new JLabel("Selecciona AFN A:");
        JComboBox<String> comboAfn1 = new JComboBox<>(afnsDisponibles);
        JLabel label2 = new JLabel("Selecciona AFN B:");
        JComboBox<String> comboAfn2 = new JComboBox<>(afnsDisponibles);

        JButton btnConcatenar = new JButton("Concatenar");
        JButton btnCancelar = new JButton("Cancelar");

        btnConcatenar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Se concatenaron: " + comboAfn1.getSelectedItem() + " y " + comboAfn2.getSelectedItem());
            this.dispose();
        });
        btnCancelar.addActionListener(e -> this.dispose());

        add(label1); add(comboAfn1);
        add(label2); add(comboAfn2);
        add(btnConcatenar); add(btnCancelar);
    }
}