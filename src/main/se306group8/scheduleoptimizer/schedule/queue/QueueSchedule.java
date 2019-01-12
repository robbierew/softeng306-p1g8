package se306group8.scheduleoptimizer.schedule.queue;

import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicAlgorithm;
import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicSchedule;
import se306group8.scheduleoptimizer.schedule.taskallocation.AllocationHistory;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class QueueSchedule extends HeuristicSchedule{

	//A store entry is a number inside the queue for fast lookup
	private int storeEntry = HeuristicPriorityQueue.NO_ENTRY;
	private DeltaScheduleStore<?> queue;
	private QueueSchedule parent;
	
	public QueueSchedule(AllocationHistory history, HeuristicAlgorithm algorithm) {
		super(history, algorithm);
	}
	
	public QueueSchedule(QueueSchedule parent,Task nextTask, int processor) {
		super(parent,nextTask,processor);
		this.parent = parent;
	}
	
	public QueueSchedule(ProblemStatement statement,HeuristicAlgorithm algorithm) {
		super(statement,algorithm);
	}
	
	@Override
	public QueueSchedule getParent() {
		if (parent == null && !isEmpty()) {
			parent = new QueueSchedule(getAllocationHistory().previous(),getHeuristicAlgorithm());
		}
		return parent;
	}
	
	int getStoreEntry(DeltaScheduleStore<?> queue) {
		if (this.queue == queue) {
			return storeEntry;
		}else {
			return HeuristicPriorityQueue.NO_ENTRY;
		}
	}
	
	void setStoreEntry(int id, DeltaScheduleStore<?> queue) {
		storeEntry = id;
		this.queue = queue;
	}

}
