package org.infinispan.iHyflow.benchmark.tm.list;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.infinispan.iHyflow.core.tm.control.ControlContext;

public interface $HY$_INode extends Remote, Serializable{
	public void setNext(Object id, ControlContext context, String nextId) throws RemoteException;
	public String getNext(Object id, ControlContext context) throws RemoteException;
	public Integer getValue(Object id, ControlContext context) throws RemoteException;
}
