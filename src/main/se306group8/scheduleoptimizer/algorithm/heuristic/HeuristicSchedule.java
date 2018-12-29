package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.schedule.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class HeuristicSchedule extends TreeSchedule{

	Heuristic heuristic = null;
	HeuristicAlgorithm algorithm;
	public HeuristicSchedule(ProblemStatement statement, HeuristicAlgorithm algorithm) {
		super(statement);
		this.algorithm = algorithm;
		heuristic = algorithm.computeHeuristic(this);
	}

	public HeuristicSchedule(HeuristicSchedule parent,Task nextTask, int processor) {
		super(parent,nextTask,processor);
		heuristic = parent.getHeuristicAlgorithm().computeHeuristic(this);
	}
	
	public int getHeuristicValue() {
		return heuristic.getHeuristicValue();
	}
	
	public Heuristic getHeuristic() {
		return heuristic;
	}
	
	public HeuristicAlgorithm getHeuristicAlgorithm() {
		return algorithm;
	}
	
	
}
