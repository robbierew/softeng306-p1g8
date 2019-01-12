package se306group8.scheduleoptimizer.schedule.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HeapTests {
	
	QueueEntryHeap heap;
	
	@BeforeEach
	public void createHeap() {
		heap = new QueueEntryHeap();
		heap.add(1, 2,2);
		heap.add(2, 2,3);
		heap.add(3, 1,2);
		heap.add(4, 4,2);
	}
	
	@Test
	public void doublePeak() {
		assertEquals(3,heap.peek());
		assertEquals(3,heap.peek());
	}
	
	@Test
	public void pop() {
		assertEquals(4,heap.getSize());
		assertEquals(3,heap.pop());
		assertEquals(3,heap.getSize());
		assertEquals(1,heap.pop());
		assertEquals(2,heap.getSize());
		assertEquals(2,heap.pop());
		assertEquals(1,heap.getSize());
		assertEquals(4,heap.pop());
		assertEquals(0,heap.getSize());
		assertEquals(HeuristicPriorityQueue.NO_ENTRY,heap.pop());
		assertEquals(0,heap.getSize());
	}
	
	@Test
	public void prune() {
		heap.removeAbove(2);
		assertEquals(3,heap.getSize());
		
	}
}
