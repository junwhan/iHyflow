package org.infinispan.iHyflow.benchmark.rmi.vacation.rw;

import java.rmi.RemoteException;
import java.util.List;

import org.infinispan.iHyflow.benchmark.rmi.ILockabelRW;

public interface ICustomer extends ILockabelRW{

	void addReservation(String reservation) throws RemoteException;
	List<String> getReservations() throws RemoteException;
}
