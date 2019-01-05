package se306group8.scheduleoptimizer.algorithm.pruner;

import java.util.ArrayList;
import java.util.List;

import se306group8.scheduleoptimizer.schedule.Schedule;
import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;

//this class discards children that are not normalized
//Normalized means that the first tasks on each processor goes in increasing ID
public class NormalPruner<T extends Schedule> implements Pruner<T>{
	public List<T> prune(List<T> schedules) {
		List<T> keptSchedules = new ArrayList<T>();
		for (T s : schedules) {
			ProblemStatement pS = s.getProblemStatement();

			int largestID = -1;
			boolean toKeep = true;
			for (int p = 1; p <= pS.getNumProcessors(); p++) {
				TaskAllocation alloc = s.getFirstAllocationForProcessor(p);
				
				
				if (alloc == null) {
					largestID = Integer.MAX_VALUE;
					continue;
				}
				
				int taskID = alloc.getTask().getID();
				
 
				if (largestID > taskID) {
					toKeep = false;
					break;
				}
				largestID = taskID;
			}
			if (toKeep) {
				keptSchedules.add(s);
			}
		}

		return keptSchedules;
	}
}
