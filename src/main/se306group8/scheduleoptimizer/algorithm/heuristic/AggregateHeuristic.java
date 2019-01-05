package se306group8.scheduleoptimizer.algorithm.heuristic;

public class AggregateHeuristic implements HeuristicAlgorithm{

	
	private HeuristicAlgorithm[] algorithms;
	
	private class AggregateMetaData{
		Object[] metadatas;
	}
	
	public AggregateHeuristic(HeuristicAlgorithm ... algorithms) {
		this.algorithms = algorithms;
	}
	
	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule) {
		AggregateMetaData mData = new AggregateMetaData();
		mData.metadatas = new Object[algorithms.length];
		int highestHeuristic = 0;
		int index = 0;
		for (HeuristicAlgorithm algorithm:algorithms) {
			Heuristic h = algorithm.computeHeuristic(schedule);
			highestHeuristic = Math.max(highestHeuristic, h.getHeuristicValue());
			mData.metadatas[index] = h.getMetadata();
			index++;
		}
		
		return new Heuristic(this, highestHeuristic, mData, schedule);
	}

	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule, Object metadata) {
		
		if (metadata instanceof AggregateMetaData) {
			AggregateMetaData mData = (AggregateMetaData)metadata;
			AggregateMetaData mDataNew = new AggregateMetaData();
			mDataNew.metadatas = new Object[algorithms.length];
			int highestHeuristic = 0;
			int index = 0;
			
			for (HeuristicAlgorithm algorithm:algorithms) {
				Heuristic h = algorithm.computeHeuristic(schedule,mData.metadatas[index]);
				highestHeuristic = Math.max(highestHeuristic, h.getHeuristicValue());
				mDataNew.metadatas[index] = h.getMetadata();
				index++;
			}
			
			return new Heuristic(this, highestHeuristic, mDataNew, schedule);
		} else {
			return computeHeuristic(schedule);
		}
		
	}

}
