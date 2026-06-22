package HOC6; // Manteniendo tu paquete original

public class Datum {
    private Float val;
    private SymbolHoc sym;

    // Constructor para valores numéricos directos (ej. 3.1416)
    public Datum(Float val) {
        this.val = val;
        this.sym = null;
    }

    // Constructor para referencias de la Tabla de Símbolos (Variables/Constantes)
    public Datum(SymbolHoc sym) {
        this.sym = sym;
        this.val = null;
    }

    // Getters y Setters
    public Float getVal() { return val; }
    public void setVal(Float val) { this.val = val; }
    public SymbolHoc getSym() { return sym; }
    public void setSym(SymbolHoc sym) { this.sym = sym; }
}