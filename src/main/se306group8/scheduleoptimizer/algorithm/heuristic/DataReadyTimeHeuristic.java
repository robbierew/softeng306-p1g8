package se306group8.scheduleoptimizer.algorithm.heuristic;

import java.util.Arrays;
import java.util.List;

import se306group8.scheduleoptimizer.schedule.Schedule;
import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class DataReadyTimeHeuristic implements HeuristicAlgorithm{
	
	private class DataReadyTimeMetaData{
		int[] tdr;
	}
	
	
	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule) {
		DataReadyTimeMetaData mData = new DataReadyTimeMetaData();
		mData.tdr = new int[schedule.getProblemStatement().getNumTasks()];
		List<Task> allocatable = schedule.getAllocatedTasks(); 
		updatetdr(allocatable,schedule,mData.tdr,schedule.getProblemStatement().getNumProcessors());
		int hValue = 0;
		for (Task t:allocatable) {
			hValue = Math.max(hValue,mData.tdr[t.getID()]);
		}
		
		return new Heuristic(this, hValue, mData, schedule);
	}

	@Override
	public Heuristic computeHeuristic(HeuristicSchedule schedule, Heuristic parentHeuristic) {
		Object metadata = parentHeuristic.getMetadata();
		if (metadata instanceof DataReadyTimeMetaData) {
			DataReadyTimeMetaData mData = (DataReadyTimeMetaData)metadata;
			DataReadyTimeMetaData mDataNew = new DataReadyTimeMetaData();
			mDataNew.tdr = Arrays.copyOf(mData.tdr, mData.tdr.length);
			//this gives you the bits that are different between them
			int newAllocMask = parentHeuristic.getHeuristicSchedule().getAllocatableMask() ^ schedule.getAllocatableMask();
			
			//we also don't care about the last allocated task
			newAllocMask &= ~schedule.getLastAllocatedTask().getMask();
			
			List<Task> allocatable = schedule.getProblemStatement().getTaskListFromMask(newAllocMask);
			updatetdr(allocatable,schedule,mDataNew.tdr,schedule.getProblemStatement().getNumProcessors());
			int hValue = 0;
			for (Task t:allocatable) {
				hValue = Math.max(hValue,mDataNew.tdr[t.getID()]);
			}
			
			return new Heuristic(this, hValue, mDataNew, schedule);
			
		}else {
			return computeHeuristic(schedule);
		}
	}
	
	private void updatetdr(List<Task> tasks,Schedule s, int[] tdr, int processors) {
		for (Task t: tasks) {
			int minDrt = Integer.MAX_VALUE;
			for (int p=1;p<=processors;p++) {
				minDrt = Math.min(minDrt, tdr(s,t,p));
			}
			tdr[t.getID()] = minDrt;
		}
	}

	private int tdr(Schedule s, Task t, int p) {
		int tdr = 0;
		ProblemStatement pS = s.getProblemStatement();
		for (Task parent:t.getParentTasks()) {
			TaskAllocation alloc = s.getAllocationForTask(parent);
			if (alloc.getProcessor() == p) {
				tdr = Math.max(tdr, alloc.getEndTime());
			}else {
				tdr = Math.max(tdr, alloc.getEndTime() + pS.getRemoteCostFor(parent, t));
			}
		}
		return tdr;
	}

}
