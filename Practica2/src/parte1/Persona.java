package parte1;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Persona extends Thread {
	static Random rnd = new Random();
	
	protected final String sexo;
	protected final int id;
	
	// PECUSA que controla el acceso múltiple al servicio
	protected Pecusa disponibilidad;
	protected Semaphore contadorPersonas;
	
	
	public Persona(int id, String sexo, Pecusa disponibilidad, Semaphore contadorPersonas){
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
			System.out.println("Hay en el bannio "+(4-contadorPersonas.availablePermits()));
			sleep(rnd.nextInt(1000));
		} catch (Exception e) {
			System.err.println("Interrupted while sleeping");
		}
	}
	
	public void run() {
		while (true) {
			trabajar();
			disponibilidad.cerrar_al_otro_genero_si_primero();

			try {
				contadorPersonas.acquire();
			} catch (InterruptedException e) {
			}

			System.out.println(sexo + " " + id + " va al servicio.");
			utilizarServicios();
			System.out.println(sexo + " " + id + " vuelve a trabajar.");

			contadorPersonas.release();

			disponibilidad.abrir_al_otro_genero_si_ultimo();
		}
	}
}
