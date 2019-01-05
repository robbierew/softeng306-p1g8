package se306group8.scheduleoptimizer.algorithm.childfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import se306group8.scheduleoptimizer.schedule.TreeSchedule;
import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class DuplicatePruner<T extends TreeSchedule> {
	
	private  Comparator<TaskAllocation> sorter = new IdSorter();
	
	private class IdSorter implements Comparator<TaskAllocation>{

		@Override
		public int compare(TaskAllocation o1, TaskAllocation o2) {
			return o2.getTask().getID() - o1.getTask().getID(); //sort by larger ID
		} 
		
	}
	
	
	
	public List<T> prune(List<T> schedules){
		List<T> keptSchedules = new ArrayList<T>();
		
		for (T s:schedules) {
			int numProcessors = s.getProblemStatement().getNumProcessors();
			List<TaskAllocation> endAllocs = new ArrayList<TaskAllocation>();
			
			for (int p = 1; p<= numProcessors;p++) {
				TaskAllocation lastAlloc = s.getLastAllocationForProcessor(p);
				if (lastAlloc != null) {
					endAllocs.add(lastAlloc);
				}
			}
			
			//sorted by largest id
			Collections.sort(endAllocs, sorter);
			
			for (TaskAllocation a:endAllocs) {
				
				
				//when you have 3 or more processors it is possible that you could
				//remove the task in the middle breaking normalization
				
				
				//because it is normal we check have higher processors have been used by checking
				//the next one
//				if (numProcessors > a.getProcessor()) {
//					if (s.getFirstAllocationForProcessor(a.getProcessor() + 1) != null) {
//						
//						//we check if we would cause a hole breaking normalization
//						//if true we consider this task not valid to be moved
//						if (s.getFirstAllocationForProcessor(a.getProcessor()) == a) {
//							continue;
//						}
//					}
//				}
				
				
				if (s.getLastAllocation() != a && s.getFirstAllocationForProcessor(a.getProcessor()) == a) {
					continue;
				}
				
				//s without t
				int allocated = s.getAllocatedMask() & ~a.getTask().getMask();
				
				//was it valid to remove t?
				if ((allocated & a.getTask().getChildTaskMask()) == 0) {

					//was the task we removed the last task from s?
					if (a == s.getLastAllocation()) {
						keptSchedules.add(s);
					}
					
					break;
				}
			}
			
		}
		
		return keptSchedules;
	}
}
