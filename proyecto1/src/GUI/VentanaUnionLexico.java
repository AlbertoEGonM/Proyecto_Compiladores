/*package GUI;

import java.awt.*;
import javax.swing.*;

import AFN.AFN;

public class VentanaUnionLexico extends JDialog {
    public VentanaUnionLexico(JFrame parent) {
        super(parent, "Unión para Analizador Léxico", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        JComboBox<String> comboAfn = new JComboBox<>(AFN.getAllERegular());
        JTextField txtToken = new JTextField(10);
        JButton btnAgregar = new JButton("Agregar al Analizador");

        btnAgregar.addActionListener(e -> {
            String token = txtToken.getText();
            JOptionPane.showMessageDialog(this, "AFN agregado con el Token: " + token);
            txtToken.setText("");
        });

        add(new JLabel("Selecciona AFN:"));
        add(comboAfn);
        add(new JLabel("Token asociado:"));
        add(txtToken);
        add(btnAgregar);
    }
}*/

package GUI;

import AFN.AFN;
import java.awt.*;
import java.util.Stack;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaUnionLexico extends JDialog {
    public VentanaUnionLexico(JFrame parent) {
        super(parent, "Unión para Analizador Léxico", true);
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
                if (columnIndex == 6) return Integer.class; // Para que valide números
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo permitimos editar la columna Token (6) y Selector (7)
                return column == 6 || column == 7;
            }
        };
        JTable tabla = new JTable(ModeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setPreferredScrollableViewportSize(new Dimension(500, 200));

        JScrollPane sp = new JScrollPane(tabla);  
        sp.setPreferredSize(new Dimension(500, 200));

        panelPrincipal.add(new JLabel("Selecciona los AFN's a Unir:"));
        panelPrincipal.add(new JLabel("El token introducido será asignado a todos los estados finales."));
        panelPrincipal.add(sp);
        JButton botonCapturar = new JButton("Capturar Selección >>");
        
        botonCapturar.addActionListener(e -> {
            int filas = ModeloTabla.getRowCount();
            int contados = 0;
            Stack<AFN> pila = new Stack<>();

            for (int i = 0; i < filas; i++) {
                // 1. Verificar si el Selector (Checkbox) está marcado
                Boolean seleccionado = (Boolean) ModeloTabla.getValueAt(i, 7);
                
                if (seleccionado != null && seleccionado) {
                    try {
                        // 2. Obtener el ID del AFN (Columna 0)
                        String idStr = ModeloTabla.getValueAt(i, 0).toString();
                        int id = Integer.parseInt(idStr);

                        // 3. Obtener el Token escrito (Columna 6)
                        Object valorToken = ModeloTabla.getValueAt(i, 6);
                        if (valorToken == null || valorToken.toString().isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Por favor, asigna un token al AFN con ID: " + id);
                            return; 
                        }
                        int token = Integer.parseInt(valorToken.toString());

                        // 4. Buscar el objeto AFN y aplicar el token
                        AFN afn = AFN.getAFNById(id); // O por ID si tienes el método
                        if (afn != null) {
                            afn.SetToken(token);
                            contados++;
                        }
                        pila.add(afn);

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "El token debe ser un número entero válido en la fila " + (i + 1));
                        return;
                    }
                }
            }

            if(pila.size() > 1){
                AFN.UnirAFN(pila);
            }

            if (contados > 0) {
                JOptionPane.showMessageDialog(this, "Se asignaron tokens a " + contados + " AFN(s).");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No has seleccionado ningún AFN.");
            }


            this.dispose();
        });

        panelPrincipal.add(botonCapturar);

        add(panelPrincipal);
    }



}

