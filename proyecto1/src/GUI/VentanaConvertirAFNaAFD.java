package GUI;

import AFD.AFD;
import AFN.AFN;
import java.awt.*;
import java.util.TreeSet;
import javax.swing.*;

public class VentanaConvertirAFNaAFD extends JDialog {
    public VentanaConvertirAFNaAFD(JFrame parent) {
        super(parent, "Convertir AFN a AFD", true);
        setSize(600, 550);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JTable tabla = new JTable();
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane sp = new JScrollPane(tabla);  

        JComboBox<String> comboAfn = new JComboBox<>(AFN.getAllERegular());
        JButton btnConvertir = new JButton("Convertir a AFD");

        btnConvertir.addActionListener(e -> {
            AFN afnSeleccionado = AFN.getAFNByER((String) comboAfn.getSelectedItem());
            if(afnSeleccionado != null) {
                AFD.afdAsignado = new AFD(afnSeleccionado);
                // Aquí podrías mostrar el AFD resultante en una nueva ventana o guardarlo
                JOptionPane.showMessageDialog(this, "AFN '" + comboAfn.getSelectedItem() + "' convertido a AFD con éxito." + "\nNúmero de estados en el AFD: " + AFD.afdAsignado.numEstadosSj);
                tabla.setModel(new javax.swing.table.DefaultTableModel(
                    AFD.getSumTable(),
                    CabeceraTabla()
                ));
                tabla.setPreferredSize(getPreferredSize());
            } else {
                JOptionPane.showMessageDialog(this, "Error al convertir el AFN seleccionado.");
            }
        });

        add(new JLabel("Selecciona el AFN a convertir:"));
        add(comboAfn);
        add(btnConvertir);
        this.add(sp);
    }

    private static String[] CabeceraTabla(){
        String [] cabecera = new String[AFD.afdAsignado.Alfabeto.size() + 2];
        cabecera[0] = "Estado";
        TreeSet<Character> alfabeto = AFD.getAlfabeto();
        int i = 1;
        for(Character c : alfabeto){
            cabecera[i] = String.valueOf(c);
            i++;
        }
        
        cabecera[AFD.afdAsignado.Alfabeto.size() + 1] = "Token";
        
        return cabecera;
    };

}