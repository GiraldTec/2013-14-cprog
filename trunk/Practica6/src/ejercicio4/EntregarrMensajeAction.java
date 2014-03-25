package ejercicio4;

import java.rmi.RemoteException;
import java.util.concurrent.RecursiveAction;

public class EntregarrMensajeAction extends RecursiveAction {
	private static final long serialVersionUID = 28354910422092509L;
	private Receiver client;
	private String msg;

	EntregarrMensajeAction(Receiver cliente, String mensaje){
		msg = mensaje;
		client = cliente;
	}

	@Override
	protected void compute() {
		try {
			client.entregarMensaje(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
