package ejercicio3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Chat extends Remote {
    String darseDeAlta() throws RemoteException;
    String darseDeBaja(Integer id) throws RemoteException;
    String difundir(String mensaje) throws RemoteException;
}
