package org.infinispan.iHyflow.benchmark.tm.tpcc;

import java.util.Random;

import org.deuce.reflection.AddressUtil;
import org.deuce.transaction.AbstractContext;
import org.deuce.transaction.Context;
import org.deuce.transaction.ContextDelegator;
import org.deuce.transaction.TransactionException;
import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.core.AbstractDistinguishable;
import org.infinispan.iHyflow.core.dir.IHyFlow;
import org.infinispan.iHyflow.core.tm.NestedContext;
import org.infinispan.iHyflow.core.tm.NestingModel;
import org.infinispan.iHyflow.core.tm.undoLog.AbstractLoggableObject;
import org.infinispan.iHyflow.helper.AbstractLockMap;
import org.infinispan.iHyflow.helper.Atomic;
import org.infinispan.iHyflow.helper.TrackerLockMap;
import org.infinispan.iHyflow.util.io.Logger;
import org.infinispan.Cache;

public class Tpcc extends AbstractLoggableObject 
{
	//TODO: implement a way to copy from-to the same bucket!!! currently, it livelocks
	private Random random = new Random();
	private AbstractLockMap locks = null;

	public static long item__ADDRESS__;
	public static long locks__ADDRESS__;

	private String id;
	public static long id__ADDRESS__;
	//private $HY$_IBankAccount $HY$_proxy;
	public static long $HY$_proxy__ADDRESS__;
	private Object $HY$_id;
	public static long $HY$_id__ADDRESS__;
	
	// Constants
	public final int NUM_ITEMS = 10; // Correct overall # of items: 100,000
	public final int NUM_WAREHOUSES = 3;
	public final int NUM_DISTRICTS = 4;
	public final int NUM_CUSTOMERS_PER_D = 30;
	public final int NUM_ORDERS_PER_D = 30;
	public final int MAX_CUSTOMER_NAMES = 10;
	
	{
		try {
			locks__ADDRESS__ = AddressUtil.getAddress(Tpcc.class
					.getDeclaredField("locks"));
			item__ADDRESS__ = AddressUtil.getAddress(Tpcc.class
					.getDeclaredField("NUM_ITEMS"));
			id__ADDRESS__ = AddressUtil.getAddress(Tpcc.class
					.getDeclaredField("id"));
			$HY$_proxy__ADDRESS__ = AddressUtil.getAddress(Tpcc.class
					.getDeclaredField("$HY$_proxy"));
			$HY$_id__ADDRESS__ = AddressUtil.getAddress(Tpcc.class
					.getDeclaredField("$HY$_id"));
		} catch (Exception e) {
			e.printStackTrace(Logger.levelStream[Logger.DEBUG]);
		}
	}
	
	public void TpccInit() {
		for (int id=0; id<NUM_ITEMS; id++) {
			final String myid = "item_"+Integer.toString(id);
			new TpccItem(myid);
		}
		
		for (int id = 0; id < NUM_WAREHOUSES; id++) {
			final String myid = "warehouse_"+Integer.toString(id);
			new TpccWarehouse(myid);
			for (int s_id=0; s_id<NUM_ITEMS; s_id++){
				final String smyid = myid+"_stock_"+ Integer.toString(s_id);
				new TpccStock(smyid);
			}
			for (int d_id = 0; d_id < NUM_DISTRICTS; d_id++) {
				String dmyid = myid+"_"+ Integer.toString(d_id);
				new TpccDistrict(dmyid);
				for (int c_id = 0; c_id < NUM_CUSTOMERS_PER_D; c_id++) {
					String cmyid = myid + "_customer_" + Integer.toString(c_id);
					HyFlow.putMyObject(cmyid, new TpccCustomer(cmyid));
					String hmyid = myid + "_history_" + Integer.toString(c_id);
					new TpccHistory(hmyid, c_id, d_id);
				}
			}
			for (int o_id = 0; o_id < NUM_ORDERS_PER_D; o_id++) {
				String omyid = myid+"_order_" + Integer.toString(o_id);
				HyFlow.putMyObject(omyid, new TpccOrder(omyid));
				String olmyid = myid+"_orderline_" + Integer.toString(o_id);
				new TpccOrderline(olmyid);
			}	
		}
				
	}

