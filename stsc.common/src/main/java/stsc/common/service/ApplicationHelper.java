package stsc.common.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class ApplicationHelper {

	public interface StopableApp {

		public void start() throws Exception;

		public void stop() throws Exception;

		public void log(Level logLevel, String message);
	}

	public static void createHelper(final StopableApp app) throws Exception {
		final AtomicBoolean finished = new AtomicBoolean(false);
		final Thread waiter = new Thread(new Runnable() {
			@Override
			public void run() {
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
				while (!checkReadExitLine(app, bufferedReader)) {
					try {
						Thread.sleep(230);
					} catch (Exception e) {
					}
					if (finished.get()) {
						break;
					}
				}
			}
		});
		waiter.start();
		app.start();
		finished.set(true);
		waiter.join();
	}

	private static boolean checkReadExitLine(StopableApp app, BufferedReader bufferedReader) {
		try {
			if (bufferedReader.ready()) {
				final String s = bufferedReader.readLine();
				if (s.equals("e")) {
					app.stop();
					return true;
				}
			}
		} catch (Exception e) {
			app.log(Level.SEVERE, "checkReadExitLine(...)" + e.getMessage());
		}
		return false;
	}

}
