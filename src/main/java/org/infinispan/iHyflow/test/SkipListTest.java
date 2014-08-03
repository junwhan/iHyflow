package org.infinispan.iHyflow.test;

import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.benchmark.tm.skiplist.checkpoint.SkipList;

public class SkipListTest {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Starting skiplist test");
		HyFlow.start(Integer.parseInt(args[0]));
		
		SkipList<Integer> sl = new SkipList<Integer>("sl");
		sl.insert(9);
		sl.insert(92);
		sl.insert(74);
		sl.insert(0);
		sl.insert(73);
		
		sl.delete(92);
		sl.delete(9);
		sl.delete(0);
		sl.delete(73);
		sl.delete(74);
		
		sl.insert(19);
		
		sl.printAll();
		System.out.println("Value:"+sl.contains(9));
	}

}
