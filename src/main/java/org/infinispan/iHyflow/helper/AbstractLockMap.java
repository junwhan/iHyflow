package org.infinispan.iHyflow.helper;

import java.util.HashMap;

import org.deuce.transaction.Context;
import org.deuce.transaction.TransactionException;

import org.infinispan.iHyflow.core.tm.NestedContext;
import org.infinispan.iHyflow.util.io.Logger;

public class AbstractLockMap extends HashMap<Object, Integer> 
{
	private String parentid;
	public AbstractLockMap(String parentid) {
		this.parentid = parentid;
	}
	// TODO: read-write locks can get lots of help from HyFlow.
	// current implementation may livelock or cause starvation
	public AbstractLockMap lock(Object key, Context __transactionContext__) 
	{
		Logger.debug("AbstractLockMap: Locking key "+key+" of "+parentid);
		if (containsKey(key)) {
			final int lockval = get(key);
			if (lockval > 0) {
				Logger.debug("AbstractLockMap: "+parentid+" already locked, rollback back to top-level transaction.");
				((NestedContext)__transactionContext__).abortContextTree();
				throw new TransactionException("Already locked, full abort.");
			}
		}
		
		AbstractLockMap result = (AbstractLockMap) this.clone();
		result.put(key, new Integer(1));
		return result;
	}
	
	public AbstractLockMap unlock(Object key, Context __transactionContext__)
	{
		Logger.debug("AbstractLockMap: UnLocking key "+key+" of "+parentid);
		AbstractLockMap result = (AbstractLockMap) this.clone();
		result.put(key, new Integer(0));
		return result;
	}
	
	public AbstractLockMap lock_read(Object keyr, Object keyw, Context __transactionContext__)
	{
		int lockval = 0;
                if (containsKey(keyr)) {
                        lockval = get(keyr);
                }
		Logger.debug("AbstractLockMap: Locking key "+keyr+" of "+parentid+" for reading. old="+lockval+",new="+ (lockval+1));
		// Read/write lock
		// Check if it has been locked for writing
		if (containsKey(keyw) && get(keyw) > 0) {
			// locked for writing, abort
			Logger.debug("AbstractLockMap: "+parentid+" R/W lock unavailable(w locked), rollback back to top-level transaction.");
			((NestedContext)__transactionContext__).abortContextTree();
			throw new TransactionException("Already locked, full abort.");
		}
		//int lockval = 0;
		//if (containsKey(keyr)) {
		//	lockval = get(keyr);
		//}
		lockval++;
		AbstractLockMap result = (AbstractLockMap) this.clone();
		result.put(keyr, new Integer(lockval));
		return result;
	}
	
	public AbstractLockMap lock_write(Object keyr, Object keyw, Context __transactionContext__)
	{
		Logger.debug("AbstractLockMap: Locking key "+keyw+" of "+parentid+" for writing.");
		if (containsKey(keyw) && get(keyw) > 0) {
			// locked for writing, abort
			Logger.debug("AbstractLockMap: "+parentid+" R/W lock unavailable(w locked), rollback back to top-level transaction.");
			((NestedContext)__transactionContext__).abortContextTree();
			throw new TransactionException("Already locked, full abort.");
		}
		
		if (containsKey(keyr) && get(keyr) > 0) {
			// locked for writing, abort
			Logger.debug("AbstractLockMap: "+parentid+" R/W lock unavailable(r locked), rollback back to top-level transaction.");
			((NestedContext)__transactionContext__).abortContextTree();
			throw new TransactionException("Already locked, full abort.");
		}
		AbstractLockMap result = (AbstractLockMap) this.clone();
		result.put(keyw, new Integer(1));
		return result;
	}
	
	public AbstractLockMap unlock_read(Object keyr, Object keyw, Context __transactionContext__)
	{
		Logger.debug("AbstractLockMap: UnLocking key "+keyr+" of "+parentid+" for reading. old="+get(keyr)+",new="+(get(keyr)-1));
		AbstractLockMap result = (AbstractLockMap) this.clone();
		result.put(keyr, get(keyr)-1);
		return result;
	}
	
	public AbstractLockMap unlock_write(Object keyr, Object keyw, Context __transactionContext__)
	{
		Logger.debug("AbstractLockMap: UnLocking key "+keyw+" of "+parentid+" for writing.");
		AbstractLockMap result = (AbstractLockMap) this.clone();
		result.put(keyw, 0);
		return result;
	}
	
	
	
}
