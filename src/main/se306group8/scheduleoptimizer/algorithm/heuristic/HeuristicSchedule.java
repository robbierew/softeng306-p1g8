package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.schedule.TreeSchedule;
import se306group8.scheduleoptimizer.schedule.taskallocation.AllocationHistory;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class HeuristicSchedule extends TreeSchedule{

	Heuristic heuristic = null;
	HeuristicAlgorithm algorithm;
	
	//Lazy loaded
	HeuristicSchedule parent;
	
	public HeuristicSchedule(ProblemStatement statement, HeuristicAlgorithm algorithm) {
		super(statement);
		applyHeuristic(algorithm);
	}

	public HeuristicSchedule(HeuristicSchedule parent,Task nextTask, int processor) {
		super(parent,nextTask,processor);
		applyHeuristic(parent.getHeuristicAlgorithm());
		this.parent = parent;
	}
	
	public HeuristicSchedule(AllocationHistory history,HeuristicAlgorithm algorithm) {
		super(history);
		applyHeuristic(algorithm);
	}
	
	public int getHeuristicValue() {
		return heuristic.getHeuristicValue();
	}
	
	public Heuristic getHeuristic() {
		return heuristic;
	}
	
	public HeuristicAlgorithm getHeuristicAlgorithm() {
		return algorithm;
	}
	
	@Override
	//This override makes the return value a HeuristicSchedule
	public HeuristicSchedule getParent() {
		if (parent == null && !isEmpty()) {
			TreeSchedule superParent = super.getParent();
			if (superParent instanceof HeuristicSchedule) {
				parent = (HeuristicSchedule)superParent;
			}else {
				parent = new HeuristicSchedule(superParent.getAllocationHistory(), getHeuristicAlgorithm());
			}
		}		
		return parent;
	}
	
	private void applyHeuristic(HeuristicAlgorithm algorithm) {
		this.algorithm = algorithm;
		heuristic = algorithm.computeHeuristic(this);
	}
}
