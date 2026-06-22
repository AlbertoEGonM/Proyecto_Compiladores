package HOC6;

public abstract class SymbolHoc {
    protected String name;
    protected int tokenType; // Almacenará valores como VAR o BLTIN

    public SymbolHoc(String name, int tokenType) {
        this.name = name;
        this.tokenType = tokenType;
    }

    public String getName() {
        return name;
    }

    public int getTokenType() {
        return tokenType;
    }
}
