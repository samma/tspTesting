package system;

import java.rmi.RemoteException;

import api.Result;
import api.Task;

public class ComputerProxyImpl extends Thread {
	final private Computer computer;
	final private SpaceImpl spaceImpl; 
	
	public ComputerProxyImpl(Computer computer, SpaceImpl spaceImpl){
		this.computer = computer;
		this.spaceImpl = spaceImpl;
	}
	public void exit() throws RemoteException{
		computer.exit();
	}
	
	public void run(){
		while(true){
			Task t;
			try {
				t = spaceImpl.takeTaskQueue();
				try {
					Result result = computer.execute(t);
					spaceImpl.putResultQueue(result);
				} catch (RemoteException e) {
					spaceImpl.putTaskQueue(t);
					spaceImpl.unRegister(this);
					return;
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				spaceImpl.unRegister(this);
				return;
			}
		}
	}
}
