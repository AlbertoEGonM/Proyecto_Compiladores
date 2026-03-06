package GUI;

import java.awt.*;
import javax.swing.*;

public class VentanaBasico extends JDialog {

    public VentanaBasico(JFrame parent) {
        super(parent, "Crear AFN Básico", true); // El 'true' la hace modal
        setSize(300, 150);
        setLocationRelativeTo(parent);
        // DISPOSE_ON_CLOSE solo cierra esta ventanita, no todo el programa
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel label = new JLabel("Ingresa el carácter para el AFN:");
        JTextField txtCaracter = new JTextField(5);
        JButton btnCrear = new JButton("Crear AFN");

        btnCrear.addActionListener(e -> {
            String caracter = txtCaracter.getText();
            if(!caracter.isEmpty()) {
                // Aquí llamarás a tu clase de Lógica: AFN miAfn = new AFN(caracter);
                JOptionPane.showMessageDialog(this, "AFN creado con el carácter: " + caracter);
                this.dispose(); // Cierra la ventana al terminar
            } else {
                JOptionPane.showMessageDialog(this, "Por favor ingresa un carácter");
            }
        });

        add(label);
        add(txtCaracter);
        add(btnCrear);
    }
}