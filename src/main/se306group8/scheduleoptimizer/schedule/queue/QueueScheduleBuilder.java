package se306group8.scheduleoptimizer.schedule.queue;

import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicAlgorithm;
import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.schedule.taskallocation.AllocationHistory;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class QueueScheduleBuilder implements TreeScheduleBuilder<QueueSchedule>{

	
	HeuristicAlgorithm algorithm;

	public QueueScheduleBuilder(HeuristicAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	@Override
	public QueueSchedule buildRootSchedule(ProblemStatement pS) {
		return new QueueSchedule(pS,algorithm);
	}

	@Override
	public QueueSchedule buildChildSchedule(QueueSchedule parent, Task nextTask, int processor) {
		return new QueueSchedule(parent,nextTask,processor);
	}

	@Override
	public QueueSchedule buildFromHistory(AllocationHistory history) {
		return new QueueSchedule(history,algorithm);
	}

}
