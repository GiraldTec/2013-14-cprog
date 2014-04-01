package ejercicio4;

import java.rmi.RemoteException;

public class EntregarrMensajeAction implements Runnable {
	private Receiver client;
	private String msg;

	EntregarrMensajeAction(Receiver cliente, String mensaje){
		msg = mensaje;
		client = cliente;
	}

	@Override
	public void run() {
		try {
			client.entregarMensaje(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
