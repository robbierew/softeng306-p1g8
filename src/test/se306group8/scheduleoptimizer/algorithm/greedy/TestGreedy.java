package se306group8.scheduleoptimizer.algorithm.greedy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.schedule.TreeSchedule;
import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilderImpl;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class TestGreedy {
	
	
	@Test
	public void testConstructA() {
		TaskGraph a = TestGraphUtils.buildTestGraphA();
		ProblemStatement pS = new ProblemStatement(a,2);
		
		TreeScheduleBuilder<TreeSchedule> builder = new TreeScheduleBuilderImpl();
		GreedyScheduleBuilder<TreeSchedule> greedyBuild = new GreedyScheduleBuilder<TreeSchedule>(builder);
		
		TreeSchedule greedy = greedyBuild.makeGreedyFromStatement(pS);
		
		assertEquals(0b1111,greedy.getAllocatedMask());
		assertEquals(9,greedy.getRuntime());
		
	}
}
