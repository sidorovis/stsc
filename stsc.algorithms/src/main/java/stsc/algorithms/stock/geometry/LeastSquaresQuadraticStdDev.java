package stsc.algorithms.stock.geometry;

import java.util.Iterator;
import java.util.LinkedList;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalContainer;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class LeastSquaresQuadraticStdDev extends StockAlgorithm {

	private final int N;
	private final String subExecutionName;

	private final String lsqName;
	private final LeastSquaresQuadraticValue lsq;

	private double currentX = 0.0;
	private final LinkedList<Double> y = new LinkedList<>();

	public LeastSquaresQuadraticStdDev(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 5).getValue();
		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(LeastSquaresQuadraticStdDev.class + " algorithm require at least one sub algorithms.");
		}
		this.subExecutionName = init.getSettings().getSubExecutions().get(0);

		this.lsqName = init.getExecutionName() + "_Lsq";
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("N", N);
		settings.getSubExecutions().addAll(init.getSettings().getSubExecutions());
		StockAlgorithmInit lsqInit = init.createInit(lsqName, settings);
		this.lsq = new LeastSquaresQuadraticValue(lsqInit);
	}

	public String getLsqName() {
		return lsqName;
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		lsq.process(day);
		final SignalContainer<? extends SerieSignal> subSerieValue = getSignal(subExecutionName, day.getDate());
		final SignalContainer<? extends SerieSignal> lsqValue = getSignal(lsqName, day.getDate());
		if (subSerieValue == null || lsqValue == null) {
			return;
		}
		y.addLast(subSerieValue.getContent(DoubleSignal.class).getValue());
		final ListOfDoubleSignal coefficients = lsqValue.getContent(ListOfDoubleSignal.class);
		final double a0 = coefficients.getValues().get(0);
		final double a1 = coefficients.getValues().get(1);
		final double a2 = coefficients.getValues().get(2);

		double sumStdDev = 0.0;
		final Iterator<Double> yIterator = y.iterator();
		double x = currentX - y.size() + 1;
		while (yIterator.hasNext()) {
			final double yValue = yIterator.next();
			sumStdDev += Math.pow(yValue - (a0 + a1 * x + a2 * Math.pow(x, 2.0)), 2.0);
			x += 1;
		}
		addSignal(day.getDate(), new DoubleSignal(sumStdDev));

		if (currentX >= N) {
			y.pollFirst();
		}
		currentX += 1;
	}
}
