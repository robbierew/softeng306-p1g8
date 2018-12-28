package se306group8.scheduleoptimizer.algorithm;

import java.util.List;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.taskgraph.TaskOld;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreedyChildScheduleFinderTest {

	@Test
	public void TestGraphCWithRoot() {
		TaskGraphOld tg1 = TestGraphUtils.buildTestGraphC();
		TaskOld firstTask = tg1.getAll().get(0);
		
		TreeSchedule empty = new TreeSchedule(tg1, (TreeSchedule s) -> 0, 2);
		TreeSchedule initial = new TreeSchedule(firstTask, 1, empty);
		GreedyChildScheduleFinder gcsf = new GreedyChildScheduleFinder(2);
		
		List<TreeSchedule> result = gcsf.getChildSchedules(initial);
		
		//1 nodes 2 processors = 2 children
		assertEquals(2,result.size());
		
		//earliest start time should be b on processor 2
		assertEquals(2,result.get(0).getMostRecentAllocation().processor);
		assertEquals("b", result.get(0).getMostRecentAllocation().task.getName());
		
		//second should be processor 1
		assertEquals(1,result.get(1).getMostRecentAllocation().processor);
		assertEquals("b", result.get(1).getMostRecentAllocation().task.getName());
		
	}
	
	@Test
	public void TestGraphAWithRoot() {
		TaskGraphOld tg1 = TestGraphUtils.buildTestGraphA();
		TaskOld firstTask = tg1.getAll().get(0);
		
		TreeSchedule empty = new TreeSchedule(tg1, (TreeSchedule s) -> 0, 2);
		TreeSchedule initial = new TreeSchedule(firstTask, 1, empty);
		GreedyChildScheduleFinder gcsf = new GreedyChildScheduleFinder(2);
		
		List<TreeSchedule> result = gcsf.getChildSchedules(initial);
		
		//2 nodes 2 processors = 4 children
		assertEquals(4,result.size());
			
	}
	
	@Test
	public void TestGraphBWithRoot() {
		TaskGraphOld tg1 = TestGraphUtils.buildTestGraphB();
		TaskOld firstTask = tg1.getAll().get(0);
		
		TreeSchedule empty = new TreeSchedule(tg1, (TreeSchedule s) -> 0, 2);
		TreeSchedule initial = new TreeSchedule(firstTask, 1, empty);
		GreedyChildScheduleFinder gcsf = new GreedyChildScheduleFinder(2);
		
		List<TreeSchedule> result = gcsf.getChildSchedules(initial);
		
		//2 nodes 2 processors = 4 children
		assertEquals(4,result.size());
			
	}
}
