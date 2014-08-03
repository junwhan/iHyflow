package org.infinispan.iHyflow.benchmark.tm.experiment;

import java.util.Random;

import org.deuce.transaction.Context;
import org.deuce.transaction.TransactionException;

import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.core.AbstractDistinguishable;
import org.infinispan.iHyflow.core.tm.NestedContext;
import org.infinispan.iHyflow.core.tm.NestingModel;
import org.infinispan.iHyflow.helper.Atomic;
import org.infinispan.iHyflow.util.io.Logger;
import org.infinispan.iHyflow.util.network.Network;

public class Benchmark extends org.infinispan.iHyflow.benchmark.tm.Benchmark
{
	
	@Override
	protected Class[] getSharedClasses() {
		return new Class[] { ExpObject.class };
	}

	@Override
	protected void checkSanity() {
	}

	@Override
	protected void createLocalObjects() {
		Integer id = Network.getInstance().getID();
		
		// Create all objects on node 0, hope it doesn't crash! :)
		if (id == 0) {
			//TODO: create all objects and buckets
			for (int i=0; i<ExpAtomic.BEFORE_OBJ_COUNT; i++) {
				new ExpObject("before-"+i);
			}
			for (int i=0; i<ExpAtomic.STAGE_OBJ_COUNT; i++) {
				new ExpObject("stage-"+i);
			}
			for (int i=0; i<ExpAtomic.AFTER_OBJ_COUNT; i++) {
				new ExpObject("after-"+i);
			}
		} else {
			//talex: See Ticket #5
			new ExpObject("dontcrashonme-"+id);
		}
	}

	@Override
	protected String getLabel() {
		return "Experiment-TM";
	}

	@Override
	protected int getOperandsCount() {
		return 0;
	}

	@Override
	protected Object randomId() {
		return Math.random();
	}

	protected void rootOperation() throws Throwable {
		ExpAtomic a = new ExpAtomic();
		a.execute(null);
	}

	@Override
	protected void writeOperation(Object... ids) {
		try {
			rootOperation();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void readOperation(Object... ids) {
		try {
			rootOperation();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
