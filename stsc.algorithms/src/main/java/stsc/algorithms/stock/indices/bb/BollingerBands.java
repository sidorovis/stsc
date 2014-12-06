package stsc.algorithms.stock.indices.bb;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.stock.factors.primitive.SmStdDev;
import stsc.algorithms.stock.factors.primitive.Sma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.AlgorithmSetting;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class BollingerBands extends StockAlgorithm {

	private final AlgorithmSetting<Integer> N;
	private final AlgorithmSetting<Double> K;
	private Integer size;

	private final String smaName;
	private final Sma sma;
	private final String smStdDevName;
	private final SmStdDev smStdDev;

	public BollingerBands(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		N = init.getSettings().getIntegerSetting("N", 20);
		K = init.getSettings().getDoubleSetting("K", 2.0);
		this.smaName = "BB_Sma_" + init.getExecutionName();
		this.smStdDevName = "BB_StdDev_" + init.getExecutionName();

		this.sma = createSma(init);
		this.smStdDev = createStdDev(init);
	}

	private Sma createSma(StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("N", N.getValue());
		settings.setInteger("size", size);
		settings.getSubExecutions().addAll(init.getSettings().getSubExecutions());
		final StockAlgorithmInit smaInit = new StockAlgorithmInit(smaName, init, settings);
		return new Sma(smaInit);
	}

	private SmStdDev createStdDev(StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("N", N.getValue());
		settings.setInteger("size", size);
		settings.getSubExecutions().addAll(init.getSettings().getSubExecutions());
		settings.addSubExecutionName(smaName);
		final StockAlgorithmInit smStdDevInit = new StockAlgorithmInit(smStdDevName, init, settings);
		return new SmStdDev(smStdDevInit);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<StockSignal>(ListOfDoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		sma.process(day);
		smStdDev.process(day);
		final Double sma = getSignal(smaName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		final Double stdDev = getSignal(smStdDevName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		final ListOfDoubleSignal signal = new ListOfDoubleSignal();
		signal.addDouble(sma - stdDev * K.getValue());
		signal.addDouble(sma + stdDev * K.getValue());
		addSignal(day.getDate(), signal);
	}

}