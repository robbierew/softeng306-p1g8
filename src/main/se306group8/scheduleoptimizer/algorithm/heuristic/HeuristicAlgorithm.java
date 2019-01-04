package se306group8.scheduleoptimizer.algorithm.heuristic;

public interface HeuristicAlgorithm {
	public Heuristic computeHeuristic(HeuristicSchedule schedule);
	public Heuristic computeHeuristic(HeuristicSchedule schedule, Object metadata);
	
}
