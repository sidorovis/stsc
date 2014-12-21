package stsc.algorithms.stock.indices.atr;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class AtrAtr extends StockAlgorithm {

	private int index = 0;
	private double sum = 0.0;
	private double prevoiusValue = 0.0;
	private final int N;

	private final String atrTrName;
	private final AtrTrueRange atrTr;

	public AtrAtr(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		N = init.getSettings().getIntegerSetting("N", 14).getValue();

		atrTrName = init.getExecutionName() + "_AtrTr";
		this.atrTr = new AtrTrueRange(init.createInit(atrTrName));
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		atrTr.process(day);
		final double atrTrValue = getSignal(atrTrName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		if (index < N) {
			sum += atrTrValue;
			prevoiusValue = sum / (index + 1);
		} else {
			prevoiusValue = (prevoiusValue * (N - 1) + atrTrValue) / N;
		}
		addSignal(day.getDate(), new DoubleSignal(prevoiusValue));
		index += 1;
	}

}
