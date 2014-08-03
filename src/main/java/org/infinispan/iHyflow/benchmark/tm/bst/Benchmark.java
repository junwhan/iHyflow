package org.infinispan.iHyflow.benchmark.tm.bst;

import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.benchmark.tm.bank.BankAccount;
import org.infinispan.iHyflow.util.network.Network;

public class Benchmark extends org.infinispan.iHyflow.benchmark.tm.Benchmark{

	@Override
	protected Class[] getSharedClasses() {
		return new Class[] { Node.class };
	}
	
	BSTHandler bstHandler = new BSTHandler();

	@Override
	protected void createLocalObjects() {
		Integer id = Network.getInstance().getID();
		int nodes = Network.getInstance().nodesCount();
		try {
			m_Cache = HyFlow.getMyObjectCache (id.toString(), getLabel());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(Network.getInstance().getID()==0){
			//bstHandler.createTree();
			try {
				bstHandler.createTree();
				//m_Cache.put(id + "-" + i, bstHandler.createTree()); //create an infinispan cache
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected String getLabel() {
		return "BST-TM";
	}

	@Override
	protected int getOperandsCount() {
		return 1;
	}

	@Override
	protected Object randomId() {
		return new Integer((int)(Math.random()*localObjectsCount));
	}

	@Override
	protected void readOperation(Object... ids) {
		try {
			bstHandler.find((Integer)ids[0]);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void writeOperation(Object... ids) {
		BSTHandler tree = bstHandler;
		if(Math.random()>0.5){
			System.out.println("[ADD]");
			try {
				tree.add((Integer)ids[0]);
				elementsSum += (Integer)ids[0];
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("[DEL]");
			try {
				if(tree.delete((Integer)ids[0]))
					elementsSum -= (Integer)ids[0];
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	int elementsSum = 0;
	@Override
	protected void checkSanity() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("Sanity Check:" + ((Network.getInstance().getID()==0) ? bstHandler.sum() : "?") + "/" + elementsSum);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
