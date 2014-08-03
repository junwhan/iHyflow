package org.infinispan.iHyflow.benchmark.rmi.vacation;

import java.rmi.RemoteException;

import org.infinispan.iHyflow.benchmark.rmi.ILockabel;

public interface IReservationInfo extends ILockabel{

	int getType() throws RemoteException;
	int getPrice() throws RemoteException;
	String getReservedResource() throws RemoteException;

}
