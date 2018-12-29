package se306group8.scheduleoptimizer.schedule;

import java.util.List;

import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;
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
	protected abstract TaskAllocation getLastAllocationForProcessorImpl(int processor);

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
	
	@Override
	public TaskAllocation getLastAllocationForProcessor(int processor) {
		validateProcessorNum(processor);
		return getLastAllocationForProcessorImpl(processor);
	}
	
	protected void checkAllocationValid(Schedule schedule, Task task, int processor) {
		
		validateTaskOrigin(schedule,task);
		validateProcessorNum(schedule,processor);
		
		if ((schedule.getAllocatedMask() & task.getMask()) != 0) {
			throw new RuntimeException("Cannot allocate task twice");
		}
		
		if ((schedule.getAllocatableMask() & task.getMask()) == 0) {
			throw new RuntimeException("Cannot allocate task some dependencies are not scheduled");
		}
		
	}
	
	protected TaskAllocation makeValidAllocation(Schedule schedule, Task task, int processor) {
		checkAllocationValid(schedule, task, processor);

		// task at the very least must be placed after the last task
		TaskAllocation last = schedule.getLastAllocationForProcessor(processor);

		// if there was no last allocation we are first task on that processor
		int starttime = last == null ? 0 : last.getEndTime();

		// task must also must be after all it parents including its remote costs
		for (Task parent : task.getParentTasks()) {
			TaskAllocation parentAlloc = schedule.getAllocationForTask(parent);
			if (parentAlloc.getProcessor() != processor) {
				starttime = Math.max(starttime,
						parentAlloc.getEndTime() + schedule.getProblemStatement().getRemoteCostFor(parent, task));
			} else {
				starttime = Math.max(starttime, parentAlloc.getEndTime());
			}
		}
		return new TaskAllocation(task, starttime, processor);
	}
	
	protected TaskAllocation makeValidAllocation(Task task, int processor) {
		return makeValidAllocation(this,task,processor);
	}
	
	private void validateProcessorNum(int processor) {
		validateProcessorNum(this,processor);
	}
	
	private void validateProcessorNum(Schedule schedule, int processor) {
		if (schedule.getProblemStatement().getNumProcessors() < processor) {
			throw new RuntimeException("Processor does not exist in problem statement");
		}
		
		if (processor < 1) {
			throw new RuntimeException("Processors must be one or greater");
		}
	}
	
	private void validateTaskOrigin(Task check) {
		validateTaskOrigin(this,check);
	}
	
	private void validateTaskOrigin(Schedule schedule ,Task check) {
		if (schedule.getProblemStatement().isTaskFromGraph(check) == false) {
			throw new RuntimeException("Task does not come from TaskGraph");
		}
	}
}
