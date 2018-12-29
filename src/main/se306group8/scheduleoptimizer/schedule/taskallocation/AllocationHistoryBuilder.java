package se306group8.scheduleoptimizer.schedule.taskallocation;

import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;

public class AllocationHistoryBuilder {
	private AllocationHistory allocHistory;
	
	public AllocationHistoryBuilder(ProblemStatement statement) {
		allocHistory = new AllocationHistory(statement);
	}
	
	public AllocationHistoryBuilder addAllocation(TaskAllocation alloc) {
		allocHistory.addAllocation(alloc);
		return this;
	}
	
	public AllocationHistory build() {
		return new AllocationHistory(allocHistory);
	}
}
