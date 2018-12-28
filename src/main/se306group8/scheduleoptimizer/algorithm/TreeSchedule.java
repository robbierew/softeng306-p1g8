package se306group8.scheduleoptimizer.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.taskgraph.DependencyOld;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskOld;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;

/**
 * This class represents a partial schedule. It stores a parent schedule, and
 * the task and processor that was added to it. This allows a nice easy to work
 * with model of a schedule.
 */
public class TreeSchedule implements Comparable<TreeSchedule> {
	// Constructed fields
	private final TreeSchedule parent;

	/** If this is null then this is the empty schedule */
	private final TaskGraphOld graph;
	private final MinimumHeuristic heuristic;

	// Calculated fields
	private final ProcessorAllocation allocation;
	/** A bound that any full solution using this solution must be greater than. */
	private final int lowerBound;
	private final int idleTime;
	private final int numberOfUsedProcessors;
	private final int runtime;

	private final int largestRoot;
	
	// Per task arrays
	private final ProcessorAllocation[] allocations;
	private final int[] numberOfParentsUncheduled;
	
	//[task][processor] P=0 is the time for a processor where nothing has been allocated
//	private final int[][] dataReadyTime;
	
	//The time when a task is required to have started by by tasks that have already been scheduled.
	private final int[] requiredBy;

	// Per processor arrays
	/** This array.length = numberOfUsedProcessors */
	private final ProcessorAllocation[] lastAllocationOnProcessor;
	private final int[] numberOfTasksOnProcessor;
	/** Tasks that can be removed from the children without invalidating the ordering. */
	private final boolean[] removableTasks;
	private final boolean[] fixed;
	
	//Sets
	private final List<TaskOld> allocatable;
	private final List<TaskOld> allocated;

	// Booleans
	private final boolean isComplete;
	
	//Id caching for the blockschedule
	private boolean hasId = false;
	private int id;

	private final boolean isFixed;

	/**
	 * Creates an empty schedule.
	 * 
	 * @param heuristic The heuristic that is used to calculate the lower bound for
	 *                  this schedule and all children.
	 */
	public TreeSchedule(TaskGraphOld graph, MinimumHeuristic heuristic, int processors) {
		assert graph != null;
		int numberOfTasks = graph.getAll().size();

		this.graph = graph;
		this.allocation = null;
		this.parent = null;
		this.heuristic = heuristic;

		idleTime = 0;
		numberOfUsedProcessors = 0;
		allocations = new ProcessorAllocation[numberOfTasks];
		runtime = 0;
		numberOfTasksOnProcessor = new int[processors];
		
		numberOfParentsUncheduled = new int[numberOfTasks];
		for (TaskOld task : graph.getAll()) {
			numberOfParentsUncheduled[task.getId()] = task.getParents().size();
		}

		lastAllocationOnProcessor = new ProcessorAllocation[processors];

		allocatable = new ArrayList<>(graph.getRoots());
		((ArrayList<TaskOld>) allocatable).ensureCapacity(graph.getAll().size());
		allocatable.sort((a, b) -> a.getId() - b.getId());
		
		allocated = new ArrayList<>(graph.getAll().size());
		((ArrayList<TaskOld>) allocated).ensureCapacity(graph.getAll().size());
		
		isComplete = false;

		lowerBound = heuristic.estimate(this);
		largestRoot = -1;
		
		removableTasks = new boolean[graph.getAll().size()];
		Arrays.fill(removableTasks, true);
		
		requiredBy = new int[graph.getAll().size()];
		Arrays.fill(requiredBy, Integer.MAX_VALUE);
		
		hasId = true;
		id = -1;
		fixed = new boolean[graph.getAll().size()];
		
//		dataReadyTime = new int[graph.getAll().size()];
		
		isFixed = isFixedOrder();
	}

	/**
	 * Creates a schedule from a parent schedule and an allocation.
	 */
	public TreeSchedule(TaskOld task, int processor, TreeSchedule parent) {
		this(-1, -1, task, processor, parent);
	}
	
