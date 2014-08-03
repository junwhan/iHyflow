package org.infinispan.iHyflow.benchmark.rmi.vacation.rw;

import java.rmi.RemoteException;

import org.infinispan.iHyflow.benchmark.rmi.ILockabelRW;

public interface IReservationInfo extends ILockabelRW{

	int getType() throws RemoteException;
	int getPrice() throws RemoteException;
	String getReservedResource() throws RemoteException;

}
