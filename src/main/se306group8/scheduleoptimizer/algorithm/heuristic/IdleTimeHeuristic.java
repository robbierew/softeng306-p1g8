package se306group8.scheduleoptimizer.algorithm.heuristic;

import java.util.Arrays;
import java.util.List;

import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class IdleTimeHeuristic implements HeuristicAlgorithm{

	private class IdleTimeMetaData{
		int idletime;
		int totalTaskTime;
		int[] endtimes;
	}
	
	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule) {
		IdleTimeMetaData metaData = new IdleTimeMetaData();
		ProblemStatement pS = schedule.getProblemStatement();
		int processors = pS.getNumProcessors();
		int taskTime = 0;
		for (Task t:pS.getTasksInParitalOrder()) {
			taskTime += t.getComputeCost();
		}
		int[] endtimes = new int[processors];
		List<TaskAllocation> allocations = schedule.getAllocations();
		
		int idletime = 0;
		for (TaskAllocation alloc:allocations) {
			idletime += alloc.getStartTime() - endtimes[alloc.getProcessor()-1];
			endtimes[alloc.getProcessor()-1] = alloc.getEndTime();
		}
		
		//Ceil div
		int hvalue =  (taskTime + idletime - 1) / processors + 1;
		
		metaData.endtimes=endtimes;
		metaData.idletime=idletime;
		metaData.totalTaskTime=taskTime;
		
		return new Heuristic(this, hvalue, metaData, schedule);
	}

	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule, Heuristic parentHeuistic) {
		Object metadata = parentHeuistic.getMetadata();
		if (metadata instanceof IdleTimeMetaData) {
			IdleTimeMetaData mData = (IdleTimeMetaData)metadata;
			IdleTimeMetaData mDataNew = new IdleTimeMetaData();
			mDataNew.endtimes = Arrays.copyOf(mData.endtimes,mData.endtimes.length);
			mDataNew.totalTaskTime = mData.totalTaskTime;
			mDataNew.idletime = mData.idletime;
			TaskAllocation alloc = schedule.getLastAllocation();
			mDataNew.idletime += alloc.getStartTime() - mDataNew.endtimes[alloc.getProcessor() -1];
			mDataNew.endtimes[alloc.getProcessor()-1] = alloc.getEndTime();
			
			//Ceil div (mData.endtimes.length is the number of processors)
			int hvalue =  (mDataNew.totalTaskTime + mDataNew.idletime - 1) / mData.endtimes.length + 1;
			
			return new Heuristic(this,hvalue,mDataNew,schedule);
		} else {
			return computeHeuristic(schedule);
		}
	}

}
