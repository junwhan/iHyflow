package org.infinispan.iHyflow.core.tm.control.vote;

import aleph.AsynchMessage;
import org.infinispan.iHyflow.core.tm.control.ControlContext;
import org.infinispan.iHyflow.util.io.Logger;

public class VoteRequest extends AsynchMessage{
	private Long txnId;
	public VoteRequest(Long txnId){
		this.txnId = txnId;
	}
	
	@Override
	public void run() {
		Logger.debug(txnId + ": Request from " + from);
		ControlContext.vote(txnId, from);
	}

}
