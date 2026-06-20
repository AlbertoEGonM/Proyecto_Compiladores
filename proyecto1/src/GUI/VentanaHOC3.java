package GUI;

import HOC3.HOC3;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class VentanaHOC3 extends JDialog {
    private final JTextArea txtExpresion;
    private final JTextArea txtResultadoLexico;
    private final JTextArea txtResultadoSintactico;
    private final HOC3 hoc3;

    public VentanaHOC3(JFrame parent) {
        super(parent, "HOC 3", false);
        this.hoc3 = new HOC3();
        setSize(760, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));

        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnLexico = new JButton("Analizar Léxicamente");
        JButton btnSintactico = new JButton("Sintáctico");
        panelAcciones.add(btnLexico);
        panelAcciones.add(btnSintactico);

        txtExpresion = new JTextArea(7, 30);
        txtExpresion.setLineWrap(true);
        txtExpresion.setWrapStyleWord(true);
        panelSuperior.add(new JLabel("Expresión a analizar"), BorderLayout.NORTH);
        panelSuperior.add(new JScrollPane(txtExpresion), BorderLayout.CENTER);
        panelSuperior.add(panelAcciones, BorderLayout.SOUTH);

        JPanel panelResultados = new JPanel(new GridLayout(1, 2, 5, 5));
        txtResultadoLexico = crearAreaResultado();
        txtResultadoSintactico = crearAreaResultado();
        panelResultados.add(crearPanelResultado("Resultado léxico", txtResultadoLexico));
        panelResultados.add(crearPanelResultado("Resultado sintáctico", txtResultadoSintactico));

        add(panelSuperior, BorderLayout.NORTH);
        add(panelResultados, BorderLayout.CENTER);

        btnLexico.addActionListener(e -> ejecutarLexico());
        btnSintactico.addActionListener(e -> ejecutarSintactico());
    }

    private JTextArea crearAreaResultado() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private JPanel crearPanelResultado(String titulo, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.add(new JLabel(titulo), BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private void ejecutarLexico() {
        try {
            txtResultadoLexico.setText(hoc3.analizarLexicamente(txtExpresion.getText()));
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void ejecutarSintactico() {
        try {
            txtResultadoSintactico.setText(hoc3.analizarSintacticamente(txtExpresion.getText()));
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void mostrarError(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error HOC 3", JOptionPane.ERROR_MESSAGE);
    }
}
