package ejercicio4;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Receiver extends Remote {
    String entregarMensaje(String mensaje) throws RemoteException;
}
