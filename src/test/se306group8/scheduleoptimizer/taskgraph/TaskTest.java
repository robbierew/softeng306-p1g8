package se306group8.scheduleoptimizer.taskgraph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIsNotIndependant() {
		TaskGraphOld a = TestGraphUtilsOld.buildTestGraphA();
		for(TaskOld t : a.getAll()) {
			assertFalse(t.isIndependant());
		}
	}
	
	@Test
	void testIsIndependant() {
		TaskGraphOld b = TestGraphUtilsOld.buildTestGraphB();
		for(TaskOld t : b.getAll()) {
			assertTrue(t.isIndependant());
		}
	}
}
