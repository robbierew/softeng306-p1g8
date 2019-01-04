package se306group8.scheduleoptimizer.algorithm.childfinder;

import java.util.ArrayList;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.DependencyOld;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskOld;

/**
 * This child finder attempts to remove all duplicate children and equivalent children, so that only one of each equivalence class is explored.
 * It does not attempt to ensure that all children are generated, only that all complete children can be generated.
 * 
 * Firstly, each child can only be generated by it's best parent. The best parent is the one that is generated by removing the least task from the top of a processor. The least task is
 * the one that has the earliest processor parent
 */
public class DuplicateRemovingChildFinder implements ChildScheduleFinderOld {
	private enum ForkJoinOrderResult {
		BEFORE, AFTER, UNSURE
	}
	
	private final int processors;
	public DuplicateRemovingChildFinder(int processors) {
		this.processors = processors;
	}
	
	@Override
	public List<TreeSchedule> getChildSchedules(TreeSchedule schedule) {
		//Only add the schedule if it is the earliest schedule that can create it.
		//Schedule a is smaller than schedule b if 
		List<TreeSchedule> childrenSchedules = new ArrayList<>(processors * schedule.getAllocatable().size());
		int largestRoot = schedule.getLargestRoot();
		
		if(schedule.isFixed()) {
			TaskOld nextTaskInFixedOrder = schedule.getAllocatable().get(0);
			
			for(int p = 1; p <= schedule.getNumberOfUsedProcessors(); p++) {
				if(checkFixedOrder(nextTaskInFixedOrder, p, schedule)) {
					childrenSchedules.add(new TreeSchedule(nextTaskInFixedOrder, p, schedule));
				}
			}
			
			if(schedule.getNumberOfUsedProcessors() != processors) { //We can always allocate on the next empty processor, regardless of fixed order.
				for (TaskOld task : schedule.getAllocatable()) {
					if(task.getId() > largestRoot) {
						//No need to check, we are on a free processor anyway
						childrenSchedules.add(new TreeSchedule(task, schedule.getNumberOfUsedProcessors() + 1, schedule));
					}
				}
			}
		} else {
			for (TaskOld task : schedule.getAllocatable()) {
				//Make sure that the processors are allocated in the order of the first task on each processor.
				int processorsToAllocate;
				if(task.getId() > schedule.getLargestRoot()) {
					processorsToAllocate = Math.min(schedule.getNumberOfUsedProcessors() + 1, processors);
				} else {
					processorsToAllocate = schedule.getNumberOfUsedProcessors();
				}

				for (int p = 1; p <= processorsToAllocate; p++) {
					if(checkFixedOrder(task, p, schedule) && isBestParent(task, p, schedule)) {
						childrenSchedules.add(new TreeSchedule(task, p, schedule));
					}
				}
			}
		}

		return childrenSchedules;
	}
	
	/** As fixing the global order is dependent on many conditions. We also enforce local ordering of the tasks. */
	private boolean checkFixedOrder(TaskOld task, int processor, TreeSchedule schedule) {
		ProcessorAllocation alloc = schedule.getLastAllocationForProcessor(processor);
		
		if(alloc == null)
			return true;
		
		return computeOrder(task, alloc.task, schedule) != ForkJoinOrderResult.BEFORE; //If they were supposed to be [task], [alloc.task] then we are in the wrong order.
	}
	
	/** Computes whether a should be BEFORE, AFTER or UNSURE b */
	private ForkJoinOrderResult computeOrder(TaskOld a, TaskOld b, TreeSchedule schedule) {
		if(b.isParent(a)) {
			return ForkJoinOrderResult.BEFORE;
		}
		
		if(!a.isForkJoinCandidate() || !b.isForkJoinCandidate()) {
			return ForkJoinOrderResult.UNSURE;
		}
		
		DependencyOld aChild = getChild(a);
		DependencyOld bChild = getChild(b);
		
		DependencyOld aParent = getParent(a);
		DependencyOld bParent = getParent(b);
		
		ForkJoinOrderResult idOrder = a.getId() < b.getId() ? ForkJoinOrderResult.BEFORE : ForkJoinOrderResult.AFTER;
		
		ForkJoinOrderResult childOrder = ForkJoinOrderResult.UNSURE;
		
		if(aChild != null && bChild != null && !bChild.getTarget().equals(aChild.getTarget())) {
			return ForkJoinOrderResult.UNSURE; //Cannot say anything about the order.
		}

		int aChildComCost = childCommunicationCost(aChild);
		int bChildComCost = childCommunicationCost(bChild);
		
		if(aChildComCost < bChildComCost) {
			childOrder = ForkJoinOrderResult.AFTER;
		} else if(aChildComCost > bChildComCost) {
			childOrder = ForkJoinOrderResult.BEFORE;
		}
		
		ForkJoinOrderResult parentOrder = ForkJoinOrderResult.UNSURE;
		
		if(bParent != null && aParent != null) {
			ProcessorAllocation aParentAlloc = schedule.getAllocationFor(aParent.getSource());
			ProcessorAllocation bParentAlloc = schedule.getAllocationFor(bParent.getSource());

			if(aParentAlloc.processor != bParentAlloc.processor) {
				return ForkJoinOrderResult.UNSURE; //Cannot say anything about the order.
			}
		}
		
		int aParentDataReadyTime = dataReadyTime(aParent, schedule);
		int bParentDataReadyTime = dataReadyTime(bParent, schedule);
		
		if(aParentDataReadyTime < bParentDataReadyTime) {
			parentOrder = ForkJoinOrderResult.BEFORE;
		} else if(aParentDataReadyTime > bParentDataReadyTime) {
			parentOrder = ForkJoinOrderResult.AFTER;
		}
		
		if(parentOrder == ForkJoinOrderResult.UNSURE && childOrder == ForkJoinOrderResult.UNSURE) {
			return idOrder;
		}
		
		if(parentOrder == childOrder) {
			return parentOrder;
		}
		
		if(parentOrder == ForkJoinOrderResult.UNSURE) {
			return childOrder;
		}
		
		if(childOrder == ForkJoinOrderResult.UNSURE) {
			return parentOrder;
		}
		
		return ForkJoinOrderResult.UNSURE; //The orders conflicted, we cannot be definitive
	}
	
