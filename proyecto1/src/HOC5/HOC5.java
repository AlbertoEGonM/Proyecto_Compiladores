package HOC5;

import HOC3.FunctionSymbol;
import HOC3.SymbolHoc;
import HOC3.VariableSymbol;
import java.util.ArrayList;
import java.util.List;

/**
 * Intérprete HOC 5 usado por la interfaz Swing.
 *
 * Soporta las capacidades de HOC 3 (expresiones, variables, constantes y
 * funciones matemáticas) y añade sentencias print, bloques, if/else, while y
 * operadores relacionales/lógicos.
 */
public class HOC5 {
    private final HOC3.SymbolTable symbols = new HOC3.SymbolTable();

    public String analizarLexicamente(String source) {
        try {
            Lexer lexer = new Lexer(source == null ? "" : source, symbols);
            StringBuilder out = new StringBuilder();
            Token token;
            do {
                token = lexer.nextToken();
                if (token.type != TokenType.EOF) {
                    out.append(String.format("%-12s %-12s línea %d, columna %d%n",
                            token.type, token.lexeme, token.line, token.column));
                }
            } while (token.type != TokenType.EOF);
            return out.length() == 0 ? "No se encontraron tokens." : out.toString();
        } catch (RuntimeException ex) {
            return "Error léxico: " + ex.getMessage();
        }
    }

    public String analizarSintacticamente(String source) {
        if (source == null || source.trim().isEmpty()) {
            return "Ingresa una expresión.";
        }
        try {
            Parser parser = new Parser(new Lexer(source, symbols));
            parser.parseProgram();
            return "Programa válido.";
        } catch (RuntimeException ex) {
            return "Error sintáctico: " + ex.getMessage();
        }
    }

    public String ejecutar(String source) {
        if (source == null || source.trim().isEmpty()) {
            return "Ingresa una expresión.";
        }
        try {
            Parser parser = new Parser(new Lexer(source, symbols));
            List<Stmt> program = parser.parseProgram();
            StringBuilder out = new StringBuilder();
            for (Stmt stmt : program) {
                stmt.execute(out);
            }
            return out.length() == 0 ? "Programa ejecutado sin salida." : out.toString();
        } catch (RuntimeException ex) {
            return "Error de ejecución: " + ex.getMessage();
        }
    }

    private enum TokenType {
        EOF, SEMIC, PLUS, MINUS, TIMES, DIVIDE, ASSIGN, POWER, LPAREN, RPAREN,
        LBRACE, RBRACE, NUMBER, VARIABLE, CONSTANT, FUNCTION, PRINT, IF, ELSE,
        WHILE, GT, GE, LT, LE, EQ, NE, NOT, AND, OR
    }

    private static final class Token {
        final TokenType type; final String lexeme; final double number; final SymbolHoc symbol; final int line; final int column;
        Token(TokenType type, String lexeme, double number, SymbolHoc symbol, int line, int column) {
            this.type = type; this.lexeme = lexeme; this.number = number; this.symbol = symbol; this.line = line; this.column = column;
        }
    }

