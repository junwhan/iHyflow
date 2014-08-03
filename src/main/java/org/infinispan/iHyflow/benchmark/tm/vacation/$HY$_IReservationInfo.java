package org.infinispan.iHyflow.benchmark.tm.vacation;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.infinispan.iHyflow.core.tm.control.ControlContext;

public interface $HY$_IReservationInfo extends Remote, Serializable{

	int getPrice(Object id, ControlContext context) throws RemoteException;
	int getType(Object id, ControlContext context) throws RemoteException;
	String getReservedResource(Object id, ControlContext context) throws RemoteException;

}
