package se306group8.scheduleoptimizer.algorithm.branchbound;

import java.util.List;

import se306group8.scheduleoptimizer.algorithm.childfinder.OptimalSearchChildFinder;
import se306group8.scheduleoptimizer.algorithm.greedy.GreedyScheduleBuilder;
import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicSchedule;
import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;

public class BnBSchedulingAlgorithm<T extends HeuristicSchedule> {
	private OptimalSearchChildFinder<T> finder;
	private TreeScheduleBuilder<T> builder;
	private GreedyScheduleBuilder<T> greedyBuilder;
	public BnBSchedulingAlgorithm(TreeScheduleBuilder<T> builder) {
		this.builder = builder;
		greedyBuilder = new GreedyScheduleBuilder<T>(builder);
	}
	
	public T findOptimalSchedule(T schedule) {
		T greedySoln = greedyBuilder.makeGreedyFromSchedule(schedule);
		this.finder = new OptimalSearchChildFinder<T>(greedySoln,builder);
		return search(schedule);
	}
	
	public T findOptimalSchedule(ProblemStatement pS) {
		return findOptimalSchedule(builder.buildRootSchedule(pS));
	}
	
	private T search(T schedule) {
		List<T> children = finder.getChildSchedules(schedule);
		for(T child:children) {
			search(child);
		}
		return finder.getBestCompleteScheduleFound();
	}
}
