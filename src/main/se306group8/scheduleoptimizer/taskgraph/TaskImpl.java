package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class TaskImpl implements Task {

	private String name;
	private int cost;
	private byte id;
	private int mask;
	private int parentTaskMask;
	private int childTaskMask;
	private List<Task> parents;
	private List<Task> children;
	private TaskGraph taskGraph;
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int getComputeCost() {
		return cost;
	}

	@Override
	public byte getID() {
		return id;
	}

	@Override
	public int getMask() {
		return mask;
	}

	@Override
	public int getParentTaskMask() {
		return parentTaskMask;
	}

	@Override
	public int getChildTaskMask() {
		return childTaskMask;
	}

	@Override
	public List<Task> getParentTasks() {
		return Collections.unmodifiableList(parents);
	}

	@Override
	public List<Task> getChildTasks() {
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public TaskGraph getTaskGraph() {
		return taskGraph;
	}
	
	//package private setters
	
	void setTaskGraph(TaskGraph graph) {
		taskGraph = graph;
	}
	
	void setName(String name) {
		this.name = name;
	}
	
	void setID(byte id) {
		this.id = id;
		
		//the mask is 2^id
		mask = 1 << id; 
	}
	
	void computeRelationMasks() {
		// compute the parent mask
		parentTaskMask = 0;
		for (Task t : parents) {

			// bitwise or
			parentTaskMask |= t.getMask();
		}
		
		// compute the child mask
		childTaskMask = 0;
		for (Task t : children) {

			// bitwise or
			childTaskMask |= t.getMask();
		}
	}
	
	void setParentTasks(Collection<? extends Task> parents) {
		
		//copy the parent tasks into own list
		this.parents = new ArrayList<Task>(parents);
		
		
	}

	void setChildTasks(Collection<? extends Task> children) {

		// copy the child tasks into own list
		this.children = new ArrayList<Task>(children);

	}

	void setCost(int cost) {
		this.cost = cost;
	}
	
}
