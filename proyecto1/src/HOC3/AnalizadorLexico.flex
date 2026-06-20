package HOC3;
import java_cup.runtime;
import java.io.Reader;

%% /* inicio de declaraciones JFlex */
%class AnalizadorLexico
%line       /* Se habilita el contador de lineas. Variable yyline, de tipo integer */
%column     /* Se habilita el contador de columnas. Variable yycolumn, de tipo integer */
%char       /* Se habilita el contador de caracteres. Variable yychar, de tipo long */
%cup        /* Se habilita la compatibilidad con java cup */

/* el código entre %{ -- %} se copia tal cual dentro de la clase del analizador léxico */

%{
    public SymbolHoc s;
    public int TipSimb; // Usado para definir el tipo de simbolo

    SymbolTable ListaSimb = new SymbolTable();

    /* Codigos que mi profesor usa en su logica, considerando que el tiene una clase unica para simbolos(en lugar de usar FunctionSymbol y VariableSymbol)
        su comentario: " Se crean los objetos sumbol para ser utilizados durante la sintésis de los atributos Symbol está especificado en java.cup.Symbol "
     */

    private Symbol symbol(int type){
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value){
        return new Symbol(type, yyline, yycolumn, value);
    }

%}

/* hacemos algunas definiciones regulares, o macros definiciones */

Letra = [a-zA-Z]
Digito = [0-9]

%% /* Expresiones regulares */

[\t\n\r]+                     { }
";"                         { return symbol(AnalizadorSintacticoSym.SEMIC); }
{Digito}+(\.{Digito}+)?     { return symbol(AnalizadorSintacticoSym.NUM, Float.valueOf(yytext())); }

"="                         { return symbol(AnalizadorSintacticoSym.OpAsig); }
"/"                         { return symbol(AnalizadorSintacticoSym.OpDiv); }
"*"                         { return symbol(AnalizadorSintacticoSym.OpProd); }
"-"                         { return symbol(AnalizadorSintacticoSym.OpResta); }
"+"                         { return symbol(AnalizadorSintacticoSym.OpSuma); }
"("                         { return symbol(AnalizadorSintacticoSym.ParIzq); }
")"                         { return symbol(AnalizadorSintacticoSym.ParDer); }
\^                          { return symbol(AnalizadorSintacticoSym.OpPotencia); }

{Letra}({Letra}|{Digito})*  {    
                                s = ListaSimb.lookup(yytext());
                                if( s == null ){ // Se agregará como variable no inicializada
                                    s = new VariableSymbol(yytext(), AnalizadorSintacticoSym.VAR);
                                    ListaSimb.install(s);
                                }
                                if (s instanceof FunctionSymbol) {
                                    TipSimb = AnalizadorSintacticoSym.BLTIN;
                                } else if (s instanceof VariableSymbol && ((VariableSymbol)s).isConstant()) {
                                    TipSimb = AnalizadorSintacticoSym.COSNT_PRED;
                                } else {
                                    TipSimb = AnalizadorSintacticoSym.VAR;
                                }

                                return symbol(TipSimb, s);
                            }   

.                           { return symbol(AnalizadorSintacticoSym.error); }