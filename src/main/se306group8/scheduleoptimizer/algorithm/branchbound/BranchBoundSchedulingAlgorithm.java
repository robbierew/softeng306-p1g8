package se306group8.scheduleoptimizer.algorithm.branchbound;

import java.util.List;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class BranchBoundSchedulingAlgorithm implements Algorithm {
	private final ChildScheduleFinder finder;
	private final MinimumHeuristic heuristic;
	
	private RuntimeMonitor monitor;
	private int children = 0;
	
	public BranchBoundSchedulingAlgorithm(ChildScheduleFinder finder, MinimumHeuristic heuristic) {
		this.finder = finder;
		this.heuristic = heuristic;
	}

	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		
		TreeSchedule emptySchedule = new TreeSchedule(graph, heuristic);
		
		if(monitor != null) {
			monitor.start();
		}
		
		// Kick off BnB (current 'best schedule' is null)
		Schedule schedule =  branchAndBound(emptySchedule, null, numberOfProcessors);
		
		if(monitor != null) {
			monitor.finish(schedule);
		}
		
		return schedule;
	}

	private Schedule branchAndBound(TreeSchedule schedule, Schedule best, int numberOfProcessors) {
		children++;
		
		if(monitor != null) {
			monitor.setSolutionsExplored(children);
		}
		
		// Get all children in order from best lower bound to worst
		// TODO add processor number to GCSF
		List<TreeSchedule> childSchedules = finder.getChildSchedules(schedule);
		
		for (TreeSchedule child : childSchedules) {
			
			// Only consider the child if its lower bound is better than current best
			if (best == null || child.getLowerBound() < best.getTotalRuntime()) {
				if (child.isComplete()) {
					best = child.getFullSchedule();
				} else {
					// Check if the child schedule is complete or not
					best = branchAndBound(child, best, numberOfProcessors);
				}
			}
			
		}
		
		return best;
	}
	
	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		this.monitor = monitor;
	}
}
