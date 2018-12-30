package se306group8.scheduleoptimizer.algorithm.greedy;

import java.util.List;

import se306group8.scheduleoptimizer.schedule.TreeSchedule;
import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class GreedyScheduleBuilder<T extends TreeSchedule> {

	TreeScheduleBuilder<T> builder;

	public GreedyScheduleBuilder(TreeScheduleBuilder<T> builder) {
		this.builder = builder;
	}

	public T makeGreedyFromStatement(ProblemStatement pS) {
		return makeGreedyFromSchedule(builder.buildRootSchedule(pS));
	}

	public T makeGreedyFromSchedule(T baseSchedule) {
		int allocatedMask = baseSchedule.getAllocatedMask();
		ProblemStatement pS = baseSchedule.getProblemStatement();
		int processors = pS.getNumProcessors();
		List<Task> order = pS.getTasksInParitalOrder();

		T next = baseSchedule;

		// we don't have to worry about what is allocatable is we use the parital order
		for (Task task : order) {

			// not allocated
			if ((task.getMask() & allocatedMask) == 0) {

				// by using max value the next schedule made will be assigned to bestSoFar
				int bestRuntime = Integer.MAX_VALUE;
				T bestSoFar = null;

				for (int processor = 1; processor <= processors; processor++) {
					T candinate = builder.buildChildSchedule(next, task, processor);
					if (bestRuntime > candinate.getRuntime()) {
						bestSoFar = candinate;
						bestRuntime = candinate.getRuntime();
					}
				}

				next = bestSoFar;
			}
		}

		return next;
	}

}
