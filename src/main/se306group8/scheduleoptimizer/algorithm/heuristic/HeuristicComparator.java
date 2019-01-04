package se306group8.scheduleoptimizer.algorithm.heuristic;

import java.util.Comparator;

public class HeuristicComparator implements Comparator<HeuristicSchedule>{

	@Override
	public int compare(HeuristicSchedule o1, HeuristicSchedule o2) {
		return o1.getHeuristicValue() - o2.getHeuristicValue();
	}

}
