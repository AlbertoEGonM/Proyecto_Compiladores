package GUI;

import AFN.AFN;
import java.awt.*;
import javax.swing.*;

public class VentanaBasico extends JDialog {

    public VentanaBasico(JFrame parent) {
        super(parent, "Crear AFN Básico", true); // El 'true' la hace modal
        setSize(300, 180);
        setLocationRelativeTo(parent);
        // DISPOSE_ON_CLOSE solo cierra esta ventanita, no todo el programa
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel label = new JLabel("Ingresa el carácter para el AFN:");
        JTextField txtCaracter = new JTextField(5);
        JLabel label2 = new JLabel("Ingresa el carácter 2 para el AFN:");
        JTextField txtCaracter2 = new JTextField(5);
        JButton btnCrear = new JButton("Crear AFN");

        btnCrear.addActionListener(e -> {
            String caracter = txtCaracter.getText();
            String caracter2 = txtCaracter2.getText();
            if(caracter.length() > 1 || caracter2.length() > 1  || caracter.equals(caracter2) ) {
                    JOptionPane.showMessageDialog(this, "Los caracteres deben ser de un solo símbolo y no pueden ser iguales");
                    return;
                }
            if(!caracter.isEmpty() && !caracter2.isEmpty()) {
                
                // Aquí llamarás a tu clase de Lógica: AFN miAfn = new AFN(caracter);
                AFN miAfn = new AFN(caracter.charAt(0),caracter2.charAt(0));

                JOptionPane.showMessageDialog(this, "AFN creado con el carácter: " + caracter + " y " + caracter2+ " con E.R.: " + miAfn.E_Regular);
                this.dispose(); // Cierra la ventana al terminar
            }else if(!caracter.isEmpty() && caracter2.isEmpty()) {
                // Aquí llamarás a tu clase de Lógica: AFN miAfn = new AFN(caracter2);
                AFN miAfn = new AFN(caracter.charAt(0));
                JOptionPane.showMessageDialog(this, "AFN creado con el carácter: " + caracter+ " con E.R.: " + miAfn.E_Regular);
                this.dispose(); // Cierra la ventana al terminar

            }else {
                JOptionPane.showMessageDialog(this, "Por favor ingresa un carácter");
            }
        });

        add(label);
        add(txtCaracter);
        add(label2);
        add(txtCaracter2);
        add(btnCrear);
    }
}