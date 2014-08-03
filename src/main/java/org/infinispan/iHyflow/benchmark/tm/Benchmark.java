package org.infinispan.iHyflow.benchmark.tm;

import org.deuce.transaction.AbstractContext;

import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.core.cm.policy.AbstractContentionPolicy;
import org.infinispan.iHyflow.core.dir.control.ControlFlowDirectory;
import org.infinispan.iHyflow.core.tm.dtl2.Context;
import org.infinispan.iHyflow.util.io.FileLogger;


abstract public class Benchmark  extends org.infinispan.iHyflow.benchmark.Benchmark{

	abstract protected Class[] getSharedClasses();
	
	@Override
	protected void initSharedClasses() {
		boolean controlFlow = HyFlow.getLocator() instanceof ControlFlowDirectory;
		for(Class<?> clazz : getSharedClasses()){
			try {
				clazz.newInstance();
			} catch (Exception e) {
			}
			if(controlFlow)
				try {
					System.out.println("Create Class Proxy - " + clazz.getName());
					Class.forName(clazz.getPackage().getName() + ".$HY$_Proxy_" + clazz.getSimpleName()).newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	
	@Override
	protected String[] result() {
		return new String[] {
				"Aborts: " + AbstractContext.aborts,
				"Conflicts: " + AbstractContentionPolicy.dist_conflicts,
				"Local Conflicts: " + AbstractContentionPolicy.conflicts,
				"Forwarding: " + Context.forwardings,
		};
	}
}
