package se306group8.scheduleoptimizer.taskgraph;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskGraphBuilderTestRefactor {

	private TaskGraph smallGraph;
	
	@BeforeEach
	void createGraph() {
		TaskGraphBuilder builder = new TaskGraphBuilder();
		builder.addTask("a", 2)
		       .addTask("b", 3)
		       .addTask("c", 3)
		       .addTask("d", 2)
		       .addDependecy("a", "b", 1)
		       .addDependecy("a", "c", 2)
		       .addDependecy("b", "d", 2)
		       .addDependecy("c", "d", 1);
		smallGraph = builder.buildGraph();
	}
	
	@Test
	void testFourNodeTaskGraph() {
			
		Iterator<Task> rootNodes =  smallGraph.getRoots().iterator();
		Task root = rootNodes.next();
		
		//Only one root node
		assertFalse(rootNodes.hasNext());
		
		assertEquals(root.getName(), "a");
		assertEquals(root.getComputeCost(), 2);
		assertEquals(root.getParentTaskMask(),0);
		assertEquals(root.getChildTaskMask(),0b110);
		
		Collection<Task> rootChildren = root.getChildTasks();
		
		//Two dependencies
		assertEquals(rootChildren.size(), 2);
		
		Task child = rootChildren.iterator().next();
		
		assertEquals(child.getParentTaskMask(),1);
		assertEquals(child.getChildTaskMask(),0b1000);
		
		Task sink = child.getChildTasks().iterator().next();
		
		assertEquals(sink.getName(),"d");
		assertEquals(sink.getComputeCost(),2);
		assertEquals(sink.getChildTaskMask(),0);
		assertEquals(sink.getParentTaskMask(),0b110);
				
	}
	
	@Test
	void testTopologicalOrder() {
		List<Task> topologicalOrder = smallGraph.getTasksInParitalOrder();
		
		assertTrue(topologicalOrder.size() == 4);
		
		assertTrue(smallGraph.getRoots().contains(topologicalOrder.get(0)));
		List<Task> secondLevel = topologicalOrder.subList(1, 3);
		
		for (Task rootChildren:topologicalOrder.get(0).getChildTasks()) {
			assertTrue(secondLevel.contains(rootChildren));
		}
		
		assertEquals(topologicalOrder.get(3).getName(),"d");
		assertEquals(topologicalOrder.get(3).getComputeCost(),2);
		
	}
	
	@Test
	void testBuilderWithNoLinks() {
		TaskGraphBuilder builder = new TaskGraphBuilder();
		builder.addTask("a", 1);
		builder.addTask("b", 1);
		
		TaskGraph graph = builder.buildGraph();
		
		assertEquals(2, graph.getTasksInParitalOrder().size());
		assertEquals(2, graph.getRoots().size());
	}
	
//	@Test
//	void testBottomTime() {
//		TaskGraphOld graph = TestGraphUtils.buildTestGraphA();
//		
//		TaskOld a = null, b = null, c = null, d = null;
//		for(TaskOld task : graph.getAll()) {
//			switch(task.getName()) {
//			case "a":
//				a = task;
//				break;
//			case "b":
//				b = task;
//				break;
//			case "c":
//				c = task;
//				break;
//			case "d":
//				d = task;
//				break;
//			}
//		}
//		
//		assertEquals(7, graph.getBottomTime(a));
//		assertEquals(5, graph.getBottomTime(b));
//		assertEquals(5, graph.getBottomTime(c));
//		assertEquals(2, graph.getBottomTime(d));
//	}
}