	@Override
	public Object getId() {
		return id;
	}
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
	protected void orderStatus(final int count) {
		try {
			new Atomic<Object>() {
				@Override
				public Object atomically(AbstractDistinguishable self, Context transactionContext) {
						String myid = "warehouse_"+Integer.toString(random.nextInt(NUM_WAREHOUSES));
						String cmyid = myid+"_customer_" + Integer.toString(random.nextInt(NUM_CUSTOMERS_PER_D));
						TpccCustomer customer = (TpccCustomer)HyFlow.getLocator().open(cmyid, "r");
						
						final String omyid = myid+"_order_" + Integer.toString(random.nextInt(NUM_ORDERS_PER_D));
						TpccOrder order = (TpccOrder)HyFlow.getLocator().open(omyid, "r");
						
						float olsum = (float)0;
						int i = 1;
						while (i < order.O_OL_CNT) {
							final String olmyid = myid+"_orderline_" + Integer.toString(i);
							TpccOrderline orderline = (TpccOrderline)HyFlow.getLocator().open(olmyid, "r");
							if(orderline != null){
								olsum += orderline.OL_AMOUNT;
								i += 1;
							}
						}
						
					return null;
				}
			}.execute(null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	protected void delivery(final int count) {
		for (int d_id = 0; d_id < NUM_DISTRICTS; d_id++) {
			final String myid = "warehouse_"+Integer.toString(random.nextInt(NUM_WAREHOUSES));
			final String omyid = myid+"_order_" + Integer.toString(random.nextInt(NUM_ORDERS_PER_D));
			final String cmyid = myid+ "_customer_" + Integer.toString(random.nextInt(NUM_CUSTOMERS_PER_D));
			try {
				new Atomic<Object>() {
					@Override
					public Object atomically(AbstractDistinguishable self,Context transactionContext) {
						
						TpccOrder order = (TpccOrder)HyFlow.getLocator().open(omyid, "r");
						//order.delete();
							
						float olsum = (float)0;
						String crtdate = new java.util.Date().toString();
						int i = 1;
						while (i < order.O_OL_CNT) {
							if(i < NUM_ORDERS_PER_D){
								final String olmyid = myid+"_orderline_" + Integer.toString(i);
								TpccOrderline orderline = (TpccOrderline)HyFlow.getLocator().open(olmyid, "r");
								if(orderline != null){
									olsum += orderline.OL_AMOUNT;
									i += 1;
								}
							}
						}
						TpccCustomer customer = (TpccCustomer)HyFlow.getLocator().open(cmyid, "w");
						customer.C_BALANCE += olsum;
						customer.C_DELIVERY_CNT += 1;
						if (((NestedContext)transactionContext).getNestingModel() == NestingModel.OPEN) {
							((NestedContext)transactionContext).onLockAction(
								customer.getId().toString(), cmyid, true, true);
							m_hasOnAbort = true;
							m_hasOnCommit = true;
						}			
						return null;
					}
					@Override
					public void onAbort(Context __transactionContext__) {
						// TODO: automatic lock release on abort/commit, override only for special cases
						final TpccCustomer customer = (TpccCustomer)HyFlow.getLocator().open(cmyid, "w");
						((NestedContext)__transactionContext__).onLockAction(
								customer.getId().toString(), cmyid, true, false);
					}
					@Override
					public void onCommit(Context __transactionContext__) {
						final TpccCustomer customer = (TpccCustomer)HyFlow.getLocator().open(cmyid, "w");
						((NestedContext)__transactionContext__).onLockAction(
								customer.getId().toString(), cmyid, true, false);
					}
				}.execute(null);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void stockLevel(final int count) {
		int i=0;
		while (i < 20) {
				try {
					new Atomic<Object>() {
						@Override
						public Object atomically(AbstractDistinguishable self, Context transactionContext) {
							final String myid = "warehouse_"+Integer.toString(random.nextInt(NUM_WAREHOUSES));
							final String omyid = myid+"_order_" + Integer.toString(random.nextInt(NUM_ORDERS_PER_D));
							TpccOrder order = (TpccOrder)HyFlow.getLocator().open(omyid, "r");
							if (order != null) {
								int j = 1;
								while (j < order.O_OL_CNT) {
									if(j < NUM_ORDERS_PER_D){
										final String olmyid = myid+"_orderline_" + Integer.toString(j);
										TpccOrderline orderline = (TpccOrderline)HyFlow.getLocator().open(olmyid, "r");
									}
									j += 1;
								}
							}
						int k = 1;
						while (k <= 10) {
							String wid = "warehouse_"+Integer.toString(random.nextInt(NUM_WAREHOUSES));
							if(k<NUM_ITEMS){
								String smyid = wid+"_stock_"+ Integer.toString(k);
								HyFlow.getLocator().open(smyid, "r");
								k += 1;
							}
							else k += 1;
						}
						return null;
						}
					}.execute(null);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			 
			i += 1;
		}
		
	}
	
	protected void newOrder(final int count) {
		try {
			new Atomic<Object>() {
				@Override
				public Object atomically(AbstractDistinguishable self,Context transactionContext) {
					final int w_id = random.nextInt(NUM_WAREHOUSES);
					final String myid = "warehouse_"+Integer.toString(w_id);
					TpccWarehouse warehouse = (TpccWarehouse)HyFlow.getLocator().open(myid, "r");
					final int d_id = random.nextInt(NUM_DISTRICTS);
					final String dmyid = myid+"_"+ Integer.toString(d_id);
					TpccDistrict district = (TpccDistrict)HyFlow.getLocator().open(dmyid, "w");
					
					double D_TAX = district.D_TAX;
					int o_id = district.D_NEXT_O_ID;
					district.D_NEXT_O_ID = o_id + 1;
					final int c_id = random.nextInt(NUM_CUSTOMERS_PER_D);
					final String cmyid = myid+ "_customer_" + Integer.toString(c_id);
					TpccCustomer customer = (TpccCustomer)HyFlow.getLocator().open(cmyid, "w");
					double C_DISCOUNT = customer.C_DISCOUNT;
					String C_LAST = customer.C_LAST;
					String C_CREDIT = customer.C_CREDIT;

					// Create entries in ORDER and NEW-ORDER
					final String omyid = myid+"_order_" + Integer.toString(random.nextInt(NUM_ORDERS_PER_D));
					
					TpccOrder order = new TpccOrder(omyid);
					order.O_C_ID = c_id;
					order.O_CARRIER_ID = Integer.toString(random.nextInt(1000));
					order.O_ALL_LOCAL = true;
					int i = 1;
					while (i <= order.O_CARRIER_ID.length()) {
						final int i_id = random.nextInt(NUM_ITEMS);
						String item_id = "item_"+Integer.toString(i_id);
						TpccItem item = (TpccItem)HyFlow.getLocator().open(item_id, "w");
						if (item == null) {
							return null;
						}
						float I_PRICE = item.I_PRICE;
						String I_NAME = item.I_NAME;
						String I_DATA = item.I_DATA;
					
						String olmyid = myid+"_orderline_" + Integer.toString(random.nextInt(1000)+NUM_ORDERS_PER_D);
						TpccOrderline orderLine = new TpccOrderline(olmyid);
						orderLine.OL_QUANTITY = random.nextInt(1000);
						orderLine.OL_I_ID = i_id;
						orderLine.OL_SUPPLY_W_ID = w_id; 
						orderLine.OL_AMOUNT = (int)(orderLine.OL_QUANTITY  * I_PRICE);
						orderLine.OL_DELIVERY_D = null;
						orderLine.OL_DIST_INFO = Integer.toString(d_id);
						i += 1;
					}
					
					return null;
				}
			}.execute(null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	protected void payment(final int count) {
		final float h_amount = (float)(random.nextInt(500000) * 0.01);
		final int w_id = random.nextInt(NUM_WAREHOUSES);
		final String myid = "warehouse_"+Integer.toString(w_id);
		final int c_id = random.nextInt(NUM_CUSTOMERS_PER_D);
		final String cmyid = myid+ "_customer_" + Integer.toString(c_id);
		try {
			new Atomic<Object>() {
				@Override
				public Object atomically(AbstractDistinguishable self, Context transactionContext) {
					// Open Wairehouse Table
					//final int w_id = random.nextInt(NUM_WAREHOUSES);
					//final String myid = "warehouse_"+Integer.toString(w_id);
					TpccWarehouse warehouse = (TpccWarehouse)HyFlow.getLocator().open(myid, "w");
					warehouse.W_YTD += h_amount;

					// In DISTRICT table
					final int d_id = random.nextInt(NUM_DISTRICTS);
					final String dmyid = myid+"_"+ Integer.toString(d_id);
					TpccDistrict district = (TpccDistrict)HyFlow.getLocator().open(dmyid, "w");
					district.D_YTD += h_amount;

					//final int c_id = random.nextInt(NUM_CUSTOMERS_PER_D);
					//final String cmyid = myid+ "_customer_" + Integer.toString(c_id);
					TpccCustomer customer = (TpccCustomer)HyFlow.getLocator().open(cmyid, "w");
					
					customer.C_BALANCE -= h_amount;
					customer.C_YTD_PAYMENT += h_amount;
					customer.C_PAYMENT_CNT += 1;
					if (((NestedContext)transactionContext).getNestingModel() == NestingModel.OPEN) {
						((NestedContext)transactionContext).onLockAction(
							customer.getId().toString(), cmyid, true, true);
						m_hasOnAbort = true;
						m_hasOnCommit = true;
					}			
					return null;
				}
				@Override
				public void onAbort(Context __transactionContext__) {
					// TODO: automatic lock release on abort/commit, override only for special cases
					final TpccCustomer customer = (TpccCustomer)HyFlow.getLocator().open(cmyid, "w");
					((NestedContext)__transactionContext__).onLockAction(
							customer.getId().toString(), cmyid, true, false);
				}
				@Override
				public void onCommit(Context __transactionContext__) {
					final TpccCustomer customer = (TpccCustomer)HyFlow.getLocator().open(cmyid, "w");
					((NestedContext)__transactionContext__).onLockAction(
							customer.getId().toString(), cmyid, true, false);
				}
			}.execute(null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}

