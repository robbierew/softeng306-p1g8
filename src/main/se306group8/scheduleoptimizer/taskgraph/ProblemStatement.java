package se306group8.scheduleoptimizer.taskgraph;

import java.util.List;

public class ProblemStatement implements TaskGraph{

	TaskGraph graph;
	byte numProcessors;
	
	public ProblemStatement(TaskGraph graph, int numProcessors) {
		this.graph = graph;
		this.numProcessors = (byte) numProcessors;
	}
	
	@Override
	public List<Task> getTasksInParitalOrder() {
		return graph.getTasksInParitalOrder();
	}

	@Override
	public List<Task> getRoots() {
		return graph.getRoots();
	}

	@Override
	public String getName() {
		return graph.getName();
	}

	@Override
	public int getRemoteCostFor(Task parent, Task child) {
		return graph.getRemoteCostFor(parent, child);
	}

	@Override
	public boolean isTaskFromGraph(Task check) {
		return graph.isTaskFromGraph(check);
	}
	
	public byte getNumProcessors() {
		return numProcessors;
	}

	@Override
	public int getRootMask() {
		return graph.getRootMask();
	}
	
	

}
