package parte2;

import parte1.*;
import java.util.concurrent.ThreadLocalRandom;

public class EscritorReentrante extends Thread {

	private MonitorArbitraje monitorArb;
	private BaseDatos baseDatos;

	public EscritorReentrante(MonitorArbitraje monitor, BaseDatos BD, String nombre) {
		super(nombre);
		monitorArb = monitor;
		baseDatos = BD;
	}

	public void run() {
		try {
			while (true) {
				monitorArb.entrarEscribir();
				sleep(10);
				monitorArb.entrarEscribir();
				sleep(ThreadLocalRandom.current().nextInt(1500,2500));
				baseDatos.escribir();
				System.out.println(getName() + " ecribe su nombre en la BD.");
				monitorArb.salirEscribir();
				sleep(10);
				monitorArb.salirEscribir();
				sleep(ThreadLocalRandom.current().nextInt(500,5000));
			}
		} catch (InterruptedException e){}
	}
	
}
