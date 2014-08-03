package org.infinispan.iHyflow.benchmark.rmi.list;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infinispan.iHyflow.benchmark.rmi.Lockable;
import org.infinispan.iHyflow.benchmark.rmi.bank.IBankAccount;
import org.infinispan.iHyflow.core.AbstractDistinguishable;
import org.infinispan.iHyflow.core.dir.IHyFlow;
import org.infinispan.iHyflow.transaction.Remote;
import org.infinispan.iHyflow.util.network.Network;

public class Node extends Lockable
		implements INode{
	
	private String id;
	private Integer value;
	private String nextId;
	
	public Node(String id, Integer value) throws RemoteException{
		super(id);
		this.id = id;
		this.value = value;
		enableTimeout = false;
	}
	
	public void setNext(String nextId){
		this.nextId = nextId;
	}

	public String getNext(){
		return nextId;
	}
	
	public Integer getValue(){
		return value;
	}
	
	public Object getId() {
		return id;
	}
}
