package se306group8.scheduleoptimizer.schedule;

import java.util.List;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;
import se306group8.scheduleoptimizer.schedule.TreeSchedule;

public class TestTreeSchedule {

	@Test
	public void testContrustPartialOrder() {
		TaskGraph a = TestGraphUtils.buildTestGraphA();
		ProblemStatement pS = new ProblemStatement(a,2);
		
		TreeSchedule aSchedule = new TreeSchedule(pS);
		List<Task> tasks = pS.getTasksInParitalOrder();
		
		for (Task t:tasks) {
			aSchedule = new TreeSchedule(aSchedule,t,1);
		}
		
		TaskGraph b = TestGraphUtils.buildTestGraphB();
		pS = new ProblemStatement(b,2);
		
		TreeSchedule bSchedule = new TreeSchedule(pS);
		tasks = pS.getTasksInParitalOrder();
		
		for (Task t:tasks) {
			bSchedule = new TreeSchedule(bSchedule,t,1);
		}
		
		TaskGraph c = TestGraphUtils.buildTestGraphC();
		pS = new ProblemStatement(c,2);
		
		TreeSchedule cSchedule = new TreeSchedule(pS);
		tasks = pS.getTasksInParitalOrder();
		
		for (Task t:tasks) {
			cSchedule = new TreeSchedule(cSchedule,t,1);
		}
		
	}
}
