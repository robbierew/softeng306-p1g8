package se306group8.scheduleoptimizer.algorithm.pruner;

import java.util.List;

import se306group8.scheduleoptimizer.schedule.Schedule;

@FunctionalInterface
public interface Pruner<T extends Schedule> {
	public List<T> prune(List<T> schedules);
}
