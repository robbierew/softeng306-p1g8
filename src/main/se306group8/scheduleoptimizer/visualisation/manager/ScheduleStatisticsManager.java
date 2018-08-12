package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class ScheduleStatisticsManager {

	private Label schedulesExploredLabel;
	private Label schedulesInArrayLabel;
	private Label schedulesInQueueLabel;
	private Label schedulesOnDiskLabel;
	private Label schedulesPerSecondLabel;

	private double schedulesPerSecond;
	private int lastScheduleCount;
	private long lastScheduleCountSampleTime;
	private final double schedulesPerSecondAdjustmentFactor; // Applied for smoothing.

	public ScheduleStatisticsManager(
			Label schedulesExploredLabel,
			Label schedulesInArrayLabel,
			Label schedulesInQueueLabel,
			Label schedulesOnDiskLabel,
			Label schedulesPerSecondLabel,
			double schedulesPerSecondAdjustmentFactor
	) {

		this.schedulesExploredLabel = schedulesExploredLabel;
		this.schedulesInArrayLabel = schedulesInArrayLabel;
		this.schedulesInQueueLabel = schedulesInQueueLabel;
		this.schedulesOnDiskLabel = schedulesOnDiskLabel;
		this.schedulesPerSecondLabel = schedulesPerSecondLabel;

		this.schedulesPerSecondAdjustmentFactor = schedulesPerSecondAdjustmentFactor;

		// Setup schedules per second data.
		lastScheduleCount = 0;
		schedulesPerSecond = 0;
		lastScheduleCountSampleTime = System.currentTimeMillis();

	}

	public void update(int schedulesExplored, int schedulesInArray, int schedulesInQueue, int schedulesOnDisk) {

		long currentSampleTime = System.currentTimeMillis();
		int newScheduleCount = schedulesExplored - lastScheduleCount;
		long timeSinceLastSampleTime = currentSampleTime - lastScheduleCountSampleTime;

		double sampleSchedulesPerSecond = 1_000.0 * newScheduleCount / timeSinceLastSampleTime;

		schedulesPerSecond = (1-schedulesPerSecondAdjustmentFactor)*schedulesPerSecond
				+ schedulesPerSecondAdjustmentFactor*sampleSchedulesPerSecond;

		lastScheduleCountSampleTime = currentSampleTime;
		lastScheduleCount = schedulesExplored;

		Platform.runLater(() -> {
			schedulesExploredLabel.textProperty().setValue(""+schedulesExplored);
			schedulesInArrayLabel.textProperty().setValue(""+schedulesInArray);
			schedulesInQueueLabel.textProperty().setValue(""+schedulesInQueue);
			schedulesOnDiskLabel.textProperty().setValue(""+schedulesOnDisk);
			schedulesPerSecondLabel.textProperty().setValue(""+(int)schedulesPerSecond);
		});
	}

}