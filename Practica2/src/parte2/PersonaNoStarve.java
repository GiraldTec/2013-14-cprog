package parte2;

import java.util.concurrent.Semaphore;

import parte1.Pecusa;
import parte1.Persona;

public class PersonaNoStarve extends Persona{

	 Semaphore torniquete;
	
	
	public PersonaNoStarve(int id, String sexo, Pecusa disponibilidad,
			Semaphore contadorPersonas, Semaphore torniquete) {
		super(id, sexo, disponibilidad, contadorPersonas);
		this.torniquete = torniquete;
	}
	
	public void run() {
		while (true) {
			trabajar();
			
			try {
				torniquete.acquire();
			} catch (InterruptedException e1) {}			
			
			disponibilidad.cerrar_al_otro_genero_si_primero();
			
			torniquete.release();
			
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
