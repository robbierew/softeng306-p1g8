package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.schedule.TreeSchedule;
import se306group8.scheduleoptimizer.schedule.taskallocation.AllocationHistory;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class HeuristicSchedule extends TreeSchedule {

	
	HeuristicAlgorithm algorithm;
	Object metadata = null;

	// Lazy loaded
	HeuristicSchedule parent;
	Heuristic heuristic = null;

	public HeuristicSchedule(ProblemStatement statement, HeuristicAlgorithm algorithm) {
		super(statement);
		this.algorithm = algorithm;
	}

	public HeuristicSchedule(HeuristicSchedule parent, Task nextTask, int processor) {
		super(parent, nextTask, processor);
		this.metadata =  parent.getHeuristic().getMetadata();
		this.parent = parent;
		this.algorithm = parent.getHeuristicAlgorithm();
	}

	public HeuristicSchedule(AllocationHistory history, HeuristicAlgorithm algorithm) {
		super(history);
		this.algorithm = algorithm;
	}

	public int getHeuristicValue() {
		return getHeuristic().getHeuristicValue();
	}

	public Heuristic getHeuristic() {
		if (heuristic == null) {
			if (metadata == null) {
				applyHeuristic(algorithm);
			}else {
				applyHeuristic(algorithm,metadata);
			}
		}
		return heuristic;
	}

	public HeuristicAlgorithm getHeuristicAlgorithm() {
		return algorithm;
	}

	@Override
	// This override makes the return value a HeuristicSchedule
	public HeuristicSchedule getParent() {
		if (parent == null && !isEmpty()) {
			TreeSchedule superParent = super.getParent();
			if (superParent instanceof HeuristicSchedule) {
				parent = (HeuristicSchedule) superParent;
			} else {
				parent = new HeuristicSchedule(superParent.getAllocationHistory(), getHeuristicAlgorithm());
			}
		}
		return parent;
	}

	private void applyHeuristic(HeuristicAlgorithm algorithm) {
		this.algorithm = algorithm;
		heuristic = algorithm.computeHeuristic(this);
	}
	
	private void applyHeuristic(HeuristicAlgorithm algorithm, Object metadata) {
		this.algorithm = algorithm;
		heuristic = algorithm.computeHeuristic(this,metadata);
	}
}
