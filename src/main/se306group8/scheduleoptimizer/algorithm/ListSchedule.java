package se306group8.scheduleoptimizer.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import se306group8.scheduleoptimizer.taskgraph.DependencyOld;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskOld;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;

/**
 * Represents an allocation of tasks to different processors in a specific order. 
 * This may not be complete, as it is often used to view partial solutions.
 * 
 * This is a simple implementation of schedule that takes stores a mapping of tasks to a processorAllocation
 */
public final class ListSchedule implements Schedule {
	
	private final TaskGraphOld graph;
	private final List<List<TaskOld>> taskLists;
	private final Map<TaskOld, ProcessorAllocation> allocations;
	
	/**
	 * Creates a schedule.
	 * 
	 * @param graph The task graph that the schedule was generated from.
	 * @param taskLists The list of tasks that is assigned to each processor. Each task must be included in the graph, and each task must
	 *        be used once and only once.
	 */
	public ListSchedule(TaskGraphOld graph, List<List<TaskOld>> taskLists) {
		this(graph, new HashMap<>(), taskLists);
	}

	public ListSchedule(TaskGraphOld graph, Map<TaskOld, ProcessorAllocation> allocation) {
		this(graph, allocation, convertToLists(allocation));
	}

	private static List<List<TaskOld>> convertToLists(Map<TaskOld, ProcessorAllocation> allocation) {
		List<List<Entry<TaskOld, ProcessorAllocation>>> sortedLists = new ArrayList<>();
		List<List<TaskOld>> result = new ArrayList<>();
		
		int maxProcessor = 0;
		for(ProcessorAllocation value : allocation.values()) {
			maxProcessor = Math.max(maxProcessor, value.processor);
		}
		
		for(int i = 0; i < maxProcessor; i++) {
			sortedLists.add(new ArrayList<>());
		}
		
		for(Entry<TaskOld, ProcessorAllocation> entry : allocation.entrySet()) {
			sortedLists.get(entry.getValue().processor - 1).add(entry);
		}
		
		for(List<Entry<TaskOld, ProcessorAllocation>> list : sortedLists) {
			list.sort((a, b) -> a.getValue().startTime - b.getValue().startTime);
			
			result.add(list.stream()
			.map(Entry::getKey)
			.collect(Collectors.toList()));
		}
		
		return result;
	}

	public ListSchedule(TaskGraphOld graph, Map<TaskOld, ProcessorAllocation> allocation, List<List<TaskOld>> taskLists) {
		assert graph != null && taskLists != null && allocation != null && checkConsistency(graph, allocation, taskLists);
		
		this.graph = graph;
		this.taskLists = taskLists;
		allocations = allocation;
	}
	
	/** This method checks that all 3 arguments are consistent with each other in their representation. */
	private static boolean checkConsistency(TaskGraphOld graph, Map<TaskOld, ProcessorAllocation> allocation, List<List<TaskOld>> taskLists) {
		IdentityHashMap<TaskOld, Object> identityHashSet = new IdentityHashMap<>();
		Object value = new Object();
		for(TaskOld t : graph.getAll()) {
			identityHashSet.put(t, value);
		}
		
		//Check if the tasks are consistent.
		//GRAPH >= allocation
		if(!identityHashSet.keySet().containsAll(allocation.keySet())) {
			return false;
		}
		
		HashSet<TaskOld> taskListTasks = new HashSet<>();
		for(List<TaskOld> list : taskLists) {
			taskListTasks.addAll(list);
		}
		
		//GRAPH >= taskList
		if(!identityHashSet.keySet().containsAll(taskListTasks)) {
			return false;
		}
		
		//taskList >= allocation
		if(!taskListTasks.containsAll(allocation.keySet())) {
			return false;
		}
		
		return true;
	}

	/** Computes the processor allocation of a task, assuming the the allocations of the parents have already been calculated. */
	private ProcessorAllocation computeAllocation(TaskOld task) {
		ProcessorAllocation alloc = allocations.get(task);
		
		if(alloc != null) {
			return alloc;
		}
		
		int processor = 1;
		int index = -1;
		for(List<TaskOld> list : taskLists) {
			if((index = list.indexOf(task)) != -1) {
				break;
			}
			
			processor++;
		}
		
		int startTime = 0;
		ProcessorAllocation previousAllocation = null;
		
		if(index != 0) {
			previousAllocation = computeAllocation(taskLists.get(processor - 1).get(index - 1));
			startTime = previousAllocation.endTime;
		}
		
		for(DependencyOld dep : task.getParents()) {
			int time;
			
			TaskOld otherTask = dep.getSource();
			ProcessorAllocation otherAlloc = computeAllocation(otherTask);
			
			if(processor != otherAlloc.processor) {
				time = otherAlloc.startTime + otherTask.getCost() + dep.getCommunicationCost();
			} else {
				time = otherAlloc.startTime + otherTask.getCost();
			}
			
			if(time > startTime) {
				startTime = time;
			}
		}
		
		alloc = new ProcessorAllocation(task, startTime, processor, previousAllocation);
		allocations.put(task, alloc);
		
		return alloc;
	}
	
	public TaskGraphOld getGraph() {
		return graph;
	}
	
	public int getNumberOfUsedProcessors() {
		return taskLists.size();
	}
	
	public List<TaskOld> getTasksOnProcessor(int processor) {
		return taskLists.get(processor - 1);
	}

	@Override
	public ListIterator<List<TaskOld>> iterator() {
		return taskLists.listIterator();
	}
	
	public int getStartTime(TaskOld task) {
		return computeAllocation(task).startTime;
	}
	
	public int getProcessorNumber(TaskOld task) {
		return computeAllocation(task).processor;
	}
	
	public int getTotalRuntime() {
		//The maximum runtime is the last end time of a particular tasks.
		
		return taskLists.stream()				//Creates a stream of task Lists
				.flatMap(List<TaskOld>::stream)	//Creates a stream of streams of tasks
				.map(this::computeAllocation)	//Map each task to the allocation object
				.mapToInt(x -> x.endTime)		//Extracts the endTime from each allocation
				.max()							//Gets the maximum endTime
				.orElse(0);						//If there are no items return 0
	}
	
	@Override
	public String toString() {
		return "S: " + taskLists.toString() + "(" + getTotalRuntime() + ")";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(graph, taskLists);
	}
	
	/** Two schedules are considered equal if the two graphs are equal and the processors have the same named tasks in the same order on them. */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Schedule)) {
			return false;
		}
		
		Schedule other = (Schedule) obj;
		
		if(getNumberOfUsedProcessors() != other.getNumberOfUsedProcessors() || !graph.equals(other.getGraph())) {
			return false;
		}
		
		for(int i = 1; i <= getNumberOfUsedProcessors(); i++) {
			List<TaskOld> listA = getTasksOnProcessor(i);
			List<TaskOld> listB = other.getTasksOnProcessor(i);
			
			if(listA.size() != listB.size())
				return false;
			
			Iterator<TaskOld> iterA = listA.iterator();
			Iterator<TaskOld> iterB = listB.iterator();
			
			for(; iterA.hasNext(); ) {
				if(!iterA.next().getName().equals(iterB.next().getName())) {
					return false;
				}
			}
		}
		
		return true;
	}
}