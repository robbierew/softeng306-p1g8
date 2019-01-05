package se306group8.scheduleoptimizer.algorithm.pruner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import se306group8.scheduleoptimizer.schedule.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

//Based on Oliver's paper he defines whats called Identical tasks,
//we use this concept to add an ordering on what tasks get added. 
public class IdenticalTaskOrderingPruner<T extends TreeSchedule> implements Pruner<T> {

	
	//be sure testing is done to make sure duplicate removing is compatible with this
	
	
	private int[] requiredAllocationMask;
	private TaskGraph graph;
	private boolean hasOrdering;
	
	private class IdenticalTaskOrderer implements Comparator<Task>{

		@Override
		public int compare(Task o1, Task o2) {
			int baseCompare = equals(o1,o2);
			if (baseCompare == 0) {
				return o1.getID() - o2.getID();
			}else {
				return baseCompare;
			}
			
		}
		
		
		public int equals(Task o1, Task o2) {
			
			if (o1.getParentTaskMask() > o2.getParentTaskMask()) {
				return 1;
			}
			
			if (o1.getParentTaskMask() < o2.getParentTaskMask()) {
				return -1;
			}
			
			if (o1.getChildTaskMask() > o2.getChildTaskMask()) {
				return 1;
			}
			
			if (o1.getChildTaskMask() < o2.getChildTaskMask()) {
				return -1;
			}
			
			if (o1.getComputeCost() > o2.getComputeCost()) {
				return 1;
			}
			
			if (o1.getComputeCost() < o2.getComputeCost()) {
				return -1;
			}
			
			List<Task> parents = o1.getParentTasks();
			
			for (Task t:parents) {
				int remote = graph.getRemoteCostFor(t, o1) - graph.getRemoteCostFor(t, o2);
				if (remote != 0) {
					return remote;
				}
			}
			
			List<Task> children = o1.getChildTasks();
			
			for (Task t:children) {
				int remote = graph.getRemoteCostFor(o1,t) - graph.getRemoteCostFor(o2,t);
				if (remote != 0) {
					return remote;
				}
			}
						
			return 0;
		}
	}
	
	public IdenticalTaskOrderingPruner(TaskGraph graph) {
		this.graph = graph;
		List<Task> tasks = new ArrayList<Task>(graph.getTasksInParitalOrder());
		requiredAllocationMask = new int[graph.getNumTasks()];
		IdenticalTaskOrderer order = new IdenticalTaskOrderer();
		Collections.sort(tasks, order);
		
		
		hasOrdering = false;
		
		for (int i=0;i<graph.getNumTasks()-1;i++) {
			
			//they are the "identical tasks" 
			if (order.equals(tasks.get(i),tasks.get(i+1)) == 0) {
				requiredAllocationMask[i+1] = tasks.get(i).getMask();
				hasOrdering = true;
			}
		}
	}
	
	public List<T> prune(List<T> schedules){
		
		if (!hasOrdering) {
			return schedules;
		}
		
		List<T> keptSchedules = new ArrayList<T>();
		for (T s:schedules) {
			Task lastTask = s.getLastAllocatedTask();
			int requiredMask = requiredAllocationMask[lastTask.getID()];
			
			// this bitwise if statement checks to see if all the we have what is required
			// if it is 0 it meets the requirements
			// if non 0 it does not
			if (~(s.getAllocatedMask() | ~requiredMask) == 0) {
				keptSchedules.add(s);
			}
			
		}
		
		return keptSchedules;
	}
}
