package org.infinispan.iHyflow.benchmark.rmi.bst;

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
	private String rightId;
	private String leftId;
	
	public Node(String id, Integer value) throws RemoteException{
		super(id);
		this.id = id;
		this.value = value;
		enableTimeout = false;
	}
	
	public void setRightChild(String rightId){
		this.rightId = rightId;
	}

	public String getRightChild(){
		return rightId;
	}

	public void setLeftChild(String leftId){
		this.leftId = leftId;
	}

	public String getLeftChild(){
		return leftId;
	}

	public Integer getValue(){
		return value;
	}
	
	public Object getId() {
		return id;
	}
}
