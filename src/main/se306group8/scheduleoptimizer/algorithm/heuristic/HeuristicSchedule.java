package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.schedule.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class HeuristicSchedule extends TreeSchedule{

	public HeuristicSchedule(ProblemStatement statement) {
		super(statement);
	}

	public HeuristicSchedule(HeuristicSchedule parent,Task nextTask, int processor) {
		super(parent,nextTask,processor);
	}
	
	public int getHeuristic() {
		return 0;//TODO
	}
}
