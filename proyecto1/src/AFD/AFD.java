package AFD;
import AFN.AFN;
import AFN.Estado;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

public class AFD {
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
		
		this.numEstadosSj = NumSj;

		// ImprimirAFD(R); Parte del Test: Imprime el AFD generado a partir del AFN

		/* Proceso Para convertir el map R en Un arreglo Bidimensional y guardarlo en un txt */
		TablaAFD = new int[NumSj][256];
		
		for(Sj s : R.values())
            System.arraycopy(s.Transiciones, 0, TablaAFD[s.j], 0, 256);
		

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
