package se306group8.scheduleoptimizer.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;
import se306group8.scheduleoptimizer.schedule.TreeSchedule;
import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;

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
	
	@Test
	public void testGraphAStatsEmpty() {
		TaskGraph a = TestGraphUtils.buildTestGraphA();
		ProblemStatement pS = new ProblemStatement(a,2);
		
		TreeSchedule aSchedule = new TreeSchedule(pS);
		
		assertEquals(aSchedule.getRuntime(),0);
		assertEquals(aSchedule.getAllocatableMask(),1);
		
		ArrayList<Task> allocatable = new ArrayList<Task>();
		allocatable.add(pS.getTaskByID(0));
		
		Collection<Task> scheduleAlloc = aSchedule.getAllocatableTasks();
		
		assertTrue(allocatable.containsAll(scheduleAlloc));
		assertTrue(scheduleAlloc.containsAll(allocatable));
		
		assertEquals(0,aSchedule.getAllocatedMask());
		assertEquals(new ArrayList<Task>(),aSchedule.getAllocatedTasks());
		
		try {
			aSchedule.getAllocationForTask(pS.getTaskByID(0));
			fail();
		}catch (RuntimeException e) {
			
		}
		
		ArrayList<TaskAllocation> noTaskAlloc = new ArrayList<TaskAllocation>();
		assertEquals(noTaskAlloc, aSchedule.getAllocations());
		assertEquals(noTaskAlloc, aSchedule.getAllocationsForProcessor(1));
		assertEquals(noTaskAlloc, aSchedule.getAllocationsForProcessor(2));
		
		try {
			aSchedule.getAllocationsForProcessor(0);
			fail();
		}catch (RuntimeException e) {
			
		}
		
		try {
			aSchedule.getAllocationsForProcessor(3);
			fail();
		}catch (RuntimeException e) {
			
		}
		
		assertNull(aSchedule.getLastAllocatedTask());
		assertNull(aSchedule.getLastAllocation());
		assertNull(aSchedule.getLastAllocationForProcessor(1));
		assertNull(aSchedule.getLastAllocationForProcessor(2));
		
		try {
			aSchedule.getLastAllocationForProcessor(0);
			fail();
		}catch (RuntimeException e) {
			
		}
		
		try {
			aSchedule.getLastAllocationForProcessor(3);
			fail();
		}catch (RuntimeException e) {
			
		}
		
		assertNull(aSchedule.getParent());
		assertEquals(pS,aSchedule.getProblemStatement());
		
		assertFalse(aSchedule.isComplete());
		assertTrue(aSchedule.isEmpty());
	}
	
	@Test
	public void testGraphAOneNode() {
		TaskGraph a = TestGraphUtils.buildTestGraphA();
		ProblemStatement pS = new ProblemStatement(a,2);
		
		TreeSchedule parent = new TreeSchedule(pS);
		TreeSchedule aSchedule = new TreeSchedule(parent,pS.getTaskByID(0),1);
		
		assertEquals(aSchedule.getRuntime(),2);
		assertEquals(aSchedule.getAllocatableMask(),0b110);
		
		ArrayList<Task> allocatable = new ArrayList<Task>();
		allocatable.add(pS.getTaskByID(1));
		allocatable.add(pS.getTaskByID(2));
		
		Collection<Task> scheduleAlloc = aSchedule.getAllocatableTasks();
		
		assertTrue(allocatable.containsAll(scheduleAlloc));
		assertTrue(scheduleAlloc.containsAll(allocatable));
		
		assertEquals(1,aSchedule.getAllocatedMask());
		
		ArrayList<Task> allocated= new ArrayList<Task>();
		allocated.add(pS.getTaskByID(0));
		assertEquals(allocated,aSchedule.getAllocatedTasks());
		
		TaskAllocation alloc = aSchedule.getAllocationForTask(pS.getTaskByID(0));
		assertEquals(0,alloc.getStartTime());
		assertEquals(2,alloc.getEndTime());
		assertEquals(1,alloc.getProcessor());
		assertEquals(pS.getTaskByID(0),alloc.getTask());
		
		ArrayList<TaskAllocation> noTaskAlloc = new ArrayList<TaskAllocation>();
		ArrayList<TaskAllocation> taskAlloc = new ArrayList<TaskAllocation>();
		taskAlloc.add(alloc);
		
		assertEquals(taskAlloc, aSchedule.getAllocations());
		assertEquals(taskAlloc, aSchedule.getAllocationsForProcessor(1));
		assertEquals(noTaskAlloc, aSchedule.getAllocationsForProcessor(2));
		
		try {
			aSchedule.getAllocationsForProcessor(0);
			fail();
		}catch (RuntimeException e) {
			
		}
		
		try {
			aSchedule.getAllocationsForProcessor(3);
			fail();
		}catch (RuntimeException e) {
			
		}
		
		assertEquals(pS.getTaskByID(0),aSchedule.getLastAllocatedTask());
		assertEquals(alloc,aSchedule.getLastAllocation());
		assertEquals(alloc,aSchedule.getLastAllocationForProcessor(1));
		assertNull(aSchedule.getLastAllocationForProcessor(2));
		
		try {
			aSchedule.getLastAllocationForProcessor(0);
			fail();
		}catch (RuntimeException e) {
			
		}
		
		try {
			aSchedule.getLastAllocationForProcessor(3);
			fail();
		}catch (RuntimeException e) {
			
		}
		
		assertEquals(parent,aSchedule.getParent());
		assertEquals(pS,aSchedule.getProblemStatement());
		
		assertFalse(aSchedule.isComplete());
		assertFalse(aSchedule.isEmpty());
	}
}
