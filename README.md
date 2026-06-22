# Analizador de Autómatas — ESCOM

Proyecto académico de la materia de **Teoría de la Computación / Compiladores**. Implementa, desde cero y con interfaz gráfica en Java Swing, el flujo completo de un compilador simplificado:

> **Expresión Regular → AFN (Thompson) → AFD (Subconjuntos) → Analizador Léxico → Analizador Sintáctico (LL(1) / LR(0))**

Incluye además un intérprete tipo *HOC* (calculadora de expresiones aritméticas) generado con **JFlex** y **CUP**, como ejercicio independiente de análisis léxico-sintáctico clásico.

---

## Tabla de contenido

- [Características](#-características)
- [Arquitectura del proyecto](#-arquitectura-del-proyecto)
- [Estructura de carpetas](#-estructura-de-carpetas)
- [Flujo de trabajo](#-flujo-de-trabajo)
- [Requisitos](#-requisitos)
- [Cómo ejecutar](#-cómo-ejecutar)
- [Guía de uso (GUI)](#-guía-de-uso-gui)
- [Detalles técnicos por módulo](#-detalles-técnicos-por-módulo)
- [Formato de archivos `.afnd`](#-formato-de-archivos-afnd)
- [Notas y limitaciones conocidas](#-notas-y-limitaciones-conocidas)

---

## Características

- **Construcción de AFN's** mediante el algoritmo de Thompson:
  - AFN básico de un símbolo o de un rango `[a-z]`.
  - Unión (`|`), concatenación (`°`), cerradura positiva (`+`), cerradura de Kleene (`*`) y cerradura opcional (`?`).
  - Conversión de **Expresión Regular → AFN** mediante descenso recursivo (`lexico.ERaAFN`) o mediante shunting-yard + evaluación postfija (`AFN.GeneradorAFN`).
- **Conversión AFN → AFD** por el algoritmo de construcción de subconjuntos (`AFD.ConvertirAFD`), con cerradura-ε e `IrA`.
- **Serialización de AFD's** a archivos binarios `.afnd` para reutilizarlos como analizador léxico de otros módulos (gramáticas, LL(1), LR(0)).
- **Analizador Léxico genérico** (`lexico.AnalisisLexico`) que recorre cualquier AFD serializado usando el algoritmo clásico de *maximal munch* (más largo lexema válido), con soporte de *undo* de tokens para *backtracking* en el análisis descendente.
- **Analizador Sintáctico LL(1)**: cálculo de conjuntos **First/Follow**, construcción de la tabla predictiva y traza paso a paso (pila / entrada / acción).
- **Analizador Sintáctico LR(0)**: cálculo de colecciones canónicas de items LR(0), cerraduras, función `IrA`, tabla de acciones (`d`/`r`/`acc`) y traza de análisis tipo *shift-reduce*.
- **Visualización gráfica interactiva** de AFN's y AFD's como grafos (nodos arrastrables, paneo del lienzo, curvas para transiciones que se sobrelapan, exportación a PNG).
- **Mini-IDE "HOC 3"**: calculadora con variables, constantes (`PI`, `E`, `PHI`, …) y funciones (`sin`, `cos`, `sqrt`, …), con un analizador léxico/sintáctico escrito a mano y una especificación equivalente en **JFlex/CUP** (`.flex` / `.cup`) para fines comparativos/educativos.

---

## Arquitectura del proyecto

```
                ┌─────────────┐
   Expresión    │  lexico.    │
   Regular ───► │  ERaAFN /   │
                │ GeneradorAFN│
                └─────┬───────┘
                      │ construye
                      ▼
                ┌────────────┐        Thompson         ┌────────────┐
                │  AFN.AFN   │ ── Unir/Concat/Cerr ──► │  AFN.AFN   │
                └─────┬──────┘                         └─────┬──────┘
                      │ ConvertirAFD()
                      ▼
                ┌────────────┐
                │  AFD.AFD   │  (tabla de transiciones int[][257])
                └─────┬──────┘
                      │ GuardarArchivoBin() / AbrirArchivoBin()
                      ▼
                ┌────────────┐
                │   *.afnd   │  (serialización Java)
                └─────┬──────┘
                      │ usado por
                      ▼
            ┌──────────────────────┐
            │ lexico.AnalisisLexico│  (yylex, maximal munch)
            └─────────┬────────────┘
                      │ tokens
                      ▼
        ┌────────────────────────────────────┐
        │ sintactico.Gramatica (First/Follow)│
        └───────┬───────────────┬────────────┘
                ▼               ▼
        ┌───────────────┐  ┌───────────────┐
        │ sintactico.LL1│  │ sintactico.LR0│
        └───────────────┘  └───────────────┘
```

Toda la interacción del usuario ocurre a través de **GUI.Panel**, que despliega un menú con ventanas modales (`JDialog`) para cada operación.

---

##  Estructura de carpetas

```
proyecto1/src/
├── Main.java                      # Punto de entrada (lanza GUI.Panel)
│
├── AFN/                            # Construcción de Autómatas Finitos No deterministas
│   ├── AFN.java                    # Operaciones de Thompson + cerradura-ε / IrA
│   ├── Estado.java                 # Nodo del AFN (transiciones, aceptación, token)
│   ├── Transicion.java             # Arista con rango [Simbolo1, Simbolo2]
│   └── SimbESP.java                # Símbolos especiales (Epsilon, Fin, Error, Omitir)
│
├── GeneradorAFN/
│   └── GeneradorAFN.java           # ER → postfix → AFN (shunting-yard), package AFN
│
├── AFD/                             # Conversión AFN → AFD
│   ├── AFD.java                    # Construcción de subconjuntos + serialización .afnd
│   └── Sj.java                     # Subconjunto de estados / nodo del AFD
│
├── lexico/                          # Analizador léxico genérico + ER→AFN por descenso recursivo
│   ├── AnalisisLexico.java         # yylex(), UndoToken(), AnalisisSimple()
│   ├── ERaAFN.java                 # Gramática de expresiones regulares (descenso recursivo)
│   └── StatusLexico.java           # Snapshot del estado del lexer (para backtracking)
│
├── sintactico/                      # Análisis sintáctico LL(1) y LR(0)
│   ├── Gramatica.java               # Reglas, First, Follow (+ parser de texto de gramática)
│   ├── Simbolo.java                 # Terminal / No terminal
│   ├── LadoIzq.java                 # Una producción (cabeza + lista de símbolos)
│   ├── LL1.java                     # Tabla predictiva LL(1) + traza
│   └── LR0.java                     # Colección canónica de items, tabla LR(0) + traza
│
├── HOC3/                            # Mini calculadora (independiente del resto del proyecto)
│   ├── HOC3.java                    # Lexer + Parser de descenso recursivo "a mano"
│   ├── SymbolTable.java / SymbolHoc.java / VariableSymbol.java / FunctionSymbol.java
│   ├── AnalizadorLexico.flex        # Especificación JFlex equivalente
│   ├── AnalizadorLexico.java        # Salida generada por JFlex
│   ├── AnalizadorSintacticoSym.java # Símbolos generados por CUP
│   └── SintacHOC3.cup               # Gramática CUP equivalente
│
└── GUI/                              # Interfaz gráfica (Swing)
    ├── Panel.java                    # Ventana principal / menú raíz
    ├── FormularioAutomatas.java       # Variante alterna de la ventana principal
    ├── VentanaBasico.java             # Crear AFN básico / de rango
    ├── VentanaUnir.java               # Unión de dos AFN's
    ├── VentanaConcatenar.java         # Concatenación de dos AFN's
    ├── VentanaCerraduraPositiva.java  # Cerradura (+)
    ├── VentanaCerraduraEstrella.java  # Cerradura de Kleene (*)
    ├── VentanaOpcional.java           # Cerradura opcional (?)
    ├── VentanaERaAFN.java             # ER → AFN (vía lexico.ERaAFN)
    ├── VentanaGenerarAFN.java         # ER → AFN (vía AFN.GeneradorAFN)
    ├── VentanaUnionLexico.java        # Asignación de tokens + unión para analizador léxico
    ├── VentanaConvertirAFNaAFD.java   # AFN → AFD + tabla resumen
    ├── VentanaAnalizarCadena.java     # (placeholder) analizar una cadena contra un AFD
    ├── VentanaProbarLexico.java       # Probar el AFD asignado como analizador léxico
    ├── VentanaAFNS.java               # Listado/selección de AFN's existentes
    ├── VentanaGrafo.java              # Visualización de grafos (AFN y AFD) + exportar PNG
    ├── VentanaLL1.java                # Flujo completo de análisis LL(1)
    ├── VentanaLR.java                 # Flujo completo de análisis LR(0)
    └── VentanaHOC3.java               # Interfaz de la calculadora HOC 3
```

---

## Flujo de trabajo

### 1. Generar uno o varios AFN's
- **Básico**: un símbolo o un rango de caracteres (`AFN/Básico`).
- **Combinarlos**: Unir, Concatenar, Cerradura +/*/? sobre AFN's ya existentes (se seleccionan por su expresión regular en un `JComboBox`).
- **Desde una expresión regular** completa: `ER -> AFN` (descenso recursivo) o `Generar AFN` (shunting-yard).

### 2. Asignar tokens y unir para análisis léxico
En **`Unión para Analizador Léxico`** se seleccionan los AFN's finales, se les asigna un token entero a cada estado de aceptación y, opcionalmente, se fusionan en un único AFN (super-unión) listo para convertirse en AFD.

### 3. Convertir a AFD
**`Convertir AFN a AFD`** aplica construcción de subconjuntos y deja el resultado en `AFD.afdAsignado` (variable estática global), mostrando la tabla de transiciones.

### 4. Guardar / cargar el AFD
Desde el menú **AFD's**: `Guardar AFD en bin` / `Cargar AFD de un Bin`, usando archivos con extensión `.afnd` (serialización estándar de Java).

### 5. Usarlo como analizador léxico
- **`Probar analizador Léxico`**: tokeniza un texto libre contra `AFD.afdAsignado`.
- **`Analisis LL1` / `Analisis LR0`**: cargan un `.afnd` propio, una gramática en texto, mapean tokens a símbolos terminales y ejecutan el análisis sintáctico completo mostrando la traza.

---

## Requisitos

- **JDK 17+** (se usan *text blocks* `"""..."""`, *switch expressions* con `yield`, y `List.removeFirst()`/`addFirst()` de listas secuenciadas — esto último requiere **Java 21+**).
- Sin gestor de dependencias externo (no usa Maven/Gradle): el código se compila directamente con `javac`.
- Para regenerar `HOC3/AnalizadorLexico.java` o el parser CUP se necesitarían las herramientas **JFlex** y **CUP**, pero el repositorio ya incluye el código generado, por lo que no son obligatorias para compilar y ejecutar el proyecto.

>  El módulo `HOC3` depende de clases (`java_cup.runtime.Symbol`, `Scanner`, etc.) de la librería **java-cup-runtime**. Si vas a compilar ese paquete, asegúrate de tener ese `.jar` en el classpath.

---

##  Cómo ejecutar

### Desde línea de comandos

```bash
cd proyecto1/src

# Compilar todo el proyecto (omite HOC3 si no tienes java-cup-runtime.jar)
javac -d ../out $(find . -name "*.java" -not -path "./HOC3/*")

# Ejecutar
java -cp ../out Main
```

Si vas a incluir `HOC3` (requiere `java-cup-runtime.jar` en el classpath):

```bash
javac -cp .:java-cup-runtime.jar -d ../out $(find . -name "*.java")
java -cp ../out:java-cup-runtime.jar Main
```

### Desde un IDE (NetBeans / IntelliJ / Eclipse)
1. Importar `proyecto1` como proyecto Java existente, con `src` como carpeta fuente raíz.
2. Marcar `Main.java` como clase principal.
3. Ejecutar `Main`.

---

## Guía de uso (GUI)

Al iniciar, **`Panel`** muestra tres menús:

| Menú | Acciones disponibles |
|---|---|
| **AFN's** | Básico, Unir, Concatenar, Cerradura +, Cerradura *, Opcional, ER→AFN, Unión para Analizador Léxico, Convertir AFN a AFD, Analizar una Cadena, Probar analizador Léxico, Mostrar AFNs |
| **AFD's** | Guardar AFD en bin, Cargar AFD de un Bin, Mostrar AFD |
| **Analisis Sintáctico** | Analisis LL1, Analisis LR0, HOC 3 |

> Cada AFN creado queda registrado en `AFN.ColeccAFNs` (colección estática) y se identifica por su **expresión regular** en los `JComboBox` de las ventanas siguientes — por eso conviene crear los AFN's en orden, de los más simples a los más complejos.

### Ejemplo de gramática para LL(1) (incluida por defecto)

```
E->T E';
E'-> + T E' | -T E' | epsilon;
T-> F T';
T'-> * F T' | / F T' | epsilon;
F-> (E)| num;
```

### Ejemplo de gramática para LR(0) (incluida por defecto)

```
E'-> E;
E->E + T|E - T|T;
T->T*F|T/F|F;
F->(E)|num;
```

En ambos casos, los **símbolos terminales** detectados (`+`, `-`, `*`, `/`, `(`, `)`, `num`) deben mapearse manualmente a los **tokens enteros** que produce el AFD léxico cargado, antes de calcular la tabla predictiva/canónica.

---

## Detalles técnicos por módulo

### `AFN`
- `Estado` numera sus instancias con un contador estático (`ContadorEdo`), por lo que los IDs son únicos y crecientes durante toda la ejecución del programa (no se reinician entre AFN's).
- Las transiciones (`Transicion`) representan rangos `[Simbolo1, Simbolo2]`; un símbolo único es un rango degenerado `(c, c)`.
- `SimbESP.Epsilon` se codifica como el carácter `(char) 5` (no imprimible) para no chocar con simbología real del alfabeto.
- `CerraduraEpsilon`, `Mover` e `IrA` implementan exactamente el algoritmo clásico de subconjuntos (Aho/Sethi/Ullman, *Dragon Book*).

### `AFD`
- La tabla de transiciones es un arreglo `int[numEstados][257]`: las columnas `0..255` son caracteres ASCII y la columna **256** almacena el **token** asociado si el estado es de aceptación (`-1` si no lo es).
- Cuando un subconjunto de estados contiene **más de un estado de aceptación** (AFN's unidos con distintos tokens), se asigna el **token mínimo** como regla de resolución de ambigüedad (`Sj.EstablecerEsFinal`).
- `AFD.afdAsignado` es un campo **estático global**: solo existe "un AFD activo" a la vez en toda la aplicación, compartido entre todas las ventanas.

### `lexico.AnalisisLexico`
- Implementa **maximal munch**: avanza carácter por carácter mientras existan transiciones válidas, recordando el último estado de aceptación visitado (`FinLexema`, `PasoPorEdoAcept`).
- Si ningún prefijo es aceptado, consume un solo carácter como lexema de **error** (`SimbESP.Error`).
- Los tokens cuyo valor sea `SimbESP.Omitir` (espacios, saltos de línea, tabulaciones) se descartan internamente y el método sigue iterando hasta encontrar el siguiente token real.
- `UndoToken()` / `Pila` permiten retroceder al índice de carácter anterior — usado intensivamente por los parsers de descenso recursivo (`ERaAFN`, `DescensoRecursivo` de `Gramatica`) para *lookahead* con retroceso.

### `sintactico.Gramatica`
- Parsea una gramática en texto plano (formato `NoTerminal -> alt1 | alt2 | ... ;`) usando su **propio AFD léxico precompilado** (`AFDGram.afnd`), vía descenso recursivo interno (`DescensoRecursivo`).
- `First` y `Follow` están memoizados en `FirmMap`/`FollMap` y se calculan de forma recursiva/perezosa, con protección básica contra recursión infinita (se inserta un conjunto vacío antes de recursar).

### `sintactico.LL1`
- Requiere mapear cada `Simbolo` terminal a un token entero (tabla `VT`) y cada No Terminal a un índice (`VNT`), ambos mediante `hashCode()` + búsqueda binaria (`Arrays.binarySearch`), por lo que los arreglos deben mantenerse **ordenados**.
- `init_Table()` llena la tabla predictiva usando `First`/`Follow` por cada producción.
- `AnalizarYRegistrar()` ejecuta el algoritmo estándar de pila para análisis predictivo y devuelve una traza completa (pila, entrada restante, acción) lista para mostrarse en una `JTable`.

### `sintactico.LR0`
- Construye la colección canónica de conjuntos de items LR(0) (`itemLR0`, `Conj_Sj`) mediante `Cerradura` e `IrA`.
- La tabla resultante (`TablaLR`) usa strings: `"dN"` (desplazar a estado N), `"rN"` (reducir por regla N), `"acc"` (aceptar) o `"-1"` (error/vacío).
- `AnalisisLR()` simula el algoritmo *shift-reduce* clásico con una pila de estados/símbolos y genera la traza de la derivación.

### `GUI.VentanaGrafo`
- Dibuja nodos y aristas con `Graphics2D`; soporta arrastre de nodos, paneo del lienzo (clic y arrastre fuera de un nodo) y curvas Bézier cuadráticas cuando detecta transiciones en ambos sentidos entre dos nodos (`Arista.Sobrelapa`).
- Exporta el grafo actual a PNG mediante `BufferedImage` + `paintAll`.

### `HOC3`
- Es un módulo **autocontenido y desacoplado** del resto del proyecto (no usa `AFN`/`AFD`/`lexico`). Implementa su propio lexer/parser de descenso recursivo a mano en `HOC3.java`.
- Adicionalmente incluye una especificación equivalente en **JFlex** (`.flex`) y **CUP** (`.cup`) con fines didácticos/comparativos; el código Java ya generado a partir de ellas está incluido en el repositorio.

---

## Formato de archivos `.afnd`

Son simplemente objetos `AFD.AFD` serializados con `ObjectOutputStream` (serialización nativa de Java), guardados con extensión convencional `.afnd`. **No son portables entre versiones incompatibles de la clase `AFD`** (cambios en sus campos pueden invalidar archivos antiguos, ya que se usa `serialVersionUID = 1L` fijo).

Algunos módulos esperan rutas **absolutas hardcodeadas** a archivos `.afnd` específicos:

```java
// lexico/ERaAFN.java
"proyecto1\\src\\lexico\\afdER1.afnd"

// sintactico/Gramatica.java (DescensoRecursivo)
"proyecto1\\src\\sintactico\\AFDGram.afnd"
```

>  Estas rutas usan separadores de Windows (`\\`) y son relativas al directorio desde donde se ejecuta el programa. Si ejecutas el proyecto en otro sistema operativo o desde otra carpeta de trabajo, **debes generar y/o ajustar la ubicación de estos archivos `.afnd`** (representando el AFD de las expresiones regulares propias y el de la gramática textual, respectivamente) para que esas funcionalidades operen correctamente.

---

## Notas y limitaciones conocidas

- **Estado global compartido**: `AFD.afdAsignado` y `AFN.ColeccAFNs` son estáticos; abrir varias ventanas o reiniciar un flujo a medias puede dejar datos de una sesión anterior visibles en los `JComboBox`.
- Algunas ventanas del menú principal (`VentanaUnionLexico`, `VentanaAnalizarCadena`) son simplificaciones o placeholders parcialmente funcionales — revisa el código fuente de cada una antes de asumir su comportamiento.
- `FormularioAutomatas.java` es una versión más antigua/alterna de la ventana principal; la que realmente lanza `Main.java` es `GUI.Panel`.
- Los caracteres del alfabeto se truncan a 1 byte (`c > 255 → c = 255` en `AnalisisLexico.yylex`), por lo que el soporte de Unicode más allá de Latin-1 es limitado.
- No hay pruebas unitarias automatizadas incluidas; la verificación se ha hecho manualmente vía la interfaz gráfica.

---

## 👥 Créditos

Proyecto desarrollado como práctica de la materia de **Teoría de la Computación / Diseño de Compiladores** — ESCOM.
