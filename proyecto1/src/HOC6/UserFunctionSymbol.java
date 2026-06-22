package HOC6; // Ajusta al paquete que estés utilizando

public class UserFunctionSymbol extends SymbolHoc {
    // Índice en el vector Prog de la clase Machine donde inicia la ejecución de esta función
    private int startAddress; 
    
    // (Opcional pero recomendado) Para validar aridad al momento de llamar a la función
    private int parameterCount; 

    public UserFunctionSymbol(String name, int tokenType) {
        super(name, tokenType);
        // -1 indica que el símbolo fue registrado, pero su código aún no se ha generado
        this.startAddress = -1; 
        this.parameterCount = 0;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
    }
}