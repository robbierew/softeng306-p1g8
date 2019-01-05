package se306group8.scheduleoptimizer.algorithm.heuristic;

import java.util.Arrays;
import java.util.List;

import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

//To reduce computation duplication I added a bottom level class to be used for 
public class BottomLevel {

	static int[] bottomLevels;
	static TaskGraph cacheGraph;
	
	public static int[] calcBottomLevel(TaskGraph graph) {
		
		if (cacheGraph == null || !cacheGraph.equals(graph)) {
			cacheGraph = graph;
			bottomLevels = new int[graph.getNumTasks()];
			List<Task> tasks = graph.getTasksInParitalOrder();
			
			//iterate in reverse partial order this makes it easy to calc
			//every child task will have their bottomlevel's calculated first
			for (int taskNum = graph.getNumTasks() -1;taskNum >= 0;taskNum--) {
				calcBottomTime(tasks.get(taskNum));
			}
		}
		
		return Arrays.copyOf(bottomLevels, bottomLevels.length);
	}

	private static void calcBottomTime(Task t) {
		int highestChildBl = 0;
		for (Task child:t.getChildTasks()) {
			if (bottomLevels[child.getID()] > highestChildBl) {
				highestChildBl = bottomLevels[child.getID()];
			}
		}
		bottomLevels[t.getID()] = t.getComputeCost() + highestChildBl;
	}
	
}
