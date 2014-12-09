package stsc.algorithms.stock.indices.mfi;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class MfiMfi extends StockAlgorithm {

	private final int N;
	private Day previousDay;
	private int currentIndex = 0;
	private double positiveSum = 0.0;
	private double negativeSum = 0.0;

	private final String mfiMfName;
	private final MfiMoneyFlow mfiMf;

	private final String mfiTpName;

	public MfiMfi(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 14).getValue();

		this.mfiMfName = init.getExecutionName() + "_mfiMf";
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("size", N + 2);
		this.mfiMf = new MfiMoneyFlow(init.createInit(mfiMfName, settings));
		this.mfiTpName = mfiMf.getMfiTpName();
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int n = initialize.getSettings().getIntegerSetting("N", 2).getValue();
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, Math.max(size + 2, n + 2));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		mfiMf.process(day);
		if (previousDay == null) {
			previousDay = day;
		}
		{
			final double previousTp = getSignal(mfiTpName, previousDay.getDate()).getSignal(DoubleSignal.class).getValue();
			final double nowTp = getSignal(mfiTpName, day.getDate()).getSignal(DoubleSignal.class).getValue();

			final double nowMf = getSignal(mfiMfName, day.getDate()).getSignal(DoubleSignal.class).getValue();

			if (nowTp >= previousTp) {
				positiveSum += nowMf;
			} else {
				negativeSum += nowMf;
			}
		}
		if (currentIndex > N) {
			final double previousOldMTp = getSignal(mfiTpName, currentIndex - N - 1).getSignal(DoubleSignal.class).getValue();
			final double oldTp = getSignal(mfiTpName, currentIndex - N).getSignal(DoubleSignal.class).getValue();

			final double oldMf = getSignal(mfiMfName, currentIndex - N).getSignal(DoubleSignal.class).getValue();

			if (oldTp >= previousOldMTp) {
				positiveSum -= oldMf;
			} else {
				negativeSum -= oldMf;
			}
		}
		if (Double.compare(negativeSum, 0.0) == 0) {
			addSignal(day.getDate(), new DoubleSignal(50.0));
		} else {
			final double value = 100.0 - (100.0 / (1 + positiveSum / negativeSum));
			addSignal(day.getDate(), new DoubleSignal(value));
		}

		currentIndex++;
		previousDay = day;
	}
}
