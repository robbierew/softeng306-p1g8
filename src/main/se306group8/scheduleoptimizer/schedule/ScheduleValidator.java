package se306group8.scheduleoptimizer.schedule;

import java.util.List;

import se306group8.scheduleoptimizer.taskgraph.Task;

/**
 * 
 * @author Robert
 *
 * Class that validates the method parameters before passing them to abstract implementation methods
 */
public abstract class ScheduleValidator implements Schedule{
	
	
	protected abstract List<TaskAllocation> getAllocationsForProcessorImpl(int processor);
	protected abstract TaskAllocation getAllocationForTaskImpl(Task task);
	

	@Override 
	public TaskAllocation getAllocationForTask(Task task) {
		
		validateTaskOrigin(task);
		
		if ((getAllocatedMask() & task.getMask()) == 0) {
			throw new RuntimeException("Cannot get allocation for unscheduled task");
		}
		
		return getAllocationForTaskImpl(task);
	}
	
	
	@Override
	public List<TaskAllocation> getAllocationsForProcessor(int processor){
		
		validateProcessorNum(processor);
		return getAllocationsForProcessorImpl(processor);
	}
	
	protected void checkAllocationValid(Task task, int processor) {
		
		validateTaskOrigin(task);
		validateProcessorNum(processor);
		
		if ((getAllocatedMask() & task.getMask()) != 0) {
			throw new RuntimeException("Cannot allocate task twice");
		}
		
		if ((getAllocatableMask() & task.getMask()) == 0) {
			throw new RuntimeException("Cannot allocate task some dependencies are not scheduled");
		}
		
	}
	
	protected TaskAllocation makeValidAllocation(Task task, int processor) {
		checkAllocationValid(task,processor);
		TaskAllocation last = getLastAllocationOnProcessor(processor);
		int starttime = last.getEndTime();
		return new TaskAllocation(task, starttime, processor);
	}
	
	private void validateProcessorNum(int processor) {
		if (getProblemStatement().getNumProcessors() < processor) {
			throw new RuntimeException("Processor does not exist in problem statement");
		}
		
		if (processor < 1) {
			throw new RuntimeException("Processors must be one or greater");
		}
	}
	
	private void validateTaskOrigin(Task check) {
		if (getProblemStatement().isTaskFromGraph(check) == false) {
			throw new RuntimeException("Task does not come from TaskGraph");
		}
	}
}
