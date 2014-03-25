package ejercicio4;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Client implements Receiver{
// start java -classpath C:\hlocal\miconc\Practica6\bin ejercicio2.Client
    private Client() {}
    private static int id = -1;
    static Random rnd = new Random();

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
        	Client obj = new Client();
            Receiver myStub = (Receiver) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Receiver", myStub);
            
            System.err.println("Client-Receiver ready");
        	
            /////////////////////////////////////////////////////
            
            registry = LocateRegistry.getRegistry(host);
            Chat stub = (Chat) registry.lookup("Chat");
            String response = stub.darseDeAlta();
            System.out.println(response);
            id = Integer.parseInt(response.split(": ")[1]);
             
       for (int cont=0;cont < 100; cont++){
    	   Thread.sleep(rnd.nextInt(500));
    	   stub.difundir("este es mi mensaje "+cont+" :: " + id );
       }
        
        // In case of "exit"
        response = stub.darseDeBaja(id);
        System.out.println(response);
        
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

	public String entregarMensaje(String mensaje) throws RemoteException {
		String msg = mensaje.split("::")[0];
		String ids = mensaje.split("::")[1];
		Integer aux = Integer.parseInt(ids);
		
		System.out.println("Client "+id+": recibo mensaje "+aux+">> " + msg);
		return "Cliente "+ id + " recibe mensaje<< "+msg;
	}
}
