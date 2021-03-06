package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * The Karma contention manager resolves conflicts by comparing the priorities of
 * the conflicting threads. For more information regarding priorities see {@link Context.getPriority()}. The
 * thread that is supposed to wait waits for a constant period of time.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public class Karma extends BackoffCM {

	//private static final Logger logger = Logger.getLogger(Context.TL2CM_LOGGER);
	private static int BACKOFF_PERIOD = 4;
	
	public Karma(int k) {
		BACKOFF_PERIOD = (int) Math.pow(10, k);
	}

	public Action resolve(WriteFieldAccess contentionPoint, Context contending, Context other) {
		int myPrio = contending.getPriority();
		int otherPrio = other.getPriority();
		BackoffData myState = getBackoffData();
		int myCurrTimestamp = contending.getLocalClock();
		// Check if the thread is running a new transaction
		// and if so we need to update the thread's state
		if (myState.originalTimestamp < myCurrTimestamp) {
			myState.originalTimestamp = myCurrTimestamp;
			myState.counter = 1;
		}
		else if (myPrio + myState.counter > otherPrio) {
			other.kill(other.getLocalClock());
			return Action.RETRY_LOCK;
		}
		// increase back-off counter and loop until
		// the time comes to retry	
		//logger.log(Level.WARNING, "TID={0} performing backoff #{1}", new Object[]{contending.getThreadId(), myState.counter});
		for (int i=0; i<BACKOFF_PERIOD; i++);
		myState.counter++;
		return Action.RETRY_LOCK;
	}
	
	public boolean requiresPriorities() {
		return true;
	}

	public boolean requiresTimestamps() {
		return false;
	}

}
