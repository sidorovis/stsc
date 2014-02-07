package stsc.MarketDataDownloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Download Market Data from Yahoo API.
 * 
 */
public final class App {
	MarketDataContext marketDataContext = new MarketDataContext();
	static final int downloadThreadSize = 2;
	static final int stockNameMaxLength = 5;

	void generateNextElement(char[] generatedText, int currentIndex, int size) {
		for (char c = 'a'; c <= 'z'; ++c) {
			generatedText[currentIndex] = c;
			if (currentIndex == size - 1) {
				String newTask = new String(generatedText);
				marketDataContext.addTask(newTask);
			} else {
				generateNextElement(generatedText, currentIndex + 1, size);
			}
		}
	}

	void generateTasks(int taskLength) {
		char[] generatedText = new char[taskLength];
		generateNextElement(generatedText, 0, taskLength);
	}

	App() throws InterruptedException, IOException {
		DownloadThread downloadThread = new DownloadThread(marketDataContext);

		for (int i = 1; i <= stockNameMaxLength; ++i)
			generateTasks(i);

		System.out.println("Tasks size: " + marketDataContext.taskQueueSize());

		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < downloadThreadSize; ++i) {
			Thread newThread = new Thread(downloadThread);
			threads.add(newThread);
			newThread.start();
		}

		for (Thread thread : threads) {
			thread.join();
		}

	}

	public static void main(String[] args) {
		try {
			new App();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
