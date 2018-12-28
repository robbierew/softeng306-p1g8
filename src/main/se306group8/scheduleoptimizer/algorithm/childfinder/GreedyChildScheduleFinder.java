package se306group8.scheduleoptimizer.algorithm.childfinder;

import java.util.ArrayList;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.TaskOld;
//import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * Implementation of ChildScheduleFinder that orders the child schedules based
 * on the Task's bottom level. For children of the same Task they are ordered by
 * starting time of the new Task.
 */
public class GreedyChildScheduleFinder implements ChildScheduleFinder {

	private int numProcessors;

	public GreedyChildScheduleFinder(int numProcessors) {
		this.numProcessors = numProcessors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TreeSchedule> getChildSchedules(TreeSchedule schedule) {
		List<TaskOld> allocatable = new ArrayList<>(schedule.getAllocatable());

		// Robert's first Java lambda
		// ordered such that largest bottom order first
		allocatable.sort(
				(TaskOld t1, TaskOld t2) -> schedule.getGraph().getBottomTime(t2) - schedule.getGraph().getBottomTime(t1));

		List<TreeSchedule> children = new ArrayList<TreeSchedule>();

		// use the schedule to get timing info
		// I didn't use getFullSchedule() because comments says it may return null
		// Schedule parentSchedule = new ListSchedule(schedule.getGraph(),
		// schedule.computeTaskLists());
		for (TaskOld task : allocatable) {
			children.addAll(greedyChildren(schedule, task));
		}

		return children;

	}

	private List<TreeSchedule> greedyChildren(TreeSchedule parentSchedule, TaskOld task) {
		
		//A small optimization to reduce equivalent schedules by only allowing 1 free processor
		int processorsToAllocate = Math.min(parentSchedule.getNumberOfUsedProcessors() + 1, numProcessors);

		List<TreeSchedule> childrenSchedules = new ArrayList<>();

		for (int p = 1; p <= processorsToAllocate; p++) {
			TreeSchedule childSchedule = new TreeSchedule(task, p, parentSchedule);
			childrenSchedules.add(childSchedule);
		}

		// sort by start times
		childrenSchedules.sort((t1, t2) -> t1.getAllocationFor(task).startTime - t2.getAllocationFor(task).startTime);

		return childrenSchedules;
	}
}
