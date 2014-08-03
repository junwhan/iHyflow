package org.infinispan.iHyflow.core.tm.control;

import org.infinispan.iHyflow.util.io.Logger;
import aleph.Message;

public class DistributedAbort extends Message{
	private Long txnId;
	public DistributedAbort(Long txnId){
		this.txnId = txnId;
	}
	
	@Override
	public void run() {
		Logger.debug(txnId + ": Distributed Abort from " + from);
		ControlContext.abort(txnId);
	}

}
