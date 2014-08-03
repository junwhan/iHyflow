package org.infinispan.iHyflow.core.tm.control.vote;

import aleph.AsynchMessage;
import org.infinispan.iHyflow.core.tm.control.ControlContext;
import org.infinispan.iHyflow.util.io.Logger;

public class VoteResult extends AsynchMessage{
	private Long txnId;
	private Boolean decision;
	public VoteResult(Long txnId, Boolean decision){
		this.txnId = txnId;
		this.decision = decision;
	}
	
	@Override
	public void run() {
		Logger.debug(txnId + ": Result from " + from);
		ControlContext.voteResult(txnId, from, decision);
	}
}
