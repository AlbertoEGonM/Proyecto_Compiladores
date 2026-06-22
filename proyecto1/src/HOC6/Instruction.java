package HOC6;

import java.util.Stack;

public class Instruction {
    
    // Interfaz funcional interna para encapsular el comportamiento ejecutable
    @FunctionalInterface
    public interface Action {
        void execute(Stack<Datum> stack, Instruction[] code, ProgramCounter pc, Stack<Frame> callStack, Instruction self);
    }

    private final Action action;
    private final SymbolHoc sym;
    private final float val;

    // Constructor para instrucciones ejecutables estándar (ADD, SUB, EVAL, STOP)
    public Instruction(Action action) {
        this.action = action;
        this.sym = null;
        this.val = 0.0f;
    }

    // Constructor especializado para envolver un Símbolo (Simula el casteo de C)
    public Instruction(SymbolHoc sym) {
        this.action = (stack, code, pc, callStack, self) -> {
            throw new RuntimeException("Error fatal: Se intentó ejecutar un Símbolo crudo en el vector.");
        };
        this.sym = sym;
        this.val = 0.0f;
    }

    // Constructor especializado para envolver Constantes o Direcciones de Salto
    public Instruction(float val) {
        this.action = (stack, code, pc, callStack, self) -> {
            throw new RuntimeException("Error fatal: Se intentó ejecutar una Dirección/Literal en el vector.");
        };
        this.val = val;
        this.sym = null;
    }

    // Método que llamará el execute() de la Máquina Virtual
    public void run(Stack<Datum> stack, Instruction[] code, ProgramCounter pc, Stack<Frame> callStack) {
        this.action.execute(stack, code, pc, callStack, this);
    }

    // Getters para que las funciones examinadoras saquen los argumentos contiguos
    public SymbolHoc getSym() { return sym; }
    public float getVal() { return val; }
    
    private static float decompress(Datum d) {
        if (d.getSym() != null && d.getSym() instanceof VariableSymbol) {
            return (float) ((VariableSymbol) d.getSym()).getValue();
        }
        return d.getVal();
    }


    // *********        Las funciones agregadas mediante code             ===>


    // PUSH CONSTANTE: Mira en la celda contigua del arreglo de código
    public static final Instruction constPush = new Instruction((stack, code, pc, callStack, self) -> {
        /* float valorLiteral = code[pc.value++].getVal(); */
        float valorLiteral = (float)((VariableSymbol)code[pc.value++].getSym()).getValue();
        stack.push(new Datum(valorLiteral));
    });

    // PUSH SÍMBOLO: Lee el símbolo de la celda contigua
    public static final Instruction varPush = new Instruction((stack, code, pc, callStack, self) -> { // equivalente a varpush
        SymbolHoc simbolo = code[pc.value++].getSym();
        stack.push(new Datum(simbolo));
    });

    // Detener la ejecución
    public static final Instruction STOP = new Instruction((stack, code, pc, callStack, self) -> {}); // equivalente a #Define STOP (Inst) 0

    // Descarta el elemento superior de la pila (Equivalente al POP en las gramáticas de HOC)
    public static final Instruction POP = new Instruction((stack, code, pc, callStack, self) -> {
        if (!stack.isEmpty()) {
            stack.pop();
        }
    });

