package ejercicio4;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;


// start rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false
// start java -classpath C:\hlocal\miconc\Practica6\bin -Djava.rmi.server.codebase=file:C:\hlocal\miconc\Practica6\bin/ ejercicio2.Server
// start java -classpath %~dp0 -Djava.rmi.server.codebase=file:%~dp0/ ejercicio3.Client
public class Server implements Chat {

	static private ConcurrentHashMap<Integer, Receiver> clientes;
	static private AtomicInteger clientIDCounter;
	static private AtomicInteger msgCounter;
	private ForkJoinPool pool;
	private static int maxHilos = 16;
	private static int MUCHOS_CLIENTES = 0;
		
    public Server() {
    	msgCounter = new AtomicInteger(0);
    	clientIDCounter = new AtomicInteger(0);
    	clientes = new ConcurrentHashMap<Integer, Receiver>();
    	
    	pool = new ForkJoinPool(maxHilos); // cachedthreadpool
    }

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
			clientes.put(clientIDCounter.incrementAndGet(), stub);
			
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
    
		System.out.println("Server: se ha conectado un cliente!");
		return "El servidor acepta tu conexion. ID: "+clientIDCounter;
	}

	@Override
	public String darseDeBaja(Integer id) throws RemoteException {
		clientes.remove(id);
		
		System.out.println("Server: se ha desconectado el cliente: "+id);
		return "Te has desconectado del servidor";
	}

	@Override
	public String difundir(String mensaje) throws RemoteException {
		System.out.println("Server: se recibe mensaje>> " + mensaje);
		System.out.println("Servidor broadcast mensaje "+msgCounter.incrementAndGet());
		
		if (clientes.size() > MUCHOS_CLIENTES){
			// Lanzamos hilos para difundir el mensaje a cada cliente
			for (Receiver rec : clientes.values()){ // values() garantiza acceso concurrente en ese instante
				EntregarrMensajeAction action = new EntregarrMensajeAction(rec, mensaje);
				pool.invoke(action);
			}
		}
		else
		{
			// Procedimiento no paralelo
			for (Receiver rec : clientes.values()) {
				rec.entregarMensaje(mensaje);
			}
		}
		return mensaje;
	}
}