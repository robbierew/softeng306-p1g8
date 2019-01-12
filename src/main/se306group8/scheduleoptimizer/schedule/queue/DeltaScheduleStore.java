package se306group8.scheduleoptimizer.schedule.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import se306group8.scheduleoptimizer.schedule.TreeScheduleBuilder;
import se306group8.scheduleoptimizer.taskgraph.ProblemStatement;

class DeltaScheduleStore<T extends QueueSchedule> {
	private Map<Integer,ArrayList<DeltaStoreBlock<T>>> heuristicBlockMap;
	private ArrayList<DeltaStoreBlock<T>> blockNumberMap;
	private Stack<Integer> reuseableBlockNumbers;
	private TreeScheduleBuilder<T> builder;
	private ProblemStatement statement;
	
	public DeltaScheduleStore(TreeScheduleBuilder<T> builder, ProblemStatement statement) {
		heuristicBlockMap = new HashMap<Integer,ArrayList<DeltaStoreBlock<T>>>();
		blockNumberMap = new ArrayList<DeltaStoreBlock<T>>();
		reuseableBlockNumbers = new Stack<Integer>();
		this.builder = builder;
		this.statement =statement;
	}
	
	
	public TreeScheduleBuilder<T> getBuilder(){
		return builder;
	}
	
	public int allocate(QueueSchedule schedule) {
		
		if (schedule.getStoreEntry(this) == HeuristicPriorityQueue.NO_ENTRY) {
			int heuristic = schedule.getHeuristicValue();
			DeltaStoreBlock<T> block = getAvailableStoreBlock(heuristic);
			schedule.setStoreEntry(block.allocate(schedule), this);;
		}
		
		return schedule.getStoreEntry(this);
	}
	
	public T getScheduleByStoreEntry(int entry) {
		
		if (entry == HeuristicPriorityQueue.NO_ENTRY) {
			return null;
		}
		
		DeltaStoreBlock<T> block = getStoreBlockByEntry(entry);
		T schedule =  block.getScheduleByIndex(entry & HeuristicPriorityQueue.INDEX_MASK);
		
		//newly created ones does have an entry
		schedule.setStoreEntry(entry, this);
		return schedule;
	}
	
	//note this method removes blocks that are ABOVE the cutoff
	public void removeAbove(int heuristic) {
		Set<Entry<Integer,ArrayList<DeltaStoreBlock<T>>>> entrySet = heuristicBlockMap.entrySet();
		for (Entry<Integer,ArrayList<DeltaStoreBlock<T>>> buckets: entrySet) {
			if (buckets.getKey() > heuristic) {
				ArrayList<DeltaStoreBlock<T>> blocks = buckets.getValue();
				
				for (DeltaStoreBlock<T> block:blocks) {
					
					//clear from the number map
					blockNumberMap.set(block.getBlockNumber(), null);
					reuseableBlockNumbers.add(block.getBlockNumber());
				}
				
				//the entry set is backed my the map so removing from this set removes from the map
				entrySet.remove(buckets);
			}
		}
	}
	
	//blocks can be destroyed when heuristics are reduced meaning block number can be reused
	private int nextAvailableBlockNumber() {
		if (reuseableBlockNumbers.isEmpty()) {
			blockNumberMap.add(null);//gives room for set
			return blockNumberMap.size() - 1;
		}else {
			return reuseableBlockNumbers.pop();
		}
	}
	
	private DeltaStoreBlock<T> getAvailableStoreBlock(int heuristic) {
		ArrayList<DeltaStoreBlock<T>> blocks = getBlocksByHeuristic(heuristic);
		
		if (blocks.size() == 0) {
			return createNewBlock(heuristic);
		}
		
		DeltaStoreBlock<T> block = blocks.get(blocks.size()-1);
		
		if (block.isFull()) {
			return createNewBlock(heuristic);
		}
		
		return block;
	}

	private DeltaStoreBlock<T> createNewBlock(int heuristic) {
		int blockNumber = nextAvailableBlockNumber();
		DeltaStoreBlock<T> block = new DeltaStoreBlock<T>(this,statement,blockNumber,heuristic);
		getBlocksByHeuristic(heuristic).add(block);
		blockNumberMap.set(blockNumber, block);
		return block;
	}
	
	private ArrayList<DeltaStoreBlock<T>> getBlocksByHeuristic(int heuristic){
		ArrayList<DeltaStoreBlock<T>> blocks = heuristicBlockMap.get(heuristic);
		if (blocks == null) {
			blocks = new ArrayList<DeltaStoreBlock<T>>();
			heuristicBlockMap.put(heuristic, blocks);
		}
		return blocks;
	}

	DeltaStoreBlock<T> getStoreBlockByEntry(int storeEntry){
		int blockIndex = storeEntry >> HeuristicPriorityQueue.BLOCK_SHIFT;
		return blockNumberMap.get(blockIndex);
	}

	
}