	/** An id of -1 means that this schedule has not been given an id. */
	public TreeSchedule(int lowerBound, int id, TaskOld task, int processor, TreeSchedule parent) {
		this.graph = parent.graph;
		this.parent = parent;
		this.heuristic = parent.heuristic;
		
		if(processor > parent.numberOfUsedProcessors) {
			largestRoot = Math.max(parent.largestRoot, task.getId());
		} else {
			largestRoot = parent.largestRoot;
		}
		
		hasId = id != -1;
		this.id = id;
		numberOfUsedProcessors = Math.max(parent.numberOfUsedProcessors, processor);
		allocatable = new ArrayList<>(graph.getAll().size());
		
		//Copy if we are not from the array. Else move, as we are the only child
		if(hasId && !parent.isEmpty()) {
			numberOfParentsUncheduled = parent.numberOfParentsUncheduled;
			lastAllocationOnProcessor = parent.lastAllocationOnProcessor;
			numberOfTasksOnProcessor = parent.numberOfTasksOnProcessor;
			removableTasks = parent.removableTasks;
			allocated = parent.allocated;
			allocations = parent.allocations;
//			dataReadyTime = parent.dataReadyTime;
			requiredBy = parent.requiredBy;
			fixed = parent.fixed;
		} else {
			numberOfParentsUncheduled = parent.numberOfParentsUncheduled.clone();
			lastAllocationOnProcessor = parent.lastAllocationOnProcessor.clone();
			numberOfTasksOnProcessor = parent.numberOfTasksOnProcessor.clone();
			removableTasks = parent.removableTasks.clone();
			allocated = new ArrayList<>(graph.getAll().size());
			allocated.addAll(parent.allocated);
			allocations = parent.allocations.clone();
//			dataReadyTime = parent.dataReadyTime.clone();
			requiredBy = parent.requiredBy.clone();
			fixed = parent.fixed.clone();
		}
		
		if(parent.isFixed && !parent.isEmpty()) {
			fixed[parent.allocation.task.getId()] = true; //We fix the task placed by the parent if this is a fixed ordering
		}
		
		numberOfTasksOnProcessor[processor - 1]++;

		//Find the new tasks to schedule by counting the number of parents they have left.
		for (int i = 0; i < task.getChildren().size(); i++) {
			TaskOld child = task.getChildren().get(i).getTarget();
			
			numberOfParentsUncheduled[child.getId()]--;

			if (numberOfParentsUncheduled[child.getId()] == 0) {
				allocatable.add(child);
			}
		}

		int processorReadyTime;

		ProcessorAllocation previousAllocation = parent.getLastAllocationForProcessor(processor);
		if (previousAllocation != null) {
			processorReadyTime = previousAllocation.endTime;
		} else {
			processorReadyTime = 0;
		}

		int startTime = 0;
		
		for (int i = 0; i < task.getParents().size(); i++) {
			DependencyOld dep = task.getParents().get(i);
			TaskOld parentTask = dep.getSource();
			removableTasks[parentTask.getId()] = false; //None of the parents can be removed.
			
			ProcessorAllocation alloc = parent.getAllocationFor(parentTask);
			int dataReadyTime;

			if (alloc.processor == processor) {
				dataReadyTime = alloc.endTime;
			} else {
				dataReadyTime = alloc.endTime + dep.getCommunicationCost();
			}

			if (dataReadyTime > startTime) {
				startTime = dataReadyTime;
			}
		}
		
		for (int i = 0; i < task.getParents().size(); i++) {
			DependencyOld dep = task.getParents().get(i);
			TaskOld parentTask = dep.getSource();
			ProcessorAllocation alloc = parent.getAllocationFor(parentTask);
			
			if(alloc.processor != processor) {
				requiredBy[parentTask.getId()] = startTime - parentTask.getCost() - dep.getCommunicationCost();
			}
		}
		
//		dataReadyTime[task.getId()] = startTime;

		startTime = Math.max(startTime, processorReadyTime);
		
		idleTime = parent.idleTime + startTime - processorReadyTime;

		for (int i = 0; i < parent.allocatable.size(); i++) {
			TaskOld oldAllocatable = parent.allocatable.get(i);
			
			if (oldAllocatable != task) {
				allocatable.add(oldAllocatable);
			}
		}

		//Sort the tasks to get a better order for schedule exploration.
		allocatable.sort((a, b) -> a.getId() - b.getId());
		
		allocation = new ProcessorAllocation(task, startTime, processor, getLastAllocationForProcessor(processor));

		lastAllocationOnProcessor[processor - 1] = allocation;
		allocations[task.getId()] = allocation;

		allocated.add(task);

		isComplete = allocatable.isEmpty();

		runtime = Math.max(parent.runtime, allocation.endTime);

		//Use the given bound if we are built from the array
		if(!hasId) {
			if (isComplete) {
				this.lowerBound = runtime;
			} else {
				this.lowerBound = heuristic.estimate(this);
			}
		} else {
			this.lowerBound = lowerBound;
		}
		
		isFixed = isFixedOrder();
	}
	
