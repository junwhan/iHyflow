package org.infinispan.iHyflow.benchmark.rmi.list;

import java.rmi.RemoteException;

import org.infinispan.iHyflow.benchmark.rmi.ILockabel;

public interface INode extends ILockabel{
	
	public String getNext() throws RemoteException;
	public Integer getValue() throws RemoteException;
	public void setNext(String nextId) throws RemoteException;
	
}
