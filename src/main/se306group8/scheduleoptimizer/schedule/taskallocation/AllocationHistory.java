package se306group8.scheduleoptimizer.schedule.taskallocation;

import java.util.Arrays;
import java.util.List;

import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class AllocationHistory {

	ProblemStatement statement;
	TaskAllocation[] allocations;
	byte numAllocations;

	byte[][] processorAllocations;
	byte[] numAllocsOnProcessor;

	byte[] taskAlloc;

	public AllocationHistory(ProblemStatement ps) {
		statement = ps;
		byte numTasks = ps.getNumTasks();
		byte numProcessors = ps.getNumProcessors();

		allocations = new TaskAllocation[numTasks];
		processorAllocations = new byte[numProcessors][numTasks];
		numAllocsOnProcessor = new byte[numProcessors];

		taskAlloc = new byte[numTasks];
		Arrays.fill(taskAlloc, (byte) -1);

	}

	public AllocationHistory(AllocationHistory clone) {
		allocations = clone.allocations.clone();
		numAllocations = clone.numAllocations;
		taskAlloc = clone.taskAlloc.clone();
		numAllocsOnProcessor = clone.numAllocsOnProcessor.clone();
		statement = clone.statement;

		byte numProcessors = (byte) numAllocsOnProcessor.length;
		processorAllocations = new byte[numProcessors][];

		for (byte i = 0; i < numProcessors; i++) {
			processorAllocations[i] = clone.processorAllocations[i].clone();
		}

	}

	// creates branch of this allocationhistory with this additional allocation
	public AllocationHistory fork(TaskAllocation alloc) {
		AllocationHistory copy = new AllocationHistory(this);
		copy.addAllocation(alloc);
		return copy;
	}

	public AllocationHistory previous() {
		AllocationHistory parent = new AllocationHistory(this);
		TaskAllocation last = getLastAllocation();

		// we only have to reduce the num of allocations to hide last allocation
		parent.numAllocations--;
		parent.taskAlloc[last.getTask().getID()] = -1;

		// we only have to reduce the num of allocations to hide last allocation
		parent.numAllocsOnProcessor[last.getProcessor() - 1]--;

		return parent;
	}

	public List<TaskAllocation> getAllocationsForProcessorAsList(int processor) {
		return Arrays.asList(getAllocationsForProcessorAsArray(processor));
	}

	public TaskAllocation[] getAllocationsForProcessorAsArray(int processor) {
		processor--;
		byte[] indexs = processorAllocations[processor];
		return findAllocationsFromIndexs(indexs, numAllocsOnProcessor[processor]);
	}

	public List<TaskAllocation> getAllocationsAsList() {
		return Arrays.asList(getAllocationsAsArray());
	}

	public TaskAllocation[] getAllocationsAsArray() {
		return Arrays.copyOf(allocations, numAllocations);
	}

	public TaskAllocation getAllocationForTask(Task task) {
		return allocations[taskAlloc[task.getID()]];
	}

	public TaskAllocation getLastAllocation() {
		if (numAllocations == 0) {
			return null;
		}
		return allocations[numAllocations - 1];
	}

	public TaskAllocation getLastAllocationForProcessor(int processor) {
		processor--;
		if (numAllocsOnProcessor[processor] == 0) {
			return null;
		}
		return getNthAllocationForProcessor(processor + 1,numAllocsOnProcessor[processor]);
	}
	
	public TaskAllocation getFirstAllocationForProcessor(int processor) {
		processor--;
		if (numAllocsOnProcessor[processor] == 0) {
			return null;
		}
		return getNthAllocationForProcessor(processor + 1,1);
	}
	
	
	private TaskAllocation getNthAllocationForProcessor(int processor, int n) {
		processor--;
		n--;
		byte index = processorAllocations[processor][n];
		return allocations[index];
	}

	// if this returns null then you have root
	public Task getLastAllocatedTask() {
		if (getLastAllocation() == null) {
			return null;
		}
		return getLastAllocation().getTask();
	}

	public ProblemStatement getStatement() {
		return statement;
	}

	// package private as this class should be immutable use builder class
	// to access this method
	void addAllocation(TaskAllocation alloc) {
		try {
			int processor = alloc.getProcessor() - 1;
			byte taskID = alloc.getTask().getID();

			allocations[numAllocations] = alloc;
			processorAllocations[processor][numAllocsOnProcessor[processor]] = numAllocations;

			taskAlloc[taskID] = numAllocations;

			numAllocations++;
			numAllocsOnProcessor[processor]++;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException("Too many allocations or task not from correct TaskGraph");
		}

	}

	private TaskAllocation[] findAllocationsFromIndexs(byte[] indexes, byte length) {
		TaskAllocation[] result = new TaskAllocation[length];
		for (int i = 0; i < length; i++) {
			result[i] = allocations[indexes[i]];
		}
		return result;
	}

}
