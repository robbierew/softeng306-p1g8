package se306group8.scheduleoptimizer.taskgraph;

import java.util.List;

public interface Task {

	public String getName();

	public int getComputeCost();

	public byte getID();

	public int getMask();

	public int getParentTaskMask();

	public int getChildTaskMask();
	
	public int getBottomLevel();

	// Not sure if there is any reason this is a list over a Collection we will see
	public List<Task> getParentTasks();

	public List<Task> getChildTasks();

	public TaskGraph getTaskGraph();

	default public boolean isChild(Task childCanditate) {

		// bitwise and
		return (getChildTaskMask() & childCanditate.getMask()) != 0;
	}

	default public boolean isParent(Task parentCanditate) {

		// bitwise and
		return (getParentTaskMask() & parentCanditate.getMask()) != 0;
	}
}
