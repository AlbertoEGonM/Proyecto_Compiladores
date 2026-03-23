package GUI;

import java.awt.*;
import javax.swing.*;

import AFD.AFD;
import lexico.AnalisisLexico;

public class VentanaProbarLexico extends JDialog {
    public VentanaProbarLexico(JFrame parent) {
        super(parent, "Probar Analizador Léxico", true);
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10)); // BorderLayout es mejor para áreas de texto grandes

        JTextArea txtCodigo = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(txtCodigo); // Permite hacer scroll si el texto es muy largo
        JButton btnProbar = new JButton("Ejecutar Analizador Léxico");


        JTable tabla = new JTable();
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setPreferredScrollableViewportSize(new Dimension(200, 180));
        JScrollPane sp = new JScrollPane(tabla);
        sp.setPreferredSize(new Dimension(200, 200));


        btnProbar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Procesando el texto ingresado para obtener Tokens...");
            
            AnalisisLexico A = new AnalisisLexico(txtCodigo.getText(),AFD.afdAsignado);

            tabla.setModel(new javax.swing.table.DefaultTableModel(A.AnalisisSimple(),new String[] {"Sigma","Token"}));
            //tabla.setPreferredSize(getPreferredSize());
        });

        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Ingresa el código o texto a analizar:"));

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());
        panelInferior.add(sp, BorderLayout.CENTER);
        panelInferior.add(btnProbar, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
}