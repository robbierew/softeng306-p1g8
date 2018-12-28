package se306group8.scheduleoptimizer.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import se306group8.scheduleoptimizer.taskgraph.DependencyOld;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskOld;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class TestScheduleUtils {
	public static Schedule createTestScheduleA() {
		TaskGraphOld graph = TestGraphUtils.buildTestGraphA();
		
		//Topological Order
		List<TaskOld> tasks = new ArrayList<>(graph.getAll());
		tasks.sort((a, b) -> a.getName().compareTo(b.getName()));
		
		List<List<TaskOld>> lists = new ArrayList<>();
		
		for(TaskOld t : tasks) {
			lists.add(Arrays.asList(t));
		}
		
		return new ListSchedule(graph, lists);
	}
	
	public static void checkValidity(Schedule schedule, int maxProcessors) {
		TaskGraphOld graph = schedule.getGraph();
		
		for(TaskOld task : graph.getAll()) {
			int processorNumber = schedule.getProcessorNumber(task);
			int index = schedule.getTasksOnProcessor(processorNumber).indexOf(task);
			
			if (processorNumber>maxProcessors) {
				fail();
			}
			
			if (processorNumber<1) {
				fail();
			}

			
			int start;
			if(index != 0) {
				TaskOld previous = schedule.getTasksOnProcessor(processorNumber).get(index - 1);
				start = schedule.getStartTime(previous) + previous.getCost();
			} else {
				start = 0;
			}
			
			for(DependencyOld dep : task.getParents()) {
				int comCost = (schedule.getProcessorNumber(task) == schedule.getProcessorNumber(dep.getSource())) ? 0 : dep.getCommunicationCost();
				start = Math.max(start, comCost + dep.getSource().getCost() + schedule.getStartTime(dep.getSource()));
			}
			
			assertEquals(start, schedule.getStartTime(task));
		}
		
		Collection<TaskOld> scheduledTasks = new HashSet<TaskOld>();
		for (int i = 1; i <= schedule.getNumberOfUsedProcessors(); i++) {
			for (TaskOld task : schedule.getTasksOnProcessor(i)) {
				if (scheduledTasks.contains(task)) {
					fail();
				}
				scheduledTasks.add(task);
			}
		}
		
		// Equality ignores order of tasks
		Collection<TaskOld> allTasks = new HashSet<TaskOld>(schedule.getGraph().getAll());
		assertTrue(scheduledTasks.equals(allTasks));
	}
}