	/** Works out if we have the required characteristics to be a fixed order. */
	private boolean isFixedOrder() {
		int[] outWeight = new int[graph.getAll().size()];
		int[] dataReadyTime = new int[graph.getAll().size()];
		
		int parentProcessor = 0;
		int childId = -1;
		
		//Populate the child values
		for(TaskOld task : allocatable) {
			switch(task.getParents().size()) {
			case 0:
				break;
			case 1:
				DependencyOld dep = task.getParents().get(0);
				dataReadyTime[task.getId()] = dep.getCommunicationCost() + getAllocationFor(dep.getSource()).endTime;
				
				ProcessorAllocation alloc = getAllocationFor(dep.getSource());
				
				//Check if this parent processor is different, if it is cry.
				if(parentProcessor != 0 && alloc.processor != parentProcessor) {
					return false;
				} else {
					parentProcessor = alloc.processor;
				}
				break;
			default:
				return false;
			}
			
			switch(task.getChildren().size()) {
			case 0:
				break;
			case 1:
				DependencyOld dep = task.getChildren().get(0);
				outWeight[task.getId()] = dep.getCommunicationCost();
				
				//Check if this child is the same as all the other children
				if(childId != -1 && dep.getTarget().getId() != childId) {
					return false;
				} else {
					childId = dep.getTarget().getId();
				}
				break;
			default:
				return false;
			}
		}
		
		allocatable.sort((a, b) -> {
			//Sort by non decreasing data-ready time.
			//Break ties by sorting by non-increasing out-edge cost
			//Break ties by sorting by increasing id.
			
			if(dataReadyTime[a.getId()] != dataReadyTime[b.getId()]) {
				return dataReadyTime[a.getId()] - dataReadyTime[b.getId()];
			}
			
			if(outWeight[a.getId()] != outWeight[b.getId()]) {
				return outWeight[b.getId()] - outWeight[a.getId()];
			}
			
			return a.getId() - b.getId();
		});
		
		for(int i = 1; i < allocatable.size(); i++) {
			if(outWeight[allocatable.get(i - 1).getId()] < outWeight[allocatable.get(i).getId()])
				return false;
		}
		
		return true;
	}
	
	/** Returns true if this task can be removed while keeping the topological ordering. */
	public boolean isRemovable(TaskOld t) {
		return removableTasks[t.getId()];
	}
	
	public int getNumberOfTasksOnProcessor(int p) {
		return p > numberOfTasksOnProcessor.length ? 0 : numberOfTasksOnProcessor[p - 1];
	}
	
	public int getLargestRoot() {
		return largestRoot;
	}

	/** Returns true if the schedule is empty */
	public boolean isEmpty() {
		return allocation == null;
	}

	/** Returns the amount of time wasted */
	public int getIdleTime() {
		return idleTime;
	}

	public int getRuntime() {
		return runtime;
	}

	/**
	 * Returns the ProcessorAllocation instance that this task was scheduled on.
	 * This returns null if the task has not been scheduled.
	 */
	public ProcessorAllocation getAllocationFor(TaskOld t) {
		return allocations[t.getId()];
	}
	
	public boolean isFixed() {
		return isFixed;
	}

	/**
	 * Returns the last processor allocation on a processor, null if no allocation
	 * 
	 * @param processor
	 * @return
	 */
	public ProcessorAllocation getLastAllocationForProcessor(int processor) {
		if (processor > lastAllocationOnProcessor.length)
			return null;

		return lastAllocationOnProcessor[processor - 1];
	}

	public List<List<TaskOld>> computeTaskLists() {
		if (isEmpty()) {
			return new ArrayList<>();
		}

		List<List<TaskOld>> result;
		if (parent.isEmpty()) {
			result = new ArrayList<>();
		} else {
			result = parent.computeTaskLists();
		}

		while (result.size() < numberOfUsedProcessors) {
			result.add(new ArrayList<>());
		}

		result.get(allocation.processor - 1).add(allocation.task);

		return result;
	}

	public boolean isComplete() {
		return isComplete;
	}

	/**
	 * Returns the full schedule. This may be null if this solution is not a full
	 * solution.
	 */
	public Schedule getFullSchedule() {
		if (!isComplete())
			return null;

		return new ListSchedule(graph, computeTaskLists());
	}

	public ProcessorAllocation getMostRecentAllocation() {
		return allocation;
	}

	public TreeSchedule getParent() {
		return parent;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public String toString() {
		return computeTaskLists().toString() + "(" + lowerBound + ")";
	}

	@Override
	public int compareTo(TreeSchedule o) {
		return getLowerBound() - o.getLowerBound();
	}

	public TaskGraphOld getGraph() {
		return this.graph;
	}

	public Collection<TaskOld> getAllocated() {
		return allocated;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TreeSchedule)) {
			return false;
		}

		TreeSchedule other = (TreeSchedule) obj;
		
		return graph.equals(other.graph) && Arrays.deepEquals(allocations, other.allocations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(graph, Arrays.hashCode(allocations));
	}

	public List<TaskOld> getAllocatable() {
		return allocatable;
	}

	/**
	 * This gets the number of used processor. A processor is considered used if
	 * there is a task allocated on it, or on some processor number larger than it.
	 */
	public int getNumberOfUsedProcessors() {
		return numberOfUsedProcessors;
	}

	public void setId(int index) {
		hasId = true;
		id = index;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean hasId() {
		return hasId;
	}

	/** Returns the time by which a task must have started for the outgoing costs to be valid. */
	public int getRequiredBy(TaskOld task) {
		return requiredBy[task.getId()];
	}

	public boolean wasFixed(TaskOld task) {
		return fixed[task.getId()];
	}
}
