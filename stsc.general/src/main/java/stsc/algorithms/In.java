package stsc.algorithms;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.SignalsSerie;
import stsc.common.StockSignal;
import stsc.signals.DoubleSignal;

public class In extends StockAlgorithm {

	enum DayField {
		OPEN, HIGH, LOW, CLOSE, VALUE
	};

	private static DayField fromString(String dayField) {
		if (dayField == null)
			return DayField.OPEN;
		switch (dayField) {
		case "open":
			return DayField.OPEN;
		case "high":
			return DayField.HIGH;
		case "low":
			return DayField.LOW;
		case "close":
			return DayField.CLOSE;
		case "value":
			return DayField.VALUE;
		default:
			return DayField.OPEN;
		}
	}

	DayField dayField;

	public In(StockAlgorithmInit initialize) throws BadAlgorithmException {
		super(initialize);
		dayField = fromString(initialize.settings.get("e"));
	}

	private DoubleSignal getData(final Day day) {
		switch (dayField) {
		case OPEN:
			return new DoubleSignal(day.getPrices().getOpen());
		case CLOSE:
			return new DoubleSignal(day.getPrices().getClose());
		case HIGH:
			return new DoubleSignal(day.getPrices().getHigh());
		case LOW:
			return new DoubleSignal(day.getPrices().getLow());
		case VALUE:
			return new DoubleSignal(day.getVolume());
		default:
			return new DoubleSignal(day.getPrices().getOpen());
		}
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(final StockAlgorithmInit init) throws BadAlgorithmException {
		return new LimitSignalsSerie<StockSignal>(DoubleSignal.class);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		addSignal(day.getDate(), getData(day));
	}

}
