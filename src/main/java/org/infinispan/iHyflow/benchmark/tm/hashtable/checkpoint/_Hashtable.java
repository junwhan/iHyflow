package org.infinispan.iHyflow.benchmark.tm.hashtable.checkpoint;

import java.util.ArrayList;
import java.util.List;

import org.deuce.transaction.AbstractContext;
import org.deuce.transaction.ContextDelegator;

import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.core.AbstractDistinguishable;

public class _Hashtable extends AbstractDistinguishable 
{
	private String id;
	private int bucketCount = 0;
	private Long [] ts;
	public Long[] getTS(){
		return ts;
	}
	public void setTS(Long [] ts){
		// for DATS
		this.ts = ts;
	}
	public _Hashtable(String id, int buckets) {
		this.id = id;
		this.bucketCount = buckets;
		for (int i=0; i<buckets; i++) {
			new _HashBucket(id+"-b"+i);
		}
		// Register
		AbstractContext context = ContextDelegator.getTopInstance();
		if (context == null)
			HyFlow.getLocator().register(this);
		else
			context.newObject(this); // add it to context publish-set
	}
	
	@Override
	public Object getId() {
		return id;
	}
	
	private _HashBucket getBucket(Object key) {
		final int bucket = key.hashCode() % bucketCount;
		return (_HashBucket)HyFlow.getLocator().open(id+"-b"+bucket);
	}
	
	public Object get(Object key) {
		// Exception instead?
		if (key==null)
			return null;
		return getBucket(key).get(key);
	}
	
	public boolean put(Object key, Object value) {
		if (key==null)
			return false;
		return getBucket(key).put(key, value);
	}
	
	public boolean remove(Object key) {
		if (key==null)
			return false;
		return getBucket(key).remove(key);
	}
	
	public boolean contains(Object key) {
		if (key==null)
			return false;
		return getBucket(key).contains(key);
	}
	
	
}
