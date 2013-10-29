package parte1;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Persona extends Thread {
	static Random rnd = new Random();
	
	final String sexo;
	final int id;
	
	// PECUSA que controla el acceso múltiple al servicio
	static Pecusa disponibilidad;
	static Semaphore contadorPersonas;
	
	
	Persona(int id, String sexo, Pecusa disponibilidad, Semaphore contadorPersonas){
		this.sexo = sexo;
		this.id = id;
		this.disponibilidad =disponibilidad;
		this.contadorPersonas = contadorPersonas;
	}
	
	public void trabajar(){
		try {
			sleep(rnd.nextInt(1000));
		} catch (Exception e) {
			System.err.println("Interrupted while sleeping");
		}
	}
	
	public void utilizarServicios(){
		try {
			sleep(rnd.nextInt(1000));
		} catch (Exception e) {
			System.err.println("Interrupted while sleeping");
		}
	}
	
	public void run(){
		while(true){
			trabajar();
			disponibilidad.cerrar_al_otro_genero_si_primero();
				 // TODO manipulacion de semaforo contador 
					try {	contadorPersonas.acquire();	} catch (InterruptedException e) {}
				 // TODO clarify
				  System.out.println(sexo + " " + id + " va al servicio.");
				  utilizarServicios();
				  System.out.println(sexo + " " + id + " vuelve a trabajar.");
				 // TODO manipulacion de semaforo contador 
				  	contadorPersonas.release();
				
				 // TODO clarify
  
  			disponibilidad.abrir_al_otro_genero_si_ultimo();
		}
	}
}
