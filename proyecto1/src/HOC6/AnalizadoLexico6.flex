package HOC6;
import java_cup.runtime.*;
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
    public int TipSimb;

    private SymbolTable tablaSimbolos;
    
    // SOLUCIÓN 1: Declaramos la variable que la Interfaz Gráfica está buscando
    public java.util.List<String> tokensLeidos = new java.util.ArrayList<>();

    // SOLUCIÓN 2: El constructor ahora tiene el nombre correcto de la clase
    public AnalizadorLexico(java.io.Reader in, SymbolTable tabla) {
        this(in);
        this.tablaSimbolos = tabla;
    }

    private Symbol symbol(int type){
        tokensLeidos.add(String.format("Token [%d] \t-> Lexema: %s", type, yytext()));
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value){
        tokensLeidos.add(String.format("Token [%d] \t-> Lexema: %s \t| Valor: %s", type, yytext(), value.toString()));
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/* hacemos algunas definiciones regulares, o macros definiciones */

Letra = [a-zA-Z]
Digito = [0-9]

%% /* Expresiones regulares */

[\t\n\r ]+                     { }
";"                         { return symbol(AnalizadorSintacticoSym.SEMIC); }
{Digito}+(\.{Digito}+)?     {   
                                s = new VariableSymbol("", AnalizadorSintacticoSym.CONST_NUM, Float.valueOf(yytext()));
                                return symbol(AnalizadorSintacticoSym.NUM, s); 
                            }

/* Palabras Clave de HOC5 */
"if"                        { return symbol(AnalizadorSintacticoSym.IF); }
"else"                      { return symbol(AnalizadorSintacticoSym.ELSE); }
"while"                     { return symbol(AnalizadorSintacticoSym.WHILE); }
"print"                     { return symbol(AnalizadorSintacticoSym.PRINT); }
"func"                      { return symbol(AnalizadorSintacticoSym.FUNC); }
"proc"                      { return symbol(AnalizadorSintacticoSym.PROC); }
"return"                    { return symbol(AnalizadorSintacticoSym.RETURN); }

/* Operadores Relacionales y Lógicos */
"=="                        { return symbol(AnalizadorSintacticoSym.EQ); }
"!="                        { return symbol(AnalizadorSintacticoSym.NE); }
"<="                        { return symbol(AnalizadorSintacticoSym.LE); }
">="                        { return symbol(AnalizadorSintacticoSym.GE); }
"<"                         { return symbol(AnalizadorSintacticoSym.LT); }
">"                         { return symbol(AnalizadorSintacticoSym.GT); }
"&&"                        { return symbol(AnalizadorSintacticoSym.AND); }
"||"                        { return symbol(AnalizadorSintacticoSym.OR); }
"!"                         { return symbol(AnalizadorSintacticoSym.NOT); }

/* Delimitadores */
"("                         { return symbol(AnalizadorSintacticoSym.ParIzq); }
")"                         { return symbol(AnalizadorSintacticoSym.ParDer); }
"{"                         { return symbol(AnalizadorSintacticoSym.CorchIzq); }
"}"                         { return symbol(AnalizadorSintacticoSym.CorchDer); }
","                         { return symbol(AnalizadorSintacticoSym.COMA); }

/* Operadores Aritmeticos y asignación */
"="                         { return symbol(AnalizadorSintacticoSym.OpAsig); }
"/"                         { return symbol(AnalizadorSintacticoSym.OpDiv); }
"*"                         { return symbol(AnalizadorSintacticoSym.OpProd); }
"-"                         { return symbol(AnalizadorSintacticoSym.OpResta); }
"+"                         { return symbol(AnalizadorSintacticoSym.OpSuma); }
"("                         { return symbol(AnalizadorSintacticoSym.ParIzq); }
")"                         { return symbol(AnalizadorSintacticoSym.ParDer); }
\^                          { return symbol(AnalizadorSintacticoSym.OpPotencia); }

/* Argumentos de Funciones ($1, $2, etc.) */
\${Digito}+                 { 
                                // Extraemos la subcadena omitiendo el '$' (índice 1 en adelante)
                                int numArg = Integer.parseInt(yytext().substring(1));
                                
                                // Devolvemos el token ARG y le pasamos el número entero como valor
                                return symbol(AnalizadorSintacticoSym.ARG, numArg); 
                            }

{Letra}({Letra}|{Digito})*  {    
                                s = tablaSimbolos.lookup(yytext()); // Como va a leer los nodos integrados previamente?
                                if( s == null ){ // Se agregará como variable no inicializada
                                    s = new VariableSymbol(yytext(), AnalizadorSintacticoSym.VAR);
                                    tablaSimbolos.install(s);
                                }
                                
                                TipSimb = s.getTokenType();

                                return symbol(TipSimb, s);
                            }   

.                           { return symbol(AnalizadorSintacticoSym.error); }
