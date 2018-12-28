package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single task in a task graph, with a weight, name and dependencies.
 */
public final class TaskOld implements GraphEquality<TaskOld> {
	private final String name;
	private List<DependencyOld> children;
	private List<DependencyOld> parents;
	private final int cost;
	private int id;
	private boolean[] isParent;
	private boolean[] isChild;
	private boolean isIndependant = true;
	
	TaskOld(String name, int cost){
		this.name = name;
		this.cost = cost;
	}
	
	// Used to create the topological ordering required by the ids.
	void setId(int id) {
		this.id = id;
	}
	
	void setChildDependencies(Collection<DependencyOld> children){
		if(children.size() != 0) {
			isIndependant = false;
		}
		
		this.children = new ArrayList<>(children);
		
		int largestChild = 0;
		for(DependencyOld dep : children) {
			largestChild = Math.max(largestChild, dep.getTarget().getId());
		}
		
		isChild = new boolean[largestChild + 1];
		
		for(DependencyOld dep : children) {
			isChild[dep.getTarget().getId()] = true;
		}
	}
	
	void setParentDependencies(Collection<DependencyOld> parents){
		if(parents.size() != 0) {
			isIndependant = false;
		}
		
		this.parents = new ArrayList<>(parents);
		
		int largestParent = 0;
		for(DependencyOld dep : parents) {
			largestParent = Math.max(largestParent, dep.getSource().getId());
		}
		
		isParent = new boolean[largestParent + 1];
		
		for(DependencyOld dep : parents) {
			isParent[dep.getSource().getId()] = true;
		}
	}
	
	/** 
	 * Returns the name of this task. 
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	/** 
	 * Returns all of the tasks that depend on this task. 
	 */
	public List<DependencyOld> getChildren() {
		return children;
	}
	
	/** 
	 * Returns all of the tasks that this task depends on. 
	 */
	public List<DependencyOld> getParents() {
		return parents;
	}
	
	/** 
	 * Returns the time units this task requires. 
	 */
	public int getCost() {
		return cost;
	}
	
	@Override
	public boolean equalsIgnoringParents(TaskOld other) {
		if(other == this)
			return true;
		
		return other.name.equals(name) && other.cost == cost && GraphEqualityUtils.setsEqualIgnoringParents(children, other.children);
	}

	@Override
	public boolean equalsIgnoringChildren(TaskOld other) {
		if(other == this)
			return true;
		
		return other.name.equals(name) && other.cost == cost && GraphEqualityUtils.setsEqualIgnoringChildren(parents, other.parents);
	}
	
	public boolean isForkJoinCandidate() {
		return parents.size() <= 1 && children.size() <= 1;
	}
	
	public boolean isIndependant() {
		return isIndependant;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this)
			return true;
		
		if(!(other instanceof TaskOld)) {
			return false;
		}
		
		TaskOld task = (TaskOld) other;
		
		// TODO is this really equality? - Name should be unique, so do we need these other checks?
		
		return task.name.equals(name) && cost == task.cost && GraphEqualityUtils.setsEqualIgnoringChildren(parents, task.parents) && GraphEqualityUtils.setsEqualIgnoringParents(children, task.children);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cost, name, children.size(), parents.size());
	}

	public boolean isChild(TaskOld task) {
		return task.getId() >= isChild.length ? false : isChild[task.getId()];
	}
	
	public boolean isParent(TaskOld task) {
		return task.getId() >= isParent.length ? false : isParent[task.getId()];
	}
}
