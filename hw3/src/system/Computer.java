package system;

import java.rmi.Remote;
import java.rmi.RemoteException;
import api.*;


public interface Computer extends Remote
{
    //Result execute( Task task ) throws RemoteException;
	public <T> T execute(Task t) throws RemoteException;
    void exit() throws RemoteException;
}