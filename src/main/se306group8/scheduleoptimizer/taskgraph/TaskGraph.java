package se306group8.scheduleoptimizer.taskgraph;

import java.util.Collection;
import java.util.List;

/**
 * 
 * TaskGraph is an immutable representation of the DAG, including the weight annotations.
 *
 */
public final class TaskGraph {
	TaskGraph(String name, Collection<Task> tasks) {
		
	}
	
	/**
	 * Returns a List containing a topological ordering of all tasks.
	 */
	public List<Task> getAll() {
		assert false : "Not done yet";
		return null;
	}
	
	/**
	 * Returns a Collection of all tasks with no dependents.
	 */
	public Collection<Task> getRoots() {
		assert false : "Not done yet";
		return null;
	}

	public Collection<Dependency> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
