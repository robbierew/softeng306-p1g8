package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.schedule.taskallocation.AllocationHistory;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class HeuristicScheduleBuilder implements TreeScheduleBuilder<HeuristicSchedule>{

	HeuristicAlgorithm algorithm;
	public HeuristicScheduleBuilder(HeuristicAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	@Override
	public HeuristicSchedule buildRootSchedule(ProblemStatement pS) {
		return new HeuristicSchedule(pS, algorithm);
	}

	@Override
	public HeuristicSchedule buildChildSchedule(HeuristicSchedule parent, Task nextTask, int processor) {
		return new HeuristicSchedule(parent,nextTask,processor);
	}

	@Override
	public HeuristicSchedule buildFromHistory(AllocationHistory history) {
		return new HeuristicSchedule(history,algorithm);
	}
	
}
