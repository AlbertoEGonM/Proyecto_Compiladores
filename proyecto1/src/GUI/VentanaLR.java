package GUI;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import lexico.AnalisisLexico;
import sintactico.*;

public class VentanaLR extends JDialog {

    // Componentes de la UI
    private JTextField txtRutaAFD;
    private JTextArea txtGramatica;
    private JTable tblTokens;
    private DefaultTableModel modelTokens;
    private JTextField txtCadenaLéxica;
    private JTextArea txtResultadoLéxico;
    private JTextField txtSigmaSintactica;
    private JTable tblBitacora;
    private DefaultTableModel modelBitacora;

    // Instancia del Motor Sintáctico
    private LR0 motorLR;
    private String rutaAFDSeleccionada = "";

    public VentanaLR(JFrame parent) {
        super(parent,"Analizador Sintáctico LR(0) / SLR(1)",true);
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 750);
        this.setLocationRelativeTo(parent);
        
        // Inicializar el contenedor principal con un BoxLayout vertical scrollable
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- SECCIÓN 1: CONFIGURACIÓN DE GRAMÁTICA Y AFD ---
        JPanel pnlConfig = new JPanel(new GridBagLayout());
        pnlConfig.setBorder(BorderFactory.createTitledBorder("1. Inicializar Gramática y AFD"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        pnlConfig.add(new JLabel("Archivo AFD:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtRutaAFD = new JTextField();
        txtRutaAFD.setEditable(false);
        pnlConfig.add(txtRutaAFD, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0;
        JButton btnBuscarAFD = new JButton("Buscar...");
        pnlConfig.add(btnBuscarAFD, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlConfig.add(new JLabel("Gramática:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weighty = 1.0;
        txtGramatica = new JTextArea(4, 40);
        pnlConfig.add(new JScrollPane(txtGramatica), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        JButton btnCargarGramatica = new JButton("Cargar Gramática y Extraer Terminales");
        pnlConfig.add(btnCargarGramatica, gbc);
        panelPrincipal.add(pnlConfig);

        // --- SECCIÓN 2: ASIGNACIÓN DE TOKENS ---
        JPanel pnlTokens = new JPanel(new BorderLayout());
        pnlTokens.setBorder(BorderFactory.createTitledBorder("2. Relación Símbolo - Token (Modificable)"));
        modelTokens = new DefaultTableModel(new Object[]{"Símbolo Terminal", "Token (Entero)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Solo la columna del token es editable
            }
        };
        tblTokens = new JTable(modelTokens);
        pnlTokens.add(new JScrollPane(tblTokens), BorderLayout.CENTER);
        
        JButton btnConstruirTablas = new JButton("Construir Estructuras y Tabla LR");
        pnlTokens.add(btnConstruirTablas, BorderLayout.SOUTH);
        pnlTokens.setPreferredSize(new Dimension(850, 150));
        panelPrincipal.add(pnlTokens);

        // --- SECCIÓN 3: ANÁLISIS LÉXICO PREVIO ---
        JPanel pnlLexico = new JPanel(new GridBagLayout());
        pnlLexico.setBorder(BorderFactory.createTitledBorder("3. Análisis Léxico de Prueba (Verificación de Asociación)"));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.gridx = 0; gbc.gridy = 0;
        pnlLexico.add(new JLabel("Cadena de Prueba:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCadenaLéxica = new JTextField();
        pnlLexico.add(txtCadenaLéxica, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0;
        JButton btnAnalizarLexico = new JButton("Escanear");
        pnlLexico.add(btnAnalizarLexico, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        txtResultadoLéxico = new JTextArea(3, 40);
        txtResultadoLéxico.setEditable(false);
        pnlLexico.add(new JScrollPane(txtResultadoLéxico), gbc);
        panelPrincipal.add(pnlLexico);

        // --- SECCIÓN 4: ANÁLISIS SINTÁCTICO ---
        JPanel pnlSintactico = new JPanel(new BorderLayout());
        pnlSintactico.setBorder(BorderFactory.createTitledBorder("4. Análisis Sintáctico de Cadena Sigma"));
        
        JPanel pnlInputSigma = new JPanel(new BorderLayout(5, 5));
        pnlInputSigma.add(new JLabel("Cadena Sigma ($ final implícito): "), BorderLayout.WEST);
        txtSigmaSintactica = new JTextField();
        pnlInputSigma.add(txtSigmaSintactica, BorderLayout.CENTER);
        JButton btnAnalizarSintactico = new JButton("Analizar Sintácticamente");
        pnlInputSigma.add(btnAnalizarSintactico, BorderLayout.EAST);
        pnlSintactico.add(pnlInputSigma, BorderLayout.NORTH);

        modelBitacora = new DefaultTableModel(new Object[]{"Pila", "Sigma (Entrada)", "Acción"}, 0);
        tblBitacora = new JTable(modelBitacora);
        pnlSintactico.add(new JScrollPane(tblBitacora), BorderLayout.CENTER);
        pnlSintactico.setPreferredSize(new Dimension(850, 220));
        panelPrincipal.add(pnlSintactico);

        // Agregar scroll global a la ventana
        this.add(new JScrollPane(panelPrincipal));

        // --- EVENTOS / LÓGICA DE CONTROL ---

        // 1. Botón Buscar AFD
        btnBuscarAFD.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleccionar archivo binario de AFD");
            int seleccion = fileChooser.showOpenDialog(this);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                rutaAFDSeleccionada = archivo.getAbsolutePath();
                txtRutaAFD.setText(archivo.getName());
            }
        });

        // 2. Botón Cargar Gramática
        btnCargarGramatica.addActionListener(e -> {
            String textoGramatica = txtGramatica.getText().trim();
            if (textoGramatica.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor introduce una gramática.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                // Instanciamos el motor LR0 pasándole la gramática
                motorLR = new LR0(textoGramatica);
                
                // Limpiar tabla de tokens previa
                modelTokens.setRowCount(0);
                
                // Cargar los símbolos terminales de la gramática a la JTable
                for (Simbolo simb : motorLR.Gram.SimbolosTerminales) {
                    modelTokens.addRow(new Object[]{simb.Nombre, ""}); // El token se deja en blanco para que lo asigne el usuario
                }
                JOptionPane.showMessageDialog(this, "Gramática cargada. Asigne los tokens correspondientes en la tabla.");
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(this, "Error al procesar la gramática: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 3. Botón Construir Tablas
        btnConstruirTablas.addActionListener(e -> {
            if (motorLR == null) {
                JOptionPane.showMessageDialog(this, "Primero debe cargar una gramática válida.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (rutaAFDSeleccionada.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione el archivo binario del AFD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar y extraer los tokens desde la interfaz
            List<Simbolo> listaSimbolos = new ArrayList<>();
            List<Integer> listaTokens = new ArrayList<>();

            try {
                for (int i = 0; i < modelTokens.getRowCount(); i++) {
                    String nombreSimb = (String) modelTokens.getValueAt(i, 0);
                    String valorTokenStr = (String) modelTokens.getValueAt(i, 1);

                    if (valorTokenStr == null || valorTokenStr.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Falta asignar el token para el símbolo: " + nombreSimb, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int tokenVal = Integer.parseInt(valorTokenStr.trim());
                    
                    // Buscar la instancia del símbolo original en la gramática
                    Simbolo simbOriginal = motorLR.Gram.SimbolosTerminales.stream()
                            .filter(s -> s.Nombre.equals(nombreSimb))
                            .findFirst().orElse(new Simbolo(nombreSimb, true));

                    listaSimbolos.add(simbOriginal);
                    listaTokens.add(tokenVal);
                }

                // Inyectar el Analizador Léxico al motor con la ruta del AFD elegida
                motorLR.Lex = new AnalisisLexico(null, rutaAFDSeleccionada);

                // Ejecución secuencial de los métodos de inicialización requeridos
                boolean vtCreado = motorLR.CreateVT(listaSimbolos, listaTokens);
                if (!vtCreado) {
                    JOptionPane.showMessageDialog(this, "Error al mapear la matriz VT.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                motorLR.CreateVNT(); // Genera array Vnt
                motorLR.CreateV();   // Genera conjunto V
                motorLR.init_table(); // Genera la matriz de transiciones y reducciones LR/SLR

                JOptionPane.showMessageDialog(this, "Estructuras Vnt, V y Tabla LR generadas correctamente. ¡Listo para analizar!");

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Los tokens introducidos deben ser valores enteros numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(this, "Error en la construcción de tablas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 4. Botón Análisis Léxico de Prueba (Escanear)
        btnAnalizarLexico.addActionListener(e -> {
            if (rutaAFDSeleccionada.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Se requiere seleccionar un archivo AFD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String cadenaPrueba = txtCadenaLéxica.getText();
            if (cadenaPrueba.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduzca una cadena para realizar el escaneo léxico.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Instanciamos un AnalisisLexico temporal para no alterar el estado del flujo sintáctico
                AnalisisLexico lexPrueba = new AnalisisLexico(cadenaPrueba, rutaAFDSeleccionada);
                StringBuilder sb = new StringBuilder();
                int token;
                
                // Bucle de análisis simple para ver qué tokens se asocian a qué lexemas
                while ((token = lexPrueba.yylex()) != 0 && token != -1) { // 0 o -1 dependiendo de tu fin de token
                    sb.append("Lexema: '").append(lexPrueba.Lexema)
                      .append("' -> Asociado al Token ID: ").append(token).append("\n");
                    if(token == 0) break; // Si llegas al token de fin por seguridad
                }
                txtResultadoLéxico.setText(sb.toString());
            } catch (Exception ex) {
                txtResultadoLéxico.setText("Error en el escaneo léxico:\n" + ex.getMessage());
            }
        });

        // 5. Botón Análisis Sintáctico (Bitácora)
        btnAnalizarSintactico.addActionListener(e -> {
            if (motorLR == null || motorLR.TablaLR == null) {
                JOptionPane.showMessageDialog(this, "La tabla LR no ha sido inicializada aún.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String sigma = txtSigmaSintactica.getText().trim();
            if (sigma.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor introduce la cadena sigma a analizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Limpiar la bitácora anterior
                modelBitacora.setRowCount(0);

                // Invocar al método que modificamos previamente
                String[][] bitacoraResultado = motorLR.AnalisisLR(sigma);

                if (bitacoraResultado != null) {
                    for (String[] fila : bitacoraResultado) {
                        modelBitacora.addRow(fila);
                    }
                    
                    // Verificar la última acción para determinar el éxito
                    if(bitacoraResultado.length > 0) {
                        String ultimaAccion = bitacoraResultado[bitacoraResultado.length - 1][2];
                        if (ultimaAccion.equals("acc") || modelBitacora.getRowCount() > 1 && tblBitacora.getValueAt(tblBitacora.getRowCount()-2, 2).equals("acc")) {
                             JOptionPane.showMessageDialog(this, "¡Cadena aceptada sintácticamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                             JOptionPane.showMessageDialog(this, "Error Sintáctico: Cadena rechazada.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado durante el análisis: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }

    /*public static void main(String[] args) {
        // Ejecución de la UI en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new FrameAnalizadorLR().setVisible(true);
        });
    }*/
}