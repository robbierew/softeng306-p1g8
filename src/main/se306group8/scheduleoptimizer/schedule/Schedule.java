package se306group8.scheduleoptimizer.schedule;

import java.util.Collection;
import java.util.List;

import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public interface Schedule {
	public ProblemStatement getProblemStatement();
	
	public TaskAllocation getAllocationForTask(Task task);
	
	public List<TaskAllocation> getAllocationsForProcessor(int processor);
	
	public TaskAllocation getLastAllocationForProcessor(int processor) ;
	
	public int getRuntime();
	
	default public boolean isComplete() {
		return getAllocatableMask() == 0;
	}
	
	default public boolean isEmpty() {
		return getAllocatedMask() == 0;
	}
	
	//should maintain a partial order
	public List<Task> getAllocatedTasks();
	
	public int getAllocatedMask();
	
	//should maintain a partial order
	public List<TaskAllocation> getAllocations();
	
	public Collection<Task> getAllocatableTasks();
	
	public int getAllocatableMask();
	
	//for your convenience this method is also in schedule
	default public List<Task> getTaskListFromMask(int mask){
		return getProblemStatement().getTaskListFromMask(mask);
	}
}
