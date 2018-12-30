package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.List;

public interface TaskGraph {
	public List<Task> getTasksInParitalOrder();

	public List<Task> getRoots();

	public int getRootMask();

	public String getName();

	default public Task getTaskByID(int id) {
		return getTasksInParitalOrder().get(id);
	}

	public int getRemoteCostFor(Task parent, Task child);

	default public boolean isTaskFromGraph(Task check) {
		return this == check.getTaskGraph();
	}

	default public byte getNumTasks() {
		return (byte) getTasksInParitalOrder().size();
	}

	default List<Task> getTaskListFromMask(int mask) {
		List<Task> result = new ArrayList<Task>();

		// Ids initaly start from 0
		int id = 0;

		// after enough left shifts mask will become zero
		while (mask != 0) {

			// gets the first bit in the mask
			if ((mask & 1) == 1) {

				// checks that the bit in the mask corresponds to a valid id
				if (id < getNumTasks()) {
					result.add(getTaskByID(id));
				} else {
					throw new RuntimeException("Invalid Mask");
				}
			}

			// move to the next bit
			mask = mask >> 1;

			// increase to the next id
			id++;
		}

		return result;
	}
}
