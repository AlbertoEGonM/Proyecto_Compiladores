package HOC3;

import java.util.ArrayList;
import java.util.List;

/**
 * Intérprete de expresiones HOC 3 usado por la interfaz Swing.
 */
public class HOC3 {
    private final SymbolTable symbols;

    public HOC3() {
        this.symbols = new SymbolTable();
    }

    public String analizarLexicamente(String source) {
        Lexer lexer = new Lexer(source == null ? "" : source, symbols);
        StringBuilder out = new StringBuilder();
        Token token;
        do {
            token = lexer.nextToken();
            if (token.type != TokenType.EOF) {
                out.append(String.format("%-14s %-12s línea %d, columna %d%n",
                        token.type, token.lexeme, token.line, token.column));
            }
        } while (token.type != TokenType.EOF);
        return out.toString();
    }

    public String analizarSintacticamente(String source) {
        Parser parser = new Parser(new Lexer(source == null ? "" : source, symbols));
        List<Double> results = parser.parseProgram();
        StringBuilder out = new StringBuilder();
        for (Double result : results) {
            out.append(format(result)).append(System.lineSeparator());
        }
        return out.toString();
    }

    private static String format(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return Double.toString(value);
        }
        if (Math.rint(value) == value) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

    private enum TokenType {
        EOF, SEMIC, PLUS, MINUS, TIMES, DIVIDE, ASSIGN, POWER, LPAREN, RPAREN, NUMBER, VARIABLE, CONSTANT, FUNCTION
    }

    private static final class Token {
        final TokenType type;
        final String lexeme;
        final double number;
        final SymbolHoc symbol;
        final int line;
        final int column;

        Token(TokenType type, String lexeme, double number, SymbolHoc symbol, int line, int column) {
            this.type = type;
            this.lexeme = lexeme;
            this.number = number;
            this.symbol = symbol;
            this.line = line;
            this.column = column;
        }
    }

    private static final class Lexer {
        private final String input;
        private final SymbolTable symbols;
        private int pos;
        private int line = 1;
        private int column = 1;

        Lexer(String input, SymbolTable symbols) {
            this.input = input;
            this.symbols = symbols;
        }

        Token nextToken() {
            skipWhitespace();
            int startLine = line;
            int startColumn = column;
            if (isAtEnd()) {
                return new Token(TokenType.EOF, "<EOF>", 0, null, startLine, startColumn);
            }
            char c = advance();
            switch (c) {
                case ';': return token(TokenType.SEMIC, ";", startLine, startColumn);
                case '+': return token(TokenType.PLUS, "+", startLine, startColumn);
                case '-': return token(TokenType.MINUS, "-", startLine, startColumn);
                case '*': return token(TokenType.TIMES, "*", startLine, startColumn);
                case '/': return token(TokenType.DIVIDE, "/", startLine, startColumn);
                case '=': return token(TokenType.ASSIGN, "=", startLine, startColumn);
                case '^': return token(TokenType.POWER, "^", startLine, startColumn);
                case '(': return token(TokenType.LPAREN, "(", startLine, startColumn);
                case ')': return token(TokenType.RPAREN, ")", startLine, startColumn);
                default:
                    if (Character.isDigit(c) || c == '.') {
                        return number(c, startLine, startColumn);
                    }
                    if (Character.isLetter(c)) {
                        return identifier(c, startLine, startColumn);
                    }
                    throw error("Caracter no reconocido: '" + c + "'", startLine, startColumn);
            }
        }

        private Token number(char first, int startLine, int startColumn) {
            StringBuilder lexeme = new StringBuilder().append(first);
            boolean hasDot = first == '.';
            while (!isAtEnd()) {
                char c = peek();
                if (Character.isDigit(c)) {
                    lexeme.append(advance());
                } else if (c == '.' && !hasDot) {
                    hasDot = true;
                    lexeme.append(advance());
                } else {
                    break;
                }
            }
            if (lexeme.toString().equals(".")) {
                throw error("Número incompleto", startLine, startColumn);
            }
            return new Token(TokenType.NUMBER, lexeme.toString(), Double.parseDouble(lexeme.toString()), null, startLine, startColumn);
        }

