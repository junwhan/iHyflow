package org.infinispan.iHyflow.benchmark.tm.list2;

import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.benchmark.tm.list.Node;
import org.infinispan.iHyflow.util.network.Network;

public class Benchmark extends org.infinispan.iHyflow.benchmark.tm.Benchmark{
	 private static final String HEAD = "list";
	@Override
	protected Class[] getSharedClasses() {
		return new Class[] { Node.class };
	}
	
	ListHandler listHandler = new ListHandler();

	@Override
	protected void createLocalObjects() {
		Integer id = Network.getInstance().getID();
		try {
			m_Cache = HyFlow.getMyObjectCache (id.toString(), getLabel());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		if(Network.getInstance().getID()==0) //listHandler.createList();
			new Node(HEAD, -1); 
	}

	@Override
	protected String getLabel() {
		return "List2-TM";
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
			listHandler.find((Integer)ids[0]);
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
		ListHandler list = listHandler;
		if(Math.random()>0.5){
			System.out.println("[ADD]");
			try {
				list.add((Integer)ids[0]);
				elementsSum += (Integer)ids[0];
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("[DEL]");
			try {
				if(list.delete((Integer)ids[0]))
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
			System.out.println("Sanity Check:" + ((Network.getInstance().getID()==0) ? listHandler.sum() : "?") + "/" + elementsSum);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
