package AFD;
import AFN.AFN;
import AFN.Estado;
import AFN.SimbESP;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;

public class AFD implements java.io.Serializable {
	public static AFD afdAsignado = null; // AFD resultante de la conversion de un AFN, se declara como variable global para poder mostrarlo en la ventana de conversion.

	public static final long serialVersionUID = 1L; // Para la serialización del AFD, se asigna un ID único para evitar problemas de compatibilidad al guardar y cargar objetos AFD desde archivos binarios.

	public int numEstadosSj;
	public HashSet<Character> Alfabeto;
	public int [][] TablaAFD;
	public String ExpRegular;

    @SuppressWarnings("OverridableMethodCallInConstructor")
	public AFD(AFN F){
		numEstadosSj = 0;
		Alfabeto = F.Alfabeto;
		ExpRegular = F.E_Regular;
		ConvertirAFD(F);
	}

	@SuppressWarnings("OverridableMethodCallInConstructor")
	public AFD(String Ruta){
		AFD afd = AbrirArchivoBin(Ruta);
		if(afd != null){
			this.numEstadosSj = afd.numEstadosSj;
			this.Alfabeto = afd.Alfabeto;
			this.TablaAFD = afd.TablaAFD;
			this.ExpRegular = afd.ExpRegular;
		}
		else{
			System.out.println("Error al cargar el AFD desde el archivo: " + Ruta);
		}
	}

    public void ConvertirAFD(AFN F){
		
		Stack<Sj> Q = new Stack<>();
		Map<HashSet<Estado>,Sj> R = new HashMap<>();
		HashSet<Estado> ConjTemp;
		
		int NumSj = 0;

		Sj SjEval = new Sj();
		Sj SjTemp;

		// Estado Inicial del AFD:: S_0
		SjEval.ConjuntoEdo.addAll(F.CerraduraEpsilon(F.EdoInicial));
		SjEval.EstablecerEsFinal(F);
		SjEval.j=NumSj++;

		R.put(SjEval.ConjuntoEdo,SjEval);
		Q.add(SjEval);
		
		while(!Q.isEmpty()){
			SjEval = Q.pop(); // Obtiene el Sj en la cola
			
			for(char a : F.Alfabeto){
				ConjTemp = F.IrA(SjEval.ConjuntoEdo, a);
				
				if(R.containsKey(ConjTemp)){
					SjEval.AgregarTransicion(R.get(ConjTemp),a);
				}
				else if(!ConjTemp.isEmpty()){
					SjTemp = new Sj(NumSj++,ConjTemp,F);
					SjEval.AgregarTransicion(SjTemp,a);
					Q.add(SjTemp);
					R.put(ConjTemp,SjTemp);
				}
			}
		}

		// Estado de simbolos omitir.

		SjTemp = new Sj(NumSj++, null, F);
		SjTemp.EsFinal = true;
		SjTemp.Token = SimbESP.Omitir;
		SjTemp.Transiciones[255] = SjTemp.Token;
		
		this.numEstadosSj = NumSj;

		// ImprimirAFD(R); Parte del Test: Imprime el AFD generado a partir del AFN

		/* Proceso Para convertir el map R en Un arreglo Bidimensional y guardarlo en un txt */
		TablaAFD = new int[NumSj][256];

		System.arraycopy(SjTemp.Transiciones, 0, TablaAFD[NumSj-1], 0, 256);
		
		for(Sj s : R.values()){
			for(Character O : SimbESP.SimbolosOmitir)
				s.AgregarTransicion(SjTemp, O);
            System.arraycopy(s.Transiciones, 0, TablaAFD[s.j], 0, 256);
		}


		// ImprimirTablaAFD(); Parte del Test: Imprime la tabla de transiciones del AFD generado a partir del AFN

	}

