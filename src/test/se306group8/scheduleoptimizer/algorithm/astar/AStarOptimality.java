package se306group8.scheduleoptimizer.algorithm.astar;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.childfinder.BasicChildScheduleFinderOld;
import se306group8.scheduleoptimizer.algorithm.childfinder.DuplicateRemovingChildFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.AggregateHeuristicOld;
import se306group8.scheduleoptimizer.algorithm.heuristic.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.DataReadyTimeHeuristicOld;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.NoIdleTimeHeuristic;
import se306group8.scheduleoptimizer.dotfile.DOTFileHandlerOld;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphOld;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtilsOld;

public class AStarOptimality {
	
	
	
	private Algorithm initAlgorithm(int numProcessors) {
		MinimumHeuristic heuristic = new AggregateHeuristicOld(new CriticalPathHeuristic(),
				new DataReadyTimeHeuristicOld(numProcessors), new NoIdleTimeHeuristic(numProcessors));
		return new AStarSchedulingAlgorithm(new DuplicateRemovingChildFinder(numProcessors), heuristic);
	}
	
	//@Test
	// Test ignored to please Travis
    void testProduceCompleteScheduleMediumGraph() throws IOException, InterruptedException {
        String graphName = "2p_Fork_Nodes_10_CCR_1.97_WeightType_Random.dot";

        DOTFileHandlerOld reader = new DOTFileHandlerOld();
        TaskGraphOld graph = reader.readTaskGraph(Paths.get("dataset", "input", graphName));
        Schedule optimal = reader.readSchedule(Paths.get("dataset", "output", graphName));

        long start = System.nanoTime();
        System.out.println("Starting '" + graphName + "'");
        Schedule s = initAlgorithm(optimal.getNumberOfUsedProcessors()).produceCompleteSchedule(graph, optimal.getNumberOfUsedProcessors());
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

            Schedule s = initAlgorithm(optimal.getNumberOfUsedProcessors()).produceCompleteSchedule(graph, optimal.getNumberOfUsedProcessors());

            System.out.println(s + " took " + (System.nanoTime() - start) / 1_000_000 + "ms");

            Assertions.assertEquals(optimal.getTotalRuntime(), s.getTotalRuntime());
        }
    }

    @Test
    void testProduceCompleteScheduleTinyGraph() throws IOException, InterruptedException {
        TaskGraphOld graph = TestGraphUtilsOld.buildTestGraphA();
        Assertions.assertEquals(8, initAlgorithm(2).produceCompleteSchedule(graph, 2).getTotalRuntime());
    }


}
