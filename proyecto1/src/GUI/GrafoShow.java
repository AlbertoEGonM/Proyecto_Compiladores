package GUI;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GrafoShow extends JFrame {

    public GrafoShow(){
        JFrame Ventana = new JFrame();
        Ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Ventana.setSize(800,500);
        Ventana.setTitle("Grafo");
        Ventana.setLocationRelativeTo(null);
        Ventana.setVisible(true);
        JPanel panel = new JPanel();
        Ventana.add(panel);
        panel.setBounds(20, 20, 600, 300);
        panel.setBackground(Color.BLUE);
        
        
    }


    public static void main(String[] args) {
        new GrafoShow();
    }
}
