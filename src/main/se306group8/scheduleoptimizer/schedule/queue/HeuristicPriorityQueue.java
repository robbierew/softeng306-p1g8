package se306group8.scheduleoptimizer.schedule.queue;

import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;

public class HeuristicPriorityQueue<T extends QueueSchedule> {
	
	static final int  NO_ENTRY = -1;
	
	static final int INDEX_MASK = 0x3FFFF; //18 1s or 262,143 per block
	static final int BLOCK_SHIFT = 18; //number of 1s in INDEX_MASK
	
	//block_number = queue_entry >> BLOCK_SHIFT
	//block_index = queue_entry & INDEX_MASK
	
	private QueueEntryHeap heap = new QueueEntryHeap();
	private DeltaScheduleStore<T> store;
	
	public HeuristicPriorityQueue(TreeScheduleBuilder<T> builder, ProblemStatement statement) {
		store = new CachingDeltaScheduleStore<T>(builder,statement);
	}
	
	public T pop() {
		T pop = store.getScheduleByStoreEntry(heap.pop());
		return pop;
	}
	
	public T peek() {
		return store.getScheduleByStoreEntry(heap.peek());
	}
	
	public void put(T schedule) {
		heap.add(store.allocate(schedule), schedule.getHeuristicValue(),schedule.getNumAllocatedTasks() );
		if (schedule.isComplete()) {
			heap.removeAbove(schedule.getRuntime());
			store.removeAbove(schedule.getRuntime());
		}
	}
}
