package se306group8.scheduleoptimizer.algorithm.childfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import se306group8.scheduleoptimizer.schedule.TreeSchedule;
import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

/**
 * Simple implementation of ChildScheduleFinder, this implementation does not do
 * anything smart to reduce the number of equivalent schedules.
 */
public class BasicChildScheduleFinder<T extends TreeSchedule> implements ChildScheduleFinder<T> {

	private TreeScheduleBuilder<T> builder;

	public BasicChildScheduleFinder(TreeScheduleBuilder<T> builder) {
		this.builder = builder;
	}

	@Override
	public List<T> getChildSchedules(T parent) {
		ProblemStatement pS = parent.getProblemStatement();
		int numProcessors = pS.getNumProcessors();
		Collection<Task> allocatable = parent.getAllocatableTasks();
		List<T> children = new ArrayList<T>();
		for (Task task : allocatable) {
			for (int p = 1; p <= numProcessors; p++) {
				children.add(builder.buildChildSchedule(parent, task, p));
			}
		}
		return children;
	}
}
