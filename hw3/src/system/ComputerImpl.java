package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import system.Computer;
import api.Result;
import api.Task;

public class ComputerImpl implements Computer {
	
	public Result execute(Task t) {
		System.out.println("Excecuting task...");
        long start = System.currentTimeMillis();
        Object value = t.execute();
		long stop = System.currentTimeMillis();
		System.out.println("completed in " + (stop-start) + " milliseconds");
		
		return (new Result(value , (stop-start)));
    }
	
	public static void main(String[] args) {
		String spaceHost = args[0];
		int port = Integer.parseInt(args[1]);
		String name = "Computer";
		String spaceName = "Space";
		if (System.getSecurityManager() == null ) 
		{ 
		   System.setSecurityManager(new java.rmi.RMISecurityManager() ); 
		}
		try{
			
			Computer computer = new ComputerImpl();
			Computer stub = (Computer) UnicastRemoteObject.exportObject(computer, 0);
			Registry registry = LocateRegistry.createRegistry( port );
			registry.rebind(name, stub);
			
			System.out.println("Connecting to space: " + spaceHost);
			Registry registrySpace = LocateRegistry.getRegistry(spaceHost);
			Computer2Space space = (Computer2Space)registrySpace.lookup(spaceName);
			space.register(computer);
			System.out.println("ComputerImpl bound");
		} catch (Exception e) {
            System.err.println("ComputerImpl exception:");
            e.printStackTrace();
        }
	}
	public static void initLocaly(ComputerImpl cmpl) { // Only used all is done in one JVM
		String spaceHost = "localhost";
		int port = 1098;
		String name = "Computer";
		String spaceName = "Space";
		if (System.getSecurityManager() == null ) 
		{ 
		   System.setSecurityManager(new java.rmi.RMISecurityManager() ); 
		}
		try{
			
			Computer stub = (Computer) UnicastRemoteObject.exportObject(cmpl, 0);
			Registry registry = LocateRegistry.createRegistry( port );
			registry.rebind(name, stub);
			
			System.out.println("Connecting to space: " + spaceHost);
			Registry registrySpace = LocateRegistry.getRegistry(spaceHost);
			Computer2Space space = (Computer2Space)registrySpace.lookup(spaceName);
			space.register(cmpl);
			System.out.println("ComputerImpl bound");
		} catch (Exception e) {
            System.err.println("ComputerImpl exception:");
            e.printStackTrace();
        }
	}

	@Override
	public void exit() throws RemoteException {
		System.out.println("Remote call to exit()");
		System.exit(0);
		
	}

}
