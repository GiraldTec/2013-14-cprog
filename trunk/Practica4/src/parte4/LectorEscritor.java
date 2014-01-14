package parte4;

import java.util.concurrent.ThreadLocalRandom;

import parte1.BaseDatos;
import parte1.MonitorArbitraje;

public class LectorEscritor extends Thread {
	private MonitorArbitraje monitorArb;
	private BaseDatos baseDatos;
	
	public LectorEscritor(ReentranteTotal monitor, BaseDatos BD, String nombre) {
		super(nombre);
		monitorArb = monitor;
		baseDatos = BD;
	}
	
	public void run() {
		try {
			while (true) {monitorArb.entrarEscribir();				
				sleep(ThreadLocalRandom.current().nextInt(1500,2500));
				monitorArb.entrarLeer();
				sleep(10);
				System.out.println(getName() + " lee lo siguiente de la BD: " + baseDatos.leer());			
				sleep(ThreadLocalRandom.current().nextInt(500,3500));
				monitorArb.salirLeer();
				baseDatos.escribir();
				System.out.println(getName() + " ecribe su nombre en la BD.");
				monitorArb.salirEscribir();
			}
		} catch (InterruptedException e){}
	}

}