    private static final class Lexer {
        private final String input; private final HOC3.SymbolTable symbols; private int pos; private int line = 1; private int column = 1;
        Lexer(String input, HOC3.SymbolTable symbols) { this.input = input; this.symbols = symbols; }
        Token nextToken() {
            skipWhitespace(); int startLine = line, startColumn = column;
            if (isAtEnd()) return token(TokenType.EOF, "<EOF>", startLine, startColumn);
            char c = advance();
            switch (c) {
                case ';': return token(TokenType.SEMIC, ";", startLine, startColumn);
                case '+': return token(TokenType.PLUS, "+", startLine, startColumn);
                case '-': return token(TokenType.MINUS, "-", startLine, startColumn);
                case '*': return token(TokenType.TIMES, "*", startLine, startColumn);
                case '/': return token(TokenType.DIVIDE, "/", startLine, startColumn);
                case '^': return token(TokenType.POWER, "^", startLine, startColumn);
                case '(': return token(TokenType.LPAREN, "(", startLine, startColumn);
                case ')': return token(TokenType.RPAREN, ")", startLine, startColumn);
                case '{': return token(TokenType.LBRACE, "{", startLine, startColumn);
                case '}': return token(TokenType.RBRACE, "}", startLine, startColumn);
                case '=': return match('=') ? token(TokenType.EQ, "==", startLine, startColumn) : token(TokenType.ASSIGN, "=", startLine, startColumn);
                case '!': return match('=') ? token(TokenType.NE, "!=", startLine, startColumn) : token(TokenType.NOT, "!", startLine, startColumn);
                case '<': return match('=') ? token(TokenType.LE, "<=", startLine, startColumn) : token(TokenType.LT, "<", startLine, startColumn);
                case '>': return match('=') ? token(TokenType.GE, ">=", startLine, startColumn) : token(TokenType.GT, ">", startLine, startColumn);
                case '&': if (match('&')) return token(TokenType.AND, "&&", startLine, startColumn); break;
                case '|': if (match('|')) return token(TokenType.OR, "||", startLine, startColumn); break;
                default:
                    if (Character.isDigit(c) || c == '.') return number(c, startLine, startColumn);
                    if (Character.isLetter(c)) return identifier(c, startLine, startColumn);
            }
            throw error("Caracter no reconocido: '" + c + "'", startLine, startColumn);
        }
        private Token number(char first, int l, int col) { StringBuilder s = new StringBuilder().append(first); boolean dot = first == '.'; while (!isAtEnd()) { char c = peek(); if (Character.isDigit(c)) s.append(advance()); else if (c == '.' && !dot) { dot = true; s.append(advance()); } else break; } if (s.toString().equals(".")) throw error("Número incompleto", l, col); return new Token(TokenType.NUMBER, s.toString(), Double.parseDouble(s.toString()), null, l, col); }
        private Token identifier(char first, int l, int col) { StringBuilder s = new StringBuilder().append(first); while (!isAtEnd() && Character.isLetterOrDigit(peek())) s.append(advance()); String name = s.toString(); if (name.equals("print")) return token(TokenType.PRINT, name, l, col); if (name.equals("if")) return token(TokenType.IF, name, l, col); if (name.equals("else")) return token(TokenType.ELSE, name, l, col); if (name.equals("while")) return token(TokenType.WHILE, name, l, col); SymbolHoc sym = symbols.lookup(name); if (sym == null) { sym = new VariableSymbol(name, AnalizadorSintacticoSym.VAR); symbols.install(sym); } if (sym instanceof FunctionSymbol) return new Token(TokenType.FUNCTION, name, 0, sym, l, col); if (sym instanceof VariableSymbol && ((VariableSymbol) sym).isConstant()) return new Token(TokenType.CONSTANT, name, 0, sym, l, col); return new Token(TokenType.VARIABLE, name, 0, sym, l, col); }
        private void skipWhitespace() { while (!isAtEnd() && Character.isWhitespace(peek())) advance(); }
        private boolean match(char expected) { if (isAtEnd() || peek() != expected) return false; advance(); return true; }
        private Token token(TokenType t, String x, int l, int c) { return new Token(t, x, 0, null, l, c); }
        private boolean isAtEnd() { return pos >= input.length(); } private char peek() { return input.charAt(pos); }
        private char advance() { char c = input.charAt(pos++); if (c == '\n') { line++; column = 1; } else column++; return c; }
        private IllegalArgumentException error(String msg, int l, int c) { return new IllegalArgumentException(msg + " en línea " + l + ", columna " + c); }
    }

    private interface Expr { double eval(); }
    private interface Stmt { void execute(StringBuilder out); }

