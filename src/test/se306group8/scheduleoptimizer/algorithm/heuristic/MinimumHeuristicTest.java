package se306group8.scheduleoptimizer.algorithm.heuristic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.BasicChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class MinimumHeuristicTest {
	private static final int PROCESSOR = 2;
	private ChildScheduleFinder childFinder;
	
	@BeforeEach
	public void createChildFinder() {
		childFinder = new BasicChildScheduleFinder(PROCESSOR);
	}
	
	public void testEstimate(MinimumHeuristic heuristic) {
		TaskGraph graph = TestGraphUtils.buildTestGraphA();
		
		TreeSchedule root = new TreeSchedule(graph, heuristic);
		getCompleteList(root, heuristic);
	}

	private Collection<Schedule> getCompleteList(TreeSchedule root, MinimumHeuristic heuristic) {
		if(root.isComplete()) {
			Schedule schedule = root.getFullSchedule();
			
			assertTrue(heuristic.estimate(root) <= schedule.getTotalRuntime());
			
			return Arrays.asList(schedule);
		}
		
		int lowerBound = heuristic.estimate(root);
		List<Schedule> allCompletes = new ArrayList<>();
		
		Collection<TreeSchedule> children = childFinder.getChildSchedules(root);
		for(TreeSchedule tree : children) {
			allCompletes.addAll(getCompleteList(tree, heuristic));
		}
		
		for(Schedule complete : allCompletes) {
			assertTrue(complete.getTotalRuntime() >= lowerBound);
		}
		
		return allCompletes;
	}
	
	@Test
	public void testNoIdleTime() {
		testEstimate(new NoIdleTimeHeuristic(PROCESSOR));
	}
	
	@Test
	public void testCriticalPath() {
		testEstimate(new CriticalPathHeuristic());
	}
}