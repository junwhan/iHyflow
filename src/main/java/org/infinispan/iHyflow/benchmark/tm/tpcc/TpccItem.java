package org.infinispan.iHyflow.benchmark.tm.tpcc;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import org.deuce.reflection.AddressUtil;
import org.deuce.transaction.AbstractContext;
import org.deuce.transaction.Context;
import org.deuce.transaction.ContextDelegator;

import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.benchmark.tm.bank.$HY$_IBankAccount;
import org.infinispan.iHyflow.core.tm.undoLog.AbstractLoggableObject;
import org.infinispan.iHyflow.helper.AbstractLockMap;
import org.infinispan.iHyflow.util.io.Logger;

public class TpccItem extends AbstractLoggableObject 
{	
	public String I_IM_ID;
	public String I_NAME;
	public float I_PRICE;
	public String I_DATA;
	
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
	public TpccItem(String id) {
		//String id = "item"+Integer.toString(myid)
		this.id = id;
		this.locks = new AbstractLockMap(id);
		this.I_IM_ID = Integer.toString(random.nextInt(100));
		this.I_NAME = Integer.toString(random.nextInt(100));
		this.I_PRICE = random.nextFloat();
		this.I_DATA = Integer.toString(random.nextInt(100));
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