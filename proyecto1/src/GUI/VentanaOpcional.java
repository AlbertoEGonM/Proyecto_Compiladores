package GUI;

import AFN.AFN;
import java.awt.*;
import javax.swing.*;

public class VentanaOpcional extends JDialog {
    public VentanaOpcional(JFrame parent) {
        super(parent, "Aplicar Operación Opcional (?)", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JComboBox<String> comboAfn = new JComboBox<>(AFN.getAllERegular());
        JButton btnAplicar = new JButton("Hacer Opcional (?)");

        btnAplicar.addActionListener(e -> {
            AFN afn = AFN.getAFNByER((String) comboAfn.getSelectedItem());
            if(afn != null) {
                afn.CerrOpcional();
                JOptionPane.showMessageDialog(this, "Operación Opcional aplicada a: " + comboAfn.getSelectedItem() + "\nNuevo AFN: " + afn.IdAFN + "\nER: " + afn.E_Regular);
                this.dispose();
            }else{
                JOptionPane.showMessageDialog(this, "Error al obtener el AFN seleccionado");
            }
        });

        add(new JLabel("Selecciona un AFN:"));
        add(comboAfn);
        add(btnAplicar);
    }
}