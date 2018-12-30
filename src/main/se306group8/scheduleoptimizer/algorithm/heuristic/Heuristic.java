package se306group8.scheduleoptimizer.algorithm.heuristic;

public class Heuristic {

	private HeuristicAlgorithm algorithm;
	private Object metadata;
	private int heuristicValue;
	private HeuristicSchedule schedule;

	Heuristic(HeuristicAlgorithm algorithm, int heuristicValue, Object metadata, HeuristicSchedule schedule) {
		this.algorithm = algorithm;
		this.heuristicValue = heuristicValue;
		this.metadata = metadata;
		this.schedule = schedule;
	}

	Heuristic(HeuristicAlgorithm algorithm, int heuristicValue, HeuristicSchedule schedule) {
		this(algorithm, heuristicValue, null, schedule);
	}

	public HeuristicAlgorithm getAlgorithm() {
		return algorithm;
	}

	public int getHeuristicValue() {
		if (schedule.isComplete()) {
			return schedule.getRuntime();
		}
		return heuristicValue;
	}

	public HeuristicSchedule getHeuristicSchedule() {
		return schedule;
	}

	Object getMetadata() {
		return metadata;
	}
}
