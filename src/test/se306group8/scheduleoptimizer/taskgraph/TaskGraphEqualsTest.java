package se306group8.scheduleoptimizer.taskgraph;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskGraphEqualsTest {

	@Test
	void testEqualsTaskGraph() {
		Assertions.assertEquals(TestGraphUtils.buildTestGraphA(), TestGraphUtils.buildTestGraphA());
	}

	@Test
	void testEqualsTask() {
		TaskOld b1 = null;
		for(TaskOld t : TestGraphUtils.buildTestGraphA().getAll()) {
			if(t.getName().equals("b")) {
				b1 = t;
			}
		}
		
		TaskOld b2 = null;
		for(TaskOld t : TestGraphUtils.buildTestGraphA().getAll()) {
			if(t.getName().equals("b")) {
				b2 = t;
			}
		}
		
		TaskOld a1 = null;
		for(TaskOld t : TestGraphUtils.buildTestGraphA().getAll()) {
			if(t.getName().equals("a")) {
				a1 = t;
			}
		}
		
		Assertions.assertEquals(b1, b2);
		Assertions.assertNotEquals(a1, b1);
	}
	
	/** Checks for two tasks with the cost, and dependencies but differing names. */
	@Test
	void testNotEquals() {
		TaskGraphBuilderOld builder = new TaskGraphBuilderOld();
		builder.addTask("a", 1);
		builder.addTask("b", 1);
		
		TaskGraphOld graph = builder.buildGraph();
		
		for(TaskOld t1 : graph.getAll()) {
			for(TaskOld t2 : graph.getAll()) {
				Assertions.assertEquals(t1.equals(t2), t1 == t2);
			}
		}
	}
	
	/** Check equality of two tasks with different costs, dependencies and same names */
	@Test
	void testNotEqualsCosts() {
		TaskGraphBuilderOld builderA = new TaskGraphBuilderOld();
		builderA.addTask("a", 1);
		
		TaskGraphBuilderOld builderB = new TaskGraphBuilderOld();
		builderB.addTask("a", 2);
		
		TaskGraphOld graphA = builderA.buildGraph();
		TaskGraphOld graphB = builderB.buildGraph();
		
		Assertions.assertNotEquals(graphA.getAll().get(0), graphB.getAll().get(0));
	}
	
	/** Check the communication cost of two nodes known to be equal, is rendered equal */
	@Test
	void testCommunicationCostsEqual() {
		TaskOld a = null;
		TaskOld b = null;
		TaskOld c = null;
		TaskOld d = null;
		
		// Assign task objects as specified in Tester graph
		for(TaskOld t : TestGraphUtils.buildTestGraphA().getAll()) {
			switch(t.getName()) {
				case "a":
					a = t;
					break;
				case "b":
					b = t;
					break;
				case "c":
					c = t;
					break;
				case "d":
					d = t;
					break;
			}
		}	
		Assertions.assertEquals(a.getCost(), d.getCost());
		Assertions.assertEquals(b.getCost(), c.getCost());
	}
}
