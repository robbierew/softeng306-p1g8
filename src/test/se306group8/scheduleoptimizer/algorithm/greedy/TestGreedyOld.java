package se306group8.scheduleoptimizer.algorithm.greedy;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.TestScheduleUtils;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtilsOld;

public class TestGreedyOld {
	
	private void testGraph(TaskGraphOld graph) throws InterruptedException {
		Algorithm greedy = new GreedySchedulingAlgorithm();
		Schedule s1 = greedy.produceCompleteSchedule(graph, 2);
		TestScheduleUtils.checkValidity(s1,2);
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
