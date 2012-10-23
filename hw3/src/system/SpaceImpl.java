package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.*;

import api.*;
import system.Computer;

public class SpaceImpl implements Space,proxy, Computer2Space{
	/**
	 * 
	 */
	private static final BlockingQueue taskQueue = new LinkedBlockingQueue();
	private static final BlockingQueue resultQueue = new LinkedBlockingQueue();
	private static BlockingQueue proxyList = new LinkedBlockingQueue();
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (System.getSecurityManager() == null ) 
		{ 
		   System.setSecurityManager(new java.rmi.RMISecurityManager() ); 
		}
		try {
			Space space = new SpaceImpl();
			Space stub = (Space)UnicastRemoteObject.exportObject(space, 0);
			Registry registry = LocateRegistry.createRegistry( 1099 );
			registry.rebind(SERVICE_NAME, stub);
			System.out.println("SpaceImpl bound");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void initLocaly(Space spacel) { // for running in same JVM
		if (System.getSecurityManager() == null ) 
		{ 
		   System.setSecurityManager(new java.rmi.RMISecurityManager() ); 
		}
		try {
			Space stub = (Space)UnicastRemoteObject.exportObject(spacel, 0);
			Registry registry = LocateRegistry.createRegistry( 1099 );
			registry.rebind(SERVICE_NAME, stub);
			System.out.println("SpaceImpl bound");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void put(Task task) throws RemoteException {
		taskQueue.add(task);
	}
	
	@Override
	public Result take() throws RemoteException, InterruptedException {
		
		return (Result)resultQueue.take();
	}
	@Override
	public void exit() {
		while(!proxyList.isEmpty()){
			try {
				ComputerProxyImpl cpl = (ComputerProxyImpl)proxyList.take();
				cpl.exit();
				cpl.stop();
			} catch (Exception e) {
		
			}
			
		}
		System.exit(0);
		
		
	}
	@Override
	public void putResultQueue(Result result) {
		resultQueue.add(result);
		
	}
	@Override
	public void putTaskQueue(Task task) {
		taskQueue.add(task);
		
	}
	public Task takeTaskQueue() throws InterruptedException {
			Task t = (Task)taskQueue.take();
			return t;
	}
	@Override
	public void register(Computer computer) throws RemoteException, InterruptedException {
		ComputerProxyImpl computerProxy = new ComputerProxyImpl(computer, this);
		computerProxy.start();
		proxyList.add(computerProxy);
		System.out.println("Computer registered");
	}
	@Override
	public void unRegister(ComputerProxyImpl computerProxy) {
		proxyList.remove(computerProxy);
		System.out.println("Computer unregistered");
	}
}
