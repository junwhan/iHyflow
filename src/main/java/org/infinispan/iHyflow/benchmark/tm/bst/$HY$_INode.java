package org.infinispan.iHyflow.benchmark.tm.bst;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.infinispan.iHyflow.core.tm.control.ControlContext;

public interface $HY$_INode extends Remote, Serializable{
	public void setRightChild(Object id, ControlContext context, String rightId) throws RemoteException;
	public String getRightChild(Object id, ControlContext context) throws RemoteException;
	
	public void setLeftChild(Object id, ControlContext context, String rightId) throws RemoteException;
	public String getLeftChild(Object id, ControlContext context) throws RemoteException;
	
	public Integer getValue(Object id, ControlContext context) throws RemoteException;
}
