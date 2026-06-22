package HOC5;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, SymbolHoc> table;

    public SymbolTable() {
        this.table = new HashMap<>();
        initStandardLibrary();
    }

    // Pre-carga los elementos nativos de HOC 3
    private void initStandardLibrary() {
        // 1. Constantes (CUP las tratará gramaticalmente como variables 'VAR')
        table.put("PI" , new VariableSymbol("PI" , AnalizadorSintacticoSym.CONST_PRED, Math.PI));
        table.put("E"  , new VariableSymbol("E"  , AnalizadorSintacticoSym.CONST_PRED, Math.E));
        table.put("GAMMA", new VariableSymbol("GAMMA", AnalizadorSintacticoSym.CONST_PRED, 0.57721566490153286060));
        table.put("DEG", new VariableSymbol("DEG", AnalizadorSintacticoSym.CONST_PRED, 57.29577951308232));
        table.put("PHI", new VariableSymbol("PHI", AnalizadorSintacticoSym.CONST_PRED, 1.618033988749895));

        // 2. Funciones matemáticas (Built-ins -> 'sym.BLTIN')
        table.put("sin",   new FunctionSymbol("sin",   AnalizadorSintacticoSym.BLTIN, Math::sin));
        table.put("cos",   new FunctionSymbol("cos",   AnalizadorSintacticoSym.BLTIN, Math::cos));
        table.put("tan",   new FunctionSymbol("tan",   AnalizadorSintacticoSym.BLTIN, Math::tan));
        table.put("atan",  new FunctionSymbol("atan",  AnalizadorSintacticoSym.BLTIN, Math::atan));
        table.put("log",   new FunctionSymbol("log",   AnalizadorSintacticoSym.BLTIN, Math::log));
        table.put("log10", new FunctionSymbol("log10", AnalizadorSintacticoSym.BLTIN, Math::log10));
        table.put("exp",   new FunctionSymbol("exp",   AnalizadorSintacticoSym.BLTIN, Math::exp));
        table.put("sqrt",  new FunctionSymbol("sqrt",  AnalizadorSintacticoSym.BLTIN, Math::sqrt));
        table.put("abs",   new FunctionSymbol("abs",   AnalizadorSintacticoSym.BLTIN, Math::abs));
    }

    public SymbolHoc lookup(String name) {
        return table.get(name);
    }

    public void install(SymbolHoc symbol) {
        table.put(symbol.getName(), symbol);
    }
}
