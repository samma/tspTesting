package system;

import system.Computer;

public interface Computer2Space extends java.rmi.Remote
{
    void register(Computer computer) throws java.rmi.RemoteException, InterruptedException;
}