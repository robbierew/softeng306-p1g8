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
	
	default public int getNumTasks() {
		return getTasksInParitalOrder().size();
	}
	
	default List<Task> getTaskListFromMask(int mask){
		List<Task> result = new ArrayList<Task>();
		int id = 0;
		while (mask != 0) {
			if ((mask & 1) == 1) {
				if (id < getNumTasks()) {
					result.add(getTaskByID(id));
				}else {
					throw new RuntimeException("Invalid Mask");
				}
			}
			mask = mask >> 1;
		}
		return result;
	}
}
