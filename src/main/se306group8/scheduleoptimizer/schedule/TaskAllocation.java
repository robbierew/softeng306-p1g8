package se306group8.scheduleoptimizer.schedule;

import se306group8.scheduleoptimizer.taskgraph.Task;

public class TaskAllocation {
	private Task task;
	private int processor;
	private int startTime;
	private int endTime;
	
	public TaskAllocation(Task task, int startTime, int processor) {
		this.task = task;
		this.startTime = startTime;
		this.processor = processor;
		
		endTime = startTime + task.getComputeCost();
	}

	public Task getTask() {
		return task;
	}

	public int getProcessor() {
		return processor;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}
	
}