	//This would be used if we had time to add it. It is mostly implemented.
	/*private boolean checkHorizon(Task task, int processor, TreeSchedule schedule) {
		//If these two tasks can be swapped
		int positionOfNewTask = schedule.getNumberOfTasksOnProcessor(processor); //The 0 indexed position of the new task
		
		//Decrement position until index order has been reached. If at any of those positions a same of better schedule is found
		//return false, else return true;
		
		List<ProcessorAllocation> allocations = new ArrayList<>(schedule.getNumberOfTasksOnProcessor(processor)); //The list of tasks other than the scheduled task on this processor
		
		ProcessorAllocation alloc = schedule.getLastAllocationForProcessor(processor);
		while(alloc != null) {
			allocations.add(alloc);
		}
		
		int[] startTimes = new int[schedule.getGraph().getAll().size()];
		for(ProcessorAllocation t : allocations) {
			startTimes[t.task.getId()] = t.startTime;
		}
		
		//Move the new task to the position before the swapTarget
		swap:
		for(int swapTarget = allocations.size() - 1; swapTarget >= 0; swapTarget--) {
			Task target = allocations.get(swapTarget).task;
			if(task.isParent(target) || task.getId() > target.getId()) { //We can't swap, this ordering is valid.
				return true;
			}
			
			int taskStartTime = schedule.getDataReadyTime(task);
			if(swapTarget != 0) {
				taskStartTime = Math.max(allocations.get(swapTarget - 1).endTime, taskStartTime);
			}
			
			//Populate the start times for the other tasks
			//Only worry about tasks ahead of the swap, and the task itself
			for(int p = swapTarget; p < allocations.size(); p++) {
				int startTime = schedule.getDataReadyTime(allocations.get(p).task);
				if(p == swapTarget) {
					startTime = Math.max(startTime, taskStartTime + task.getCost());
				} else {
					startTime = Math.max(startTime, startTimes[allocations.get(p - 1).task.getId()] + allocations.get(p - 1).task.getCost());
				}
				
				startTimes[allocations.get(p).task.getId()] = startTime;
				
				if(startTime <= allocations.get(p).startTime) {
					return false; //This task is scheduled the same or better. Therefore all tasks after it will also be scheduled the same or better
				} else if(startTime > schedule.getRequiredBy(allocations.get(p).task)) {
					continue swap; //This item was scheduled too late. The swap may not be valid. Continue to next swap
				} else {
					//Check the comms to tasks that have not been scheduled yet.
					
				}
			}
		}
	}*/
	
	//Ensures that the parent schedule is the earliest schedule that can produce this child. This is our stateless duplicate removal.
	private boolean isBestParent(TaskOld task, int processor, TreeSchedule schedule) {
		//Look at each task on the top of the processor, if it is a later task then the schedule is not valid, provided removing it leaves a valid schedule.
		for(int p = 1; p <= schedule.getNumberOfUsedProcessors(); p++) {
			if(p != processor) {
				ProcessorAllocation alloc = schedule.getLastAllocationForProcessor(p);
				
				if(task.getId() > alloc.task.getId()) { //We should have scheduled alloc.task first anyway.
					continue;
				}
				
				if(p != schedule.getNumberOfUsedProcessors() && schedule.getNumberOfTasksOnProcessor(p) == 1) {
					//We can't remove this one, as it would leave a processor free in the middle of the run
					continue;
				}

				if(!schedule.isRemovable(alloc.task) || task.isParent(alloc.task) || schedule.wasFixed(alloc.task)) {
					continue; //This task is needed for child - parent reasons, or it was part of a fixed chain
				}

				return false;
			}
		}
		
		return true;
	}
	
	/** Returns null if there is no child */
	private DependencyOld getChild(TaskOld task) {
		assert task.isForkJoinCandidate();
		
		if(task.getChildren().size() == 0) {
			return null;
		}
		
		return task.getChildren().get(0);
	}
	
	private DependencyOld getParent(TaskOld task) {
		assert task.isForkJoinCandidate();
		
		if(task.getParents().size() == 0) {
			return null;
		}
		
		return task.getParents().get(0);
	}
	
	/** Returns -1 if the dependency is null */
	private int childCommunicationCost(DependencyOld dep) {
		if(dep == null)
			return -1;
		
		return dep.getCommunicationCost();
	}
	
	/** Returns -1 if the dependency is null. Else returns the time that the data produced by this parent is ready for the child. */
	private int dataReadyTime(DependencyOld dep, TreeSchedule schedule) {
		if(dep == null)
			return -1;
		
		return dep.getCommunicationCost() + schedule.getAllocationFor(dep.getSource()).endTime;
	}
}