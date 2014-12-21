package stsc.algorithms.stock.indices.adi;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class AdiAccDist extends StockAlgorithm {

	private double value = 0.0;

	private final String adiClvName;
	private final AdiClv adiClv;

	private final double koefficient;

	public AdiAccDist(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.koefficient = init.getSettings().getDoubleSetting("K", 0.0001).getValue();

		this.adiClvName = init.getExecutionName() + "_AdiClv";
		this.adiClv = new AdiClv(init.createInit(adiClvName));
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		adiClv.process(day);
		final double clv = getSignal(adiClvName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		value += clv * day.getVolume() * koefficient;
		addSignal(day.getDate(), new DoubleSignal(value));
	}

}
