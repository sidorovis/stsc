package stsc.common.service;

import java.util.logging.Level;

public interface StopableApp {

	public void start() throws Exception;

	public void stop() throws Exception;

	public void log(Level logLevel, String message);
}