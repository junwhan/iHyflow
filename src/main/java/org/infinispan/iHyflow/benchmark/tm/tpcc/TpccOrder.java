package org.infinispan.iHyflow.benchmark.tm.tpcc;

import java.util.Random;
import org.deuce.reflection.AddressUtil;
import org.deuce.transaction.AbstractContext;
import org.deuce.transaction.Context;
import org.deuce.transaction.ContextDelegator;

import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.core.tm.undoLog.AbstractLoggableObject;
import org.infinispan.iHyflow.helper.AbstractLockMap;
import org.infinispan.iHyflow.util.io.Logger;

public class TpccOrder extends AbstractLoggableObject 
{	
	public int O_C_ID;
	public String O_ENTRY_D;
	public String O_CARRIER_ID;
	public int O_OL_CNT;
	public Boolean O_ALL_LOCAL;
	
	private Random random = new Random();
	
	public AbstractLockMap locks = null;
	private String id;
	private Long [] ts;
	@Override
	public Long[] getTS(){
		return ts;
	}
	@Override
	public void setTS(Long [] ts){
		// for DATS
		this.ts = ts;
	}
	public TpccOrder(String id) {
		//String id = "item"+Integer.toString(myid)
		this.id = id;
		this.locks = new AbstractLockMap(id);

		this.O_C_ID = random.nextInt(100);
		this.O_ENTRY_D = Integer.toString(random.nextInt(100));
		this.O_CARRIER_ID = Integer.toString(random.nextInt(100));
		this.O_OL_CNT = 5 + random.nextInt(11);
		this.O_ALL_LOCAL = true;
		this.ts = new Long[5]; //For DATS
		AbstractContext context = ContextDelegator.getTopInstance();
		if (context == null)
			HyFlow.getLocator().register(this);
		else
			context.newObject(this); 
	}

	@Override
	public Object getId() {
		return id;
	}

}