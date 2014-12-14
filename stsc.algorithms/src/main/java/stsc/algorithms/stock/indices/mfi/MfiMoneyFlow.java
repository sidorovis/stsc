package stsc.algorithms.stock.indices.mfi;

import stsc.algorithms.stock.indices.TypicalPrice;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class MfiMoneyFlow extends StockAlgorithm {

	private final String mfiTpName;
	private final TypicalPrice mfiTp;

	public MfiMoneyFlow(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.mfiTpName = init.getExecutionName() + "_mfiTp";
		final StockAlgorithmInit tpInit = init.createInit(mfiTpName);
		this.mfiTp = new TypicalPrice(tpInit);
	}
	
	public String getMfiTpName() {
		return this.mfiTpName;
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		mfiTp.process(day);
		final double tpValue = getSignal(mfiTpName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		addSignal(day.getDate(), new DoubleSignal(tpValue * day.getVolume()));
	}

}
