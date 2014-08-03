package org.infinispan.iHyflow.benchmark.rmi.bank;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.infinispan.iHyflow.benchmark.rmi.ILockabel;

public interface IBankAccount extends ILockabel{
	public void deposit(int dollars) throws RemoteException;
	public boolean withdraw(int dollars) throws RemoteException;
	public Integer checkBalance() throws RemoteException;
}
