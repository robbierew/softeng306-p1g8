package se306group8.scheduleoptimizer.algorithm.heuristic;

import java.util.Collection;

import se306group8.scheduleoptimizer.algorithm.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.DependencyOld;
import se306group8.scheduleoptimizer.taskgraph.TaskOld;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;

public class DataReadyTimeHeuristic implements MinimumHeuristic{

	private final int processors;
	
	public DataReadyTimeHeuristic(int processors) {
		this.processors=processors;
	}
	
	@Override
	public int estimate(TreeSchedule schedule) {
		Collection<TaskOld> free = schedule.getAllocatable();
		TaskGraphOld tg = schedule.getGraph();
		
		//formula from "Reducing the solution space of optimal task scheduling" page 6
		int max = 0;
		
		for (TaskOld n:free) {
			max=Math.max(max, tdr(n,schedule)+tg.getBottomTime(n));
		}
		
		return max;
		
	}
	
	//formula from "Reducing the solution space of optimal task scheduling" page 3
	private int tdr(TaskOld nj, int p, TreeSchedule schedule) {
		int max = 0;
		for (DependencyOld dep:nj.getParents()) {
			ProcessorAllocation pa = schedule.getAllocationFor(dep.getSource());
			if (pa.processor==p) {
				max = Math.max(max, pa.endTime);
			}else{
				max = Math.max(max, pa.endTime + dep.getCommunicationCost());
			}
		}
		return max;
	}
	
	//formula from "Reducing the solution space of optimal task scheduling" page 5
	private int tdr(TaskOld n, TreeSchedule schedule) {
		int min = Integer.MAX_VALUE;
		for (int p=1;p<=processors;p++) {
			min=Math.min(min, tdr(n,p,schedule));
		}
		return min;
	}

}
