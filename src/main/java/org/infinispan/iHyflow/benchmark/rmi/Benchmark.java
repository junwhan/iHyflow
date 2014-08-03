package org.infinispan.iHyflow.benchmark.rmi;


public abstract class Benchmark extends org.infinispan.iHyflow.benchmark.Benchmark{
	
	@Override
	protected String[] result() {
		return new String[] {
				"Timeouts: " + Lockable.timouts,
		};
	}
}
