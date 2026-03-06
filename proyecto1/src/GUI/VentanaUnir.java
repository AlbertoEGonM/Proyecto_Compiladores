package GUI;

import java.awt.*;
import javax.swing.*;

public class VentanaUnir extends JDialog {

    public VentanaUnir(JFrame parent) {
        super(parent, "Unir AFNs (Algoritmo de Thompson)", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10)); // Una cuadrícula para acomodar elementos

        // Estos datos luego vendrán de una lista real donde guardes tus AFNs en memoria
        String[] afnsDisponibles = {"AFN 1", "AFN 2", "AFN 3"}; 

        JLabel label1 = new JLabel("Selecciona AFN A:");
        JComboBox<String> comboAfn1 = new JComboBox<>(afnsDisponibles);

        JLabel label2 = new JLabel("Selecciona AFN B:");
        JComboBox<String> comboAfn2 = new JComboBox<>(afnsDisponibles);

        JButton btnUnir = new JButton("Unir");
        JButton btnCancelar = new JButton("Cancelar");

        btnUnir.addActionListener(e -> {
            String afnA = (String) comboAfn1.getSelectedItem();
            String afnB = (String) comboAfn2.getSelectedItem();
            // Lógica: afnA.unirCon(afnB);
            JOptionPane.showMessageDialog(this, "Se unieron: " + afnA + " y " + afnB);
            this.dispose();
        });

        btnCancelar.addActionListener(e -> this.dispose());

        add(label1);
        add(comboAfn1);
        add(label2);
        add(comboAfn2);
        add(btnUnir);
        add(btnCancelar);
    }
}