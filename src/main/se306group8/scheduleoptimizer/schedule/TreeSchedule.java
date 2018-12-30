package se306group8.scheduleoptimizer.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se306group8.scheduleoptimizer.schedule.taskallocation.AllocationHistory;
import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class TreeSchedule extends ScheduleValidator{

	
	private ProblemStatement statement;
	private int allocatedMask;
	private int allocatableMask;
	private int runtime;

	
	//these variables are lazy loaded please use there getters to ensure it has a value
	//there value are calculated from their mask
	private List<Task> allocatedTasks;
	private Collection<Task> allocatableTasks;
	private TreeSchedule parent;
	///////////////////////////////////////////
	
	
	private AllocationHistory allocHistory;
	
	//empty schedule constructor
	public TreeSchedule(ProblemStatement statement) {
		
		this.statement = statement;
		
		allocatedMask = 0;
		runtime = 0;
		
		allocatableMask = statement.getRootMask();
		
		allocHistory = new AllocationHistory(statement);
		
		allocatedTasks = new ArrayList<Task>();
		allocatableTasks = statement.getRoots();		
		
		parent = null;
	}
	

	public TreeSchedule(TreeSchedule parent,Task nextTask, int processor) {
		
		statement = parent.getProblemStatement();

		//throws an exception if invalid
		TaskAllocation lastAlloc = makeValidAllocation(parent,nextTask,processor);
		
		allocatedMask = parent.getAllocatedMask();
		
		runtime = Math.max(parent.getRuntime(), lastAlloc.getEndTime());
		
		allocatableMask = parent.getAllocatableMask();
		
		updateMasksWithNextTask(nextTask);
				
		allocHistory = parent.allocHistory.fork(lastAlloc);
		
		this.parent = parent;
	}
	
	public TreeSchedule(AllocationHistory history) {
		this(history,false);
	}

	protected TreeSchedule(AllocationHistory history, boolean safeHistory) {
		
		//init values before adding the TaskAllocations
		statement = history.getStatement();
		allocatedMask = 0;
		allocatableMask = statement.getRootMask();
		runtime = 0;
		
		TaskAllocation[] allocations = history.getAllocationsAsArray();
		for (TaskAllocation alloc: allocations) {
			if (!safeHistory) {
				
				//throws an exception if false
				checkAllocationValid(alloc.getTask(),alloc.getProcessor());
			}
			
			updateMasksWithNextTask(alloc.getTask());
			
			runtime = Math.max(runtime, alloc.getEndTime());
			
		}
		
		allocHistory = history;
		
	}

	public AllocationHistory getAllocationHistory() {
		return allocHistory;
	}

	//if this returns null then you have root
	public TreeSchedule getParent() {
		if (parent == null && !isEmpty()) {
			parent = new TreeSchedule(allocHistory.previous());
		}
		
		return parent;
	}
	
	//if this returns null then you have root
	public TaskAllocation getLastAllocation() {
		return allocHistory.getLastAllocation();
	}
	
	//if this returns null then you have root
	public Task getLastAllocatedTask() {
		return allocHistory.getLastAllocatedTask();
	}

	@Override
	public ProblemStatement getProblemStatement() {
		return statement;
	}

	@Override
	public int getRuntime() {
		return runtime;
	}

	@Override
	public List<Task> getAllocatedTasks() {
		
		//lazy load
		if (allocatedTasks == null) {
			allocatedTasks = getTaskListFromMask(allocatedMask);
		}
		
		return Collections.unmodifiableList(allocatedTasks);
	}

	@Override
	public int getAllocatedMask() {
		return allocatedMask;
	}

	@Override
	public List<TaskAllocation> getAllocations() {
		return allocHistory.getAllocationsAsList();
	}

	@Override
	public Collection<Task> getAllocatableTasks() {
		
		//lazy load
		if (allocatableTasks == null) {
			allocatableTasks = getTaskListFromMask(allocatableMask);
		}
		return Collections.unmodifiableCollection(allocatableTasks);
	}

	@Override
	public int getAllocatableMask() {
		return allocatableMask;
	}

	@Override
	protected List<TaskAllocation> getAllocationsForProcessorImpl(int processor) {
		return allocHistory.getAllocationsForProcessorAsList(processor);
	}

	@Override
	protected TaskAllocation getAllocationForTaskImpl(Task task) {
		return allocHistory.getAllocationForTask(task);
	}


	@Override
	protected TaskAllocation getLastAllocationForProcessorImpl(int processor) {
		return allocHistory.getLastAllocationForProcessor(processor);
	}
	
	private void updateMasksWithNextTask(Task nextTask) {
		// remove the previous task from the list
		allocatableMask &= ~nextTask.getMask();
		allocatedMask |= nextTask.getMask();
		for (Task child : nextTask.getChildTasks()) {
			// this bitwise if statement checks to see if all the child's parents are
			// allocated
			if (~(allocatedMask | ~child.getParentTaskMask()) == 0) {
				allocatableMask |= child.getMask();
			}
		}
	}

}
