package org.infinispan.iHyflow.benchmark.tm.bst;

import org.infinispan.iHyflow.core.AbstractDistinguishable;
import org.infinispan.iHyflow.core.dir.IHyFlow;
import org.infinispan.iHyflow.transaction.Remote;

public class _Node 
	extends 	AbstractDistinguishable{

	private String id;
	private Integer value;
	private String rightId;
	private String leftId;
	
	public _Node(String id, Integer value) {
		this.id = id;
		this.value = value;
	}
	
	@Remote
	public void setRightChild(String rightId){
		this.rightId = rightId;
	}
	
	@Remote
	public void setLeftChild(String leftId){
		this.leftId = leftId;
	}

	@Remote
	public String getRightChild(){
		return rightId;
	}

	@Remote
	public String getLeftChild(){
		return leftId;
	}

	@Remote
	public Integer getValue(){
		return value;
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
}
