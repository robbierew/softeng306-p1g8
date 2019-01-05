package se306group8.scheduleoptimizer.algorithm.childfinder;

import java.util.ArrayList;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicSchedule;
import se306group8.scheduleoptimizer.algorithm.pruner.DuplicatePruner;
import se306group8.scheduleoptimizer.algorithm.pruner.HeuristicSortPruner;
import se306group8.scheduleoptimizer.algorithm.pruner.IdenticalTaskOrderingPruner;
import se306group8.scheduleoptimizer.algorithm.pruner.NormalPruner;
import se306group8.scheduleoptimizer.schedule.Schedule;
import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;


//This is the class one should use for finding the optimal it combines all the pruning
//features into one useful package
public class OptimalSearchChildFinder<T extends HeuristicSchedule> implements ChildScheduleFinder<T>{

	private T bestSoFar;
	private ChildScheduleFinder<T> finder;
	private HeuristicSortPruner<T> heuristicPruner;
	private NormalPruner<T> normalPruner;
	private IdenticalTaskOrderingPruner<T> taskOrderingPruner;
	private DuplicatePruner<T> dupePruner;
	
	public OptimalSearchChildFinder(T upperBound, TreeScheduleBuilder<T> builder) {
		this(upperBound.getRuntime(),upperBound.getProblemStatement(), builder);		
		if (!upperBound.isComplete()) {
			throw new RuntimeException("Upperbound must be from complete schedule");
		}
		bestSoFar = upperBound;
		
		
	}
	
	public OptimalSearchChildFinder(TaskGraph graph,TreeScheduleBuilder<T> builder) {
		this(Integer.MAX_VALUE,graph, builder);
	}
	
	private OptimalSearchChildFinder(int upperBound,TaskGraph graph, TreeScheduleBuilder<T> builder) {
		finder = new BasicChildScheduleFinder<T>(builder);
		heuristicPruner = new HeuristicSortPruner<T>(upperBound);
		normalPruner = new NormalPruner<T>();
		taskOrderingPruner = new IdenticalTaskOrderingPruner<T>(graph);
		dupePruner = new DuplicatePruner<T>();
	}
	
	public T getBestCompleteScheduleFound() {
		return bestSoFar;
	}
	
	@Override
	public List<T> getChildSchedules(T parent) {
		List<T> children = finder.getChildSchedules(parent);
		children = normalPruner.prune(children);
		children = dupePruner.prune(children);
		children = taskOrderingPruner.prune(children);
		
		//we do heuristicPruner last because it requires computing heuristics so it is best
		//to reduce the number of schedules to compute first
		children = heuristicPruner.prune(children);
		
		//possible to get no children
		if (children.size() > 0) {
			//heuristicPruner returns sorted
			T best = children.get(0);
			if (best.isComplete() && (bestSoFar == null || bestSoFar.getRuntime() > best.getRuntime())) {
				bestSoFar = best;
				//update pruning level
				heuristicPruner.setUpperbound(best.getRuntime());
				
				//we only need to return the best
				List<T> justBest = new ArrayList<T>();
				justBest.add(best);
				return justBest;
			}
		}
		
		return children;
	}

}
