package stsc.performance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.joda.time.LocalDate;

import stsc.common.TimeTracker;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticFactory;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

final class MemoryConsumption {

	private static boolean warmUp = true;
	private static boolean onlyReport = true;

	static private String getDateRepresentation(LocalDate date) {
		int day = date.getDayOfMonth();
		int month = date.getMonthOfYear();
		int year = date.getYear();
		return String.format("%02d-%02d-%04d", day, month, year);
	}

	public MemoryConsumption(SearcherType type, long N, boolean testMemory) throws IOException, BadAlgorithmException {
		if (type == SearcherType.GRID_SEARCHER)
			gridConsumptionCheck(N, testMemory);
		else
			geneticRandomConsumptionCheck(N, testMemory);
	}

	private static void gridConsumptionCheck(long N, boolean testMemory) throws IOException {
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
			if (!testMemory && (i % 1000000 == 0) && !onlyReport) {
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
			if (!onlyReport)
				System.out.println(" " + N + " " + TimeTracker.lengthInSeconds(tt.lengthNonStop()));
			if (!warmUp)
				System.out.print(TimeTracker.lengthInSeconds(tt.finish()) + " ");
		}
	}

	static private void geneticRandomConsumptionCheck(long N, boolean testMemory) throws IOException, BadAlgorithmException {
		final StockStorage stockStorage = StockStorageSingleton.getInstance();
		final List<String> elements = Arrays.asList(new String[] { "open", "high", "low", "close", "value", "open", "high", "low", "close" });
		final LocalDate startOfPeriod = new LocalDate(1970, 1, 1);
		final LocalDate endOfPeriod = new LocalDate(2014, 1, 1);
		final TimeTracker tt = new TimeTracker();
		final SimulatorSettingsGeneticFactory factory = SimulatorSettingsGenerator.getGeneticFactory(false, stockStorage, elements,
				getDateRepresentation(startOfPeriod), getDateRepresentation(endOfPeriod));
		final long size = factory.size();
		final SimulatorSettingsGeneticList list = factory.getList();
		final ArrayList<SimulatorSettings> settings = new ArrayList<SimulatorSettings>((int) Math.min(100000, N));
		for (long i = 0; i < N; ++i) {
			if (!testMemory && (i % 1000000 == 0) && !onlyReport) {
				System.out.println(" " + i + " " + TimeTracker.lengthInSeconds(tt.lengthNonStop()));
			}
			final SimulatorSettings setting = list.generateRandom();
			if (testMemory)
				settings.add(setting);
		}
		if (testMemory) {
			System.out.println("Time in secs: (" + N + "/" + size + ") " + TimeTracker.lengthInSeconds(tt.finish()));
			System.in.read();
		} else {
			if (!onlyReport)
				System.out.println(" " + N + " " + TimeTracker.lengthInSeconds(tt.lengthNonStop()));
			if (!warmUp)
				System.out.print(TimeTracker.lengthInSeconds(tt.finish()) + " ");
		}
	}

	static private void geneticMergeConsumptionCheck(long N, boolean testMemory) throws IOException, BadAlgorithmException {
		final StockStorage stockStorage = StockStorageSingleton.getInstance();
		final List<String> elements = Arrays.asList(new String[] { "open", "high", "low", "close", "value", "open", "high", "low", "close" });
		final LocalDate startOfPeriod = new LocalDate(1970, 1, 1);
		final LocalDate endOfPeriod = new LocalDate(2014, 1, 1);
		final TimeTracker tt = new TimeTracker();
		final SimulatorSettingsGeneticFactory factory = SimulatorSettingsGenerator.getGeneticFactory(false, stockStorage, elements,
				getDateRepresentation(startOfPeriod), getDateRepresentation(endOfPeriod));
		final long size = factory.size();
		final SimulatorSettingsGeneticList list = factory.getList();
		SimulatorSettings left = list.generateRandom();
		SimulatorSettings right = list.generateRandom();
		final ArrayList<SimulatorSettings> settings = new ArrayList<SimulatorSettings>((int) Math.min(100000, N));
		for (long i = 0; i < N; ++i) {
			if (!testMemory && (i % 1000000 == 0) && !onlyReport) {
				System.out.println(" " + i + " " + TimeTracker.lengthInSeconds(tt.lengthNonStop()));
			}
			final SimulatorSettings setting = list.merge(left, right);
			if (testMemory)
				settings.add(setting);
		}
		if (testMemory) {
			System.out.println("Time in secs: (" + N + "/" + size + ") " + TimeTracker.lengthInSeconds(tt.finish()));
			System.in.read();
		} else {
			if (!onlyReport)
				System.out.println(" " + N + " " + TimeTracker.lengthInSeconds(tt.lengthNonStop()));
			if (!warmUp)
				System.out.print(TimeTracker.lengthInSeconds(tt.finish()) + " ");
		}
	}

