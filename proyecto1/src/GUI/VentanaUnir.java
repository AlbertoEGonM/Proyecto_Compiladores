package GUI;

import java.awt.*;
import javax.swing.*;

import AFN.AFN;

public class VentanaUnir extends JDialog {

    public VentanaUnir(JFrame parent) {
        super(parent, "Unir AFNs (Algoritmo de Thompson)", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10)); // Una cuadrícula para acomodar elementos


        // Estos datos luego vendrán de una lista real donde guardes tus AFNs en memoria

        JLabel label1 = new JLabel("Selecciona AFN A:");
        JComboBox<String> comboAfn1 = new JComboBox<>(AFN.getAllERegular());

        JLabel label2 = new JLabel("Selecciona AFN B:");
        JComboBox<String> comboAfn2 = new JComboBox<>(AFN.getAllERegular());

        JButton btnUnir = new JButton("Unir");
        JButton btnCancelar = new JButton("Cancelar");

        btnUnir.addActionListener(e -> {
            String afnA = (String) comboAfn1.getSelectedItem();
            String afnB = (String) comboAfn2.getSelectedItem();
            if(afnA.equals(afnB)) {
                JOptionPane.showMessageDialog(this, "No puedes unir el mismo AFN consigo mismo");
            } else {
                AFN afn1 = AFN.getAFNByER(afnA); // Supongamos que esta función devuelve el AFN correspondiente al nombre seleccionado
                AFN afn2 = AFN.getAFNByER(afnB);
                if(afn1 == null || afn2 == null) {
                    JOptionPane.showMessageDialog(this, "Error al obtener los AFNs seleccionados");
                }else{
                    afn1.UnirAFN(afn2); // Aquí llamas a tu método de unión en la clase AFN
                    JOptionPane.showMessageDialog(this, "Se unieron: " + afnA + " y " + afnB + "\nNuevo AFN con ID: " + afn1.IdAFN + " : E.R.: " + afn1.E_Regular);
                    this.dispose();
                }
                
            }

            // Lógica: afnA.unirCon(afnB);
            
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