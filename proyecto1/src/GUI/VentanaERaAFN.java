package GUI;

import java.awt.*;
import javax.swing.*;

import AFN.AFN;
import lexico.ERaAFN;

public class VentanaERaAFN extends JDialog {

    public VentanaERaAFN(JFrame parent) {
        super(parent, "Convertir Expresión Regular a AFN", true);
        setSize(400, 150);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel label = new JLabel("Ingresa la Expresión Regular (ej. (a|b)*abb ):");
        JTextField txtExpresion = new JTextField(20);
        JButton btnConvertir = new JButton("Convertir");

        btnConvertir.addActionListener(e -> {
            // Lógica para evaluar la ER

            AFN f = new AFN();

            new ERaAFN(txtExpresion.getText(),f);
            txtExpresion.setText("");
            //f.E_Regular = er;
            JOptionPane.showMessageDialog(this, "Convirtiendo, expresión resultante: " + f.E_Regular);
        });

        add(label);
        add(txtExpresion);
        add(btnConvertir);
    }
}