	static private void geneticMutateConsumptionCheck(long N, boolean testMemory) throws IOException, BadAlgorithmException {
		final StockStorage stockStorage = StockStorageSingleton.getInstance();
		final List<String> elements = Arrays.asList(new String[] { "open", "high", "low", "close", "value", "open", "high", "low", "close" });
		final LocalDate startOfPeriod = new LocalDate(1970, 1, 1);
		final LocalDate endOfPeriod = new LocalDate(2014, 1, 1);
		final TimeTracker tt = new TimeTracker();
		final SimulatorSettingsGeneticFactory factory = SimulatorSettingsGenerator.getGeneticFactory(false, stockStorage, elements,
				getDateRepresentation(startOfPeriod), getDateRepresentation(endOfPeriod));
		final long size = factory.size();
		final SimulatorSettingsGeneticList list = factory.getList();
		SimulatorSettings left = list.generateRandom();
		final ArrayList<SimulatorSettings> settings = new ArrayList<SimulatorSettings>((int) Math.min(100000, N));
		for (long i = 0; i < N; ++i) {
			if (!testMemory && (i % 1000000 == 0) && !onlyReport) {
				System.out.println(" " + i + " " + TimeTracker.lengthInSeconds(tt.lengthNonStop()));
			}
			final SimulatorSettings setting = list.mutate(left);
			if (testMemory)
				settings.add(setting);
		}
		if (testMemory) {
			System.out.println("Time in secs: (" + N + "/" + size + ") " + TimeTracker.lengthInSeconds(tt.finish()));
			System.in.read();
		} else {
			if (!onlyReport)
				System.out.println(" " + N + " " + TimeTracker.lengthInSeconds(tt.lengthNonStop()));
			if (!warmUp)
				System.out.println(TimeTracker.lengthInSeconds(tt.finish()) + " ");
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			final long N = 500000;
			final long lastN = N * 4;
			final long stepN = N / 10;
			new MemoryConsumption(SearcherType.GRID_SEARCHER, 2 * N, false);
			new MemoryConsumption(SearcherType.GENETIC_SEARCHER, 2 * N, false);
			warmUp = false;
			System.out.print(" ");
			for (long i = N; i <= lastN; i += stepN) {
				System.out.print(i + " ");
			}
			System.out.println();
			System.out.print("Grid ");
			for (long i = N; i <= lastN; i += stepN) {
				gridConsumptionCheck(i, false);
			}
			System.out.println();
			System.out.print("Random ");
			for (long i = N; i <= lastN; i += stepN) {
				geneticRandomConsumptionCheck(i, false);
			}
			System.out.println();
			System.out.print("Merge ");
			for (long i = N; i <= lastN; i += stepN) {
				geneticMergeConsumptionCheck(i, false);
			}
			System.out.println();
			System.out.print("Mutate ");
			for (long i = N; i <= lastN; i += stepN) {
				geneticMutateConsumptionCheck(i, false);
			}
			System.out.println();
		} catch (BadAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
