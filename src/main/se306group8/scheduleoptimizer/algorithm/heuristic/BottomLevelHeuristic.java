package se306group8.scheduleoptimizer.algorithm.heuristic;


import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;


public class BottomLevelHeuristic implements HeuristicAlgorithm{

	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule) {
		
		TaskAllocation[] allocations = schedule.getAllocationHistory().getAllocationsAsArray();
		int highestBt = 0;
		for (TaskAllocation a:allocations) {
			int bt = a.getStartTime() + a.getTask().getBottomLevel();
			if (bt > highestBt) {
				highestBt = bt;
			}
		}
		return new Heuristic(this, highestBt, schedule);
	}

	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule, Heuristic parentHeuristic) {
		if (parentHeuristic.getAlgorithm()==schedule.getHeuristicAlgorithm()) {
			
			
			TaskAllocation alloc = schedule.getLastAllocation();
			int highestBt = parentHeuristic.getHeuristicValue();
			int bt = alloc.getStartTime() + alloc.getTask().getBottomLevel();
			
			if (bt > highestBt) {
				highestBt = bt;
			}
			
			return new Heuristic(this, highestBt, schedule);
			
		}else {
			return computeHeuristic(schedule);
		}
	
	}

}
