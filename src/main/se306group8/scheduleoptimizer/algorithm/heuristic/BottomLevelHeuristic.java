package se306group8.scheduleoptimizer.algorithm.heuristic;


import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;


public class BottomLevelHeuristic implements HeuristicAlgorithm{

	
	private class BottomLevelMetaData{
		int[] bottomLevels;
		int parentHeuristicValue;
	}
	
	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule) {
		BottomLevelMetaData mData = new BottomLevelMetaData();
		mData.bottomLevels = BottomLevel.calcBottomLevel(schedule.getProblemStatement());
		
		TaskAllocation[] allocations = schedule.getAllocationHistory().getAllocationsAsArray();
		int highestBt = 0;
		for (TaskAllocation a:allocations) {
			int bt = a.getStartTime() + mData.bottomLevels[a.getTask().getID()];
			if (bt > highestBt) {
				highestBt = bt;
			}
		}
		mData.parentHeuristicValue = highestBt;
		return new Heuristic(this, highestBt, mData, schedule);
	}

	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule, Object metadata) {
		if (metadata instanceof BottomLevelMetaData) {
			BottomLevelMetaData mData = (BottomLevelMetaData)metadata;
			BottomLevelMetaData mDataNew = new BottomLevelMetaData();
			
			//we can copy by reference as these values will never change
			mDataNew.bottomLevels = mData.bottomLevels;
			
			TaskAllocation alloc = schedule.getLastAllocation();
			int highestBt = mData.parentHeuristicValue;
			int bt = alloc.getStartTime() + mData.bottomLevels[alloc.getTask().getID()];
			
			if (bt > highestBt) {
				highestBt = bt;
			}
			
			mDataNew.parentHeuristicValue = highestBt;
			return new Heuristic(this, highestBt, mDataNew, schedule);
			
		}else {
			return computeHeuristic(schedule);
		}
	
	}

}
