package se306group8.scheduleoptimizer.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class TreeSchedule extends ScheduleValidator{

	
	private ProblemStatement statement;
	private int allocatedMask;
	private int allocatableMask;
	private int runtime;
	private TaskAllocation[] allocationTaskMap;
	private List<Task> allocatedTasks;
	private Collection<Task> allocatableTasks;
	private List<TaskAllocation>[] allocationProcessorMap;
	
	private TreeSchedule parent;
	
	//empty schedule constructor
	@SuppressWarnings("unchecked")
	public TreeSchedule(ProblemStatement statement) {
		int arraySize = statement.getNumTasks();
		
		this.statement = statement;
		
		allocatedMask = 0;
		runtime = 0;
		
		allocatableMask = statement.getRootMask();
		
		allocationTaskMap = new TaskAllocation[arraySize];
		
		allocatedTasks = new ArrayList<Task>();
		allocatableTasks = new ArrayList<Task>();
		
		//Java has stupid generics rules
		allocationProcessorMap = new ArrayList[arraySize];
		for (int i=0;i<arraySize;i++) {
			allocationProcessorMap[i] = new ArrayList<TaskAllocation>();
		}
		
		parent = null;
	}
	
	public TreeSchedule(TreeSchedule parent,Task nextTask, int processor) {
		//throws an exception if invalid
		TaskAllocation nextAlloc = makeValidAllocation(nextTask,processor);
		
		statement = parent.getProblemStatement();
		allocatedMask = parent.getAllocatableMask() | nextTask.getMask();
		
		runtime = Math.max(parent.getRuntime(), nextAlloc.getEndTime());
		
		//remove the previous task from the list
		allocatableMask = parent.getAllocatableMask() & ~nextTask.getMask();
		
		for (Task child:nextTask.getChildTasks()) {
			//this bitwise if statement checks to see if all the child's parents are allocated
			if (~(allocatedMask | ~child.getParentTaskMask()) == 0){
				allocatableMask |= child.getMask();
			}
		}
		
		//add the next allocation to the map
		allocationTaskMap = parent.allocationTaskMap; 
		allocationTaskMap[nextTask.getID()] = nextAlloc;
		
		allocatedTasks = getTaskListFromMask(allocatedMask);
		allocatableTasks = getTaskListFromMask(allocatableMask);
		
		this.parent = parent;
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
		return new ArrayList<Task>(allocatedTasks);
	}

	@Override
	public int getAllocatedMask() {
		return allocatedMask;
	}

	@Override
	public List<TaskAllocation> getAllocations() {
		ArrayList<TaskAllocation> result = new ArrayList<TaskAllocation>();
		for (List<TaskAllocation> allocations:allocationProcessorMap) {
			result.addAll(allocations);
		}
		return result;
	}

	@Override
	public Collection<Task> getAllocatableTasks() {
		return new ArrayList<Task>(allocatableTasks);
	}

	@Override
	public int getAllocatableMask() {
		return allocatableMask;
	}

	@Override
	protected List<TaskAllocation> getAllocationsForProcessorImpl(int processor) {
		return allocationProcessorMap[processor];
	}

	@Override
	protected TaskAllocation getAllocationForTaskImpl(Task task) {
		return allocationTaskMap[task.getID()];
	}

	public TreeSchedule getParent() {
		return parent;
	}
}
