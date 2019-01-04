package se306group8.scheduleoptimizer.algorithm.astar;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.TestScheduleUtils;
import se306group8.scheduleoptimizer.algorithm.childfinder.BasicChildScheduleFinderOld;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtilsOld;

public class TestAStar {

	private void testGraph(TaskGraphOld graph) throws InterruptedException {
		// TODO improve these tests!
		Algorithm algorithm = new AStarSchedulingAlgorithm(new BasicChildScheduleFinderOld(1), h -> 0);
		Schedule s = algorithm.produceCompleteSchedule(graph, 1);
		TestScheduleUtils.checkValidity(s,1);
	}
	
	@Test
	public void testGraphA() throws InterruptedException {
		TaskGraphOld tgA = TestGraphUtilsOld.buildTestGraphA();
		testGraph(tgA);
	}
	
	@Test
	public void testGraphB() throws InterruptedException {
		TaskGraphOld tgB = TestGraphUtilsOld.buildTestGraphB();
		testGraph(tgB);
	}
	
	@Test
	public void testGraphC() throws InterruptedException {
		TaskGraphOld tgC = TestGraphUtilsOld.buildTestGraphC();
		testGraph(tgC);
	}
	
}
