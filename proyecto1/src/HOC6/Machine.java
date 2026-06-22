package HOC6;

import java.util.Stack;

public class Machine {

    public SymbolTable TablaSim;
    public Instruction[] Prog;
    public int progp;
    public ProgramCounter PC;
    public Stack<Datum> stack;

    public Stack<Frame> callStack; // Parte del HOC6
    
    private final int NPROG = 2048;


    public Machine(){
        TablaSim = new SymbolTable();
        Prog = new Instruction[NPROG];
        PC = new ProgramCounter();
        PC.value = 0;
        progp = 0;

        stack = new Stack<>();

        callStack = new Stack<>();

    }

    public int code(Instruction ins) {
        if (progp >= NPROG) {
            System.err.println("Error: Desbordamiento del vector de código (Machine)");
            System.exit(1);
        }
        Prog[progp] = ins;
        return progp++; // Retorna el índice actual e incrementa progp
    }

    public int code(Instruction ins1, Instruction ins2){
        code(ins1);
        return code(ins2);
    }

    public int code(Instruction ins1, Instruction ins2, Instruction ins3){
        code(ins1);
        code(ins2);
        return code(ins3);
    }

    public void execute(int startAddress) {
        PC.value = startAddress;
        
        while (PC.value < progp) {
            Instruction currentInstr = Prog[PC.value];
            
            // PROTECCIÓN: Si la instrucción es null o no tiene acción ejecutable, DETÉN EL PROGRAMA
            if (currentInstr == null) {
                System.err.println("¡ERROR: Celda vacía en PC=" + PC.value);
                break;
            }
            
            PC.value++;
            try {
                currentInstr.run(stack, Prog, PC, callStack);
            } catch (Exception e) {
                System.err.println("CRASH en PC=" + (PC.value-1) + ": " + e.getMessage());
                break; // En lugar de que se cierre el programa, el bucle se rompe limpiamente
            }
        }
    }

    // Prepara la máquina pero no la arranca
    public void initEjecucion(int startAddress) {
        PC.value = startAddress;
    }

    // Ejecuta una sola línea de código y se pausa. Retorna 'false' cuando el programa termina.
    public boolean ejecutarPaso() {
        if (PC.value >= progp || Prog[PC.value] == Instruction.STOP) {
            return false; // Ya no hay más código
        }
        
        Instruction currentInstr = Prog[PC.value];
        if (currentInstr == null) return false;
        
        PC.value++; // Avanzamos el PC
        currentInstr.run(stack, Prog, PC, callStack); // Ejecutamos la instrucción
        
        return true; // Aún hay programa por ejecutar
    }

    // Metodo para HOC6
    public UserFunctionSymbol define(SymbolHoc sym) {
        // 1. Creamos el nuevo símbolo de función usando el nombre del símbolo original
        UserFunctionSymbol nuevaFuncion = new UserFunctionSymbol(sym.getName(), AnalizadorSintacticoSym.VAR);
        
        // 2. Le decimos en qué parte de la memoria va a empezar a guardarse su código (el progp actual)
        nuevaFuncion.setStartAddress(this.progp);
        
        // 3. Sobreescribimos el símbolo viejo en la Tabla de Símbolos
        TablaSim.install(nuevaFuncion);
        
        return nuevaFuncion;
    }

    public int getProgP(){return progp;}

    public SymbolTable getTable(){return TablaSim;}
}
