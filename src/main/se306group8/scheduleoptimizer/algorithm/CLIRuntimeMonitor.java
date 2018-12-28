package se306group8.scheduleoptimizer.algorithm;

import java.math.BigDecimal;
import java.util.BitSet;

import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskOld;

/** A runtime monitor that outputs to the command line. */
public class CLIRuntimeMonitor implements RuntimeMonitor {
	
	// Variables to store time values
	private long duration;
	private long startTime;
	private long finishTime;
	
	private final int numProcessors;
	private final String seperator;
	private final String header;
	
	public CLIRuntimeMonitor() {
		this(0);
	}
	
	// Constructor 
	public CLIRuntimeMonitor(int numProcessors) {
		this.numProcessors = numProcessors;
		
		String processors = "t  ||";
		String line = "==";
		for (int i = 1; i <= numProcessors; i++) {
			processors += "\t" + i + "\t||";
			line += "================";
		}
		
		seperator = line;
		header = processors;
	}
	
	@Override
	public void start(String name, String graphName, int numberOfProcessors, int coresToUseForExecution) {
		// Start timing variable
		this.startTime = System.nanoTime();
		
		// Print out number of processors to stdout (the top row)
		logMessage(seperator);
		logMessage("TEAM: 'Team Name is Trivial and Left as a Exercise for the Reader'");
		logMessage("Welcome! You are running " + name + ".\nA valid schedule will be returned shortly.");
		logMessage(seperator);
		logMessage("Log:");
	}
	
	// Method that prints out final schedule to stdout
	public void printSchedule(Schedule schedule, int maxLength) {
		BitSet importantTimes = new BitSet();
		for(TaskOld t : schedule.getGraph().getAll()) {
			int startTime = schedule.getStartTime(t); //The time unit in which the first task occurs
			int endTime = startTime + t.getCost(); //The time unit after the end of the task
			
			importantTimes.set(startTime);
			importantTimes.set(endTime - 1);
		}
		
		//Plot some other times to fill up the list, if they are needed
		int remainingLength = maxLength - importantTimes.cardinality();
		
		if(remainingLength > 0) {
			int frequency = schedule.getTotalRuntime() / remainingLength;

			if(frequency == 0) {
				frequency = 1;
			}

			for(int t = 0; t < schedule.getTotalRuntime(); t += frequency) {
				importantTimes.set(t);
			}
		}
		
		// print each of the important times in order
		importantTimes.stream().forEachOrdered(time -> {
			printTasksAtTime(time, schedule);
		});
	}
	
	//Prints one line at time T
	private void printTasksAtTime(int time, Schedule schedule) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(String.format("%-3d||", time + 1));
		
		for(int p = 1; p <= numProcessors; p++) {
			builder.append(taskOnProcessorAtTime(time, p, schedule));
		}
		
		logMessage(builder.toString());
	}

	private String taskOnProcessorAtTime(int time, int p, Schedule schedule) {
		StringBuilder builder = new StringBuilder();
		
		for(TaskOld task : schedule.getGraph().getAll()) {
			int startTime = schedule.getStartTime(task); //The time unit in which the first task occurs
			int endTime = startTime + task.getCost(); //The time unit after the end of the task
			
			if(startTime <= time && time < endTime && schedule.getProcessorNumber(task) == p) {
				builder.append(formatTaskName(task.getName()));
				return builder.toString(); //There will only ever be one printable task
			}
		}
		
		builder.append(formatTaskName(""));
		
		return builder.toString();
	}

	/**
	 * Helper method to correctly format a task in the output.
	 * @param taskName Name of task to be printed 
	 * @return concatenated task name in correct format
	 */
	private String formatTaskName(String taskName) {
		if(taskName.length() > 5) {
			taskName = taskName.substring(0, 5);
		}
		
		return String.format("\t%s\t||", taskName);
	}
	
	
	@Override
	public void finish(Schedule optimalSchedule) {
		// Log and print out finished time
		this.finishTime = System.nanoTime();
		this.duration = this.finishTime - this.startTime;
		
		logMessage(seperator);
		logMessage(header);
		logMessage(seperator);
		
		printSchedule(optimalSchedule, 40);
		
		logMessage(seperator);
		logMessage("Finished! Valid schedule found in: " + new BigDecimal("" + this.duration/1000000000.0) + " seconds.");
		logMessage(seperator);
		logMessage("Statistics: ");
		// Print statistics about final schedule
		logMessage("Number of used processors: " + optimalSchedule.getNumberOfUsedProcessors() + "/" + this.numProcessors);
		logMessage("Total runtime of output schedule: " + optimalSchedule.getTotalRuntime());
		logMessage(seperator);
		logMessage("\n\n");
	}
	
	@Override
	public void logMessage(String message) {
		System.out.println(message);
		
	}

	@Override public void updateBestSchedule(TreeSchedule optimalSchedule) {  }
	@Override public void setSchedulesExplored(long number) {  }
	@Override public void setSchedulesInArray(long number) {  }
	@Override public void setScheduleInArrayStorageSize(int bytes) {  }
	@Override public void setSchedulesInQueue(long number) {  }
	@Override public void setScheduleInQueueStorageSize(int bytes) {  }
	@Override public void setSchedulesOnDisk(long number) {  }
	@Override public void setScheduleOnDiskStorageSize(int bytes) {  }

}