	public void GuardarArchivoBin(String Ruta){
		try(ObjectOutputStream file = new ObjectOutputStream(new FileOutputStream(Ruta))){
			file.writeObject(this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public AFD AbrirArchivoBin(String Ruta){
		try(ObjectInputStream file = new ObjectInputStream(new FileInputStream(Ruta))){
			AFD afd = (AFD) file.readObject();
			return afd;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static String[][] getSumTable() {
		String [][] sumTable = new String[afdAsignado.numEstadosSj ][afdAsignado.Alfabeto.size()+2 + SimbESP.SimbolosOmitir.size()];
		TreeSet<Character> alfabeto = AFD.getAlfabeto();


		for(int i = 0; i < afdAsignado.numEstadosSj; i++) {
			sumTable[i][0] = "S" + i;

			int z=1;
			for(Character a : alfabeto){
				if(afdAsignado.TablaAFD[i][a] != -1)
					sumTable[i][z] = "" + afdAsignado.TablaAFD[i][a];
				z++;
			}
			for(Character O : SimbESP.SimbolosOmitir){
				if(afdAsignado.TablaAFD[i][O] != -1)
					sumTable[i][z] = "" + afdAsignado.TablaAFD[i][O];
				z++;
			}

			/*for(int j = 0, z = 1; j < 255; j++){
				if(afdAsignado.TablaAFD[i][j] != -1){
					sumTable[i][z++] = "" + afdAsignado.TablaAFD[i][j];
				}
			}*/
			sumTable[i][afdAsignado.Alfabeto.size()+1+ SimbESP.SimbolosOmitir.size()] = ""+ afdAsignado.TablaAFD[i][255];
		}

		return sumTable;
	}

	public static String[] CabeceraTabla(){
        String [] cabecera = new String[AFD.afdAsignado.Alfabeto.size() + 2 + SimbESP.SimbolosOmitir.size()];
        cabecera[0] = "Estado";
        TreeSet<Character> alfabeto = AFD.getAlfabeto();
        int i = 1;
        for(Character c : alfabeto){
            cabecera[i] = String.valueOf(c);
            i++;
        }
		for(Character O : SimbESP.SimbolosOmitir){
			cabecera[i] = String.valueOf(O);
			i++;
		}
        
        cabecera[AFD.afdAsignado.Alfabeto.size() + 1 + SimbESP.SimbolosOmitir.size()] = "Token";
        
        return cabecera;
    };

	public static TreeSet<Character> getAlfabeto() {
		TreeSet<Character> set = new TreeSet<>();
		set.addAll(afdAsignado.Alfabeto);
		return set;
	}

	/*	Habilitar en caso de Test para imprimir el AFD generado a partir del AFN
	void ImprimirAFD(Map<HashSet<Estado>,Sj> R){
		System.out.println("AFD:");
		for(Sj s : R.values()){
			System.out.println("Estado: " + s.j + " Conjunto de Estados: " + ImprimirEstadosAFD(s.ConjuntoEdo) + " Es Final: " + s.EsFinal);
			for(int i = 0; i < 256; i++){
				if(s.Transiciones[i] != -1){
					System.out.println("  Transicion con '" + (char)i + "' a estado: " + s.Transiciones[i]);
				}
			}
		}
	}

	String ImprimirEstadosAFD(HashSet<Estado> ConjuntoEdo){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for(Estado e : ConjuntoEdo){
			sb.append(e.IdEdo).append(", ");
		}
		sb.append("}");
		return sb.toString();
	}
    
	void ImprimirTablaAFD(){
		System.out.println("Tabla de Transiciones del AFD:");
		for(int i = 0; i < TablaAFD.length; i++){
			System.out.print("Estado " + i + ": ");
			for(int j = 0; j < TablaAFD[i].length; j++){
				if(TablaAFD[i][j] != -1){
					System.out.print("'" + (char)j + "'->" + TablaAFD[i][j] + " ");
				}
			}
			System.out.println();
		}
	}
	*/
}