    private static final class Parser {
        private final Lexer lexer; private Token current; private Token next;
        Parser(Lexer lexer) { this.lexer = lexer; current = lexer.nextToken(); next = lexer.nextToken(); }
        List<Stmt> parseProgram() { List<Stmt> stmts = new ArrayList<>(); while (current.type != TokenType.EOF) { if (match(TokenType.SEMIC)) continue; stmts.add(statement()); } return stmts; }
        private Stmt statement() { if (match(TokenType.PRINT)) { Expr e = expression(); consume(TokenType.SEMIC, "Se esperaba ';'"); return out -> out.append(format(e.eval())).append(System.lineSeparator()); } if (match(TokenType.IF)) { consume(TokenType.LPAREN, "Se esperaba '('"); Expr c = expression(); consume(TokenType.RPAREN, "Se esperaba ')'"); Stmt thenS = statement(); Stmt elseS = match(TokenType.ELSE) ? statement() : out -> {}; return out -> { if (truth(c.eval())) thenS.execute(out); else elseS.execute(out); }; } if (match(TokenType.WHILE)) { consume(TokenType.LPAREN, "Se esperaba '('"); Expr c = expression(); consume(TokenType.RPAREN, "Se esperaba ')'"); Stmt body = statement(); return out -> { int guard = 0; while (truth(c.eval())) { if (++guard > 1_000_000) throw new IllegalStateException("Posible ciclo infinito"); body.execute(out); } }; } if (match(TokenType.LBRACE)) { List<Stmt> block = new ArrayList<>(); while (current.type != TokenType.RBRACE && current.type != TokenType.EOF) block.add(statement()); consume(TokenType.RBRACE, "Se esperaba '}'"); return out -> { for (Stmt s : block) s.execute(out); }; } Expr e = expression(); consume(TokenType.SEMIC, "Se esperaba ';'"); return out -> e.eval(); }
        private Expr expression() { return assignment(); }
        private Expr assignment() { if (current.type == TokenType.VARIABLE && next.type == TokenType.ASSIGN) { Token v = current; advance(); advance(); Expr rhs = assignment(); return () -> { double value = rhs.eval(); ((VariableSymbol) v.symbol).setValue(value); return value; }; } if (current.type == TokenType.CONSTANT && next.type == TokenType.ASSIGN) throw error("No se puede reasignar la constante " + current.lexeme, current); return or(); }
        private Expr or() { Expr l = and(); while (match(TokenType.OR)) { Expr a = l, r = and(); l = () -> truth(a.eval()) || truth(r.eval()) ? 1 : 0; } return l; }
        private Expr and() { Expr l = equality(); while (match(TokenType.AND)) { Expr a = l, r = equality(); l = () -> truth(a.eval()) && truth(r.eval()) ? 1 : 0; } return l; }
        private Expr equality() { Expr l = comparison(); while (current.type == TokenType.EQ || current.type == TokenType.NE) { TokenType op = current.type; advance(); Expr a = l, r = comparison(); l = () -> (op == TokenType.EQ ? a.eval() == r.eval() : a.eval() != r.eval()) ? 1 : 0; } return l; }
        private Expr comparison() { Expr l = additive(); while (current.type == TokenType.GT || current.type == TokenType.GE || current.type == TokenType.LT || current.type == TokenType.LE) { TokenType op = current.type; advance(); Expr a = l, r = additive(); l = () -> switch (op) { case GT -> a.eval() > r.eval() ? 1 : 0; case GE -> a.eval() >= r.eval() ? 1 : 0; case LT -> a.eval() < r.eval() ? 1 : 0; default -> a.eval() <= r.eval() ? 1 : 0; }; } return l; }
        private Expr additive() { Expr l = multiplicative(); while (current.type == TokenType.PLUS || current.type == TokenType.MINUS) { TokenType op = current.type; advance(); Expr a = l, r = multiplicative(); l = () -> op == TokenType.PLUS ? a.eval() + r.eval() : a.eval() - r.eval(); } return l; }
        private Expr multiplicative() { Expr l = unary(); while (current.type == TokenType.TIMES || current.type == TokenType.DIVIDE) { TokenType op = current.type; advance(); Expr a = l, r = unary(); l = () -> op == TokenType.TIMES ? a.eval() * r.eval() : a.eval() / r.eval(); } return l; }
        private Expr unary() { if (match(TokenType.MINUS)) { Expr e = unary(); return () -> -e.eval(); } if (match(TokenType.NOT)) { Expr e = unary(); return () -> truth(e.eval()) ? 0 : 1; } return power(); }
        private Expr power() { Expr base = primary(); if (match(TokenType.POWER)) { Expr b = base, exp = unary(); return () -> Math.pow(b.eval(), exp.eval()); } return base; }
        private Expr primary() { Token t = current; switch (t.type) { case NUMBER: advance(); return () -> t.number; case VARIABLE: case CONSTANT: advance(); return () -> ((VariableSymbol) t.symbol).getValue(); case FUNCTION: advance(); consume(TokenType.LPAREN, "Se esperaba '('"); Expr arg = expression(); consume(TokenType.RPAREN, "Se esperaba ')'"); return () -> ((FunctionSymbol) t.symbol).apply(arg.eval()); case LPAREN: advance(); Expr e = expression(); consume(TokenType.RPAREN, "Se esperaba ')'"); return e; default: throw error("Token inesperado: " + t.lexeme, t); } }
        private boolean match(TokenType t) { if (current.type == t) { advance(); return true; } return false; }
        private void consume(TokenType t, String msg) { if (!match(t)) throw error(msg, current); }
        private void advance() { current = next; next = lexer.nextToken(); }
        private IllegalArgumentException error(String msg, Token t) { return new IllegalArgumentException(msg + " en línea " + t.line + ", columna " + t.column); }
        private static boolean truth(double v) { return v != 0.0; }
        private static String format(double v) { return Math.rint(v) == v ? Long.toString((long) v) : Double.toString(v); }
    }
}
