package se306group8.scheduleoptimizer.algorithm.heuristic;

import java.util.List;

import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class DataReadyTimeHeuristic implements HeuristicAlgorithm{
	
	private class DataReadyTimeMetaData{
		int[] tdr;
		
	}
	
	
	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule) {
		DataReadyTimeMetaData mData = new DataReadyTimeMetaData();
		mData.tdr = new int[schedule.getProblemStatement().getNumTasks()];
		List<Task> allocatable = schedule.getAllocatedTasks(); 
		
	}

	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule, Object metadata) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void updatetdr(List<Task> tasks, int[] tdr, int processors) {
		for (Task t: tasks) {
			int minDrt = Integer.MAX_VALUE;
			for (int p=1;p<=processors;p++) {
				minDrt = Math.min(minDrt, b)
			}
		}
	}

}
