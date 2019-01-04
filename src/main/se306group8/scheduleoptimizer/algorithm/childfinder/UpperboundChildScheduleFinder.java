package se306group8.scheduleoptimizer.algorithm.childfinder;

import java.util.ArrayList;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicComparator;
import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicSchedule;


public class UpperboundChildScheduleFinder<T extends HeuristicSchedule> implements ChildScheduleFinder<T>{

	
	private ChildScheduleFinder<T> finder;
	private int upperbound;
	private HeuristicComparator comparator = new HeuristicComparator();
	
	public UpperboundChildScheduleFinder(ChildScheduleFinder<T> finder,int initalUpperbound) {
		upperbound = initalUpperbound;
		this.finder = finder;
	}
	
	public UpperboundChildScheduleFinder(ChildScheduleFinder<T> finder) {
		this(finder,Integer.MAX_VALUE);
	}
	
	public void setUpperbound(int upperbound) {
		this.upperbound = upperbound;
	}
	
	@Override
	public List<T> getChildSchedules(T parent) {
		List<T> children = finder.getChildSchedules(parent);
		children.sort(comparator);
		List<T> prunedChildren = new ArrayList<T>();
		for (T child:children) {
			
			if (child.getHeuristicValue() < upperbound) {
				prunedChildren.add(child);
			} else {
				break;//since list is sorted no more are valid
			}
		}
		return prunedChildren;
	}

}
