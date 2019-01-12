package se306group8.scheduleoptimizer.schedule.queue;

import java.util.ArrayList;

//This class provides the priority queue to get the queueEntry numbers to get Schedules from the DeltaStore
class QueueEntryHeap {
	
	private class EntryHeuristicTuple {
		
		int queueEntry;
		int heuristic;
		byte length;
		
		public EntryHeuristicTuple(int queueEntry, int heuristic, byte length) {
			this.queueEntry = queueEntry;
			this.heuristic = heuristic;
			this.length = length;
		}


		public int compareTo(EntryHeuristicTuple o) {
			if (heuristic == o.heuristic) {
				return length - o.length;
			}else {
				return heuristic - o.heuristic;
			}
		}
		
	}

	private ArrayList<EntryHeuristicTuple> heap;
	private int numSchedules = 0;
	
	
	public QueueEntryHeap(){
		heap = new ArrayList<EntryHeuristicTuple>();
	}
	
	public void add(int queueEntry,int heuristic, int length) {
		EntryHeuristicTuple tuple = new EntryHeuristicTuple(queueEntry,heuristic,(byte)length);
		add(tuple);		
	}
	
	private void add(EntryHeuristicTuple tuple) {
		heap.add(numSchedules, tuple);
		numSchedules++;
		heapifyUp(numSchedules - 1);
	}

	public int peek() {
		if (numSchedules == 0) {
			return HeuristicPriorityQueue.NO_ENTRY;
		}
		return heap.get(0).queueEntry;
	}
	
	public int pop() {
		int value = peek();
		if (value != HeuristicPriorityQueue.NO_ENTRY) {
			heap.set(0, heap.get(numSchedules-1));
			numSchedules--;
			heapifyDown(0);
		}
		return value;
	}
	
	public int getSize() {
		return numSchedules;
	}
	
	//note this method removes entrys that are ABOVE the cutoff
	public void removeAbove(int cutoff) {
		QueueEntryHeap newHeap = new QueueEntryHeap();
		for (EntryHeuristicTuple tuple:heap) {
			if (tuple.heuristic<=cutoff) {
				newHeap.add(tuple);
			}
		}
		heap = newHeap.heap;
		numSchedules = newHeap.numSchedules;
	}
	
	private void heapifyUp(int index) {
		if (index == 0) {		
			return;
		}
		int parentIndex = parent(index);
		EntryHeuristicTuple entry = heap.get(index);
		EntryHeuristicTuple parentEntry = heap.get(parentIndex);
		
		if (entry.compareTo(parentEntry) < 0) {
			//swap
			heap.set(parentIndex, entry);
			heap.set(index, parentEntry);
			
			//heapify
			heapifyUp(parentIndex);
		}
	}
	
	private void heapifyDown(int index) {
		int child1Index = index * 2 + 1;
		int child2Index = index * 2 + 2;
		
		
		EntryHeuristicTuple lesserChild;
		EntryHeuristicTuple entry = heap.get(index);
		int lesserIndex;
		
		if (child1Index >= numSchedules) {
			return;
		}else if (child2Index >= numSchedules) {
			lesserChild = heap.get(child1Index);
			lesserIndex = child1Index;
		}else {
			
			EntryHeuristicTuple childEntry1 = heap.get(child1Index);
			EntryHeuristicTuple childEntry2 = heap.get(child2Index);
			
			
			if (childEntry1.compareTo(childEntry2) < 0) {
				lesserChild = childEntry1;
				lesserIndex = child1Index;
			}else {
				lesserChild = childEntry2;
				lesserIndex = child2Index;
			}
		}
		
		
		
		if (entry.compareTo(lesserChild) > 0) {
			//swap
			heap.set(lesserIndex,entry);
			heap.set(index,lesserChild);
			
			//heapify
			heapifyDown(lesserIndex);
		}
	}
	
	
	private int parent(int index) {
		//check even or odd
		if (index % 2 == 0) {
			return index/2 -1;
		}else {
			return index/2;
		}
	}
}
