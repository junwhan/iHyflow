package org.infinispan.iHyflow.benchmark.rmi.vacation.rw;

import java.rmi.RemoteException;

import org.infinispan.iHyflow.benchmark.rmi.Lockable;
import org.infinispan.iHyflow.benchmark.rmi.LockableRW;
import org.infinispan.iHyflow.core.AbstractDistinguishable;
import org.infinispan.iHyflow.transaction.Remote;

public class ReservationInfo extends LockableRW
		implements IReservationInfo{

	private String id;
	private int price;
	private int type;
	private String resourceid;
	
	public ReservationInfo(String id, String resourceid, int price, int type) throws RemoteException {
		super(id);
		this.id = id;
		this.resourceid = resourceid;
		this.price = price;
		this.type = type;
	}
	
	public Object getId() {
		return id;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public int getPrice() {
		return price;
	}

	@Override
	public String getReservedResource() {
		return resourceid;
	}
}
