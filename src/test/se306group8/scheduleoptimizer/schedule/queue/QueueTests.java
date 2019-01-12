package se306group8.scheduleoptimizer.schedule.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.branchbound.BnBSchedulingAlgorithm;
import se306group8.scheduleoptimizer.algorithm.greedy.GreedyScheduleBuilder;
import se306group8.scheduleoptimizer.algorithm.heuristic.AggregateHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.BottomLevelHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.DataReadyTimeHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicAlgorithm;
import se306group8.scheduleoptimizer.algorithm.heuristic.IdleTimeHeuristic;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class QueueTests {
	QueueScheduleBuilder builder;
	@BeforeEach
	public void init() {
		HeuristicAlgorithm iAlgotithm = new IdleTimeHeuristic();
		HeuristicAlgorithm bAlgorithm = new BottomLevelHeuristic();
		HeuristicAlgorithm dAlgorithm = new DataReadyTimeHeuristic();
		HeuristicAlgorithm aAlgorithm = new AggregateHeuristic(iAlgotithm,bAlgorithm,dAlgorithm);
		builder = new QueueScheduleBuilder(aAlgorithm);
	}
	
	@Test
	public void cacheHit() {
		TaskGraph a = TestGraphUtils.buildTestGraphA();
		ProblemStatement statement = new ProblemStatement(a, 2);
		HeuristicPriorityQueue<QueueSchedule> queue = new HeuristicPriorityQueue<QueueSchedule>(builder,statement);
		
		QueueSchedule s = builder.buildRootSchedule(statement);
		queue.put(s);
		
		//the are equal cause of cache hit
		assertEquals(s,queue.peek());
		assertEquals(s,queue.pop());
		assertNull(queue.peek());
		assertNull(queue.pop());
	}
	
	@Test
	public void greedyAndRoot() {
		TaskGraph a = TestGraphUtils.buildTestGraphA();
		ProblemStatement statement = new ProblemStatement(a, 2);
		HeuristicPriorityQueue<QueueSchedule> queue = new HeuristicPriorityQueue<QueueSchedule>(builder,statement);
		GreedyScheduleBuilder<QueueSchedule> gBuilder = new GreedyScheduleBuilder<QueueSchedule>(builder);
		QueueSchedule root = builder.buildRootSchedule(statement);
		QueueSchedule greedy = gBuilder.makeGreedyFromSchedule(root);
		queue.put(root);
		queue.put(greedy);
		
		QueueSchedule root2 = queue.pop();
		assertEquals(root.getAllocatableMask(),root2.getAllocatableMask());
		assertEquals(root.getAllocatedMask(),root2.getAllocatedMask());
		
		QueueSchedule greedy2 = queue.pop();
		assertEquals(greedy.getAllocatableMask(),greedy2.getAllocatableMask());
		assertEquals(greedy.getRuntime(),greedy2.getRuntime());
		
		assertNull(queue.pop());
	}
	
	@Test
	public void pruneTest() {
		TaskGraph a = TestGraphUtils.buildTestGraphA();
		ProblemStatement statement = new ProblemStatement(a, 2);
		HeuristicPriorityQueue<QueueSchedule> queue = new HeuristicPriorityQueue<QueueSchedule>(builder,statement);
		GreedyScheduleBuilder<QueueSchedule> gBuilder = new GreedyScheduleBuilder<QueueSchedule>(builder);
		BnBSchedulingAlgorithm<QueueSchedule> bnb = new BnBSchedulingAlgorithm<QueueSchedule>(builder);
		QueueSchedule root = builder.buildRootSchedule(statement);
		QueueSchedule large = builder.buildChildSchedule(root, statement.getTaskByID(0), 1);
		large = builder.buildChildSchedule(large, statement.getTaskByID(1), 1);
		large = builder.buildChildSchedule(large, statement.getTaskByID(2), 1);
		large = builder.buildChildSchedule(large, statement.getTaskByID(3), 1);
		QueueSchedule optimal = bnb.findOptimalSchedule(root);
		queue.put(large);
		queue.put(optimal);
		QueueSchedule out = queue.pop();
		assertEquals(optimal.getRuntime(),out.getRuntime());
		
		assertNull(queue.pop());
		
	}
	
}
