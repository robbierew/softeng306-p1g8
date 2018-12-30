package se306group8.scheduleoptimizer.schedule;

import se306group8.scheduleoptimizer.schedule.taskallocation.AllocationHistory;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

//Since there will be multiple tree schedules types it is best that
//whatever type a parent is the type of the child so for that I have made
//a builder to be used to keep the type
public interface TreeScheduleBuilder<T extends TreeSchedule> {
	public T buildRootSchedule(ProblemStatement pS);
	
	public T buildChildSchedule(T parent,Task nextTask, int processor);
	
	public T buildFromHistory(AllocationHistory history);
}
