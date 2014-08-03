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

public class TpccOrderline extends AbstractLoggableObject 
{	
	public int OL_I_ID; 
	public int OL_SUPPLY_W_ID; 
	public String OL_DELIVERY_D; 
	public int OL_QUANTITY; 
	public int OL_AMOUNT; 
	public String OL_DIST_INFO; 
	
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
	private int genAmount(int a){
		if (a < 2101) return 0; 
		else { 
			return (1 + random.nextInt(999999));
		}
		
	}
	public TpccOrderline(String id) {
		//String id = "item"+Integer.toString(myid)
		this.id = id;
		this.locks = new AbstractLockMap(id);
		
		this.OL_I_ID = 1 + random.nextInt(100000);
		this.OL_SUPPLY_W_ID = random.nextInt(1000);
		this.OL_DELIVERY_D = Integer.toString(random.nextInt(100));
		this.OL_QUANTITY = 5;
		this.OL_AMOUNT = genAmount(random.nextInt(3000));
		this.OL_DIST_INFO = Integer.toString(random.nextInt(100));
		this.ts = new Long[6]; //For DATS				
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