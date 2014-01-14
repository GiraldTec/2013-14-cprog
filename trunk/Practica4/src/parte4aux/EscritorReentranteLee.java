package parte4aux;

import parte1.*;
import java.util.concurrent.ThreadLocalRandom;

public class EscritorReentranteLee extends Thread {

	private MonitorArbitraje monitorArb;
	private BaseDatos baseDatos;

	public EscritorReentranteLee(MonitorArbitraje monitor, BaseDatos BD, String nombre) {
		super(nombre);
		monitorArb = monitor;
		baseDatos = BD;
	}

	public void run() {
		try {
			while (true) {
				monitorArb.entrarEscribir();
				sleep(ThreadLocalRandom.current().nextInt(1500,2500));
				baseDatos.escribir();
				System.out.println(getName() + " ecribe su nombre en la BD.");
				monitorArb.entrarLeer();
				sleep(10);
				baseDatos.leer();
				sleep(10);
				monitorArb.salirEscribir();
				sleep(10);
				monitorArb.salirLeer();
				sleep(ThreadLocalRandom.current().nextInt(500,5000));
			}
		} catch (InterruptedException e){}
	}
	
}
