import javax.swing.*;
import java.awt.*;

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
            String er = txtExpresion.getText();
            // Lógica para evaluar la ER
            JOptionPane.showMessageDialog(this, "Convirtiendo la expresión: " + er);
            this.dispose();
        });

        add(label);
        add(txtExpresion);
        add(btnConvertir);
    }
}