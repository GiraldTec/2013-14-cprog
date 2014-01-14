package parte3;

import parte1.*;

public class LectoresEscritoresReentrantes {

	static int numLectoresEscritores = 6;

	public static void main(String[] argv) {
 
		BaseDatos BD = new BaseDatos();
		ReentranteLecturaAEscritura monitor = new ReentranteLecturaAEscritura();

		try {
			for (int i = 1; i <= numLectoresEscritores; ++i) 
				new LectorEscritor(monitor, BD, "LectorEscritor"+i).start();
		} catch (Exception e) {System.out.println("Exception: " + e);}
	}
	
}





