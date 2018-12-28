package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
/**
 * 
 * @brief Create an object representation of a task graph.
 *
 */
public class TaskGraphBuilder {
		
	private int nextID;
	private Map<SimpleEntry<String, String>, Integer> remoteCostMap;
	private Map<String, List<String>> parentMap;
	private Map<String, List<String>> childMap;
	private Set<TaskImpl> taskSet;
	private String name;
	
	public TaskGraphBuilder(){
		taskSet = new HashSet<TaskImpl>();
		remoteCostMap = new HashMap<SimpleEntry<String,String>, Integer>();
		parentMap = new HashMap<String, List<String>>();
		childMap = new HashMap<String, List<String>>();
	}
	
	public TaskGraphBuilder addTask(String name, int cost){
		TaskImpl t = new TaskImpl();
		t.setCost(cost);
		t.setName(name);
		
		parentMap.put(name, new ArrayList<String>());
		childMap.put(name, new ArrayList<String>());
		
		taskSet.add(t);
		return this;
		
	}
	
	public TaskGraphBuilder addDependecy(String sourceTaskName, String targetTaskName, int cost){
		remoteCostMap.put(new SimpleEntry<String, String>(sourceTaskName,targetTaskName), cost);
		
		parentMap.get(targetTaskName).add(sourceTaskName);
		childMap.get(sourceTaskName).add(targetTaskName);
		
		return this;
	}
	
	public TaskGraph buildGraph(){
		
		//clone the tasks so the builder can be used multiple times
		Collection<TaskImpl> clonedTasks = cloneTasks(taskSet);
		Map<String, TaskImpl> nameMap = createNameMap(clonedTasks);
		linkDependencies(clonedTasks,nameMap);
		
		//creates a partial order with the IDs
		nextID = 0;
		setIDs(clonedTasks);
		computeRelationMasks(clonedTasks);
		List<Task> ordered = getOrder(clonedTasks);
		int[][] remoteCostMatrix = createRemoteCostMatrix(clonedTasks);
		
		TaskGraph taskGraph = new TaskGraphImpl(name,ordered,remoteCostMatrix);
		
		return taskGraph;
		
	}

	

	
	private List<Task> getOrder(Collection<TaskImpl> clonedTasks) {
		List<Task> order = new ArrayList<Task>(Collections.nCopies(clonedTasks.size(), null));
		for (Task t:clonedTasks) {
			order.set(t.getID(), t);
		}
		return order;
	}

	//assumes IDs have been set up
	private int[][] createRemoteCostMatrix(Collection<TaskImpl> clonedTasks) {
		int size = clonedTasks.size();
		int[][] matrix = new int[size][size];
		
		for (TaskImpl parent: clonedTasks) {
			for (Task child: parent.getChildTasks()) {
				SimpleEntry<String, String> tuple = new SimpleEntry<String, String>(parent.getName(),child.getName());
				matrix[parent.getID()][child.getID()] = remoteCostMap.get(tuple);
			}
		}
		
		return matrix;
	}

	public void setGraphName(String name) {
		this.name = name;
	}

	
	
	//we want to be able to use the TaskGraphBuilder multiple times best to make easy clones
	private TaskImpl cloneTask(TaskImpl old) {
			TaskImpl clone = new TaskImpl();
			clone.setCost(old.getComputeCost());
			clone.setName(old.getName());
			return clone;		
	}
	
	private Collection<TaskImpl> cloneTasks(Collection<TaskImpl> oldTasks){
		Set<TaskImpl> cloneTasks = new HashSet<TaskImpl>();
		for (TaskImpl t: oldTasks) {
			cloneTasks.add(cloneTask(t));
		}
		return cloneTasks;
	}
	
	private void linkDependencies(Collection<TaskImpl> clonedTasks, Map<String, TaskImpl> nameMap) {
		
		for (TaskImpl t:clonedTasks) {
			Collection<TaskImpl> parentTasks = getTasksFromNameList(parentMap.get(t.getName()), nameMap);
			Collection<TaskImpl> childTasks = getTasksFromNameList(childMap.get(t.getName()), nameMap);
			
			t.setParentTasks(parentTasks);
			t.setChildTasks(childTasks);
		}
		
	}
	
	//this methods assumes the tasks are not linked ie linkDependencies is called
	private void setIDs(Collection<? extends Task> clonedTasks) {
		for (Task t: clonedTasks) {
			setID(t);		
		}
	}
	
	private void setID(Task task) {
		
		//tasks with no ID set will have mask of 0
		if (task.getMask() == 0) {
			
			//parents have to be allocated beforehand
			setIDs(task.getParentTasks());
			
			//once parents have an ID this task can have an ID
			((TaskImpl)task).setID(nextID++);
		}
	}
	
	//can only be called after IDs have been set
	private void computeRelationMasks(Collection<TaskImpl> tasks) {
		for (TaskImpl t: tasks) {
			t.computeRelationMasks();
		}
	}
	
	private Collection<TaskImpl> getTasksFromNameList(Collection<String> names, Map<String, TaskImpl> nameMap){
		Collection<TaskImpl> tasks = new HashSet<TaskImpl>();
		for (String name:names) {
			tasks.add(nameMap.get(name));
		}
		return tasks;
	}
	
	private Map<String, TaskImpl> createNameMap(Collection<TaskImpl> tasks){
		Map<String, TaskImpl> nameMap = new HashMap<String,TaskImpl>();
		
		for (TaskImpl t: tasks) {
			nameMap.put(t.getName(), t);
		}
		return nameMap;
	}
}
