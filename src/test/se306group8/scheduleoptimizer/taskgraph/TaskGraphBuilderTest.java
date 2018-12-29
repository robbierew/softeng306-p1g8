package se306group8.scheduleoptimizer.taskgraph;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskGraphBuilderTest {

	private TaskGraphOld smallGraph;
	
	@BeforeEach
	void createGraph() {
		TaskGraphBuilderOld builder = new TaskGraphBuilderOld();
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
			
		Iterator<TaskOld> rootNodes =  smallGraph.getRoots().iterator();
		TaskOld root = rootNodes.next();
		
		//Only one root node
		assertFalse(rootNodes.hasNext());
		
		assertEquals(root.getName(), "a");
		assertEquals(root.getCost(), 2);
		
		Collection<DependencyOld> rootChildren = root.getChildren();
		
		//Two dependencies
		assertEquals(rootChildren.size(), 2);
		
		DependencyOld dep = rootChildren.iterator().next();
		TaskOld sink = dep.getTarget().getChildren().iterator().next().getTarget();
		assertEquals(dep.getSource(), root);
		assertEquals(sink.getName(),"d");
		assertEquals(sink.getCost(),2);
				
	}
	
	@Test
	void testTopologicalOrder() {
		List<TaskOld> topologicalOrder = smallGraph.getAll();
		
		assertTrue(topologicalOrder.size() == 4);
		
		assertTrue(smallGraph.getRoots().contains(topologicalOrder.get(0)));
		List<TaskOld> secondLevel = topologicalOrder.subList(1, 3);
		
		for (DependencyOld rootChildren:topologicalOrder.get(0).getChildren()) {
			assertTrue(secondLevel.contains(rootChildren.getTarget()));
		}
		
		assertEquals(topologicalOrder.get(3).getName(),"d");
		assertEquals(topologicalOrder.get(3).getCost(),2);
		
	}
	
	@Test
	void testBuilderWithNoLinks() {
		TaskGraphBuilderOld builder = new TaskGraphBuilderOld();
		builder.addTask("a", 1);
		builder.addTask("b", 1);
		
		TaskGraphOld graph = builder.buildGraph();
		
		assertEquals(2, graph.getAll().size());
		assertEquals(2, graph.getRoots().size());
		assertEquals(0, graph.getEdges().size());
	}
	
	@Test
	void testBottomTime() {
		TaskGraphOld graph = TestGraphUtilsOld.buildTestGraphA();
		
		TaskOld a = null, b = null, c = null, d = null;
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
			}
		}
		
		assertEquals(7, graph.getBottomTime(a));
		assertEquals(5, graph.getBottomTime(b));
		assertEquals(5, graph.getBottomTime(c));
		assertEquals(2, graph.getBottomTime(d));
	}
}
