package stsc.algorithms.stock.indices;

import java.util.Optional;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class MarketTrend extends StockAlgorithm {

	private final String stockName;
	private final String stockWeExecuteAt;

	private final String spyAlgoName;
	private final StockMarketCycle spyAlgo;

	public MarketTrend(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.stockName = init.getSettings().getStringSetting("SN", "spy").getValue();
		stockWeExecuteAt = init.getStockName();
		this.spyAlgoName = init.getExecutionName() + "_Spy";
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		final StockAlgorithmInit spyInit = new StockAlgorithmInit(spyAlgoName, init, stockName, settings);
		spyAlgo = new StockMarketCycle(spyInit);
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final int size = getIndexForStock(stockName, spyAlgoName);
		if (size > 0) {
			final double value = getSignal(stockName, spyAlgoName, size - 1).getContent(DoubleSignal.class).getValue();
			addSignal(day.getDate(), new DoubleSignal(value));
		} else {
			addSignal(day.getDate(), new DoubleSignal(0.0));
		}
		if (stockName.equals(stockWeExecuteAt)) {
			spyAlgo.process(day);
		}
	}

}
