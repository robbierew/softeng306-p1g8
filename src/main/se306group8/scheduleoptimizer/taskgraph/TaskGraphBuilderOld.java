package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @brief Create an object representation of a task graph.
 *
 */
public class TaskGraphBuilderOld {
	private Map<String,TaskOld> taskMap;
	private Map<String, Collection<DependencyOld>> parentMap;
	private Map<String, Collection<DependencyOld>> childMap;
	private String name;
	
	public TaskGraphBuilderOld(){
		taskMap = new HashMap<String,TaskOld>();
		parentMap = new HashMap<String, Collection<DependencyOld>>();
		childMap = new HashMap<String, Collection<DependencyOld>>();
	}
	
	public TaskGraphBuilderOld addTask(String name, int cost){
		taskMap.put(name, new TaskOld(name,cost));
		parentMap.put(name, new ArrayList<DependencyOld>());
		childMap.put(name, new ArrayList<DependencyOld>());
		return this;
		
	}
	
	public TaskGraphBuilderOld addDependecy(String sourceTaskName, String targetTaskName, int cost){
		DependencyOld dep = new DependencyOld(taskMap.get(sourceTaskName),taskMap.get(targetTaskName), cost);
		parentMap.get(targetTaskName).add(dep);
		childMap.get(sourceTaskName).add(dep);
		return this;
	}
	
	public TaskGraphOld buildGraph(){
		Set<String> taskNames = taskMap.keySet();
		for (String task:taskNames){
			TaskOld t = taskMap.get(task);
			t.setChildDependencies(childMap.get(task));
			t.setParentDependencies(parentMap.get(task));
		}
		return new TaskGraphOld(name, taskMap.values());
		
	}

	public void setName(String name) {
		this.name = name;
	}

}
