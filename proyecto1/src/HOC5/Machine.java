package HOC5;

import HOC6.Frame;
import java.util.Stack;

public class Machine {

    SymbolTable TablaSim;
    public Instruction[] Prog;
    int progp;
    ProgramCounter PC;
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
        while (Prog[PC.value] != Instruction.STOP) {
            Instruction currentInstr = Prog[PC.value];
            PC.value++; // Avanzamos el PC para apuntar a la siguiente celda / argumento
            currentInstr.run(stack, Prog, PC, callStack);
        }
    }

    // Prepara la máquina pero no la arranca de golpe
    public void initEjecucion(int startAddress) {
        PC.value = startAddress;
    }

    // Ejecuta una sola línea de código y se pausa
    public boolean ejecutarPaso() {
        if (PC.value >= progp || Prog[PC.value] == Instruction.STOP) {
            return false; // Terminó el código
        }
        
        Instruction currentInstr = Prog[PC.value];
        if (currentInstr == null) return false;
        
        PC.value++; // Avanzamos el PC
        currentInstr.run(stack, Prog, PC, callStack);
        
        return true; // Sigue vivo
    }
    public int getPC(){return PC.value;};

    public int getProgP(){return progp;}

    public SymbolTable getTable(){return TablaSim;}
}
