package sintactico;

import java.util.Objects;

public class Simbolo {
    public static Simbolo SimbEPS = new Simbolo(){{ // Su token asociado debe ser el dado en SimbESP.Epsilon
            this.Nombre = "epsilon";
            this.Terminal = true;
        }};

    public static Simbolo SimboloFinal = new Simbolo(){ // Su token asociado debe ser el dado en SimbESP.SimboloFinal
        {
            this.Nombre = String.valueOf('$');
            this.Terminal = true;
        }
    };

    public String Nombre;
    public boolean Terminal;

    public Simbolo(){}

    public Simbolo(String Nombre, boolean Terminal){
        this.Nombre = Nombre;
        this.Terminal = Terminal;
    }

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
