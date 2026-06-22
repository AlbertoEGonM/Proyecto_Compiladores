package HOC5;

public class VariableSymbol extends SymbolHoc {
    private double value;
    private final boolean isConstant; // Para proteger PI o E de ser sobreescritas

    // Constructor para variables ordinarias (se inicializan en 0.0)
    public VariableSymbol(String name, int tokenType) {
        super(name, tokenType);
        this.value = 0.0;
        this.isConstant = false;
    }

    // Constructor para constantes matemáticas
    public VariableSymbol(String name, int tokenType, double value) {
        super(name, tokenType);
        this.value = value;
        this.isConstant = true;
    }

    public boolean isConstant() {
        return this.isConstant; // Retorna el atributo booleano de la clase
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        if (isConstant) {
            System.err.println("Error: No se puede reasignar un valor a la constante " + getName());
            return; 
        }
        this.value = value;
    }
}
