package HOC5;
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
    public int TipSimb; // Usado para definir el tipo de simbolo

    private SymbolTable tablaSimbolos;

    // Nuevo constructor que acepta la tabla compartida
  public AnalizadorLexico(java.io.Reader in, SymbolTable tabla) {
        this(in); // Llama al constructor interno de JFlex
        this.tablaSimbolos = tabla;
    }
    
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


/* Operadores Aritmeticos y asignación */
"="                         { return symbol(AnalizadorSintacticoSym.OpAsig); }
"/"                         { return symbol(AnalizadorSintacticoSym.OpDiv); }
"*"                         { return symbol(AnalizadorSintacticoSym.OpProd); }
"-"                         { return symbol(AnalizadorSintacticoSym.OpResta); }
"+"                         { return symbol(AnalizadorSintacticoSym.OpSuma); }
"("                         { return symbol(AnalizadorSintacticoSym.ParIzq); }
")"                         { return symbol(AnalizadorSintacticoSym.ParDer); }
\^                          { return symbol(AnalizadorSintacticoSym.OpPotencia); }

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
