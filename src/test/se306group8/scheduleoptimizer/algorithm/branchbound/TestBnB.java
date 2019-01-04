package se306group8.scheduleoptimizer.algorithm.branchbound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicAlgorithm;
import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicSchedule;
import se306group8.scheduleoptimizer.algorithm.heuristic.HeuristicScheduleBuilder;
import se306group8.scheduleoptimizer.algorithm.heuristic.IdleTimeHeuristic;
import se306group8.scheduleoptimizer.dotfile.DOTFileHandler;
import se306group8.scheduleoptimizer.dotfile.DOTFileHandlerOld;
import se306group8.scheduleoptimizer.schedule.Schedule;
import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class TestBnB {
	
	private BnBSchedulingAlgorithm<HeuristicSchedule> bnb;
	
	@BeforeEach
	public void prepBnB() {
		HeuristicAlgorithm hAlgotithm = new IdleTimeHeuristic();
		TreeScheduleBuilder<HeuristicSchedule> builder = new HeuristicScheduleBuilder(hAlgotithm);
		bnb = new BnBSchedulingAlgorithm<HeuristicSchedule>(builder);
	}
	
	@Test
	public void correctASchedule() {
		TaskGraph a = TestGraphUtils.buildTestGraphA();
		ProblemStatement statement = new ProblemStatement(a, 2);
		Schedule s = bnb.findOptimalSchedule(statement);

		assertTrue(s.isComplete());
		assertEquals(8, s.getRuntime());
	}
	
	@Test
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
			DOTFileHandler reader = new DOTFileHandler();
			DOTFileHandlerOld oldReader = new DOTFileHandlerOld();
			
			long start = System.nanoTime();
			se306group8.scheduleoptimizer.taskgraph.Schedule optimal = oldReader.readSchedule(Paths.get("dataset", "output", graphName));

			System.out.println("Starting '" + graphName + "'");

			TaskGraph graph = reader.readTaskGraph(Paths.get("dataset", "input", graphName));

			int processors = optimal.getNumberOfUsedProcessors();
			
			if (processors > 2) {
				continue;
			}
			
			Schedule s = bnb.findOptimalSchedule(new ProblemStatement(graph,processors));

			System.out.println(" took " + (System.nanoTime() - start) / 1_000_000 + "ms");

			Assertions.assertEquals(optimal.getTotalRuntime(), s.getRuntime());
		}
	}

}
