package stsc.algorithms.stock.patterns;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.primitive.Sma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class StockMarketCycle extends StockAlgorithm {

	private final String inputName;
	private final Input input;
	private final Map<String, Sma> smas = new HashMap<>();

	private boolean firstValue = false;
	private double lastValue;

	public StockMarketCycle(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.inputName = init.getExecutionName() + "_Input";
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("size", 500);
		this.input = new Input(init.createInit(inputName, settings));
		// initializeSmaAlgo(10, init);
		// initializeSmaAlgo(50, init);
		initializeSmaAlgo(75, init);
		initializeSmaAlgo(100, init);
		initializeSmaAlgo(125, init);
		initializeSmaAlgo(250, init);
		initializeSmaAlgo(300, init);
		initializeSmaAlgo(400, init);
		initializeSmaAlgo(500, init);
	}

	private void initializeSmaAlgo(int n, StockAlgorithmInit init) throws BadAlgorithmException {
		final String smaName = init.getExecutionName() + "_Sma" + String.valueOf(n);
		final Sma sma = createSmaAlgo(smaName, n, init);
		smas.put(smaName, sma);
	}

	private Sma createSmaAlgo(String name, int smaN, StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("N", smaN);
		settings.setInteger("size", smaN);
		settings.addSubExecutionName(inputName);
		return new Sma(init.createInit(name, settings));
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		double sum = 0.0;
		input.process(day);
		for (Entry<String, Sma> e : smas.entrySet()) {
			e.getValue().process(day);
			final double v = getSignal(e.getKey(), day.getDate()).getContent(DoubleSignal.class).getValue();
			sum += v;
		}
		double currentV = sum / smas.size();
		if (!firstValue) {
			final double diff = (currentV - lastValue) / 10.0;
			if (diff > 0.01) {
				addSignal(day.getDate(), new DoubleSignal(100));
			} else if (diff < -0.01) {
				addSignal(day.getDate(), new DoubleSignal(-100));
			} else {
				addSignal(day.getDate(), new DoubleSignal(0));
			}
		} else {
			firstValue = false;
		}
		lastValue = currentV;
	}
}
