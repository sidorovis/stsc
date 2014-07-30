package stsc.performance;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;

class PerformanceCalculatorSettings {

	final DecimalFormat formatter = new DecimalFormat("#0.000000000");

	final int storedStrategyAmount = 100;
	final int calculationsForAverage = 10;

	final LocalDate startOfPeriod = new LocalDate(1970, 1, 1);

	final int threadsFrom = 4;
	final int threadsTo = 4;

	boolean shouldWarmUp = false;
	boolean printAdditionalInfo = false;
	boolean printStarterInfo = true;

	int maxSelectionIndex = 10;
	int populationSize = 10;

	final List<String> elements = Arrays.asList(new String[] { "open", "high", "low", "close", "value", "open", "high", "low", "close" });
	SearcherType searcherType = SearcherType.GENETIC_SEARCHER;

	public String format(double value) {
		return formatter.format(value);
	}

	public String getStartOfPeriod() {
		return getDateRepresentation(startOfPeriod);
	}

	static private String getDateRepresentation(LocalDate date) {
		int day = date.getDayOfMonth();
		int month = date.getMonthOfYear();
		int year = date.getYear();
		return String.format("%02d-%02d-%04d", day, month, year);
	}

}
