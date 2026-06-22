package HOC6;

public class Frame {
    
    // Dirección de retorno (El valor del Program Counter antes del salto)
    private final int retPC;       
    
    // Índice base en la pila de datos (stack de Datums) donde comienzan los argumentos de ESTA llamada
    private final int argOffset;   
    
    // Referencia al símbolo de la función (Muy útil para imprimir errores o debuggear)
    private final UserFunctionSymbol function; 

    public Frame(int retPC, int argOffset, UserFunctionSymbol function) {
        this.retPC = retPC;
        this.argOffset = argOffset;
        this.function = function;
    }

    public int getRetPC() {
        return retPC;
    }

    public int getArgOffset() {
        return argOffset;
    }

    public UserFunctionSymbol getFunction() {
        return function;
    }
}