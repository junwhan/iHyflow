package org.infinispan.iHyflow.benchmark.tm.test;

import org.deuce.Atomic;

import aleph.dir.DirectoryManager;
import org.infinispan.iHyflow.util.network.Network;

public class Benchmark extends org.infinispan.iHyflow.benchmark.Benchmark{

	@Override
	protected Object randomId() {
		int temp = (int) (100*Math.random()); 
		return temp%10;
	}

	@Override
	protected int getOperandsCount() {
		return  2;
	}

	@Atomic
	@Override
	protected void readOperation(Object... ids) {
		TestObject temp;
		System.out.println("Read Transaction");
		for (Object key : ids) {
			Object object = DirectoryManager.getManager().open(key);
			temp = (TestObject) object;
			temp.setData(-10); 
			System.out.println(temp.getData());
		}
		
	}
	

	@Atomic
	@Override
	protected void writeOperation(Object... ids) {
		TestObject temp;
		System.out.println("Write Transaction");
		for (Object key : ids) {
			Object object = DirectoryManager.getManager().open(key);
			temp = (TestObject) object;
			temp.setData(10); 
			System.out.println(temp.getData());
		}	
	}

	@Override
	protected void checkSanity(){
		
	}
	
	@Override
	protected void createLocalObjects() {
		for(int i=0;i<10;i++)
			if(i%2==Network.getInstance().getID())
				new TestObject(i);	
	}

	@Override
	protected String getLabel() {
		return "Sol";
	}

}
