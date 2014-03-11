package ejercicio1;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;


// rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false
// http://docs.oracle.com/javase/7/docs/technotes/guides/rmi/enhancements-7.html
public class Server implements Chat {

	static private HashMap<Integer, Receiver> clientes;
	static private int clientIDCounter = 0;
	
    public Server() {clientes = new HashMap<Integer, Receiver>();}

    public static void main(String args[]) {

        try {
            Server obj = new Server();
            Chat stub = (Chat) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Chat", stub);
            
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }


	public String darseDeAlta() throws RemoteException {
		Registry registry = LocateRegistry.getRegistry();
        Receiver stub;
		try {
			stub = (Receiver) registry.lookup("Receiver"); 
			clientes.put(clientIDCounter++, stub);
			
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
    
		System.out.println("Server: se ha conectado un cliente!");
		return "El servidor acepta tu conexion. ID: "+clientIDCounter;
	}

	@Override
	public String darseDeBaja(Integer id) throws RemoteException {
		clientes.remove(id);
		
		System.out.println("Server: se ha desconectado un cliente!");
		return "Te has desconectado del servidor";
	}

	@Override
	public String difundir(String mensaje) throws RemoteException {
		System.out.println("Server: se recibe mensaje>> " + mensaje);
		
		for (Map.Entry<Integer, Receiver> cursor : clientes.entrySet()) {
			System.out.println("Servidor broadcast mensaje: "+ 
						cursor.getValue().entregarMensaje(mensaje));
		}
		return mensaje;
	}
}