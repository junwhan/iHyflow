package org.infinispan.iHyflow.benchmark.tm.bank;

import java.util.Random;

import org.deuce.Atomic;

import aleph.dir.DirectoryManager;
import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.util.io.Logger;
import org.infinispan.iHyflow.util.network.Network;


public class Benchmark extends org.infinispan.iHyflow.benchmark.tm.Benchmark{

	final int amount = 10000;
	final int transfer = 10;
	private Random random = new Random(hashCode());

	@Override
	protected Class[] getSharedClasses() {
		return new Class[] { BankAccount.class };
	}
	
	@Override
	protected void createLocalObjects() {
		Integer id = Network.getInstance().getID();
		int nodes = Network.getInstance().nodesCount();
		try {
			m_Cache = HyFlow.getMyObjectCache (id.toString(), getLabel());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		//Logger.debug("Distributeding " + localObjectsCount + " objects over " + nodes + " nodes.");
		for(int i=0; i<localObjectsCount; i++){
			Logger.debug("Try creating object " + i);
			if((i % nodes)== id){
				Logger.debug("Created locally object " + i);
				try {
					new BankAccount(id + "-" + i).deposit(amount);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected int getOperandsCount() {	return 2; }

	@Override
	protected Object randomId() {
		int obj = random.nextInt(localObjectsCount);
		return (obj%Network.getInstance().nodesCount()) + "-" + obj ;
	}

	@Override
	protected void readOperation(Object... ids) {
		try {
			BankAccount.totalBalance(String.valueOf(ids[0]), String.valueOf(ids[1]));
		} catch (Throwable e) {
			e.printStackTrace();
		} 
	}

	@Override
	protected void writeOperation(Object... ids) {
		try {
			BankAccount.transfer(String.valueOf(ids[0]), String.valueOf(ids[1]), transfer);
		} catch (Throwable e) {
			e.printStackTrace();
		} 
	}

	@Override
	protected String getLabel() {
		return "Bank-TM";
	}


	@Override
	@Atomic
	protected void checkSanity() {
		if(Network.getInstance().getID()!=0)
			 return;

		try {
			// TODO: WTF?!?
			Thread.sleep(120000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long balance = 0;
		//DirectoryManager locator = HyFlow.getLocator();
		int node = Network.getInstance().nodesCount();
		for(int i=0; i<localObjectsCount; i++){
			for(int j=0;j<node;j++){
				if(i%node==j){
					try {
						//BankAccount account = ((BankAccount) locator.open(j + "-" + i,"r"));
						balance+=BankAccount.checkBalance(j + "-" + i);
					} catch (Throwable e) {
						e.printStackTrace();
					}	
				}
			}
		}
		if(balance==localObjectsCount*amount)
			System.err.println("Passed sanity check");
		else
			System.err.println("Failed sanity check."+
					"\nbalance = "+ balance+
					"\nexpected = "+localObjectsCount*amount);
	}	
}

