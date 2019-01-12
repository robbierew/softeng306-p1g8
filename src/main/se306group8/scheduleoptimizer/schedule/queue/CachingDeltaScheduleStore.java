package se306group8.scheduleoptimizer.schedule.queue;

import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;

public class CachingDeltaScheduleStore<T extends QueueSchedule> extends DeltaScheduleStore<T>{

	private final int  CACHE_SIZE = 1024;
	QueueSchedule[] cache = new QueueSchedule[CACHE_SIZE];
	
	public CachingDeltaScheduleStore(TreeScheduleBuilder<T> builder, ProblemStatement statement) {
		super(builder, statement);
	}

	
	@Override
	public int allocate(QueueSchedule schedule) {
		int entry = super.allocate(schedule);
		cache[entry % CACHE_SIZE] = schedule;
		return entry;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getScheduleByStoreEntry(int entry) {
		if (entry != HeuristicPriorityQueue.NO_ENTRY) {
			QueueSchedule cacheResult = cache[entry % CACHE_SIZE];
			if (cacheResult != null && cacheResult.getStoreEntry(this) == entry) {
				return (T) cacheResult;
			}
		}
		return super.getScheduleByStoreEntry(entry);
	}
}
