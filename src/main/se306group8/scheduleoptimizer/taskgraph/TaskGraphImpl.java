package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TaskGraphImpl implements TaskGraph {

	private String name;
	private List<Task> tasks;
	private int[][] costMatrix;
	private List<Task> roots;
	private int rootMask;

	public TaskGraphImpl(String name, List<Task> ordered, int[][] remoteCostMatrix) {
		this.name = name;
		tasks = Collections.unmodifiableList(ordered);
		costMatrix = remoteCostMatrix;
		roots = Collections.unmodifiableList(computeRoots(tasks));
		
		
		//iterate in reverse partial order this makes it easy to calc
		//every child task will have their bottomlevel's calculated first
		for (int taskNum = this.getNumTasks() -1;taskNum >= 0;taskNum--) {
			calcBottomTime((TaskImpl)tasks.get(taskNum));
		}
	}

	@Override
	public List<Task> getTasksInParitalOrder() {
		return tasks;
	}

	@Override
	public List<Task> getRoots() {
		return roots;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getRemoteCostFor(Task parent, Task child) {
		if ((parent.getChildTaskMask() & child.getMask()) == 0) {
			throw new RuntimeException("Tasks are not in parent child relation");
		}

		return costMatrix[parent.getID()][child.getID()];
	}

	private List<Task> computeRoots(List<Task> tasks) {
		List<Task> calcRoots = new ArrayList<Task>();
		rootMask = 0;
		for (Task t : tasks) {
			if (t.getParentTaskMask() == 0) {
				calcRoots.add(t);
				rootMask |= t.getMask();
			}
		}
		return calcRoots;
	}

	@Override
	public int getRootMask() {
		return rootMask;
	}
	
	private void calcBottomTime(TaskImpl t) {
		int highestChildBl = 0;
		for (Task child:t.getChildTasks()) {
			if (child.getBottomLevel() > highestChildBl) {
				highestChildBl = child.getBottomLevel();
			}
		}
		t.setBottomLevel(t.getComputeCost() + highestChildBl);
	}

}