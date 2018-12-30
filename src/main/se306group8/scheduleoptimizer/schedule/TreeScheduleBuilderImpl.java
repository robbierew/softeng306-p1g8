package se306group8.scheduleoptimizer.schedule;

import se306group8.scheduleoptimizer.schedule.taskallocation.AllocationHistory;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class TreeScheduleBuilderImpl implements TreeScheduleBuilder<TreeSchedule>{

	@Override
	public TreeSchedule buildRootSchedule(ProblemStatement pS) {
		return new TreeSchedule(pS);
	}

	@Override
	public TreeSchedule buildChildSchedule(TreeSchedule parent, Task nextTask, int processor) {
		return new TreeSchedule(parent,nextTask,processor);
	}

	@Override
	public TreeSchedule buildFromHistory(AllocationHistory history) {
		return new TreeSchedule(history);
	}

}
