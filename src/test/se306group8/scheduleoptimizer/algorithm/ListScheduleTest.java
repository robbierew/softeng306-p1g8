package se306group8.scheduleoptimizer.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.taskgraph.TaskOld;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtilsOld;

class ListScheduleTest {
	private ListSchedule schedule, scheduleFromList, scheduleFromAllocation;
	private TaskGraphOld graph;
	
	private TaskOld a, b, c, d;
	
	/** We create a schedule with the same task graph as testGraphA, and schedule the tasks
	 * with task A and B on processor 1 and C and D on processor 2. 
	 * 
	 * The three different variants are used for differing test. For some schedules the requested data is trivial. */
	@BeforeEach
	void setUp() {
		graph = TestGraphUtilsOld.buildTestGraphA();
		
		for(TaskOld task : graph.getAll()) {
			switch(task.getName()) {
			case "a":
				a = task;
				break;
			case "b":
				b = task;
				break;
			case "c":
				c = task;
				break;
			case "d":
				d = task;
				break;
			default:
				assert false;
			}
		}
		
		List<TaskOld> processorOne = Arrays.asList(a, b);
		List<TaskOld> processorTwo = Arrays.asList(c, d);
		
		Map<TaskOld, ProcessorAllocation> allocations = new HashMap<>();
		
		allocations.put(a, new ProcessorAllocation(a, 0, 1));
		allocations.put(b, new ProcessorAllocation(b, 2, 1));
		
		allocations.put(c, new ProcessorAllocation(c, 4, 2));
		allocations.put(d, new ProcessorAllocation(d, 7, 2));
		
		scheduleFromList = new ListSchedule(graph, Arrays.asList(processorOne, processorTwo));
		scheduleFromAllocation = new ListSchedule(graph, allocations);
		schedule = new ListSchedule(graph, allocations, Arrays.asList(processorOne, processorTwo));
	}

	@Test
	void testListScheduleTaskGraphListOfListOfTask() {
		assertEquals(schedule, scheduleFromList);
	}

	@Test
	void testListScheduleTaskGraphMapOfTaskProcessorAllocation() {
		assertEquals(schedule, scheduleFromAllocation);
	}

	@Test
	void testGetGraph() {
		assertEquals(graph, schedule.getGraph());
	}

	@Test
	void testGetNumberOfUsedProcessors() {
		assertEquals(2, schedule.getNumberOfUsedProcessors());
	}

	@Test
	void testGetStartTime() {
		assertEquals(0, scheduleFromList.getStartTime(a));
		assertEquals(2, scheduleFromList.getStartTime(b));
		assertEquals(4, scheduleFromList.getStartTime(c));
		assertEquals(7, scheduleFromList.getStartTime(d));
	}

	@Test
	void testGetTasksOnProcessor() {
		assertEquals(Arrays.asList(a, b), scheduleFromAllocation.getTasksOnProcessor(1));
		assertEquals(Arrays.asList(c, d), scheduleFromAllocation.getTasksOnProcessor(2));
	}
	
	@Test
	void testGetProcessorNumber() {
		assertEquals(1, scheduleFromList.getProcessorNumber(a));
		assertEquals(1, scheduleFromList.getProcessorNumber(b));
		assertEquals(2, scheduleFromList.getProcessorNumber(c));
		assertEquals(2, scheduleFromList.getProcessorNumber(d));
	}

	@Test
	void testGetTotalRuntime() {
		assertEquals(9, scheduleFromList.getTotalRuntime());
		assertEquals(9, scheduleFromAllocation.getTotalRuntime());
	}

	@Test
	void testEqualsObject() {
		assertEquals(scheduleFromAllocation, scheduleFromList);
		assertNotEquals(TestScheduleUtils.createTestScheduleA(), schedule);
	}
	
	@Test
	void testValidity() {
		TestScheduleUtils.checkValidity(scheduleFromList,2);
	}
}
