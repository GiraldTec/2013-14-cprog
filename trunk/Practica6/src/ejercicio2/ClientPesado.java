package ejercicio2;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class ClientPesado implements Receiver{
// start java -classpath C:\hlocal\miconc\Practica6\bin ejercicio2.Client
    private ClientPesado() {}
    private static int id = -1;
    static Random rnd = new Random();

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
        	ClientPesado obj = new ClientPesado();
            Receiver myStub = (Receiver) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Receiver", myStub);
            
            System.err.println("Client-Receiver ready");
        	
            /////////////////////////////////////////////////////
            
            registry = LocateRegistry.getRegistry(host);
            Chat stub = (Chat) registry.lookup("Chat");
            
             
       for (int cont=0;cont < 100; cont++){
    	   Thread.sleep(rnd.nextInt(50));
    	   String response = stub.darseDeAlta();
           System.out.println(response);
           id = Integer.parseInt(response.split(": ")[1]);
           
    	   // In case of "exit"
           response = stub.darseDeBaja(id);
           System.out.println(response);  
       }
       
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


//Client exception: java.util.ConcurrentModificationException
//java.util.ConcurrentModificationException
//        at java.util.HashMap$HashIterator.nextEntry(Unknown Source)
//        at java.util.HashMap$EntryIterator.next(Unknown Source)
//        at java.util.HashMap$EntryIterator.next(Unknown Source)
//        at ejercicio2.Server.difundir(Server.java:66)
//        at sun.reflect.GeneratedMethodAccessor1.invoke(Unknown Source)
//        at sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)
//        at java.lang.reflect.Method.invoke(Unknown Source)
//        at sun.rmi.server.UnicastServerRef.dispatch(Unknown Source)
//        at sun.rmi.transport.Transport$1.run(Unknown Source)
//        at sun.rmi.transport.Transport$1.run(Unknown Source)
//        at java.security.AccessController.doPrivileged(Native Method)
//        at sun.rmi.transport.Transport.serviceCall(Unknown Source)
//        at sun.rmi.transport.tcp.TCPTransport.handleMessages(Unknown Source)
//        at sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run0(Unknown Source)
//        at sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run(Unknown Source)
//        at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
//        at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
//        at java.lang.Thread.run(Unknown Source)
//        at sun.rmi.transport.StreamRemoteCall.exceptionReceivedFromServer(Unknown Source)
//        at sun.rmi.transport.StreamRemoteCall.executeCall(Unknown Source)
//        at sun.rmi.server.UnicastRef.invoke(Unknown Source)
//        at java.rmi.server.RemoteObjectInvocationHandler.invokeRemoteMethod(Unknown Source)
//        at java.rmi.server.RemoteObjectInvocationHandler.invoke(Unknown Source)
//        at com.sun.proxy.$Proxy1.difundir(Unknown Source)
//        at ejercicio2.Client.main(Client.java:38)

