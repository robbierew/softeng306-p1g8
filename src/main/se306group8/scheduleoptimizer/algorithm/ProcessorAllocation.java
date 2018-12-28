package se306group8.scheduleoptimizer.algorithm;

import java.util.Objects;

import se306group8.scheduleoptimizer.taskgraph.TaskOld;

/** This class represents an allocation for a single task. It is used as an input to one of the constructors,
 * and is used internally within most of the algorithm systems. */
public final class ProcessorAllocation {
	public final int startTime;
	public final int processor;
	public final int endTime;
	
	public final TaskOld task;
	public final ProcessorAllocation previousAlloc;
	
	public ProcessorAllocation(TaskOld task, int startTime, int processor, ProcessorAllocation previous) {
		this.startTime = startTime;
		this.endTime = startTime + task.getCost();
		this.processor = processor;
		this.task = task;
		this.previousAlloc = previous;
	}

	public ProcessorAllocation(TaskOld task, int startTime, int processor) {
		this(task, startTime, processor, null);
	}

	@Override
	public String toString() {
		return "P[" + processor + "](" + startTime + ", " + endTime + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ProcessorAllocation)) {
			return false;
		}
		
		ProcessorAllocation alloc = (ProcessorAllocation) obj;
		
		return alloc.startTime == startTime && alloc.task.equals(task) && alloc.processor == processor;
	}
	
	public int hashCode() {
		return Objects.hash(startTime, processor, task);
	};
}