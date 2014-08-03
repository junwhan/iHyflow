package org.infinispan.iHyflow.benchmark.tm.vacation;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import aleph.comm.Address;
import aleph.dir.DirectoryManager;

import org.infinispan.iHyflow.HyFlow;
import org.infinispan.iHyflow.core.dir.control.ControlFlowDirectory;
import org.infinispan.iHyflow.core.tm.control.ControlContext;
import org.infinispan.iHyflow.util.io.Logger;
import org.infinispan.iHyflow.util.network.Network;

public class $HY$_Proxy_Reservation implements $HY$_IReservation{

	private static final long serialVersionUID = 1L;
	DirectoryManager locator;
	
    public $HY$_Proxy_Reservation() throws RemoteException{
		Logger.debug("Creating PROXY");
		((ControlFlowDirectory)HyFlow.getLocator()).addProxy(this);
		// Install Secrutiy Manager
    	if (System.getSecurityManager() == null)
			System.setSecurityManager ( new RMISecurityManager() );
    	// Create objects registery
    	int port = Network.getInstance().getPort()+1000;
    	Registry registry = null;
		try {
			Logger.debug("Reg: " + port);
			registry = LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    	
		// Remove old registered object
		try {
			UnicastRemoteObject.unexportObject(this, true);
		} catch (Exception e) {
			Logger.error("RMI unexporting");
		}
		$HY$_IReservation stub = ($HY$_IReservation) UnicastRemoteObject.exportObject(this, 0);
		// Bind the remote object's stub in the registry
		registry.rebind(Reservation.class.getName(), stub);
		Logger.debug("RMI stub inited");
		locator = HyFlow.getLocator();
    }

	@Override
	public boolean isAvaliable(Object id, ControlContext context)
			throws RemoteException {
		Address caller = ((ControlContext)context).getLastExecuter();
		Network.linkDelay(true, caller);
		ControlContext.getNeighbors(context.getContextId()).add(caller);
		return ((Reservation)locator.open(context, id, "r")).isAvailable(context);
	}

	@Override
	public boolean reserve(Object id, ControlContext context)
			throws RemoteException {
		Address caller = ((ControlContext)context).getLastExecuter();
		Network.linkDelay(true, caller);
		ControlContext.getNeighbors(context.getContextId()).add(caller);
		return ((Reservation)locator.open(context, id, "w")).reserve(context);
	}

	@Override
	public void release(Object id, ControlContext context)
			throws RemoteException {
		Address caller = ((ControlContext)context).getLastExecuter();
		Network.linkDelay(true, caller);
		ControlContext.getNeighbors(context.getContextId()).add(caller);
		((Reservation)locator.open(context, id, "w")).release(context);
	}

	@Override
	public int getPrice(Object id, ControlContext context)
			throws RemoteException {
		Address caller = ((ControlContext)context).getLastExecuter();
		Network.linkDelay(true, caller);
		ControlContext.getNeighbors(context.getContextId()).add(caller);
		return ((Reservation)locator.open(context, id, "r")).getPrice(context);
	}

	@Override
	public void setPrice(Object id, ControlContext context, int price)
			throws RemoteException {
		Address caller = ((ControlContext)context).getLastExecuter();
		Network.linkDelay(true, caller);
		ControlContext.getNeighbors(context.getContextId()).add(caller);
		((Reservation)locator.open(context, id, "w")).setPrice(price, context);
	}

	@Override
	public void retrieItem(Object id, ControlContext context)
			throws RemoteException {
		Address caller = ((ControlContext)context).getLastExecuter();
		Network.linkDelay(true, caller);
		ControlContext.getNeighbors(context.getContextId()).add(caller);
		((Reservation)locator.open(context, id, "w")).retrieItem(context);
	}

}
