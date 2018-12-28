package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * 
 * TaskGraph is an immutable representation of the DAG, including the weight annotations.
 *
 */
public final class TaskGraphOld {
	private final List<TaskOld> topologicalOrder;
	private final Collection<TaskOld> roots;
	private final Collection<DependencyOld> edges;
	private final String name;
	private final int[] bottomTime;
	private final TaskOld[] taskMap;
	private final int totalTime;
	
	TaskGraphOld(String name, Collection<TaskOld> tasks) {
		assert tasks.stream().noneMatch(Objects::isNull);
		
		topologicalOrder = new ArrayList<>();
		roots = new ArrayList<>();
		this.name = name;
		this.taskMap = new TaskOld[tasks.size()];
		
		int sum = 0;
		for(TaskOld task : tasks) {
			addTask(task);
			if(task.getParents().isEmpty()) {
				roots.add(task);
			}
			
			sum += task.getCost();
		}
		
		totalTime = sum;
		edges = tasks.stream()
				.flatMap(t -> t.getChildren().stream())
				.collect(Collectors.toList());
		
		bottomTime = new int[topologicalOrder.size()];
		
		//Iterate backwards calculating the bottom times
		HashMap<TaskOld, Integer> bottomMap = new HashMap<>(); //Temporary bottom time map
		for(int i = topologicalOrder.size() - 1; i >= 0; i--) {
			TaskOld task = topologicalOrder.get(i);
			int taskBottomTime = task.getCost();
			
			for(DependencyOld dep : task.getChildren()) {
				TaskOld child = dep.getTarget();
				taskBottomTime = Math.max(taskBottomTime, bottomMap.get(child) + task.getCost());
			}
			
			bottomMap.put(task, taskBottomTime);
		}
		
		buildIDs(bottomMap);
		
		for(TaskOld task : topologicalOrder) {
			bottomTime[task.getId()] = bottomMap.get(task);
		}
	}
	
	/** Tasks with smaller bottom times should have lower Ids */
	private void buildIDs(Map<TaskOld, Integer> bottomTimeMap) {
		int id = 0;
		
		HashMap<TaskOld, Integer> numberOfParentsLeft = new HashMap<>();
		
		for(TaskOld task : topologicalOrder) {
			numberOfParentsLeft.put(task, task.getParents().size());
		}
		
		 //Only when the parent has an ID can we assign an ID to the child.
		PriorityQueue<TaskOld> freeTasks = new PriorityQueue<>((a, b) -> bottomTimeMap.get(b) - bottomTimeMap.get(a));
		freeTasks.addAll(roots);
		
		while(!freeTasks.isEmpty()) {
			TaskOld task = freeTasks.remove();
			
			for(DependencyOld child : task.getChildren()) {
				if(numberOfParentsLeft.compute(child.getTarget(), (t, i) -> i - 1) == 0) {
					//No parents left
					freeTasks.add(child.getTarget());
				}
			}
			
			task.setId(id);
			taskMap[id] = task;
			
			id++;
		}
	}

	private void addTask(TaskOld parent) {
		assert parent != null;
		
		for(DependencyOld dep : parent.getParents()) {
			TaskOld task = dep.getSource();
			if(!topologicalOrder.contains(task)) {
				addTask(task);
			}
		}
		
		if(!topologicalOrder.contains(parent)) {
			topologicalOrder.add(parent);
		}
	}
	
	/** Gets the task with the given id. */
	public TaskOld getTask(int id) {
		return taskMap[id];
	}
	
	/**
	 * Returns a List containing a topological ordering of all tasks.
	 */
	public List<TaskOld> getAll() {
		return topologicalOrder;
	}
	
	/**
	 * Returns a Collection of all tasks with no dependents.
	 */
	public Collection<TaskOld> getRoots() {
		return roots;
	}

	public Collection<DependencyOld> getEdges() {
		return edges;
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets the time from the start of this task to the end of the farthest descendant
	 */
	public int getBottomTime(TaskOld task) {
		return bottomTime[task.getId()];
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this)
			return true;
		
		if(!(other instanceof TaskGraphOld)) {
			return false;
		}
		
		TaskGraphOld otherGraph = (TaskGraphOld) other;
		
		//Compare two graphs by comparing the tasks
		return name.equals(otherGraph.name) && GraphEqualityUtils.setsEqualIgnoringParents(roots, otherGraph.roots);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, topologicalOrder.size(), edges.size());
	}

	public int getTotalTaskTime() {
		return totalTime;
	}
}
