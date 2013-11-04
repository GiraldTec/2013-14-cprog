package parte2;

import java.util.concurrent.Semaphore;

import parte1.Pecusa;
import parte1.Persona;

public class ServiciosUnisex {
		
	public static void main(String[] args){
		Semaphore banno = new Semaphore(1);
		Semaphore contadorPersonas = new Semaphore(4);
		Semaphore torniquete = new Semaphore(1);
		
		Pecusa p_hombres= new Pecusa(banno);
		Pecusa p_mujeres= new Pecusa(banno);
		
		if(args.length==1){
			if(args[0].equals("starve")){
				for(int i=0; i<5; i++){
					new Persona(i, "Hombre", p_hombres, contadorPersonas).start();	
					new Persona(i, "Mujer", p_mujeres, contadorPersonas).start();
				}
			}
			if(args[0].equals("nostarve")){
				for(int i=0; i<5; i++){
					new PersonaNoStarve(i, "Hombre", p_hombres, contadorPersonas,torniquete).start();	
					new PersonaNoStarve(i, "Mujer", p_mujeres, contadorPersonas,torniquete).start();
				}
			}
			
		}
	}
}
