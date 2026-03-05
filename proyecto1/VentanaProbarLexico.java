import javax.swing.*;
import java.awt.*;

public class VentanaProbarLexico extends JDialog {
    public VentanaProbarLexico(JFrame parent) {
        super(parent, "Probar Analizador Léxico", true);
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10)); // BorderLayout es mejor para áreas de texto grandes

        JTextArea txtCodigo = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(txtCodigo); // Permite hacer scroll si el texto es muy largo
        JButton btnProbar = new JButton("Ejecutar Analizador Léxico");

        btnProbar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Procesando el texto ingresado para obtener Tokens...");
        });

        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Ingresa el código o texto a analizar:"));

        JPanel panelInferior = new JPanel();
        panelInferior.add(btnProbar);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
}