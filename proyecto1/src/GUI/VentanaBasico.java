package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VentanaBasico extends JDialog {

    public VentanaBasico(JFrame parent) {
        super(parent, "Creación de AFN básico", true);
        setSize(550, 350);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 

        //    Centro (CENTER) para la imagen
        setLayout(new BorderLayout(10, 10));

        // --- PANEL IZQUIERDO (CAMPOS DE TEXTO) ---
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new GridLayout(6, 2, 5, 15)); // 6 filas, 2 columnas
        panelIzquierdo.setBorder(new EmptyBorder(20, 20, 20, 10)); // Agregamos márgenes

        JCheckBox chkAscii = new JCheckBox("Usar código ascii");
        
        JLabel lblInf = new JLabel("Caracter Inf");
        JTextField txtInf = new JTextField(5);
        
        JLabel lblSup = new JLabel("Caracter Sup");
        JTextField txtSup = new JTextField(5);
        
        JLabel lblId = new JLabel("Id del AFN");
        JTextField txtId = new JTextField(5);
        
        JButton btnCrear = new JButton("Crear AFN");

        // Añadir componentes al panel izquierdo
        // Fila 1
        panelIzquierdo.add(chkAscii);
        panelIzquierdo.add(new JLabel("")); // Celda vacía para rellenar
        
        // Fila 2
        panelIzquierdo.add(lblInf);
        panelIzquierdo.add(txtInf);
        
        // Fila 3
        panelIzquierdo.add(lblSup);
        panelIzquierdo.add(txtSup);
        
        // Fila 4
        panelIzquierdo.add(lblId);
        panelIzquierdo.add(txtId);
        
        // Fila 5 y 6 vacías para espaciado
        panelIzquierdo.add(new JLabel("")); panelIzquierdo.add(new JLabel("")); 
        panelIzquierdo.add(btnCrear); panelIzquierdo.add(new JLabel("")); 

        // --- PANEL DERECHO (IMAGEN) ---
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBorder(new EmptyBorder(20, 10, 20, 20)); // Márgenes

        // Cargar la imagen del diagrama
        ImageIcon iconDiagrama = new ImageIcon("images/basico.png"); 
        
        // Mostrar la imagen en un JLabel
        JLabel lblDiagrama = new JLabel();
        lblDiagrama.setIcon(iconDiagrama);
        lblDiagrama.setHorizontalAlignment(JLabel.CENTER); // Centrar la imagen

        // Añadir el JLabel al panel derecho
        panelDerecho.add(lblDiagrama, BorderLayout.CENTER);

        // --- ENSAMBLAR TODO ---
        add(panelIzquierdo, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);
        
        // Lógica del botón Crear (igual que antes, pero puedes expandir con los nuevos campos)
        btnCrear.addActionListener(e -> {
            String carInf = txtInf.getText();
            String carSup = txtSup.getText();
            String id = txtId.getText();
            boolean useAscii = chkAscii.isSelected();

            if(!carInf.isEmpty() && !carSup.isEmpty() && !id.isEmpty()) {
                
                // AFN miAfn = new AFN(id, carInf, carSup, useAscii);
                JOptionPane.showMessageDialog(this, "AFN '" + id + "' creado para el rango [" + carInf + ", " + carSup + "]");
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Por favor completa todos los campos");
            }
        });
    }
}