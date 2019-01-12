package se306group8.scheduleoptimizer.schedule.queue;


import se306group8.scheduleoptimizer.schedule.taskallocation.AllocationHistoryBuilder;
import se306group8.scheduleoptimizer.schedule.taskallocation.TaskAllocation;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class DeltaStoreBlock<T extends QueueSchedule> {
	private int nextIndex;
	private int blockNumber;
	private int heuristic;
	private DeltaScheduleStore<T> store;
	private ProblemStatement statement;
	
	
	int[] parentQueueEntry = new int[HeuristicPriorityQueue.INDEX_MASK+1];
	byte[] task = new byte[HeuristicPriorityQueue.INDEX_MASK+1];
	byte[] processor = new byte[HeuristicPriorityQueue.INDEX_MASK+1];
	int[] starttime = new int[HeuristicPriorityQueue.INDEX_MASK+1];
	
	public DeltaStoreBlock(DeltaScheduleStore<T> store, ProblemStatement statement, int blockNumber, int heuristic) {
		this.blockNumber = blockNumber;
		this.heuristic = heuristic;
		this.store = store;
		this.statement = statement;
	}

	public boolean isFull() {
		return nextIndex > HeuristicPriorityQueue.INDEX_MASK;
	}

	public int getHeuristic() {
		return heuristic;
	}
	
	public int getBlockNumber() {
		return blockNumber;
	}
	
	public int allocate(QueueSchedule schedule) {
		assert(schedule.getHeuristicValue() == heuristic);
		int index = nextIndex;
		nextIndex++;
		
		if (schedule.isEmpty()) {
			parentQueueEntry[index] = HeuristicPriorityQueue.NO_ENTRY;
		}else {
			TaskAllocation alloc = schedule.getLastAllocation();
			task[index] = alloc.getTask().getID();
			processor[index] = alloc.getProcessor();
			starttime[index] = alloc.getStartTime();
			QueueSchedule parent = schedule.getParent();

			//The store will check if the parent is already allocated 
			//parent needs to be allocated to ensure it has a store entry
			store.allocate(parent);
			
			parentQueueEntry[index] = parent.getStoreEntry(store);
		}
		
		
		
		return (blockNumber << HeuristicPriorityQueue.BLOCK_SHIFT) + index;
	}

	public T getScheduleByIndex(int index) {
		AllocationHistoryBuilder allocHistory = new AllocationHistoryBuilder(statement);
		rebuildHistory(allocHistory,index);
		return store.getBuilder().buildFromHistory(allocHistory.build());
	}

	private void rebuildHistory(AllocationHistoryBuilder allocHistory, int index) {
		
		int parentEntry = parentQueueEntry[index];
		if (parentEntry != HeuristicPriorityQueue.NO_ENTRY) {
			Task task = statement.getTaskByID(this.task[index]);
			TaskAllocation alloc = new TaskAllocation(task,starttime[index],processor[index]);
			DeltaStoreBlock<T> parentBlock = store.getStoreBlockByEntry(parentEntry);
			parentBlock.rebuildHistory(allocHistory,parentQueueEntry[index] & HeuristicPriorityQueue.INDEX_MASK);
			allocHistory.addAllocation(alloc);
		}	
		
	}
}
