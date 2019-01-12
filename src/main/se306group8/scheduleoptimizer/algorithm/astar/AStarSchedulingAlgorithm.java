package se306group8.scheduleoptimizer.algorithm.astar;

import se306group8.scheduleoptimizer.algorithm.childfinder.OptimalSearchChildFinder;
import se306group8.scheduleoptimizer.algorithm.greedy.GreedyScheduleBuilder;
import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.schedule.queue.HeuristicPriorityQueue;
import se306group8.scheduleoptimizer.schedule.queue.QueueSchedule;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;

public class AStarSchedulingAlgorithm<T extends QueueSchedule> {
	
	
	private TreeScheduleBuilder<T> builder;
	private OptimalSearchChildFinder<T> finder;
	private GreedyScheduleBuilder<T> greedyBuilder;

	public AStarSchedulingAlgorithm(TreeScheduleBuilder<T> builder) {
		this.builder = builder;
		greedyBuilder = new GreedyScheduleBuilder<T>(builder);
	}
	
	public T findOptimalSchedule(T schedule) {
		T greedySoln = greedyBuilder.makeGreedyFromSchedule(schedule);
		this.finder = new OptimalSearchChildFinder<T>(greedySoln,builder);
		HeuristicPriorityQueue<T> queue = new HeuristicPriorityQueue<T>(builder,schedule.getProblemStatement());
		queue.put(schedule);
		queue.put(greedySoln);
		
		T best = queue.pop();
		while (!best.isComplete()) {
			queue.putAll(finder.getChildSchedules(best));
			best = queue.pop();
		}
		return best;
	}
	
	public T findOptimalSchedule(ProblemStatement pS) {
		return findOptimalSchedule(builder.buildRootSchedule(pS));
	}
}
