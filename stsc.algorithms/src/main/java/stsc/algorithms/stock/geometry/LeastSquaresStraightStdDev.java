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
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class LeastSquaresStraightStdDev extends StockAlgorithm {

	private final int N;
	private final String subExecutionName;

	private final String lssName;
	private final LeastSquaresStraightValue lss;

	private double currentX = 0.0;
	private final LinkedList<Double> y = new LinkedList<>();

	public LeastSquaresStraightStdDev(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 5).getValue();
		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(LeastSquaresStraightStdDev.class + " algorithm require at least one sub algorithms.");
		}
		this.subExecutionName = init.getSettings().getSubExecutions().get(0);

		this.lssName = init.getExecutionName() + "_Lss";
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("N", N);
		settings.getSubExecutions().addAll(init.getSettings().getSubExecutions());
		StockAlgorithmInit lssInit = init.createInit(lssName, settings);
		this.lss = new LeastSquaresStraightValue(lssInit);
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		lss.process(day);
		y.addLast(getSignal(subExecutionName, day.getDate()).getSignal(DoubleSignal.class).getValue());
		final ListOfDoubleSignal coefficients = getSignal(lssName, day.getDate()).getSignal(ListOfDoubleSignal.class);
		final double a0 = coefficients.getValues().get(0);
		final double a1 = coefficients.getValues().get(1);

		double sumStdDev = 0.0;
		final Iterator<Double> yIterator = y.iterator();
		double x = currentX - y.size() + 1;
		while (yIterator.hasNext()) {
			final double yValue = yIterator.next();
			sumStdDev += Math.pow(yValue - (a0 + a1 * x), 2.0);
			x += 1;
		}
		addSignal(day.getDate(), new DoubleSignal(sumStdDev));

		if (currentX >= N) {
			y.pollFirst();
		}

		currentX += 1;
	}

}
