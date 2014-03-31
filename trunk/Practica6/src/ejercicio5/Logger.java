package ejercicio5;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import ejercicio4.Chat;
import ejercicio4.Message;
import ejercicio4.Publisher;
import ejercicio4.Receiver;

public class Logger implements Receiver{
// start java -classpath C:\hlocal\miconc\Practica6\bin ejercicio2.Client
	private Publisher publisher;
	
    private Logger() {
    	setPublisher(new LogPublisher("log.txt"));
    	publisher.start();
    }
    private static int id = -1;
    static Random rnd = new Random();

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
        	Logger obj = new Logger();
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
            stub.difundir("Entra el LOG "+" :: " + id );
      
        
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

	public String entregarMensaje(String mensaje) throws RemoteException {
		String msg = mensaje.split("::")[0];
		String ids = mensaje.split(":: ")[1];  // ID de un cliente
		String msgCounter = mensaje.split(":: ")[2];
		
		Message m = new Message(Integer.parseInt(msgCounter),"Mensaje número: "+msgCounter+" >> Autor >> "+ ids + " >> " + msg);
		
		publisher.addMessage(m);
		//Integer aux = Integer.parseInt(ids);
		
		//System.out.println("Client "+id+": recibo mensaje "+aux+">> " + msg);
		return "Cliente "+ id + " recibe mensaje<< "+msg;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}
}
