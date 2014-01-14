package parte4aux;

import parte1.*;

public class LectoresEscritores {

	static int numLectores = 4; 
	static int numEscritores = 2; 

	public static void main(String[] argv) {
 
		BaseDatos BD = new BaseDatos();
		ReentranteCasiTotal monitor = new ReentranteCasiTotal();

		try {
			for (int i = 1; i <= numLectores; ++i) 
				new LectorReentrante(monitor, BD, "Lector"+i).start();
			for (int i = 1; i <= numEscritores; ++i) 
				new EscritorReentranteLee(monitor, BD, "Escritor"+i).start();
		} catch (Exception e) {System.out.println("Exception: " + e);}
	}
	
}





