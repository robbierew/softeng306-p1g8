package se306group8.scheduleoptimizer.algorithm.pruner;

import java.util.ArrayList;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicComparator;
import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicSchedule;


public class HeuristicSortPruner<T extends HeuristicSchedule> implements Pruner<T>{

	
	private int upperbound;
	private HeuristicComparator comparator = new HeuristicComparator();
	
	public HeuristicSortPruner(int initalUpperbound) {
		upperbound = initalUpperbound;
	}
	
	public HeuristicSortPruner() {
		this(Integer.MAX_VALUE);
	}
	
	public void setUpperbound(int upperbound) {
		this.upperbound = upperbound;
	}
	
	public List<T> prune(List<T> schedules) {
		schedules.sort(comparator);
		List<T> keptSchedules = new ArrayList<T>();
		for (T s:schedules) {
			
			if (s.getHeuristicValue() < upperbound) {
				keptSchedules.add(s);
			} else {
				break;//since list is sorted no more are valid
			}
		}
		return keptSchedules;
	}

}
