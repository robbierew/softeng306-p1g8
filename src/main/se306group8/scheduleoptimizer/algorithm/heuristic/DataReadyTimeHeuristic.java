package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;

public class DataReadyTimeHeuristic implements HeuristicAlgorithm{
	
	private class DataReadyTimeMetaData{
		int[] bottomLevels;
		int[] tdr;
		
	}
	
	
	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule) {
		DataReadyTimeMetaData mData = new DataReadyTimeMetaData();
		mData.bottomLevels = BottomLevel.calcBottomLevel(schedule.getProblemStatement());
		TaskAllocation[] allocations = schedule.getAllocationHistory().getAllocationsAsArray();
		
		
		
	}

	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule, Object metadata) {
		// TODO Auto-generated method stub
		return null;
	}

}
