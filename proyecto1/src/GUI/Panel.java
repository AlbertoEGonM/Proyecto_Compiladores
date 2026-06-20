package GUI;

import AFD.AFD;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Panel extends JFrame {

    public Panel() {
        setTitle("Analizador de Autómatas - ESCOM");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuAFN = new JMenu("AFN's");
        JMenu menuSintactico = new JMenu("Analisis Sintáctico");
        JMenu menuAFD = new JMenu("AFD's");
        /*JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());*/

        JMenuItem itemBasico = new JMenuItem("Básico");
        JMenuItem itemUnir = new JMenuItem("Unir");
        JMenuItem itemConcatenar = new JMenuItem("Concatenar");
        JMenuItem itemCerraduraPositiva = new JMenuItem("Cerradura +");
        JMenuItem itemCerraduraEstrella = new JMenuItem("Cerradura *");
        JMenuItem itemOpcional = new JMenuItem("Opcional");
        JMenuItem itemERaAFN = new JMenuItem("ER->AFN");
        JMenuItem itemUnionLexico = new JMenuItem("Unión para Analizador Léxico");
        JMenuItem itemConvertirAFNaAFD = new JMenuItem("Convertir AFN a AFD");
        JMenuItem itemAnalizarCadena = new JMenuItem("Analizar una Cadena");
        JMenuItem itemProbarLexico = new JMenuItem("Probar analizador Léxico");
        JMenuItem itemVentanaAFNs = new JMenuItem("Mostrar AFNs");
        JMenuItem itemMostrarAFD = new JMenuItem("Mostrar AFD");
        JMenuItem itemLL1 = new JMenuItem("Analisis LL1");
        JMenuItem itemLR = new JMenuItem("Analisis LR0");
        JMenuItem itemHOC3 = new JMenuItem("HOC 3");

        // --- CONEXIÓN DE TODAS LAS VENTANAS ---
        // Nota: Para que este archivo compile sin errores, deberás crear 
        // un archivo .java para cada una de estas clases.
        
        itemBasico.addActionListener(e -> new VentanaBasico(this).setVisible(true));
        itemUnir.addActionListener(e -> new VentanaUnir(this).setVisible(true));
        itemConcatenar.addActionListener(e -> new VentanaConcatenar(this).setVisible(true));
        
        itemCerraduraPositiva.addActionListener(e -> new VentanaCerraduraPositiva(this).setVisible(true));
        itemCerraduraEstrella.addActionListener(e -> new VentanaCerraduraEstrella(this).setVisible(true));
        itemOpcional.addActionListener(e -> new VentanaOpcional(this).setVisible(true));
        
        itemERaAFN.addActionListener(e -> new VentanaERaAFN(this).setVisible(true));
        itemUnionLexico.addActionListener(e -> new VentanaUnionLexico(this).setVisible(true));
        itemConvertirAFNaAFD.addActionListener(e -> new VentanaConvertirAFNaAFD(this).setVisible(true));
        
        itemAnalizarCadena.addActionListener(e -> new VentanaAnalizarCadena(this).setVisible(true));
        itemProbarLexico.addActionListener(e -> new VentanaProbarLexico(this).setVisible(true));
        itemVentanaAFNs.addActionListener(e-> new VentanaAFNS(this).setVisible(true));

        itemMostrarAFD.addActionListener(e-> {if(AFD.afdAsignado != null){new VentanaGrafo(this, AFD.afdAsignado).setVisible(AFD.afdAsignado != null);}});
        itemLL1.addActionListener(e-> new VentanaLL1(this).setVisible(true));
        itemLR.addActionListener(e-> new VentanaLR(this).setVisible(true));
        itemHOC3.addActionListener(e -> new VentanaHOC3(this).setVisible(true));


        // Ensamblar el menú desplegable
        menuAFN.add(itemBasico);
        menuAFN.add(itemUnir);
        menuAFN.add(itemConcatenar);
        menuAFN.add(itemCerraduraPositiva);
        menuAFN.add(itemCerraduraEstrella);
        menuAFN.add(itemOpcional);
        menuAFN.addSeparator(); // Divisor visual
        menuAFN.add(itemERaAFN);
        menuAFN.add(itemUnionLexico);
        menuAFN.add(itemConvertirAFNaAFD);
        menuAFN.addSeparator(); // Divisor visual
        menuAFN.add(itemAnalizarCadena);
        menuAFN.add(itemProbarLexico);
        menuAFN.add(itemVentanaAFNs);

        // Acciones del menu AFD
        menuAFD.add(GuardarAFD());
        menuAFD.add(CargarAFD());
        menuAFD.add(itemMostrarAFD);

        // Ensamblar menu sintactico
        menuSintactico.add(itemLL1);
        menuSintactico.add(itemLR);
        menuSintactico.add(itemHOC3);

        menuBar.add(menuAFN);
        menuBar.add(menuAFD);
        menuBar.add(menuSintactico);
        setJMenuBar(menuBar);
    }

    // Acción de Guardar AFD en forma de Bin
    private JMenuItem GuardarAFD(){
        JMenuItem GuardarAFd = new JMenuItem("Guardar AFD en bin");
        
        GuardarAFd.addActionListener(e->{
            if(AFD.afdAsignado != null){
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar AFD");
                
                // Filtrar para mostrar solo archivos .afnd
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos bin, AFD (*.afnd)", "afnd");
                fileChooser.setFileFilter(filter);
                
                int userSelection = fileChooser.showSaveDialog(Panel.this);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String filePath = fileToSave.getAbsolutePath();
                    
                    // Asegurar que el archivo termine en .afnd
                    if (!filePath.toLowerCase().endsWith(".afnd")) {
                        filePath += ".afnd";
                    }
                    
                    AFD.afdAsignado.GuardarArchivoBin(filePath);
                }
            }
        });

        return GuardarAFd;
    }

    private JMenuItem CargarAFD(){
        JMenuItem CargarAFD = new JMenuItem("Cargar AFD de un Bin");
        CargarAFD.addActionListener(e->{
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Cargar AFD");
            
            // Filtrar para mostrar solo archivos .png
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos bin, AFD (*.afnd)", "afnd");
            fileChooser.setFileFilter(filter);
            
            int userSelection = fileChooser.showSaveDialog(Panel.this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToCharge = fileChooser.getSelectedFile();
                String filePath = fileToCharge.getAbsolutePath();
                
                // Asegurar que el archivo termine en .afnd
                if (!filePath.toLowerCase().endsWith(".afnd")) {
                    filePath += ".afnd";
                }

                if(!fileToCharge.exists() || !fileToCharge.canRead()){
                    JOptionPane.showMessageDialog(this, "No se puede leer el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                    
                AFD.afdAsignado = AFD.AbrirArchivoBin(filePath);
            }

        });

        return CargarAFD;
    }


    public static void main(String[] args) {
        /*SwingUtilities.invokeLater(() -> {
            FormularioAutomatas ventana = new FormularioAutomatas();
            ventana.setVisible(true);
        });*/
        new Panel().setVisible(true);


    }
}