package org.deuce.transaction.lsa;

import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.lsa.field.Field;
import org.deuce.transaction.lsa.field.Field.Type;
import org.deuce.transaction.lsa.field.WriteFieldAccess;
import org.deuce.transaction.lsa.ReadSet;
import org.deuce.transaction.lsa.WriteSet;
import org.deuce.transaction.util.BooleanArrayList;
import org.deuce.transform.Exclude;

/**
 * LSA implementation
 * 
 * @author Pascal Felber
 */
@Exclude
final public class Context extends org.deuce.transaction.AbstractContext {

	final private static TransactionException WRITE_FAILURE_EXCEPTION =
		new TransactionException("Fail on write (read previous version).");

	final private static TransactionException EXTEND_FAILURE_EXCEPTION =
		new TransactionException("Fail on extend.");

	final private static TransactionException READ_ONLY_FAILURE_EXCEPTION =
		new TransactionException("Fail on write (read-only hint was set).");

	final private static AtomicInteger clock = new AtomicInteger(0);
	final private static AtomicInteger threadID = new AtomicInteger(0);

	final private static boolean RO_HINT = Boolean.getBoolean("org.deuce.transaction.lsa.rohint");

	final private ReadSet readSet = new ReadSet(1024);
	final private WriteSet writeSet = new WriteSet(32);

	// Keep per-thread read-only hints (uses more memory but faster)
	final private BooleanArrayList readWriteMarkers = new BooleanArrayList();
	private boolean readWriteHint = true;
	private int atomicBlockId;

	private int readHash;
	private int readLock;
	private Object readValue;

	private int startTime;
	private int endTime;
	private int id;

	public Context() {
		// Unique identifier among active threads
		id = threadID.incrementAndGet();
	}

	public void init(int blockId) {
		readSet.clear();
		writeSet.clear();
		startTime = endTime = clock.get();
		if (RO_HINT) {
			atomicBlockId = blockId;
			readWriteHint = readWriteMarkers.get(atomicBlockId);
		}
	}

	public boolean commit() {
		if (!writeSet.isEmpty()) {
			int newClock = clock.incrementAndGet();
			if (newClock != startTime + 1 && !readSet.validate(id)) {
				rollback();
				return false;
			}
			// Write values and release locks
			writeSet.commit(newClock);
		}
		return true;
	}

	public boolean rollback() {
		// Release locks
		writeSet.rollback();
		return true;
	}

	private boolean extend() {
		int now = clock.get();
		if (readSet.validate(id)) {
			endTime = now;
			return true;
		}
		return false;
	}

	public void beforeReadAccess(Object obj, long field) {
		readHash = LockTable.hash(obj, field);
		// Check if the field is locked (may throw an exception)
		readLock = LockTable.checkLock(readHash, id);
	}

	private boolean onReadAccess(Object obj, long field, Type type) {
		if (readLock < 0) {
			// We already own that lock
			WriteFieldAccess w = writeSet.get(readHash, obj, field);
			if (w == null)
				return false;
			readValue = w.getValue();
			return true;
		}
		boolean b = false;
		while (true) {
			while (readLock <= endTime) {
				// Re-read timestamp (check for race)
				int lock = LockTable.checkLock(readHash, id);
				if (lock != readLock) {
					readLock = lock;
					readValue = Field.getValue(obj, field, type);
					b = true;
					continue;
				}
				// We have read a valid value (in snapshot)
				if (readWriteHint) {
					// Save to read set
					readSet.add(obj, field, readHash, lock);
				}
				return b;
			}

			// Try to extend snapshot
			if (!(readWriteHint && extend())) {
				throw EXTEND_FAILURE_EXCEPTION;
			}
		}
	}

	private void onWriteAccess(Object obj, long field, Object value, Type type) {
		if (!readWriteHint) {
			// Change hint to read-write
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}

		int hash = LockTable.hash(obj, field);

		// Lock entry (might throw an exception)
		int timestamp = LockTable.lock(hash, id);

		if (timestamp < 0) {
			// We already own that lock
			writeSet.append(hash, obj, field, value, type);
			return;
		}

		if (timestamp > endTime) {
			// Handle write-after-read
			if (readSet.contains(obj, field)) {
				// Abort
				LockTable.setAndReleaseLock(hash, timestamp);
				throw WRITE_FAILURE_EXCEPTION;
			}
			// We delay validation until later (although we could already validate once here)
		}

		// Add to write set
		writeSet.add(hash, obj, field, value, type, timestamp);
	}

	public Object onReadAccess(Object obj, Object value, long field) {
		return (onReadAccess(obj, field, Type.OBJECT) ? readValue : value);
	}

	public boolean onReadAccess(Object obj, boolean value, long field) {
		return (onReadAccess(obj, field, Type.BOOLEAN) ? (Boolean) readValue : value);
	}

	public byte onReadAccess(Object obj, byte value, long field) {
		return (onReadAccess(obj, field, Type.BYTE) ? ((Number) readValue).byteValue() : value);
	}

	public char onReadAccess(Object obj, char value, long field) {
		return (onReadAccess(obj, field, Type.CHAR) ? (Character) readValue : value);
	}

	public short onReadAccess(Object obj, short value, long field) {
		return (onReadAccess(obj, field, Type.SHORT) ? ((Number) readValue).shortValue() : value);
	}

	public int onReadAccess(Object obj, int value, long field) {
		return (onReadAccess(obj, field, Type.INT) ? ((Number) readValue).intValue() : value);
	}

	public long onReadAccess(Object obj, long value, long field) {
		return (onReadAccess(obj, field, Type.LONG) ? ((Number) readValue).longValue() : value);
	}

	public float onReadAccess(Object obj, float value, long field) {
		return (onReadAccess(obj, field, Type.FLOAT) ? ((Number) readValue).floatValue() : value);
	}

	public double onReadAccess(Object obj, double value, long field) {
		return (onReadAccess(obj, field, Type.DOUBLE) ? ((Number) readValue).doubleValue() : value);
	}

	public void onWriteAccess(Object obj, Object value, long field) {
		onWriteAccess(obj, field, value, Type.OBJECT);
	}

	public void onWriteAccess(Object obj, boolean value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.BOOLEAN);
	}

	public void onWriteAccess(Object obj, byte value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.BYTE);
	}

	public void onWriteAccess(Object obj, char value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.CHAR);
	}

	public void onWriteAccess(Object obj, short value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.SHORT);
	}

	public void onWriteAccess(Object obj, int value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.INT);
	}

	public void onWriteAccess(Object obj, long value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.LONG);
	}

	public void onWriteAccess(Object obj, float value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.FLOAT);
	}

	public void onWriteAccess(Object obj, double value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.DOUBLE);
	}
}
