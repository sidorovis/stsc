package stsc.frontend.zozka.components;

import java.util.Queue;

import javafx.concurrent.Task;
import stsc.yahoo.YahooFileStockStorage;

public class ProgressBarTask extends Task<Integer> {

	private final Queue<String> queue;
	private final int initialSize;

	public ProgressBarTask(YahooFileStockStorage stockStorage) {
		queue = stockStorage.getTasks();
		initialSize = queue.size();
	}

	@Override
	protected Integer call() throws Exception {
		int iterations = initialSize - queue.size();
		while (!queue.isEmpty()) {
			updateProgress(iterations, initialSize);
			iterations = initialSize - queue.size();
			Thread.sleep(300);
		}
		return iterations;
	}
}