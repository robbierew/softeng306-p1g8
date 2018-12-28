package se306group8.scheduleoptimizer.algorithm.astar;

import java.util.List;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.DuplicateRemovingChildFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.storage.BlockScheduleStorage;
import se306group8.scheduleoptimizer.algorithm.storage.ScheduleStorage;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;

/**
 * Produce a optimal schedule using A* algorithm. Requires you to provide your
 * own heuristics. This algorithm may consume a lot of memory protections are in
 * place to prevent crashing by switching to a DFS branch and bound algorithm
 * when memory is too high.
 */
public class AStarSchedulingAlgorithm extends Algorithm {
	
	private final ChildScheduleFinder childGenerator;
	private final MinimumHeuristic heuristic;
	private ScheduleStorage queue;
	private long explored = 0;
	
	private TreeSchedule dfsBest;
	private long maxQueueSize;
	
	public AStarSchedulingAlgorithm(ChildScheduleFinder childGenerator, MinimumHeuristic heuristic, RuntimeMonitor monitor, ScheduleStorage storage) {
		super(monitor);
		
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
		this.queue = storage;
	}

	public AStarSchedulingAlgorithm(ChildScheduleFinder childGenerator, MinimumHeuristic heuristic, RuntimeMonitor monitor) {
		super(monitor);
		
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
	}

	public AStarSchedulingAlgorithm(ChildScheduleFinder childGenerator, MinimumHeuristic heuristic) {
		super();
		
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraphOld graph, int numberOfProcessors) throws InterruptedException {
		getMonitor().logMessage("Starting A*.");
		
		//used for the console output
		boolean contingency = false;

		getMonitor();

		TreeSchedule best = new TreeSchedule(graph, heuristic, numberOfProcessors);

		//Compute a greedy upper bound for pruning 
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		
		//Calculate an initial bound
		TreeSchedule greedySoln = best;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}
		
		if(queue == null) {
			int range = greedySoln.getRuntime() - best.getLowerBound();
			int granularity = range / 200 + 1; //This caps our memory losses at 100Mb
			int blockSize = 100_000;
			
			queue = new BlockScheduleStorage(granularity, blockSize);
		}
		
		queue.signalStorageSizes(getMonitor());
		queue.put(greedySoln);
		getMonitor().updateBestSchedule(greedySoln);
		getMonitor().setUpperBound(greedySoln.getRuntime());
		
		Runtime memory = Runtime.getRuntime();
		//A conservative amount of the memory to use
		maxQueueSize = (long) (memory.maxMemory() * 0.65) / 10;
		
		//In A* once a complete schedule comes out of the queue we are
		while (!best.isComplete()) {
			
			queue.signalMonitor(getMonitor());
			if (getMonitor().isInterupted()) {
				throw new InterruptedException();
			}
						
			//Run A* if we have enough memory overwise DFS
			if (queue.size() < maxQueueSize) {
				if (contingency) {
					contingency = false;
					getMonitor().logMessage("Switching back to A*");
				}
				
				getMonitor().setLowerBound(best.getLowerBound());
				explore(best);
			} else {
				
				if (!contingency) {
					contingency = true;
					getMonitor().logMessage("Using contingency plan Branch and Bound");
				}
				//System.out.println("Using contingency plan");
				
				dfsBest = queue.getBestSchedule();
				queue.put(branchAndBound(best));		
				
			}
			
			best = queue.pop();

			// MERGE CONFLICTS - COMMENTED OUT TO CHECK
			//explored += children.size();

			queue.signalMonitor(getMonitor());
			getMonitor().updateBestSchedule(best);
			getMonitor().setSchedulesExplored(explored);
		}
		
		return best.getFullSchedule();
	}

	@Override
	public String toString() {
		return "A*";
	}

	/**
	 * To help the queue we recursively search until the children have a different
	 * lower bound than the @param schedule
	 */
	void explore(TreeSchedule best) throws InterruptedException {
		if (getMonitor().isInterupted()) {
			throw new InterruptedException();
		}
		
		if(queue.getBestSchedule().getRuntime() <= best.getLowerBound()) {
			return; //We are done, someone has already found the solution
		}
		
		if(queue.size() >= maxQueueSize) {
			queue.put(best);
			return; //Early exit to avoid filling up the queue
		}
		
		if(best.isComplete()) {
			queue.put(best);
			return; //Exit algorithm
		}
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		List<TreeSchedule> children = childGenerator.getChildSchedules(best);

		explored += children.size();
		
		for(TreeSchedule child : children) {
			if(child.getLowerBound() == best.getLowerBound()) {				
				explore(child);
			} else {
				queue.put(child);
			}
		}
		
		queue.signalMonitor(getMonitor());
		getMonitor().updateBestSchedule(best);
		getMonitor().setSchedulesExplored(explored);
	}
	
	/** The contingency plan. Use BnB if we are low on memory. */
	private TreeSchedule branchAndBound(TreeSchedule schedule) throws InterruptedException {
		if(getMonitor().isInterupted()) {
			throw new InterruptedException();
		}
		
		// Get all children in order from best lower bound to worst
		List<TreeSchedule> childSchedules = childGenerator.getChildSchedules(schedule);
		childSchedules.sort(null);
		
		explored += childSchedules.size();
		
		for (TreeSchedule child : childSchedules) {
			// Only consider the child if its lower bound is better than current best
			if (child.getLowerBound() < dfsBest.getRuntime()) {
				if (child.isComplete()) {
					getMonitor().updateBestSchedule(child);
					dfsBest=child;
				} else {
					// Check if the child schedule is complete or not
					dfsBest = branchAndBound(child);
				}
			} else {
				getMonitor().updateBestSchedule(schedule);
				break;
			}
		}
		
		getMonitor().setSchedulesExplored(explored);
		
		return dfsBest;
	}
}
