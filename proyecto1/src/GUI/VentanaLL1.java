package GUI;

import AFD.AFD;
import lexico.AnalisisLexico;
import sintactico.LL1;
import sintactico.Gramatica;
import sintactico.Simbolo;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VentanaLL1 extends JDialog {

    // Componentes de entrada y control visual
    private JTextArea txtGramatica;
    private JTextField txtSigma;
    private JLabel lblRutaAFD;
    
    // Modelos para las tablas Swing
    private final DefaultTableModel modeloLexico;
    private final DefaultTableModel modeloTerminales;
    private final DefaultTableModel modeloLL1;
    
    private final JTable tablaTerminales;

    // Entidades lógicas backend
    private AFD afdCargado = null;
    private Gramatica gramaticaProcesada = null;
    private List<Simbolo> listaTerminalesActuales = new ArrayList<>();

    // String ejemplo de gramatica
    private final String Ejemplo = """
                                    E->T E';
                                    E'-> + T E' | -T E' | epsilon;
                                    T-> F T';
                                    T'-> * F T' | / F T' | epsilon;
                                    F-> (E)| num;""";

    public VentanaLL1(JFrame parent) {
        super(parent,"Analizador Sintáctico LL(1) - Interfaz Gráfica",true);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(parent);
        
        // 1. Inicializar Menú Superior (Carga del AFD)
        setJMenuBar(crearBarraMenu());

        // 2. Panel Superior: Entradas de Texto (Gramática y Cadena Sigma)
        JPanel panelEntradas = new JPanel(new BorderLayout(5, 5));
        panelEntradas.setBorder(BorderFactory.createTitledBorder(" 1. Parámetros de Entrada "));
        
        txtGramatica = new JTextArea(8, 50);
        txtGramatica.setFont(new Font("Monospaced", Font.PLAIN, 13));
        // Gramática muestra de ejemplo inicial tal como solicitaste
        txtGramatica.setText(Ejemplo); 
        JScrollPane spGramatica = new JScrollPane(txtGramatica);
        
        JPanel panelSigmaYAFD = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSigmaYAFD.add(new JLabel("Cadena Sigma a analizar:"));
        txtSigma = new JTextField(30);
        panelSigmaYAFD.add(txtSigma);
        
        lblRutaAFD = new JLabel("AFD Binario: No seleccionado (*.afnd)");
        lblRutaAFD.setForeground(Color.RED);
        panelSigmaYAFD.add(lblRutaAFD);

        panelEntradas.add(new JLabel(" Introduzca la Gramática Textual:"), BorderLayout.NORTH);
        panelEntradas.add(spGramatica, BorderLayout.CENTER);
        panelEntradas.add(panelSigmaYAFD, BorderLayout.SOUTH);

        // 3. Panel Central: Pestañas de Trabajo para las Tablas solicitadas
        JTabbedPane pestañasProceso = new JTabbedPane();

        // Pestaña I: Mapeo manual de Tokens Enteros a Símbolos Terminales
        JPanel panelTerminales = new JPanel(new BorderLayout(5, 5));
        modeloTerminales = new DefaultTableModel(new String[]{"Símbolo Terminal", "Token Asignado (Tipo Entero)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Solo la columna del número de Token es editable manualmente
            }
        };
        tablaTerminales = new JTable(modeloTerminales);
        panelTerminales.add(new JScrollPane(tablaTerminales), BorderLayout.CENTER);
        
        JPanel panelBotonesTerminales = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnExtraerTerminales = new JButton("1. Analizar Gramática y Extraer Terminales");
        btnExtraerTerminales.addActionListener(e -> extraerTerminalesDeGramatica());
        panelBotonesTerminales.add(btnExtraerTerminales);
        panelTerminales.add(panelBotonesTerminales, BorderLayout.SOUTH);
        pestañasProceso.addTab("Mapeo de Tokens Manuales", panelTerminales);

        // Pestaña II: Tabla de Análisis Léxico Simple
        JPanel panelLexico = new JPanel(new BorderLayout(5, 5));
        modeloLexico = new DefaultTableModel(new String[]{"Lexema Detectado", "Token"}, 0);
        JTable tablaLexico = new JTable(modeloLexico);
        panelLexico.add(new JScrollPane(tablaLexico), BorderLayout.CENTER);
        
        JPanel panelBotonesLexico = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAnalisisSimple = new JButton("2. Ejecutar Análisis Léxico Simple");
        btnAnalisisSimple.addActionListener(e -> ejecutarAnalisisLexicoSimple());
        panelBotonesLexico.add(btnAnalisisSimple);
        panelLexico.add(panelBotonesLexico, BorderLayout.SOUTH);
        pestañasProceso.addTab("Resultado Análisis Léxico", panelLexico);

        // Pestaña III: Tabla de Rastreo Sintáctico LL(1)
        JPanel panelLL1 = new JPanel(new BorderLayout(5, 5));
        modeloLL1 = new DefaultTableModel(new String[]{"Pila", "Entrada Restante", "Acción / Regla Aplicada"}, 0);
        JTable tablaLL1 = new JTable(modeloLL1);
        panelLL1.add(new JScrollPane(tablaLL1), BorderLayout.CENTER);
        
        JPanel panelBotonesLL1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAnalisisLL1 = new JButton("3. Ejecutar Análisis Sintáctico LL(1)");
        btnAnalisisLL1.addActionListener(e -> ejecutarAnalisisSintacticoLL1());
        panelBotonesLL1.add(btnAnalisisLL1);
        panelLL1.add(panelBotonesLL1, BorderLayout.SOUTH);
        pestañasProceso.addTab("Resultado Análisis LL(1)", panelLL1);

        // 4. Agregar componentes al contenedor principal
        setLayout(new BorderLayout(10, 10));
        add(panelEntradas, BorderLayout.NORTH);
        add(pestañasProceso, BorderLayout.CENTER);
    }

    /**
     * Construye el menú que contiene la lógica exacta de JFileChooser que proveíste.
     */
    private JMenuBar crearBarraMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuConfig = new JMenu("Configuración");

        JMenuItem cargarAFDItem = new JMenuItem("Cargar AFD de un Bin");
        cargarAFDItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Cargar AFD");
            
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos bin, AFD (*.afnd)", "afnd");
            fileChooser.setFileFilter(filter);
            
            int userSelection = fileChooser.showOpenDialog(this); // Cambiado showSaveDialog a showOpenDialog para carga
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToCharge = fileChooser.getSelectedFile();
                String filePath = fileToCharge.getAbsolutePath();
                
                if (!filePath.toLowerCase().endsWith(".afnd")) {
                    filePath += ".afnd";
                }

                if (!fileToCharge.exists() || !fileToCharge.canRead()) {
                    JOptionPane.showMessageDialog(this, "No se puede leer el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Mapeo nativo a tu deserializador de AFDs
                this.afdCargado = AFD.AbrirArchivoBin(filePath);
                
                if (this.afdCargado != null) {
                    lblRutaAFD.setText("AFD: " + fileToCharge.getName());
                    lblRutaAFD.setForeground(new Color(0, 128, 0)); // Color verde indicando éxito
                    JOptionPane.showMessageDialog(this, "Archivo de Autómata (AFD) cargado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al deserializar el archivo binario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        menuConfig.add(cargarAFDItem);
        menuBar.add(menuConfig);
        return menuBar;
    }

    /**
     * Acción 1: Instancia tu clase 'Gramatica' con el texto del JTextArea,
     * extrayendo automáticamente el conjunto 'SimbolosTerminales' resultante del parser recursivo.
     */
    private void extraerTerminalesDeGramatica() {
        String textoGramatica = txtGramatica.getText();
        if (textoGramatica.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor introduzca las reglas de la gramática textual primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Se limpia la memoria visual previa
        modeloTerminales.setRowCount(0);
        listaTerminalesActuales.clear();

        try {
            // Invoca de manera nativa a tu constructor: pasándole el texto al DescensoRecursivo
            this.gramaticaProcesada = new Gramatica(textoGramatica);

            // Poblar la tabla editable con los terminales descubiertos por tu parser
            for (Simbolo simb : this.gramaticaProcesada.getSimbolosTerminales()) {
                listaTerminalesActuales.add(simb);
                // Añade la fila con el nombre del símbolo y deja la celda del token vacía para entrada manual
                modeloTerminales.addRow(new Object[]{simb.Nombre, ""});
            }

            JOptionPane.showMessageDialog(this, "Gramática procesada correctamente.\nPor favor, introduzca manualmente los tokens enteros para cada símbolo terminal en la tabla.");
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Error al parsear la estructura gramatical: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Acción 2: Realiza el análisis léxico simple sobre la cadena 'sigma'
     * utilizando el AFD binario previamente cargado.
     */
    private void ejecutarAnalisisLexicoSimple() {
        if (afdCargado == null) {
            JOptionPane.showMessageDialog(this, "Debe cargar el AFD (.afnd) desde el menú de Configuración primero.", "Falta Autómata", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String sigma = txtSigma.getText();
        modeloLexico.setRowCount(0);

        // Instancia tu AnalisisLexico usando el constructor (String, AFD) que provee tu archivo
        AnalisisLexico analfLex = new AnalisisLexico(sigma, afdCargado);
        
        // Invoca tu método AnalisisSimple() que retorna un String[][]
        String[][] tablaResultado = analfLex.AnalisisSimple();

        if (tablaResultado != null) {
            for (String[] fila : tablaResultado) {
                modeloLexico.addRow(fila);
            }
        }
    }

    /**
     * Acción 3: Recolecta los tokens enteros digitados manualmente por el usuario,
     * configura las matrices VT/VNT de la clase LL1 y ejecuta el análisis sintáctico.
     */
    private void ejecutarAnalisisSintacticoLL1() {
        if (gramaticaProcesada == null) {
            JOptionPane.showMessageDialog(this, "Primero debe ejecutar el análisis y extracción de la gramática (Paso 1).", "Error de Flujo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (afdCargado == null) {
            JOptionPane.showMessageDialog(this, "Falta cargar el AFD binario para el control léxico.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (modeloTerminales.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No existen símbolos terminales sobre los cuales evaluar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Forzar a Swing a detener la edición de cualquier celda activa (asegura que el último token escrito se guarde)
        if (tablaTerminales.getCellEditor() != null) {
            tablaTerminales.getCellEditor().stopCellEditing();
        }

        List<Integer> tokensManuales = new ArrayList<>();
        List<Simbolo> simbolosAsociados = new ArrayList<>();

        // Validar e interpretar las entradas numéricas manuales ingresadas en la JTable
        try {
            for (int i = 0; i < modeloTerminales.getRowCount(); i++) {
                Object valorCelda = modeloTerminales.getValueAt(i, 1);
                if (valorCelda == null || valorCelda.toString().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Falta asignar un Token entero al símbolo: " + modeloTerminales.getValueAt(i, 0), "Token Vacío", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int tokenEntero = Integer.parseInt(valorCelda.toString().trim());
                tokensManuales.add(tokenEntero);
                simbolosAsociados.add(listaTerminalesActuales.get(i));
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Error de formato: Los tokens introducidos manualmente deben ser números enteros válidos.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        modeloLL1.setRowCount(0);

        try {
            // Instanciar el analizador sintáctico LL1 enviándole la gramática y el análisis léxico actual
            AnalisisLexico lexicoSintax = new AnalisisLexico(txtSigma.getText(), afdCargado);
            LL1 analizadorLL1 = new LL1(gramaticaProcesada, lexicoSintax);

            // Ejecutar los métodos de inicialización obligatorios presentes en tu clase LL1.java
            boolean vtConstruido = analizadorLL1.CreateVT(simbolosAsociados, tokensManuales);
            if (!vtConstruido) {
                JOptionPane.showMessageDialog(this, "Ocurrió un inconveniente al armar la matriz asociativa de Terminales (VT).", "Error Interno", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            analizadorLL1.CreateVNT();
            analizadorLL1.init_Table(); // Genera la matriz predictiva TablaLL

            // Ejecutar el motor sintáctico y obtener el rastro de la matriz de transiciones
            String[][] registrosProceso = analizadorLL1.AnalizarYRegistrar();

            if (registrosProceso == null || registrosProceso.length == 0) {
                modeloLL1.addRow(new String[]{"-", "-", "El análisis falló inmediatamente o la cadena no pertenece a la gramática."});
                return;
            }

            // Volcar las filas del historial en la JTable de resultados sintácticos.
            // Control defensivo: Dado que el método 'getArray()' en FilaProcesoLL1 asigna la Pila en la posición 1,
            // la Entrada en la 2 y la Acción en la 3, realizamos el ajuste de índices para evitar un desbordamiento.
            for (String[] fila : registrosProceso) {
                if (fila.length >= 4) {
                    modeloLL1.addRow(new Object[]{fila[1], fila[2], fila[3]});
                } else {
                    modeloLL1.addRow(fila);
                }
            }

        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Error durante la ejecución del algoritmo LL(1):\n" + ex.getMessage(), "Error Sintáctico", JOptionPane.ERROR_MESSAGE);
        }
    }


}