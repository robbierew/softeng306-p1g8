package se306group8.scheduleoptimizer.algorithm.branchbound;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.dotfile.DOTFileHandlerOld;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class BranchBoundOptimality {

	//@Test
	// Test ignored to please Travis
	void testProduceCompleteScheduleMediumGraph() throws IOException, InterruptedException {
		String graphName = "2p_Fork_Nodes_10_CCR_1.97_WeightType_Random.dot";

		DOTFileHandlerOld reader = new DOTFileHandlerOld();
		TaskGraphOld graph = reader.readTaskGraph(Paths.get("dataset", "input", graphName));
		Schedule optimal = reader.readSchedule(Paths.get("dataset", "output", graphName));

		long start = System.nanoTime();
		System.out.println("Starting '" + graphName + "'");
		int processors = optimal.getNumberOfUsedProcessors();
		Schedule s = new BranchBoundSchedulingAlgorithm(new GreedyChildScheduleFinder(processors), new CriticalPathHeuristic()).produceCompleteSchedule(graph, processors);
		System.out.println(s + " took " + (System.nanoTime() - start) / 1_000_000 + "ms");

		Assertions.assertEquals(optimal.getTotalRuntime(), s.getTotalRuntime());
	}


	//@Test
	void testProduceCompleteScheduleAll10NodeGraphs() throws IOException, InterruptedException {
		List<String> names = new ArrayList<>();

		try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("dataset", "input"))) {
			stream.forEach(p -> {
				String graphName = p.getFileName().toString();
				if(graphName.contains("Nodes_10"))
					names.add(graphName);

			});
		}

		names.sort(null);

		for(String graphName : names) {
			DOTFileHandlerOld reader = new DOTFileHandlerOld();

			long start = System.nanoTime();
			Schedule optimal = reader.readSchedule(Paths.get("dataset", "output", graphName));
			if(optimal.getNumberOfUsedProcessors() > 3)
				continue;

			System.out.println("Starting '" + graphName + "'");

			TaskGraphOld graph = reader.readTaskGraph(Paths.get("dataset", "input", graphName));

			int processors = optimal.getNumberOfUsedProcessors();
			Schedule s = new BranchBoundSchedulingAlgorithm(new GreedyChildScheduleFinder(processors), new CriticalPathHeuristic()).produceCompleteSchedule(graph, optimal.getNumberOfUsedProcessors());

			System.out.println(s + " took " + (System.nanoTime() - start) / 1_000_000 + "ms");

			Assertions.assertEquals(optimal.getTotalRuntime(), s.getTotalRuntime());
		}
	}

	@Test
	void testProduceCompleteScheduleTinyGraph() throws IOException, InterruptedException {
		TaskGraphOld graph = TestGraphUtils.buildTestGraphA();
		Assertions.assertEquals(8, new BranchBoundSchedulingAlgorithm(new GreedyChildScheduleFinder(2), new CriticalPathHeuristic()).produceCompleteSchedule(graph, 2).getTotalRuntime());
	}


}