    // Operación de Suma
    public static final Instruction ADD = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum(v1 + v2));
    });

    // Operación de Resta
    public static final Instruction SUB = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum(v1 - v2));
    });

    //Operación de Multiplicación
    public static final Instruction MUL = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum(v1 * v2));
    });

    //Operación de División
    public static final Instruction DIV = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum(v1 / v2));
    });

    //Operación de Potencia
    public static final Instruction POW = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum( (float)Math.pow(v1, v2)));
    });

    //Operación de Negación
    public static final Instruction NEG = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d = stack.pop();
        float v1 = decompress(d);
        stack.push(new Datum( -v1 ));
    });

    //Operación de evaluación
    public static final Instruction EVAL = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d = stack.pop();
        if (d.getSym() != null) {
            float valReal = (float) ((VariableSymbol) d.getSym()).getValue();
            stack.push(new Datum(valReal));
        } else {
            stack.push(d); // Si ya era un literal, se queda igual
        }
    });

    // Operación asignación
    public static final Instruction ASSIGN = new Instruction((stack, code, pc, callStack, self)-> {
        // CORRECCIÓN: El orden de extracción estaba invertido.
        Datum dVar = stack.pop(); // 1. El tope de la pila es la variable (puesto por varPush)
        Datum dVal = stack.pop(); // 2. Debajo está el valor evaluado de la expresión
        
        float valor = decompress(dVal);
        
        // Modificar el valor en la tabla de símbolos a través de su referencia
        ((VariableSymbol)dVar.getSym()).setValue(valor);
        
        // HOC deja el resultado de la asignación en la pila para encadenamientos (ej. a = b = 5)
        stack.push(new Datum(valor));
    });

    // Operación Print
    public static final Instruction PRINT = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d = stack.pop();
        float val = decompress(d);
        System.out.println(val);     // O mandarlo a tu formHoc3
    });

    // FUNCIONES MATEMÁTICAS: bltin lee el FunctionSymbol de la celda contigua
    public static final Instruction BLTIN = new Instruction((stack, code, pc, callStack, self) -> {
        // Obtenemos el símbolo matemático alojado en la celda contigua y avanzamos el PC
        FunctionSymbol funcSimb = (FunctionSymbol) code[pc.value++].getSym();
        
        // Sacamos el argumento que se calculó para la función (ej. el 'expr' dentro de sin(expr))
        Datum d = stack.pop();
        double argumento = (d.getSym() != null) ? ((VariableSymbol)d.getSym()).getValue() : (double) d.getVal();
        
        // Aplicamos la función matemática nativa de HOC3 y el resultado lo devolvemos a la pila
        double resultado = funcSimb.apply(argumento);
        stack.push(new Datum((float) resultado));
    });

    // Operación Mayor Que (>)
    public static final Instruction GT = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum(v1 > v2 ? 1.0f : 0.0f));
    });

    // Operación Mayor Que (>=)
    public static final Instruction GE = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum(v1 >= v2 ? 1.0f : 0.0f));
    });

    // Operación Mayor Que (<)
    public static final Instruction LT = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum(v1 < v2 ? 1.0f : 0.0f));
    });

    // Operación Mayor Que (<=)
    public static final Instruction LE = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum(v1 <= v2 ? 1.0f : 0.0f));
    });

    // Operación de Igualdad (==)
    public static final Instruction EQ = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum(v1 == v2 ? 1.0f : 0.0f));
    });

    // Operación Diferente De (!=)
    public static final Instruction NE = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum(v1 != v2 ? 1.0f : 0.0f));
    });

    // Operación NOT Lógico (!)
    public static final Instruction NOT = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d = stack.pop();
        float v = decompress(d);
        stack.push(new Datum(v == 0.0f ? 1.0f : 0.0f));
    });

    // Operación AND Lógico (&&)
    public static final Instruction AND = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum((v1 != 0.0f && v2 != 0.0f) ? 1.0f : 0.0f));
    });

    // Operación OR Lógico (||)
    public static final Instruction OR = new Instruction((stack, code, pc, callStack, self) -> {
        Datum d2 = stack.pop();
        Datum d1 = stack.pop();
        float v1 = decompress(d1);
        float v2 = decompress(d2);
        stack.push(new Datum((v1 != 0.0f || v2 != 0.0f) ? 1.0f : 0.0f));
    });

    // JUMP INCONDICIONAL: Salta directo a la dirección guardada en la celda contigua
    public static final Instruction JUMP = new Instruction((stack, code, pc, callStack, self) -> {
        // Leemos la dirección destino de la celda actual y movemos el PC a ese lugar
        int destino = (int) code[pc.value].getVal();
        pc.value = destino;
    });

    // JUMP TRUE: Saca de la pila y salta si el valor NO es cero
    public static final Instruction jumpTrue = new Instruction((stack, code, pc, callStack, self) -> {
        Datum cond = stack.pop();
        float valCond = decompress(cond);
        
        int destino = (int) code[pc.value].getVal();
        
        if (valCond != 0.0f) {
            pc.value = destino;      // Condición verdadera -> Saltamos al destino
        } else {
            pc.value++;              // Condición falsa -> Ignoramos el destino y avanzamos al siguiente comando
        }
    });

    // JUMP FALSE: Saca de la pila y salta si el valor ES cero
    public static final Instruction jumpFalse = new Instruction((stack, code, pc, callStack, self) -> {
        Datum cond = stack.pop();
        float valCond = decompress(cond);
        
        int destino = (int) code[pc.value].getVal();
        
        if (valCond == 0.0f) {
            pc.value = destino;      // Condición falsa -> Saltamos al destino (normalmente al final o al else)
        } else {
            pc.value++;              // Condición verdadera -> Ignoramos el destino y entramos al bloque
        }
    });

    // **   --  --  Instrucciones de HOC6   --  --      **  //

    public static final Instruction call = new Instruction((stack, code, pc, callStack, self) -> {
        // 1. Leemos qué función vamos a ejecutar y avanzamos el PC
        UserFunctionSymbol func = (UserFunctionSymbol) code[pc.value++].getSym();
        
        // 2. Leemos cuántos argumentos se metieron a la pila y avanzamos el PC
        int nArgs = (int) code[pc.value++].getVal();
        
        // 3. Calculamos la posición base de los argumentos en la pila de datos
        int argOffset = stack.size() - nArgs;
        
        // 4. Armamos el Marco y lo guardamos en el historial
        // pc.value actual es la dirección exacta a la que debemos volver
        callStack.push(new Frame(pc.value, argOffset, func));
        
        // 5. ¡El gran salto! Cambiamos el PC al inicio de la función
        pc.value = func.getStartAddress(); 
    });

    public static final Instruction ret = new Instruction((stack, code, pc, callStack, self) -> {
        // 1. Rescatamos el valor que la función calculó (está en la cima de la pila)
        Datum resultado = stack.pop();
        
        // 2. Sacamos nuestra "foto" del historial
        Frame marco = callStack.pop();
        
        // 3. Limpiamos todos los argumentos que usamos de la pila principal
        while (stack.size() > marco.getArgOffset()) {
            stack.pop();
        }
        
        // 4. Dejamos el resultado limpio para que el programa original lo use
        stack.push(resultado);
        
        // 5. Restauramos el Program Counter para volver a donde estábamos
        pc.value = marco.getRetPC();
    });

    public static final Instruction procret = new Instruction((stack, code, pc, callStack, self) -> {
        Frame marco = callStack.pop();
        
        while (stack.size() > marco.getArgOffset()) {
            stack.pop();
        }
        
        // NUEVO: Agregamos un valor "basura" seguro (0.0) para que la instrucción 
        // POP de las funciones sueltas no rompa la pila de datos.
        stack.push(new Datum(0.0f));
        
        pc.value = marco.getRetPC();
    });

    // arg: Lee el valor de un parámetro (ej. $1, $2)
    public static final Instruction arg = new Instruction((stack, code, pc, callStack, self) -> {
        // 1. Leemos el número de argumento que se solicitó (el '1' de '$1')
        int numArg = (int) code[pc.value++].getVal();
        
        // 2. Consultamos nuestro marco de ejecución actual
        Frame marcoActual = callStack.peek();
        
        // 3. Calculamos la posición exacta en la memoria
        // offset es la base. Le sumamos (numArg - 1) porque $1 es el primer elemento (desplazamiento 0)
        int index = marcoActual.getArgOffset() + numArg - 1;
        
        // 4. Obtenemos el dato real (los argumentos ya fueron evaluados antes del call, así que son valores puros)
        Datum argData = stack.get(index);
        
        // 5. Lo empujamos a la cima de la pila para que las operaciones matemáticas (ADD, SUB, etc.) lo usen
        stack.push(new Datum(argData.getVal()));
    });

    // argAssign: Permite sobrescribir el valor de un parámetro (ej. $1 = 5)
    public static final Instruction argAssign = new Instruction((stack, code, pc, callStack, self) -> {
        // 1. Leemos a qué argumento le vamos a asignar (el '1' de '$1')
        int numArg = (int) code[pc.value++].getVal();
        
        // 2. Extraemos el valor a asignar que la expresión dejó en la cima de la pila
        Datum dVal = stack.pop();
        float valor = decompress(dVal);
        
        // 3. Consultamos nuestro marco actual
        Frame marcoActual = callStack.peek();
        
        // 4. Calculamos su posición original en la pila
        int index = marcoActual.getArgOffset() + numArg - 1;
        
        // 5. Reemplazamos el dato en esa celda específica de la memoria
        stack.set(index, new Datum(valor));
        
        // 6. Por regla de HOC, una asignación siempre deja el resultado en la pila para encadenamientos (ej. x = $1 = 5)
        stack.push(new Datum(valor));
    });



}