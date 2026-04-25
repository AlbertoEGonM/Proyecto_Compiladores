package sintactico;

import java.util.Objects;

import AFN.SimbESP;

public class Simbolo {
    public static Simbolo SimbEPS = new Simbolo(){{
            this.Nombre = String.valueOf(SimbESP.Epsilon);
            this.Terminal = true;
        }};

    public static Simbolo SimboloFinal = new Simbolo(){
        {
            this.Nombre = String.valueOf(SimbESP.Fin);
            this.Terminal = true;
        }
    };

    public String Nombre;
    public boolean Terminal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Simbolo simbolo = (Simbolo) o;
        return Objects.equals(Nombre, simbolo.Nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Nombre);
    }
}