        private Token identifier(char first, int startLine, int startColumn) {
            StringBuilder lexeme = new StringBuilder().append(first);
            while (!isAtEnd() && Character.isLetterOrDigit(peek())) {
                lexeme.append(advance());
            }
            String name = lexeme.toString();
            SymbolHoc symbol = symbols.lookup(name);
            if (symbol == null) {
                symbol = new VariableSymbol(name, AnalizadorSintacticoSym.VAR);
                symbols.install(symbol);
            }
            if (symbol instanceof FunctionSymbol) {
                return new Token(TokenType.FUNCTION, name, 0, symbol, startLine, startColumn);
            }
            if (symbol instanceof VariableSymbol && ((VariableSymbol) symbol).isConstant()) {
                return new Token(TokenType.CONSTANT, name, 0, symbol, startLine, startColumn);
            }
            return new Token(TokenType.VARIABLE, name, 0, symbol, startLine, startColumn);
        }

        private void skipWhitespace() {
            while (!isAtEnd() && Character.isWhitespace(peek())) {
                advance();
            }
        }

        private Token token(TokenType type, String lexeme, int line, int column) {
            return new Token(type, lexeme, 0, null, line, column);
        }

        private boolean isAtEnd() { return pos >= input.length(); }
        private char peek() { return input.charAt(pos); }
        private char advance() {
            char c = input.charAt(pos++);
            if (c == '\n') { line++; column = 1; } else { column++; }
            return c;
        }
        private IllegalArgumentException error(String msg, int line, int column) {
            return new IllegalArgumentException(msg + " en línea " + line + ", columna " + column);
        }
    }

    private static final class Parser {
        private final Lexer lexer;
        private Token current;
        private Token next;

        Parser(Lexer lexer) {
            this.lexer = lexer;
            this.current = lexer.nextToken();
            this.next = lexer.nextToken();
        }

        List<Double> parseProgram() {
            List<Double> results = new ArrayList<>();
            while (current.type != TokenType.EOF) {
                if (match(TokenType.SEMIC)) {
                    continue;
                }
                results.add(expression());
                consume(TokenType.SEMIC, "Se esperaba ';' al final de la expresión");
            }
            return results;
        }

        private double expression() { return assignment(); }

        private double assignment() {
            if (current.type == TokenType.VARIABLE && next.type == TokenType.ASSIGN) {
                Token variable = current;
                advance();
                advance();
                double value = assignment();
                ((VariableSymbol) variable.symbol).setValue(value);
                return value;
            }
            if (current.type == TokenType.CONSTANT && next.type == TokenType.ASSIGN) {
                throw error("No se puede reasignar la constante " + current.lexeme, current);
            }
            return additive();
        }

        private double additive() { return additiveFrom(multiplicative()); }

        private double additiveFrom(double left) {
            while (current.type == TokenType.PLUS || current.type == TokenType.MINUS) {
                TokenType op = current.type;
                advance();
                double right = multiplicative();
                left = op == TokenType.PLUS ? left + right : left - right;
            }
            return left;
        }

        private double multiplicative() {
            double left = unary();
            while (current.type == TokenType.TIMES || current.type == TokenType.DIVIDE) {
                TokenType op = current.type;
                advance();
                double right = unary();
                left = op == TokenType.TIMES ? left * right : left / right;
            }
            return left;
        }

        private double unary() {
            if (match(TokenType.MINUS)) {
                return -unary();
            }
            return power();
        }

        private double power() {
            double base = primary();
            if (match(TokenType.POWER)) {
                return Math.pow(base, unary());
            }
            return base;
        }

        private double primary() {
            Token token = current;
            switch (token.type) {
                case NUMBER:
                    advance();
                    return token.number;
                case VARIABLE:
                case CONSTANT:
                    advance();
                    return ((VariableSymbol) token.symbol).getValue();
                case FUNCTION:
                    advance();
                    consume(TokenType.LPAREN, "Se esperaba '(' después de la función " + token.lexeme);
                    double argument = expression();
                    consume(TokenType.RPAREN, "Se esperaba ')' después del argumento");
                    return ((FunctionSymbol) token.symbol).apply(argument);
                case LPAREN:
                    advance();
                    double value = expression();
                    consume(TokenType.RPAREN, "Se esperaba ')' para cerrar la expresión");
                    return value;
                default:
                    throw error("Token inesperado: " + token.lexeme, token);
            }
        }

        private boolean match(TokenType type) {
            if (current.type == type) {
                advance();
                return true;
            }
            return false;
        }

        private void consume(TokenType type, String message) {
            if (!match(type)) {
                throw error(message, current);
            }
        }

        private void advance() {
            current = next;
            next = lexer.nextToken();
        }
        private IllegalArgumentException error(String msg, Token token) {
            return new IllegalArgumentException(msg + " en línea " + token.line + ", columna " + token.column);
        }
    }
}
