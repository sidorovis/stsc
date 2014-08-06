package stsc.performance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.joda.time.LocalDate;

import stsc.common.TimeTracker;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

final class MemoryConsumption {

	static private String getDateRepresentation(LocalDate date) {
		int day = date.getDayOfMonth();
		int month = date.getMonthOfYear();
		int year = date.getYear();
		return String.format("%02d-%02d-%04d", day, month, year);
	}

	public MemoryConsumption(long N, boolean testMemory) throws IOException {
		final StockStorage stockStorage = StockStorageSingleton.getInstance();
		final List<String> elements = Arrays.asList(new String[] { "open", "high", "low", "close", "value", "open", "high", "low", "close" });
		final LocalDate startOfPeriod = new LocalDate(1970, 1, 1);
		final LocalDate endOfPeriod = new LocalDate(2014, 1, 1);
		final TimeTracker tt = new TimeTracker();
		final SimulatorSettingsGridFactory factory = SimulatorSettingsGenerator.getGridFactory(false, stockStorage, elements,
				getDateRepresentation(startOfPeriod), getDateRepresentation(endOfPeriod));
		final long size = factory.size();
		final SimulatorSettingsGridList list = factory.getList();
		final Iterator<SimulatorSettings> iterator = list.iterator();
		final ArrayList<SimulatorSettings> settings = new ArrayList<SimulatorSettings>((int) Math.min(100000, N));
		for (long i = 0; i < N; ++i) {
			if (!iterator.hasNext()) {
				break;
			}
			if (!testMemory && (i % 1000000 == 0)) {
				System.out.println(" " + i + " " + TimeTracker.lengthInSeconds(tt.lengthNonStop()));
			}
			final SimulatorSettings setting = iterator.next();
			if (testMemory)
				settings.add(setting);
		}
		if (testMemory) {
			System.out.println("Time in secs: (" + N + "/" + size + ") " + TimeTracker.lengthInSeconds(tt.finish()));
			System.in.read();
		} else {
			System.out.println(" " + N + " " + TimeTracker.lengthInSeconds(tt.lengthNonStop()));
			System.out.println("Time in secs: (" + N + "/" + size + ") " + TimeTracker.lengthInSeconds(tt.finish()));
		}
	}

	public static void main(String[] args) throws IOException {
		new MemoryConsumption(100000L, true);
	}
}
