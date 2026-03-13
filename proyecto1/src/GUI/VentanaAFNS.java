package GUI;

import AFN.AFN;
import java.awt.*;
import javax.swing.*;

public class VentanaAFNS extends JDialog {
    public VentanaAFNS(JFrame parent) {
        super(parent, "AFN's:", true);
        setSize(600, 300);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        JTable tabla = new JTable(AFN.getAllInfoAFN(), new String[] {"ID", "E.R", "Alfabeto", "Estados", "Estado Inicial"});
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        JScrollPane sp = new JScrollPane(tabla);  
        panelPrincipal.add(sp, BorderLayout.CENTER);

        add(panelPrincipal);

    }
}
