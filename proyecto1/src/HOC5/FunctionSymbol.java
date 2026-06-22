package HOC3;

import java.util.function.Function;

public class FunctionSymbol extends SymbolHoc {
    // Acepta un Double y devuelve un Double
    private final Function<Double, Double> functionRef; 

    public FunctionSymbol(String name, int tokenType, Function<Double, Double> functionRef) {
        super(name, tokenType);
        this.functionRef = functionRef;
    }

    /**
     * Ejecuta la función matemática asociada.
     * @param argument El valor de la expresión dentro de los paréntesis: func(expr)
     * @return El resultado del cálculo matemático
     */
    public double apply(double argument) {
        return functionRef.apply(argument);
    }
}