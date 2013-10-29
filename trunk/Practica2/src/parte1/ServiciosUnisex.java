package parte1;

import java.util.concurrent.Semaphore;

public class ServiciosUnisex {
	static Semaphore banno = new Semaphore(1);
	static Semaphore contadorPersonas = new Semaphore(4);
	
	public static void main(String[] args){
		Pecusa p_hombres= new Pecusa(banno);
		Pecusa p_mujeres= new Pecusa(banno);
		
		for(int i=0; i<5; i++){
			new Persona(i, "Hombre", p_hombres, contadorPersonas).start();	
			new Persona(i, "Mujer", p_mujeres, contadorPersonas).start();
		}
	}
}
