package stsc.algorithms.stock.indices;

import java.util.Optional;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.stock.indices.primitive.SmStDev;
import stsc.algorithms.stock.indices.primitive.Sma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class Cci extends StockAlgorithm {

	private final double K;

	private final String typicalPriceName;
	private final TypicalPrice typicalPrice;

	private final String smaName;
	private final Sma sma;

	private final String stDevName;
	private final SmStDev stDev;

	public Cci(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.K = init.getSettings().getDoubleSetting("K", 1.0 / (0.015)).getValue();

		this.typicalPriceName = init.getExecutionName() + "_TypicalPrice";
		this.typicalPrice = new TypicalPrice(init.createInit(typicalPriceName));

		this.smaName = init.getExecutionName() + "_Sma";
		this.sma = createSma(init);

		this.stDevName = init.getExecutionName() + "_SmStDev";
		this.stDev = createStDev(init);
	}

	private Sma createSma(StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl smaSettings = new AlgorithmSettingsImpl(init);
		smaSettings.setInteger("N", init.getSettings().getIntegerSetting("N", 5).getValue());
		smaSettings.addSubExecutionName(typicalPriceName);
		return new Sma(init.createInit(smaName, smaSettings));
	}

	private SmStDev createStDev(StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("N", init.getSettings().getIntegerSetting("N", 5).getValue());
		settings.addSubExecutionName(typicalPriceName);
		settings.addSubExecutionName(smaName);
		return new SmStDev(init.createInit(stDevName, settings));
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<SerieSignal>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		typicalPrice.process(day);
		sma.process(day);
		stDev.process(day);
		final double tpV = getSignal(typicalPriceName, day.getDate()).getContent(DoubleSignal.class).getValue();
		final double smaV = getSignal(smaName, day.getDate()).getContent(DoubleSignal.class).getValue();
		final double stDevV = getSignal(stDevName, day.getDate()).getContent(DoubleSignal.class).getValue();
		if (Double.compare(stDevV, 0.0) != 0) {
			final double v = K * (tpV - smaV) / stDevV;
			addSignal(day.getDate(), new DoubleSignal(v));
		} else {
			addSignal(day.getDate(), new DoubleSignal(0.0));
		}
	}

}
