package GUI;

import AFN.AFN;
import java.awt.*;
import java.util.Stack;

import javax.print.DocFlavor.STRING;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaAFNS extends JDialog {
    public VentanaAFNS(JFrame parent) {
        super(parent, "AFN's:", true);
        setSize(600, 300);
        setLocationRelativeTo(parent);
        //setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel();
        //panelPrincipal.setLayout(new FlowLayout(FlowLayout.TRAILING , 5, 10));
        String[][] info = AFN.getAllInfoAFN();
        String[] Columnas = new String[] {"ID", "E.R", "Alfabeto", "Estados", "Estado Inicial" , "Estados Finales", "Token", "Selector"};
        DefaultTableModel ModeloTabla = new DefaultTableModel(info, Columnas) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
        };
        JTable tabla = new JTable(ModeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setPreferredScrollableViewportSize(new Dimension(500, 200));

        JScrollPane sp = new JScrollPane(tabla);  
        sp.setPreferredSize(new Dimension(500, 200));

        panelPrincipal.add(new JLabel("Selecciona El AFN a visualizar"));
        panelPrincipal.add(sp);
        JButton botonCapturar = new JButton("Capturar Selección >>");
        
        botonCapturar.addActionListener(e -> {
            int filas = ModeloTabla.getRowCount();

            for (int i = 0; i < filas; i++) {
                // 1. Verificar si el Selector (Checkbox) está marcado
                Boolean seleccionado = (Boolean) ModeloTabla.getValueAt(i, 7);
                
                if (seleccionado != null && seleccionado) {
                    int id = Integer.parseInt((String)ModeloTabla.getValueAt(i, 0).toString());
                    AFN afn = AFN.getAFNById(id);
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    VentanaGrafo vGrafo = new VentanaGrafo(parentFrame, afn);
                    
                    vGrafo.setVisible(true); // Mostramos el grafo resultante
                }
            }

        });

        panelPrincipal.add(botonCapturar);

        add(panelPrincipal);
    }



}
