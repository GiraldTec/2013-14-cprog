package parte4;

import parte1.BaseDatos;


public class LectoresEscritoresReentrantes {

	static int numLectoresEscritores = 6;

	public static void main(String[] argv) {
 
		BaseDatos BD = new BaseDatos();
		ReentranteTotal monitor = new ReentranteTotal();

		try {
			for (int i = 1; i <= numLectoresEscritores; ++i) 
				new LectorEscritor(monitor, BD, "LectorEscritor"+i).start();
		} catch (Exception e) {System.out.println("Exception: " + e);}
	}
	
}






